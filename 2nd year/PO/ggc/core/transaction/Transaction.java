package ggc.core.transaction;

import java.io.Serializable;

import ggc.core.partner.Partner;
import ggc.core.product.Product;

public abstract class Transaction implements Serializable {

    public enum TransactionType {
        ACQUISITION,
        SALE
    };

    private int _id;
    private int _quantity;
    private Product _product;
    private Partner _partner;
    private boolean _paid;
    private int _paymentDate;
    private TransactionType _type;
    

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    public Transaction(int id, Product product, int quantity, Partner partner, int paymentDate, TransactionType type) {
        _id = id;
        _product = product;
        _quantity = quantity;
        _partner = partner;
        _paymentDate = paymentDate;
        _type = type;
    }

    public int getId() {
        return _id;
    }

    public TransactionType getType() {
        return _type;
    }

    public boolean isPaid() {
        return _paid;
    } 

    public void pay() {
        _paid = true;
    }

    public Partner getPartner() {
        return _partner;
    }

    public Product getProduct() {
        return _product;
    }

    public int getQuantity() {
        return _quantity;
    }

    public int getPaymentDate() {
        return _paymentDate;
    }

    public void setPaymentDate(int value) {
        _paymentDate = value;
    }

    public abstract double getBasePrice();

    public double calculatePriceToPay() {
        return 0.0;
    }

    @Override
    public String toString() {
        return _id + "|" + _partner.getId() + "|" + _product.getId() +"|" + _quantity;
    }

    /* Override equals in order to compare Transactions by id */
    @Override
    public boolean equals(Object a) {

        if (a == this) {
            return true;
        }

        if (a == null) {
            return false;
        }

        return (((Transaction)a).getId() == _id);
    }

    /* Override hashCode to compare Transaction objects by their id */
    @Override
    public int hashCode() {
        return _id;
    }


}
