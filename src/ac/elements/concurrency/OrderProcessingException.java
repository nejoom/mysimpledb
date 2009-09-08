package ac.elements.concurrency;

/**
 * OrderProcessingException.
 */
public class OrderProcessingException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * OrderProcessingException Constructor.
     */
    public OrderProcessingException() {
    }

    /**
     * OrderProcessingException Constructor.
     */
    public OrderProcessingException(String message) {
        super(message);
    }

    /**
     * OrderProcessingException Constructor.
     */
    public OrderProcessingException(Throwable cause) {
        super(cause);
    }

    /**
     * OrderProcessingException Constructor.
     */
    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
