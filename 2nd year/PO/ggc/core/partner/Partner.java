package ggc.core.partner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


import ggc.core.notifications.Notification;
import ggc.core.StockEntity;
import ggc.core.exception.NotEnoughResourcesException;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.exception.UnknownObjectKeyException.ObjectType;
import ggc.core.partner.rank.Normal;
import ggc.core.partner.rank.Rank;
import ggc.core.product.Batch;
import ggc.core.product.comparators.OrderByLowerPriceFirst;
import ggc.core.transaction.Transaction;
import ggc.core.notifications.Subscriber;
import ggc.app.exception.UnknownProductKeyException;

/** Implements Partner class */
public class Partner extends StockEntity implements Subscriber, Comparable<Partner> {

    /** Partner Id */
    private String _id;

    /** Partner name */
    private String _name;

    /** Partner address */
    private String _address;

    /** Partner points */
    private int _points;

    /** Partner Total Sales */
    private double _totalSales;

    /** Partner Total Purchases */
    private double _totalPurchases;

    /** Partner Total Paid Sales */
    private double _paidSales;

    private Set<Transaction> _transactions;

    /** Partner Rank */
    private Rank _rank;

    /** Notifications List */
    private List<Notification> _notifications;

    /**
     * Creates a new Partner
     * @param id Partner id
     * @param name Partner name
     * @param address Partner address
     */
    public Partner(String id, String name, String address) {
        super();
        _id = id;
        _name = name;
        _address = address;
        _transactions = new HashSet<>();
        _rank = new Normal();
        _notifications = new ArrayList<>();
    }

    /**
     * Get partner id
     * @return id
     */
    public String getId() {
        return _id;
    }

    /**
     * Get partner name
     * @return name
     */
    public String getName() {
        return _name;
    }

    /**
     * Get partner points
     * @return points
     */
    public int getPoints() {
        return _points;
    }
     
    /**
     * Get partner address
     * @return address
     */
    public String getAddress() {
        return _address;
    }

    /**
     * Get partner Total Purchases
     * @return total purchases
     */
    public double getTotalPurchases() {
        return _totalPurchases;
    }

    /**
     * Get partner Total Sales
     * @return total sales
     */
    public double getTotalSales() {
        return _totalSales;
    }

    /**
     * Get partner Paid Sales
     * @return paid sales
     */
    public double getPaidSales() {
        return _paidSales;
    }

    /**
     * Get partner rank name
     * @return rank
     */
    public Rank getRank() {
        return _rank;
    }


    public List<Transaction> getTransactions() {
        return new ArrayList<>(_transactions);
    }

    public List<Notification> showNotifications() {
        List<Notification> tempNotifications = new ArrayList<>(_notifications);
        _notifications.clear();
        return tempNotifications;
    }

    /**
     * Check if the current rank is still valid (for the current points)
     * and updates partner rank according to his points
     */
    private void updateRank() {
        _rank.updateRank(this, _points);
    }

    /**
     * Sets the partner rank
     * @param newRank
     */
    public void setRank(Rank newRank) {
        _rank = newRank;
    }

    /**
     * Gives partner a certain amount of points 
     * calculated with the supplied price
     * @param amount
     */
    public void addPoints(double price) {
        _points += 10 * price;
        updateRank();
    }

    /**
     * Takes partner a certain amount of points
     * calculated with the supplied time period and the current rank policies
     * @param period
     */
    public void takePoints(int period) {
        _points *= _rank.getPointsPenalty(period);
        updateRank();
    }

    public void increasePurchases(double price) {
        _totalPurchases += price;
    }

    public void increaseSales(double price) {
        _totalSales += price;
    }

    public void increasePaidSales(double price) {
        _paidSales += price;
    }

    public void addTransaction(Transaction transaction) {
        _transactions.add(transaction);
    }

    /**
     * Sells a specific amount of partner product
     * @param productId
     * @param amount
     * @return total transaction price
     * @throws NotEnoughResourcesException
     * @throws UnknownObjectKeyException
     */
    public double sellBatch(String productId, int amount, double unitPrice) throws NotEnoughResourcesException, UnknownObjectKeyException {
        if (hasAvailableStock(productId, amount)) {
            List<Batch> tempBatches = getBatchesByProduct(productId);

            double totalPrice = amount * unitPrice;
            int remain = amount;

            Collections.sort(tempBatches, new OrderByLowerPriceFirst());
            Iterator<Batch> batchIterator = tempBatches.iterator();

            while (batchIterator.hasNext()) {
                Batch batch = batchIterator.next();
                remain = takeBatchAmount(batch, remain);
                if (remain == 0) {
                    break;
                }
            }

            _totalSales += totalPrice;
            return totalPrice;

        } else {
            throw new NotEnoughResourcesException(productId, amount, countStock(productId));
        }
    }

    /**
     * Displays Partner Information
     * @return String (id|nome|endereço|estatuto|pontos|valor-compras|valor-vendas-efectuadas|valor-vendas-pagas)
     */
    public String toString() {
        return getId() + "|" + getName() + "|" + getAddress() + "|" + getRank().getRankName() + "|" + 
        getPoints() + "|" + Math.round(getTotalPurchases()) + "|" + Math.round(getTotalSales()) + "|" + Math.round(getPaidSales());
    }

    /* Override equals in order to compare Partners by id */
    @Override
    public boolean equals(Object a) {
        if (a == this) {
            return true;
        }

        if (a == null) {
            return false;
        }

        return ((Partner)a).getId().equalsIgnoreCase(_id);
    }

    /* Override hashCode to compare Partner objects by their id */
    @Override
    public int hashCode() {
        return _id.toLowerCase().hashCode();
    }

    /* Implements Comparable interface method for sorting purposes */
    public int compareTo(Partner partner) {
        return _id.compareToIgnoreCase(partner.getId());
    }

    public void update(Notification n) {
        _notifications.add(n);
    }
    
}
