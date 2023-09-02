package ggc.core.product;

import java.util.ArrayList;
import java.util.List;

import ggc.core.product.Product;
import ggc.core.product.RecipeComponent;

/** Implements Derivative Product class */
public class DerivativeProduct extends Product {

    /** Derivative Product Recipe */
    private List<RecipeComponent> _recipe;

    /** Alpha Product value */
    private double _alpha;

    /**
     * Creates a Derivative Product
     * @param id Product id
     * @param recipe recipe
     * @param alpha Alpha factor
     */
    public DerivativeProduct(String id, List<RecipeComponent> recipe, double alpha) {
        super(id);
        _recipe = new ArrayList<>(recipe);
        _alpha = alpha;
    }

    /**
     * Get Product recipe
     * @return RecipeComponent set
     */
    public List<RecipeComponent> getRecipe() {
        return _recipe;
    }

    @Override
    public double getAlpha() {
        return _alpha;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    /**
     * Displays Derivative Product Information
     * @return String (idProduto|preço-máximo|stock-actual-total|agravamento|componente-1:quantidade-1#...#componente-n:quantidade-n)
     */
    @Override
    public String toString() {
        String temp = super.toString() + "|";

        for(RecipeComponent component : _recipe) {
            temp += component.getProduct().getId() + ":" + component.getAmount() + "#";
        }

        // Remove last "#" and return
        return temp.subSequence(0, temp.length() - 1).toString();
    }
}
