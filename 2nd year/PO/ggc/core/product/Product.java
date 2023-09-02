package ggc.core.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ggc.core.notifications.Notification;
import ggc.core.notifications.Subscriber;
import ggc.core.StockEntity;
import ggc.core.exception.NotEnoughResourcesException;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.partner.Partner;
import ggc.core.product.Batch;
import ggc.core.product.comparators.OrderByLowerPriceFirst;

/** Implements Product Base (abstract) class */
public abstract class Product extends StockEntity implements Comparable<Product> {

    /** The maximum product price */
    private double _maxPrice;

    /** Product total stock */
    private int _totalStock;
    
    /** Product id */
    private String _id;

    /** Partners list */
    private List<Subscriber> _subscribers;

    private boolean canCheckForNotifications;

    /**
     * Creates a new Product
     * @param id product id
     */
    public Product(String id) {
        super();
        _id = id;
        _subscribers = new ArrayList<>();
    }

    /**
     * Get product id
     * @return String id
     */
    public String getId() {
        return _id;
    }

    /**
     * Get max price
     * @return double price
     */
    public double getMaxPrice() {
        return _maxPrice;
    }

    /**
     * Get total stock
     * @return int total stock
     */
    public int getTotalStock() {
        return _totalStock;
    }

    public boolean isSimple() {
        return true;
    }

    public List<RecipeComponent> getRecipe() {
        return new ArrayList<>();
    }

    /**
     * Displays Simple Product Information 
     * @return String (idProduto|preço-máximo|stock-actual-total)
     * @see DerivativeProduct#toString()
     */
    public String toString() {
        return getId() + "|" + Math.round(getMaxPrice()) + "|" + getTotalStock();
    }

    public void updateMaxPrice() {
        double maxPrice = 0;
        for (Batch batch : getBatches()) {
            if (batch.getUnitPrice() > maxPrice) {
                maxPrice = batch.getUnitPrice();
            }
        }
        _maxPrice = maxPrice;
    }

    /**
     * Adds a new product batch
     * @param batch
     */
    @Override
    public void addBatch(Batch batch) {

        checkForUpdates(batch.getUnitPrice());
        
        _totalStock += batch.getAmount();
        if (batch.getUnitPrice() > _maxPrice) {
            _maxPrice = batch.getUnitPrice();
        }

        super.addBatch(batch);

        if (!canCheckForNotifications) {
            canCheckForNotifications = true;
        }
    }

    public void checkForUpdates(double price) {
        if (canCheckForNotifications) {
            if (getBatches().isEmpty()) {
                notifySubscribers(new Notification(this, "NEW", price));
            } else {
                double lowestPriceBefore = getLowestPrice();
                if (price < lowestPriceBefore) {
                    notifySubscribers(new Notification(this, "BARGAIN", price));
                }
            }
        }
    }


    public double getLowestPrice() throws IndexOutOfBoundsException {
        return getBatches(new OrderByLowerPriceFirst()).get(0).getUnitPrice();
    }


    /**
     * Sell a specific amount of this product, returns the price
     * @param batch
     */
    public double sellAmount(int amount) throws NotEnoughResourcesException {
        if (hasAvailableStock(amount)) {
            int remain = amount;
            _totalStock -= amount;
            double price = 0.0;
            Iterator<Batch> batchIterator = getBatches(new OrderByLowerPriceFirst()).iterator();

            while (batchIterator.hasNext()) {
                Batch batch = batchIterator.next();
                double batchPrice = batch.getUnitPrice();
                int previousRemain = remain;
                remain = takeBatchAmount(batch, remain);
                price += batchPrice * (previousRemain - remain);
                if (remain == 0) {
                    break;
                }
            }

            return price;

        } else {
            throw new NotEnoughResourcesException(_id, amount, _totalStock);
        }
    }

    public double getAlpha() {
        return 0;
    }
    
    /* Override equals in order to compare Products by id */
    @Override
    public boolean equals(Object a) {

        if (a == this) {
            return true;
        }

        if (a == null) {
            return false;
        }

        return ((Product)a).getId().equalsIgnoreCase(_id.toLowerCase());
    }

    /* Override hashCode to compare Product objects by their id */
    @Override
    public int hashCode() {
        return _id.toLowerCase().hashCode();
    }

    /* Implements Comparable interface method for sorting purposes */
    public int compareTo(Product product) {
        return _id.compareToIgnoreCase(product.getId());
    }

    public boolean isSubscribed(Subscriber subscriber) {
        return _subscribers.contains(subscriber);
    }

    public void subscribe(Subscriber subscriber) {
        _subscribers.add(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) {
        _subscribers.remove(subscriber);
    }

    public void notifySubscribers(Notification n) {
        for (Subscriber subscriber: _subscribers) {
            subscriber.update(n);
        }
    } 
}
