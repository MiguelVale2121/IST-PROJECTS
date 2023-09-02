package ggc.core.product;

import ggc.core.product.Product;

/** Implements Recipe Component class */
public class RecipeComponent implements Comparable<RecipeComponent> {

    /** Product associated to the recipe component */
    private Product _product;

    /** Product amount for the recipe */
    private int _amount;

    /**
     * Creates a Recipe Component
     * @param product product associated
     * @param amount product amount for the recipe
     */
    public RecipeComponent(Product product, int amount) {
        _product = product;
        _amount = amount;
    }

    /**
     * Get associated product
     * @return Product
     */
    public Product getProduct() {
        return _product;
    }

    /**
     * Get product amount for the recipe
     * @return int amount
     */
    public int getAmount() {
        return _amount;
    }

    public int compareTo(RecipeComponent a) {
        return _product.getId().compareToIgnoreCase(a.getProduct().getId());
    }

}
