package ggc.core;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ggc.core.Warehouse;
import ggc.core.Date;
import ggc.core.Parser;
import ggc.core.product.Product;
import ggc.core.transaction.Transaction;
import ggc.core.transaction.Transaction.TransactionType;
import ggc.core.product.Batch;
import ggc.core.partner.Partner;

import ggc.core.exception.BadEntryException;
import ggc.core.exception.ImportFileException;
import ggc.core.exception.InvalidDateValueException;
import ggc.core.exception.UnavailableFileException;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.lookups.BatchesWithPriceLowerThan;
import ggc.core.lookups.Lookup;
import ggc.core.lookups.LookupStrategy;
import ggc.core.lookups.TransactionsPaidByPartner;
import ggc.core.exception.MissingFileAssociationException;
import ggc.core.exception.NotEnoughResourcesException;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.app.exception.UnknownProductKeyException;

/** Façade for access. */
public class WarehouseManager {

  /** Name of file storing current warehouse. */
  private String _filename = "";

  /** The wharehouse itself. */
  private Warehouse _warehouse;

  private Lookup _lookupModule;

  public WarehouseManager() {
    _warehouse = new Warehouse();
    _lookupModule = new Lookup(_warehouse);
  }

  public Collection<Object> lookupOperation(LookupStrategy strategy) throws UnknownObjectKeyException {
    _lookupModule.setStrategy(strategy);
    return _lookupModule.execute();
  }

  public Collection<Object> lookupProductsUnderTopPrice(double price) {
    Collection<Object> results = new ArrayList<>();
    try {
      results = lookupOperation(new BatchesWithPriceLowerThan(price));
    } catch (UnknownObjectKeyException ignored) {
      // This will never happen
    }
    return results;
  }

  public Collection<Object> lookupPaymentsByPartner(String partnerId) throws UnknownObjectKeyException {
    return lookupOperation(new TransactionsPaidByPartner(partnerId));
  }

  /**
   * Get the current date object
   * @return Date
   */
  public Date getDate() {
    return Date.now();
  }

  /**
   * Advance Warehouse date with the supplied value
   * @param value
   * @throws InvalidDateValueException
   */
  public void advanceDate(int value) throws InvalidDateValueException {
    Date.add(value);
  }

  /**
   * Get the name of the current program associated file
   * @return filename
   */
  public String getFilename() {
    return _filename;
  }

  /**
   * Get Warehouse products list
   * @return products list
   * @see Warehouse#getProducts()
   */
  public List<Product> getProducts() {
    return _warehouse.getProducts();
  }
  
  /**
   * Get Warehouse partners list
   * @return partners list
   * @see Warehouse#getPartners()
   */
  public List<Partner> getPartners() {
    return _warehouse.getPartners();
  }

  /**
   * Get all available warehouse batches 
   * @return batches list
   * @see Warehouse#getAvailableBatches()
   */
  public List<Batch> getAvailableBatches() {
    return _warehouse.getAvailableBatches();
  }

  public List<Batch> getBatchesByPartner(String partnerId) throws UnknownObjectKeyException {
    return _warehouse.getBatchesByPartner(partnerId);
  }


  /**
   * Get a specific partner by its id
   * @param id partner id
   * @return Partner
   * @throws UnknownObjectKeyException
   * @see Warehouse#getPartner(String id)
   */
  public Partner getPartner(String id) throws UnknownObjectKeyException {
    return _warehouse.getPartner(id);
  }

  /**
   * Get a specific product by its id
   * @param id product id
   * @return Product
   * @throws UnknownObjectKeyException
   * @see Warehouse#getProduct(String id)
   */
  public Product getProduct(String id) throws UnknownObjectKeyException {
    return _warehouse.getProduct(id);
  }

  /**
   * Register a new Partner on the Warehouse
   * @param partner Partner object
   * @return if the operation was successful or not
   * @see Warehouse#addPartner(Partner partner)
   */
  public boolean addPartner(String id, String name, String address){
    return _warehouse.addPartner(new Partner(id, name, address));
  }

