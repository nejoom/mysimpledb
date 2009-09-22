package ac.elements.concurrency;

/**
 * http://www.devx.com/Java/Article/41377/1954: OrderVO.
 */
public class OrderVO {

    private int orderId;

    private String deptCode;

    public OrderVO(int orderId, String deptCode) {
        this.deptCode = deptCode;
        this.orderId = orderId;
    }

    /**
     * Getter for the orderId.
     * 
     * @return The orderId
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of orderId.
     * 
     * @param orderId
     *            Sets the orderId to orderId
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * Getter for the deptCode.
     * 
     * @return The deptCode
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * Sets the value of deptCode.
     * 
     * @param deptCode
     *            Sets the deptCode to deptCode
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Override
    public String toString() {
        return "OrderVO: orderId=" + orderId + ", deptCode=" + deptCode;
    }

}
