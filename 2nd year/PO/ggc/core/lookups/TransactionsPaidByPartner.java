package ggc.core.lookups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ggc.core.Warehouse;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.product.Batch;
import ggc.core.product.Product;
import ggc.core.transaction.Transaction;
import ggc.core.transaction.Transaction.TransactionType;

public class TransactionsPaidByPartner implements LookupStrategy {
    private String _partnerId;

    public TransactionsPaidByPartner(String partnerId) {
        _partnerId = partnerId;
    }

    public Collection<Object> execute(Warehouse store) throws UnknownObjectKeyException {
        Collection<Object> objects = new ArrayList<>();
        for (Transaction transaction : store.getPartner(_partnerId).getTransactions()) {
            if (transaction.getType() == TransactionType.SALE && transaction.isPaid()) {
                objects.add(transaction);
            }
        }
        return objects;
    }
}
