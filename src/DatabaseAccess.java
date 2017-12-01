import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;


public class DatabaseAccess {

	private static Connection conn;

    private static PreparedStatement beginTxnStmt;
    private static PreparedStatement commitTxnStmt;
    private static PreparedStatement abortTxnStmt;

	/** Opens a connection to the database using the given settings. */
	public static void open(){
		// Make sure the JDBC driver is loaded.
        try {
            String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            Class.forName(driverClassName).newInstance();

            // Open a connection to our database.
            conn = DriverManager.getConnection("jdbc:sqlserver://info340a-au17.ischool.uw.edu;database=P2_Group8;",
                    "ericpeng", "sql-1636149");
        }
        catch (Exception e){
            e.printStackTrace();
        }
	}

	/** Closes the connection to the database. */
	public static void close() {
	    try {
            conn.close();
            conn = null;
        }
        catch (Exception e){
	        e.printStackTrace();
        }
	}

    /** Performs additional preparation after the connection is opened. */
    public void prepare() throws SQLException {
        // NOTE: We must explicitly set the isolation level to SERIALIZABLE as it
        //       defaults to allowing non-repeatable reads.
        beginTxnStmt = conn.prepareStatement(
                "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;");
        commitTxnStmt = conn.prepareStatement("COMMIT TRANSACTION");
        abortTxnStmt = conn.prepareStatement("ROLLBACK TRANSACTION");
    }
	

	public static Order [] GetPendingOrders(){
		// TODO:  Query the database and retrieve the information.
		// resultset.findcolumn(string col)

		//try {
            //PreparedStatement pending = conn.prepareStatement("SELECT * FROM Orders o WHERE o.Status = 'Processing'");
            //ResultSet order = pending.executeQuery();
        //}
        //catch(SQLException e){

        //}
	    /*Order[] a = new Order[];
	    if (order.next()) {
	    	while(order.next()) {
	    	    a.
		    	a.(new Order(order.getInt("OrderID"), order.getDate("OrderDate"),
		    			order.getString(order.toString()), order.getCustomer("Customer"),
		    			order.getDouble("TotalCost"),order.getLineItem[](LineItems),
		    			order.getString("ShippingAddress"), order.getString("BillingAddress"),
		    			order.getString("BillingInfo")));
		    }
	    }
	    order.close();*/
		return new Order[]{};
	}
	
	public static Product[] GetProducts() throws Exception{
	    ResultSet result = null;

	    try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products");
            result = stmt.executeQuery();
            stmt.clearParameters();
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        List<Product> prod = new ArrayList<Product>();

        while(result.next() && result != null){
            prod.add(new Product(result.getInt("id"), result.getInt("QtyInStock"),
                    result.getString("Name"), result.getString("Description"),
                    result.getDouble("Price"), 0, null));
        }

        return prod.toArray(new Product[prod.size()]);
	}

	public static void main(String [] args) throws Exception{
	    DatabaseAccess.open();
	    DatabaseAccess.GetProducts();
	    DatabaseAccess.GetProductDetails(1);
	    DatabaseAccess.close();
		//DatabaseAccess access = new DatabaseAccess();
		//access.GetProducts();
        //access.open();
		//DatabaseAccess.GetProducts();
	}

	public static Order GetOrderDetails(int OrderID) {
		// TODO:  Query the database to get the flight information as well as all 
		// the reservations.
		
		// DUMMY DATA FOLLOWS

        Customer cust = new Customer(1, "Kevin", "kevin@pathology.washington.edu");
        Order o = new Order(1, new Date(), "ORDERED", cust, 520.20,
                null, "1959 NE Pacific St, Seattle, WA 98195",
                "1959 NE Pacific St, Seattle, WA 98195",
                "PO 12345");

		Product p = new Product(1, 2, "Computer Mouse",
                "A great product", 0, 0, null);
		LineItem li = new LineItem(p, o, 2, 540);

		o.setLineItems(new LineItem[]{li});
		return o;
	}

    /**
     * Gets the product details by running a SQL query on the database
     *
     * @param productID the product id to find in the db
     * @return a Product object from the query or if the product id does
     *         not exist then null is returned
     */
	public static Product GetProductDetails (int productID) {
        Product p = null;

        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT pc.ProductId, p.Name, p.Description, p.Price, " +
                    "p.QtyInStock, pc.Comment FROM ProductComments pc, Products p WHERE \n" +
                    "p.id = pc.id AND pc.ProductId = ?");
            stmt.setInt(1, productID);
            ResultSet result = stmt.executeQuery();
            ResultSetMetaData data = result.getMetaData();

            while (result.next()) {
                p = new Product(productID, result.getInt("QtyInStock"),
                        result.getString("Name"), result.getString("Description"),
                        result.getDouble("Price"), 0, null);
            }

            return p;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
		return null;
	}
	
	public static Customer [] GetCustomers () {
		// TODO:  Query the database to retrieve a list of customers.
		
		// DUMMY VALUES FOLLOW
		Customer c1 = new Customer(1, "Kevin Fleming", "k@u");

		Customer c2 = new Customer(2, "Niki Cassaro", "k@u");

		Customer c3 = new Customer(3, "Ava Fleming", "k@u");
		
		return new Customer [] { c1, c2, c3 };
	}
	
	public static Order [] GetCustomerOrders (Customer c) {

	    // TODO: Get the Orders

        // DUMMY VALUES FOLLOW
	    Customer cust = new Customer(1, "Kevin", "kevin@pathology.washington.edu");
	    Order o = new Order(1, new Date(), "ORDERED", cust, 520.20,
                null, "1959 NE Pacific St, Seattle, WA 98195",
                "1959 NE Pacific St, Seattle, WA 98195",
                "PO 12345");

		return new Order [] { o };
	}
	
	public static Product [] SearchProductReviews(String query) {

		// DUMMY VALUES
		Product p = new Product(1, 10, "Monitor, 19 in", "A great monitor",
                196, 0.7, null);
		return new Product [] {p} ;
	}
	                    
	public static void MakeOrder(Customer c, LineItem [] LineItems) {
		// TODO: Insert data into your database.
		// Show an error message if you can not make the reservation.
		
		JOptionPane.showMessageDialog(null, "Create order for " + c.getName() + " for " + Integer.toString(LineItems.length) + " items.");
	}

    /** Puts the connection into a new transaction. */
    public static void beginTransaction() throws SQLException {
        conn.setAutoCommit(false);  // do not commit until explicitly requested
        beginTxnStmt.executeUpdate();
    }

    /** Commits the current transaction. */
    public static void commitTransaction() throws SQLException {
        commitTxnStmt.executeUpdate();
        conn.setAutoCommit(true);  // go back to one transaction per statement
    }

    /** Aborts the current transaction. */
    public static void rollbackTransaction() throws SQLException {
        abortTxnStmt.executeUpdate();
        conn.setAutoCommit(true);  // go back to one transaction per statement
    }
}
