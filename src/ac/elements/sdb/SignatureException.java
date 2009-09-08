package ac.elements.sdb;

/**
 * Exception for invalid signature cases.
 */
public class SignatureException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8062149785748598962L;

    /**
     * Instantiates a new SignatureException
     */
    public SignatureException() {
        super();
    }

    /**
     * Create that kind of exception.
     * 
     * @param msg
     *            The associated error message
     */
    public SignatureException(String msg) {
        super(msg);
    }
}