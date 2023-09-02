package ggc.core.lookups;

import java.util.Collection;

import ggc.core.Warehouse;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.partner.Partner;
import ggc.core.product.Product;
import ggc.core.transaction.Transaction;

public interface LookupStrategy {
    public Collection<Object> execute(Warehouse store) throws UnknownObjectKeyException;
}
