
public class LineItem {
	private Product product;
	private Order order;
	private int quantity;
	private double pricePaid;

    /**
     * Empty constructor which creates a line item with
     * default values of 0 and null
     */
	public LineItem(){
	    this.product = null;
        this.order = null;
        this.quantity = 0;
        this.pricePaid = 0.0;
    }

    /**
     * Constructor which takes in parameters for all the fields
     *
     * @param product
     * @param order
     * @param quantity
     * @param pricePaid
     */
	public LineItem(Product product, Order order, int quantity, double pricePaid) {
		this.product = product;
		this.order = order;
		this.quantity = quantity;
		this.pricePaid = pricePaid;
	}

    /**
     * Gets the Product for the line item object
     *
     * @return the Product for the line item
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the Order for this line item object
     *
     * @return the Order for the line item
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Gets the quantity for the line item
     *
     * @return an int representing the quantity for the line item
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the price paid for this line item
     *
     * @return double of the price paid
     */
    public double getPricePaid() {
        return pricePaid;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPricePaid(double pricePaid) {
        this.pricePaid = pricePaid;
    }

    /**
     * Standard toString method which returns a string in the format
     * of product name and quantity
     *
     * @return a string representation of LineItem
     */
    @Override
	public String toString () {
		return product.getName() + " (Qty " + Integer.toString(quantity) + ")";
	}
}
