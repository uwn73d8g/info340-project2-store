
public class Product {
	/** stores product info */
	private int productID, inStock;

	/** stores product name and description */
	private String name, description;

	/** price stores the produce prixe and relavence is used for fulltext searching */
	private double price, relevance;

	/** string array that stores all user comments */
	private String[] userComments;

    /**
     * Creates a new product object
     *
     * @param productID
     * @param inStock
     * @param name
     * @param description
     * @param price
     * @param relavance
     * @param userComments
     */
	public Product(int productID, int inStock, String name, String description,
				   double price, double relavance, String[] userComments) {
		this.productID = productID;
		this.inStock = inStock;
		this.name = name;
		this.description = description;
		this.price = price;
		this.relevance = relavance;
		this.userComments = userComments;
	}

    public int getProductID() {
        return productID;
    }

    public int getInStock() {
        return inStock;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public double getRelevance() {
        return relevance;
    }

    public String[] getUserComments() {
        return userComments;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public void setUserComments(String[] userComments) {
        this.userComments = userComments;
    }

    @Override
    public String toString() {
		return this.name;
	}
}
