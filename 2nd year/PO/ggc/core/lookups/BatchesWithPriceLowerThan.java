package ggc.core.lookups;

import java.util.ArrayList;
import java.util.Collection;

import ggc.core.Warehouse;
import ggc.core.product.Batch;

public class BatchesWithPriceLowerThan implements LookupStrategy {
    private double _price;

    public BatchesWithPriceLowerThan(double price) {
        _price = price;
    }

    public Collection<Object> execute(Warehouse store) {
        Collection<Object> objects = new ArrayList<>();
        for (Batch batch : store.getAvailableBatches()) {
            if (batch.getUnitPrice() < _price) {
                objects.add(batch);
            }
        }
        return objects;
    }
}
