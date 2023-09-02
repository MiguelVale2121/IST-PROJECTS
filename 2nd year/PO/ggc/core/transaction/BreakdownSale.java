package ggc.core.transaction;

import ggc.core.Date;
import ggc.core.partner.Partner;
import ggc.core.partner.rank.Rank;
import ggc.core.product.Product;
import ggc.core.transaction.Sale;

public class BreakdownSale extends Sale {

    private String _componentsString;

    public BreakdownSale(int currentId, Product product, int quantity, Partner partner, double basePrice, String componentsString) {
        super(currentId, product, quantity, partner, basePrice, 0);
        _componentsString = componentsString;
    }

    @Override
    public double calculatePriceToPay() {
        double basePrice = getBasePrice();
        if (basePrice < 0) {
            return 0.0;
        } else {
            return basePrice;
        }
    }

    @Override
    public void pay() {
        if (!isPaid()) {
            Partner partner = getPartner();
            setPaymentDate(Date.now().getValue());
            partner.addPoints(calculatePriceToPay());
            super.pay();
        }
    }

    @Override
    public String toString() {
        return "DESAGREGAÇÃO|" + super.toString() + "|" + Math.round(calculatePriceToPay()) + "|" + Math.round(getPaymentDate()) + "|" + _componentsString;
    }
}
