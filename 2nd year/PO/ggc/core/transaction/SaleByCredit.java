package ggc.core.transaction;

import ggc.core.Date;
import ggc.core.partner.Partner;
import ggc.core.partner.rank.Rank;
import ggc.core.product.Product;

public class SaleByCredit extends Sale {

    private int _deadline;
    private int _period;

    public SaleByCredit(int currentId, Product product, int quantity, Partner partner, double basePrice, int deadline) {
        super(currentId, product, quantity, partner, basePrice, 0);
        _deadline = deadline;
        if (product.isSimple()) {
            _period = 5;
        } else {
            _period = 3;
        }
        partner.increaseSales(getBasePrice());
    }

    @Override
    public double calculatePriceToPay() {
        Rank partnerRank = getPartner().getRank();
        int interval;
        if (!isPaid()) {
            interval = _deadline - Date.now().getValue();
        } else {
            interval = _deadline - getPaymentDate();
        }
        return getBasePrice() * partnerRank.getDiscount(interval, _period) * partnerRank.getPenalty(interval, _period);
    }

    @Override
    public void pay() {
        if (!isPaid()) {
            Partner partner = getPartner();
            setPaymentDate(Date.now().getValue());
            double pricePay = calculatePriceToPay();
            partner.increasePaidSales(pricePay);
            if (_deadline - getPaymentDate() >= 0) {
                partner.addPoints(pricePay);
            } else {
                partner.takePoints(_deadline - getPaymentDate());
            }
            super.pay();
        }
    }

    @Override
    public String toString() {
        String displayString = "VENDA|" + super.toString() + "|" + Math.round(calculatePriceToPay()) + "|" + _deadline;
        if (super.isPaid()) {
            displayString += "|" + super.getPaymentDate();
        }
        return displayString;
    }
}
