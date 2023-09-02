package ggc.core.product;

import java.io.Serializable;
import ggc.core.partner.Partner;
import ggc.core.product.Product;

/** Implements Batch class */
public class Batch implements Serializable, Comparable<Batch> {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    /** Batch supplier */
    private Partner _supplier;

    /** Batch associated product */
    private Product _product;

    /** Total amount of product units */
    private int _amount;

    /** Price per unit */
    private double _unitPrice;

    /**
     * Creates a new Product Batch
     * @param supplier Partner associated to the batch
     * @param product Product associated to the batch
     * @param amount amount of product units
     * @param unitPrice Price per unit
     */
    public Batch(Partner supplier, Product product, int amount, double unitPrice) {
        _supplier = supplier;
        _product = product;
        _amount = amount;
        _unitPrice = unitPrice;
    }

    public Batch makeCopy() {
        return new Batch(_supplier, _product, _amount, _unitPrice);
    }

    /**
     * Get amount of product units
     * @return amount
     */
    public int getAmount() {
        return _amount;
    }

    public void addAmount(int amount) {
        _amount += amount;
    }

    public void takeAmount(int amount) {
        _amount -= amount;
    }

    /**
     * Get price per unit
     * @return price
     */
    public double getUnitPrice() {
        return _unitPrice;
    }

    /**
     * Get batch associated Product id
     * @return product id
     */
    public String getProductId() {
        return _product.getId();
    }

    /**
     * Get batch associated Partner (supplier) id 
     * @return supplier id
     */
    public String getSupplierId() {
        return _supplier.getId();
    }

    /**
     * Displays Batch Information
     * @return String (idProduto|idParceiro|preço|stock-actual)
     */
    @Override
    public String toString() {
        return _product.getId() + "|" + _supplier.getId() + "|" + Math.round(_unitPrice) + "|" + _amount;
    }

    /* Implements Comparable interface method for sorting purposes */
    public int compareTo(Batch batch) {
        if (getProductId().equalsIgnoreCase(batch.getProductId())) {
            // Product Id is the same, compare by supplier Id
            if (getSupplierId().equalsIgnoreCase(batch.getSupplierId())) {
                // supplier Id is the same, compare by Unit price
                if (Math.round(getUnitPrice()) == Math.round(batch.getUnitPrice())) {
                    // Unit price is the same, compare by amount
                    return getAmount() - batch.getAmount();
                } else {
                    return (int) (Math.round(getUnitPrice()) - Math.round(batch.getUnitPrice()));
                }
            } else {
                return getSupplierId().compareTo(batch.getSupplierId());
            }
        } else {
            return getProductId().compareTo(batch.getProductId());
        }
    }
}
