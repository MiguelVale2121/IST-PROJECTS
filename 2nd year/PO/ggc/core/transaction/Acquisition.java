package ggc.core.transaction;

import ggc.core.Date;
import ggc.core.partner.Partner;
import ggc.core.product.Product;
import ggc.core.transaction.Transaction;

public class Acquisition extends Transaction {

    private double _paidValue;

    public Acquisition(int currentId, Product product, int quantity, Partner partner, int paymentDate, double paidValue) {
        super(currentId, product, quantity, partner, paymentDate, TransactionType.ACQUISITION);
        _paidValue = paidValue;
    }

    @Override
    public double getBasePrice() {
        return _paidValue;
    } 

    @Override
    public String toString() {
        return "COMPRA|" + super.toString() + "|" + Math.round(_paidValue) + "|" + super.getPaymentDate();
    }
}
