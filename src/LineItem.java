/**
 * This class represents a line item
 */
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
     * @param product product object
     * @param order order object
     * @param quantity quantity of line item
     * @param pricePaid price paid for line item
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

    /**
     * Sets the product for the line item
     *
     * @param product the product for this line item
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Sets the order for this line item
     *
     * @param order the order for the line item
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Sets the quantity for this line item
     *
     * @param quantity the quantity for the line item
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the price paid for this line item
     *
     * @param pricePaid the price for the line item
     */
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
