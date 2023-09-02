package ggc.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.exception.UnknownObjectKeyException.ObjectType;
import ggc.core.partner.Partner;
import ggc.core.product.Batch;
import ggc.core.product.Product;

public abstract class StockEntity implements Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    private List<Batch> _batches;

    public StockEntity() {
        _batches = new ArrayList<>();
    }

    /**
     * Adds a new product batch
     * @param batch
     */
    public void addBatch(Batch batch) {
        _batches.add(batch);
    }

    /**
     * Count branches with the supplied product Id
     * @param productId
     * @return
     */
    public int countStock(String productId) {
        int total = 0;
        for (Batch batch : _batches) {
            if (batch.getProductId().equals(productId)) {
                total += batch.getAmount();
            }
        }
        return total;
    }

    /**
     * Count all branches
     * @param productId
     * @return
     */
    public int countStock() {
        int total = 0;
        for (Batch batch : _batches) {
            total += batch.getAmount();
        }
        return total;
    }

    /**
     * Check if stock is available by productId
     * @param productId
     * @param amount
     * @return
     */
    public boolean hasAvailableStock(String productId, int amount) {
        return (countStock(productId) >= amount);
    }

    /**
     * Check if there's enough stock to satistfy the requested amount
     * @param amount
     * @return
     */
    public boolean hasAvailableStock(int amount) {
        return (countStock() >= amount);
    }

    public int takeBatchAmount(Batch batch, int amount) {
        int remain = amount - batch.getAmount();
        if (remain >= 0) {
            _batches.remove(batch);
        } else {
            remain = 0;
            batch.takeAmount(amount);
        }
        return remain;
    }

    public List<Batch> getBatchesByProduct(String productId) {
        List<Batch> tempBatches;
        tempBatches = new ArrayList<>();
        for (Batch batch : _batches) {
            if (batch.getProductId().equals(productId)) {
                tempBatches.add(batch);
            }
        }
        return tempBatches;
    }

    public List<Batch> getBatchesByPartner(String partnerId) {
        List<Batch> tempBatches;
        tempBatches = new ArrayList<>();
        for (Batch batch : _batches) {
            if (batch.getSupplierId().equals(partnerId)) {
                tempBatches.add(batch);
            }
        }
        return tempBatches;
    }

    public List<Batch> getBatches(Comparator<Batch> comparator) {
        List<Batch> batches = new ArrayList<>(_batches);
        Collections.sort(batches, comparator);
        return batches;
    }

    /**
     * Get product batches
     * @return list of the product batches
     */
    public List<Batch> getBatches() {
        List<Batch> batches = new ArrayList<>(_batches);
        Collections.sort(batches);
        return batches;
    }

}
