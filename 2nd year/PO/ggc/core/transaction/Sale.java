package ggc.core.transaction;

import ggc.core.Date;
import ggc.core.partner.Partner;
import ggc.core.product.Product;

public abstract class Sale extends Transaction {

    private double _basePrice;

    public Sale(int currentId, Product product, int quantity, Partner partner, double basePrice, int date) {
        super(currentId, product, quantity, partner, date, TransactionType.SALE);
        _basePrice = basePrice;
    }

    @Override
    public double getBasePrice() {
        return _basePrice;
    } 

    @Override
    public String toString() {
        return super.toString() + "|" + Math.round(_basePrice);
    }
}
