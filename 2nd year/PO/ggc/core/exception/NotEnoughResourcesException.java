package ggc.core.exception;

public class NotEnoughResourcesException extends Exception {
    
    /** The requested objectId */
    private String _objectId;

    /** The requested amount */
    private int _requestedAmount;

    /** The available amount */
    private int _availableAmount;

    /** Class serial number. */
    private static final long serialVersionUID = 201409301048L;

    public NotEnoughResourcesException(String objectId, int requestedAmount, int availableAmount) {
        super("Quantidade insuficiente: " + objectId + "| Pedido: " + requestedAmount + " Disponivel: " + availableAmount);
        _objectId = objectId;
        _requestedAmount = requestedAmount;
        _availableAmount = availableAmount;
    }

    /**
     * @return the requested object key
     */
    public String getObjectKey() {
        return _objectId;
    }
    
    public int getRequestedAmount() {
        return _requestedAmount;
    }
    
    public int getAvailableAmount() {
        return _availableAmount;
    }
}
