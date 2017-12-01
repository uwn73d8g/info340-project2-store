import java.util.Date;

public class Order {

	private int orderID;
    private double totalCost;
	private Date orderDate;
	private Customer customer;
	private LineItem[] lineItems;
	private String shippingAddress, billingAddress, billingInfo, status;

	public Order(int orderID, Date orderDate, String status, Customer customer, double totalCost,
				 LineItem[] lineItems, String shippingAddress, String billingAddress, String billingInfo) {
		this.orderID = orderID;
		this.orderDate = orderDate;
		this.status = status;
		this.customer = customer;
		this.totalCost = totalCost;
		this.lineItems = lineItems;
		this.shippingAddress = shippingAddress;
		this.billingAddress = billingAddress;
		this.billingInfo = billingInfo;
	}

    public int getOrderID() {
        return orderID;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LineItem[] getLineItems() {
        return lineItems;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public String getBillingInfo() {
        return billingInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setLineItems(LineItem[] lineItems) {
        this.lineItems = lineItems;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public void setBillingInfo(String billingInfo) {
        this.billingInfo = billingInfo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
	public String toString() {
		return this.status;
	}
}
