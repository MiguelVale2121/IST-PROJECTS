package ggc.core;

import java.io.Serializable;
import java.io.IOException;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import ggc.core.Date;
import ggc.core.Parser;
import ggc.core.product.Product;
import ggc.core.product.RecipeComponent;
import ggc.core.product.comparators.OrderByLowerPriceFirst;
import ggc.core.transaction.Acquisition;
import ggc.core.transaction.BreakdownSale;
import ggc.core.transaction.SaleByCredit;
import ggc.core.transaction.Transaction;
import ggc.core.transaction.Transaction.TransactionType;
import ggc.core.product.Batch;
import ggc.core.partner.Partner;
import ggc.core.exception.BadEntryException;
import ggc.core.exception.NotEnoughResourcesException;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.exception.UnknownObjectKeyException.ObjectType;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.app.exception.UnknownProductKeyException;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202109192006L;

  /** Warehouse products */
  private Set<Product> _products;

  /** Warehouse associated partners */
  private Set<Partner> _partners;
  

  private int _nextTransactionId;

  private double _acquisitionsBalance;

  private double _salesBalance;

  private HashSet<Transaction> _transactions;

  /**
   * Creates a new Warehouse
   */
  public Warehouse() {
    _products = new HashSet<>();
    _partners = new HashSet<>();
    _transactions = new HashSet<>();
  }

  /**
   * Get Warehouse products list
   * @return products list
   */
  public List<Product> getProducts() {
    List<Product> products = new ArrayList<>(_products);
    Collections.sort(products);
    return products;
  }

  /**
   * Get Warehouse partners list
   * @return partners list
   */
  public List<Partner> getPartners() {
    List<Partner> partners = new ArrayList<>(_partners);
    Collections.sort(partners);
    return partners;
  }

  /**
   * Get a specific product by its id
   * @param id product id
   * @return Product
   * @throws UnknownObjectKeyException
   */
  public Product getProduct(String id) throws UnknownObjectKeyException {
    for (Product product : getProducts()) {
      if (product.getId().equalsIgnoreCase(id)) {
        return product;
      }
    }
    throw new UnknownObjectKeyException(id, ObjectType.PRODUCT);
  }

  /**
   * Get a specific partner by its id
   * @param id partner id
   * @return Partner
   * @throws UnknownObjectKeyException
   */
  public Partner getPartner(String id) throws UnknownObjectKeyException {
    for (Partner partner : getPartners()) {
      if (partner.getId().equalsIgnoreCase(id)) {
        return partner;
      }
    }
    throw new UnknownObjectKeyException(id, ObjectType.PARTNER);
  }

  /**
   * Register a new Partner on the Warehouse
   * @param partner Partner object
   * @return if the operation was successful or not
   */
  public boolean addPartner(Partner partner){
    return _partners.add(partner);
  }

  /**
   * Register a new Product on the Warehouse
   * @param product Product object
   * @return if the operation was successful or not
   */
  public boolean addProduct(Product product) {
    for (Partner partner : _partners) {
      product.subscribe(partner);
    }
    return _products.add(product);
  }

  /**
   * Create a new Batch on Warehouse
   * @param idPartner
   * @param idProduct
   * @param stock
   * @param price
   * @throws UnknownObjectKeyException
   */
  public void addBatch(String idPartner, String idProduct, int stock, double price) throws UnknownObjectKeyException {
    Product product = getProduct(idProduct);
    Partner partner = getPartner(idPartner);
    Batch batch = new Batch(partner, product, stock, price);
    product.addBatch(batch);
  }

  /**
   * Get all available warehouse batches 
   * @return batches list
   */
  public List<Batch> getAvailableBatches() {
    List<Batch> batchList = new ArrayList<>();
    for(Product product : _products) {
      for(Batch batch : product.getBatches()) {
        batchList.add(batch);
      }
    }
    Collections.sort(batchList);
    return batchList;
  }

  public List<Batch> getBatchesByPartner(String partnerId) throws UnknownObjectKeyException {
    List<Batch> batchList = new ArrayList<>();

    getPartner(partnerId);

    for(Product product : _products) {
      for(Batch batch : product.getBatchesByPartner(partnerId)) {
        batchList.add(batch);
      }
    }
    Collections.sort(batchList);
    return batchList;
  }

  public void registerAcquisition(String partnerId, String productId, double price, int amount) throws UnknownObjectKeyException {
    
    // Get Transaction Entities info
    Partner supplier = getPartner(partnerId);
    Product product = getProduct(productId);

    // Process operation
    double totalPrice = 0.0;

    try {
      totalPrice = supplier.sellBatch(productId, amount, price);
    } catch (NotEnoughResourcesException exception) {
      totalPrice = price * amount;
      supplier.increasePurchases(totalPrice);
    }
    _acquisitionsBalance += totalPrice;
    
    // Update inventory
    product.addBatch(new Batch(supplier, product, amount, price));

    // Register transaction
    Transaction acquisitionTransaction = new Acquisition(_nextTransactionId++, product, amount, supplier, Date.now().getValue(), totalPrice);

    _transactions.add(acquisitionTransaction);
    supplier.addTransaction(acquisitionTransaction);
    acquisitionTransaction.pay();
  }

  public void checkDerivativeStock(Product product, int amount) throws NotEnoughResourcesException {
    for (RecipeComponent component : product.getRecipe()) {
      Product componentProduct = component.getProduct();
      int amountNeeded = component.getAmount() * amount;
      if (!componentProduct.hasAvailableStock(amountNeeded)) {
        throw new NotEnoughResourcesException(componentProduct.getId(), amountNeeded, componentProduct.getTotalStock());
      }
    }
  }

  public double createProduct(Product product) throws NotEnoughResourcesException {
    double balance = 0.0;
    for (RecipeComponent component : product.getRecipe()) {
      balance += component.getProduct().sellAmount(component.getAmount());
    }
    return balance;
  }

  public void registerSaleByCredit(String partnerId, String productId, int paymentDeadline, int amount) throws NotEnoughResourcesException, UnknownObjectKeyException {
    // Get Transaction Entities info
    Partner partner = getPartner(partnerId);
    Product product = getProduct(productId);

    int remainingAmount = 0; // For derivative products
    
    // Check stocks
    if (!product.hasAvailableStock(amount)) {
      // Check if we can create more
      if (!product.isSimple()) {
        remainingAmount = amount - product.getTotalStock();
        checkDerivativeStock(product, remainingAmount);
      } else {
        throw new NotEnoughResourcesException(productId, amount, product.getTotalStock());
      }
    }

    // Process operation
    try {
      double unitPrice = product.getLowestPrice();
      partner.addBatch(new Batch(partner, product, amount - remainingAmount, unitPrice));
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }

    // Agregate products (Derivative aditional stage)
    if (remainingAmount > 0) {
      double previousUnitPrice = 0.0;
      double agregationCost = 1 + product.getAlpha();
      int currentAmount = 0;
      for (int i = 0; i < remainingAmount; i++) {
        double currentUnitPrice = createProduct(product);
        if (previousUnitPrice != 0.0) {
          if (previousUnitPrice != currentUnitPrice) {
            product.addBatch(new Batch(partner, product, currentAmount, previousUnitPrice * agregationCost));
            currentAmount = 0;
          }
        }
        previousUnitPrice = currentUnitPrice;
        currentAmount++;
      }
      product.addBatch(new Batch(partner, product, currentAmount, previousUnitPrice * agregationCost));
    }
    
    // Update inventory
    double basePrice = product.sellAmount(amount);

    // Register transaction
    Transaction saleTransaction = new SaleByCredit(_nextTransactionId++, product, amount, partner, basePrice, paymentDeadline);
    
    _transactions.add(saleTransaction);
    partner.addTransaction(saleTransaction);
  }

  public void registerBreakdown(String partnerId, String productId, int amount) throws NotEnoughResourcesException, UnknownObjectKeyException {
    // Get Transaction Entities info
    Partner partner = getPartner(partnerId);
    Product product = getProduct(productId);

    if (product.isSimple()) {
      return;
    }

    // Check stocks
    if (!product.hasAvailableStock(amount)) {
      throw new NotEnoughResourcesException(productId, amount, product.getTotalStock());
    }

    // Process operation
    double acquisitonPrice = 0.0;
    double unitPrice = 0.0;
    String componentsString = "";
    
    for (RecipeComponent component : product.getRecipe()) {

      Product componentProduct = component.getProduct();

      try {
        unitPrice = componentProduct.getLowestPrice();
      } catch (IndexOutOfBoundsException ignored) {
        unitPrice = getTransactionsHighestPrice(componentProduct.getId());
      }

      int productAmount = component.getAmount() * amount;
  

      componentsString += componentProduct.getId() + ":" + productAmount + ":" + Math.round(productAmount * unitPrice) + "#";
      componentProduct.addBatch(new Batch(partner, componentProduct, productAmount, unitPrice));

      acquisitonPrice += unitPrice * productAmount;
    }

    // Update inventory 
    double sellPrice = product.sellAmount(amount);

    // Register transaction
    Transaction breakdownTransaction = new BreakdownSale(_nextTransactionId++, product, amount, partner, sellPrice - acquisitonPrice, componentsString.subSequence(0, componentsString.length() - 1).toString());
    
    _transactions.add(breakdownTransaction);
    partner.addTransaction(breakdownTransaction);
    breakdownTransaction.pay();

  }

  public void registerPartnerPayment(int transactionId) throws UnknownObjectKeyException {
    Transaction transaction = getTransaction(transactionId);
    transaction.pay();
  }

  public List<Transaction> getTransactionsByPartner(String partnerId, TransactionType type) throws UnknownObjectKeyException {
    List<Transaction> transactions = new ArrayList<>();
    for (Transaction transaction : getPartner(partnerId).getTransactions()) {
      if (transaction.getType() == type) {
        transactions.add(transaction);
      }
    }
    return transactions;
  }

  public List<Transaction> getTransactionsByType(TransactionType type) {
    List<Transaction> transactions = new ArrayList<>();
    for (Transaction transaction : _transactions) {
      if (transaction.getType() == type) {
        transactions.add(transaction);
      }
    }
    return transactions;
  }

  public double getTransactionsHighestPrice(String productId) {
    double price = 0.0;
    for (Transaction transaction : _transactions) {
      Product product = transaction.getProduct();
      if (product.getId().equals(productId)) {
        double transactionUnitPrice = transaction.getBasePrice() / transaction.getQuantity();
        if (transactionUnitPrice > price) {
          price = transactionUnitPrice;
        }
      }
    }
    return price;
  }

  public Transaction getTransaction(int transactionId) throws UnknownObjectKeyException {
    for (Transaction transaction : _transactions) {
      if (transaction.getId() == transactionId) {
        return transaction;
      }
    }
    throw new UnknownObjectKeyException(Integer.toString(transactionId), ObjectType.TRANSACTION);
  }

  public double getAccountingBalance() {
    double balance = 0.0;
    for (Transaction transaction : _transactions) {
      balance += transaction.calculatePriceToPay();
    }
    return balance - _acquisitionsBalance;
  }

  public double getAvailableBalance() {
    return _salesBalance - _acquisitionsBalance;
  }

  /**
   * @param txtfile filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   */
  void importFile(String txtfile) throws IOException, BadEntryException, UnknownObjectKeyException {
    Parser parser = new Parser(this);
    parser.parseFile(txtfile);
  }

  public void toggleNotificationStatus(String partnerId, String productId) throws UnknownObjectKeyException {
    Partner partner = getPartner(partnerId);
    Product product = getProduct(productId);
    if (product.isSubscribed(partner)) {
      product.unsubscribe(partner);
    } else {
      product.subscribe(partner);
    }
  }

}