  /**
   * Register a new Product on the Warehouse
   * @param product Product object
   * @return if the operation was successful or not
   * @see Warehouse#addProduct(Product product) 
   */
  public boolean addProduct(Product product) {
    return _warehouse.addProduct(product);
  }
  
  public void registerAcquisition(String partnerId, String productId, double price, int amount) throws UnknownObjectKeyException, NotEnoughResourcesException {
    _warehouse.registerAcquisition(partnerId, productId, price, amount);
  }

  public void registerSaleByCredit(String partnerId, String productId, int paymentDeadline, int amount) throws NotEnoughResourcesException, UnknownObjectKeyException {
    _warehouse.registerSaleByCredit(partnerId, productId, paymentDeadline, amount);
  }

  public void registerBreakdown(String partnerId, String productId, int amount) throws NotEnoughResourcesException, UnknownObjectKeyException {
    _warehouse.registerBreakdown(partnerId, productId, amount);
  }

  public List<Transaction> getTransactionsByPartner(String partnerId, TransactionType type) throws UnknownObjectKeyException {
    return _warehouse.getTransactionsByPartner(partnerId, type);
  }

  public String viewTransaction(int transactionId) throws UnknownObjectKeyException {
    return _warehouse.getTransaction(transactionId).toString();
  }

  public void registerPartnerPayment(int transactionId) throws UnknownObjectKeyException {
    _warehouse.registerPartnerPayment(transactionId);
  }

  public double getAccountingBalance() {
    return _warehouse.getAccountingBalance();
  }

  public double getAvailableBalance() {
    return _warehouse.getAvailableBalance();
  }

  /**
   * Save current state on the associated file
   * @throws IOException
   * @throws FileNotFoundException
   * @throws MissingFileAssociationException
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    if (_filename.isEmpty()) {
      throw new MissingFileAssociationException();
    }
    try (
      FileOutputStream outFile = new FileOutputStream(_filename);
      ObjectOutputStream outStream = new ObjectOutputStream(outFile)
    ) {
      outStream.writeObject(_warehouse);
      outStream.writeObject(Date.now().getValue());
    }
  }

  /**
   * Associate a file and save current state
   * @param filename
   * @throws IOException
   * @throws FileNotFoundException
   * @see WarehouseManager#save()
   */
  public void saveAs(String filename) throws FileNotFoundException, IOException {
    // Avoid MissingFileAssociationException when the supplied filename is empty
    if (filename.isEmpty())
      throw new FileNotFoundException();
    
    _filename = filename;
    try {
      save();
    } catch (MissingFileAssociationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load state from file
   * @param filename
   * @throws UnavailableFileException
   * @throws ClassNotFoundException
   */
  public void load(String filename) throws UnavailableFileException, ClassNotFoundException  {
    try (
      FileInputStream inFile = new FileInputStream(filename);
      ObjectInputStream inStream = new ObjectInputStream(inFile)
    ) {
      _warehouse = (Warehouse) inStream.readObject();
      _lookupModule = new Lookup(_warehouse);
      try {
        Date.set((int) inStream.readObject());
      } catch (InvalidDateValueException ignored) {
        // This is not possible to happen
      }
      _filename = filename;
    } catch (IOException ignored) {
      throw new UnavailableFileException(filename);
    } catch (ClassNotFoundException ignored) {
      throw new ClassNotFoundException();
    }
  }

  /**
   * Loads entities from text file
   * @param textfile
   * @throws ImportFileException
   * @throws BadEntryException
   */
  public void importFile(String textfile) throws ImportFileException, BadEntryException {
    Parser parser = new Parser(_warehouse);
    try {
      parser.parseFile(textfile);
    } catch (IOException | BadEntryException | UnknownObjectKeyException e) {
      throw new ImportFileException(textfile, e);
    }
  }

  public void toggleNotificationStatus(String partnerId, String productId) throws UnknownObjectKeyException {
    _warehouse.toggleNotificationStatus(partnerId, productId);
  }

}
