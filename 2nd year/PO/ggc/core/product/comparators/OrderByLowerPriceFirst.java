package ggc.core.product.comparators;

import java.util.Comparator;

import ggc.core.product.Batch;

public class OrderByLowerPriceFirst implements Comparator<Batch> {
    
    @Override
    public int compare(Batch o1, Batch o2) {
        return Double.compare(o1.getUnitPrice(), o2.getUnitPrice());
    }
}
