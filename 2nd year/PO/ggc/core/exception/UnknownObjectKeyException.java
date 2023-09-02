package ggc.core.exception;

/**
 * Exception for unknown object keys
 */
public class UnknownObjectKeyException extends Exception {

    public enum ObjectType {
        PARTNER,
        PRODUCT,
        TRANSACTION
    }    

    /** The requested objectId */
    private String _objectId;

    private ObjectType _type;

    /** Class serial number. */
    private static final long serialVersionUID = 201409301048L;
    

    /**
     * @param objectId
     */
    public UnknownObjectKeyException(String objectId, ObjectType type) {
        super("Objeto nao encontrado: " + objectId);
        _objectId = objectId;
        _type = type;
    }

    public ObjectType getType() {
        return _type;
    }

    /**
     * @return the requested object key
     */
    public String getObjectKey() {
        return _objectId;
    }
    
}
