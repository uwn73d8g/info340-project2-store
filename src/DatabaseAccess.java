import com.sun.org.apache.regexp.internal.RE;

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
	

	public static Order [] getPendingOrders(){
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

    /**
     * Gets all of the products currently being stored in the database
     * by running a SQL query
     *
     * @return an array of all of the products in the database and if
     *         there are no products then the array will be empty
     */
	public static Product[] getProducts(){
	    ResultSet result = null;

        List<Product> prod = new ArrayList<Product>();

	    try {
	        DatabaseAccess.open();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products");
            result = stmt.executeQuery();
            stmt.clearParameters();

            while(result.next() && result != null){
                prod.add(new Product(result.getInt("id"), result.getInt("QtyInStock"),
                        result.getString("Name"), result.getString("Description"),
                        result.getDouble("Price"), 0, null));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            DatabaseAccess.close();
        }

        return prod.toArray(new Product[prod.size()]);
	}

	public static Order getOrderDetails(int OrderID) {
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
	public static Product getProductDetails (int productID) {
        Product p = null;

        try{
            DatabaseAccess.open();
            PreparedStatement stmt = conn.prepareStatement("SELECT pc.ProductId, p.Name, p.Description, p.Price, " +
                    "p.QtyInStock, pc.Comment FROM ProductComments pc, Products p WHERE " +
                    "p.id = pc.id AND pc.ProductId = ?");
            stmt.setInt(1, productID);
            ResultSet result = stmt.executeQuery();
            stmt.clearParameters();

            while (result.next()) {
                p = new Product(productID, result.getInt("QtyInStock"),
                        result.getString("Name"), result.getString("Description"),
                        result.getDouble("Price"), 0,
                        (String[])result.getArray("Comment").getArray());
            }

            return p;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            DatabaseAccess.close();
        }
        return null;
	}

    /**
     * Gets all of the customers that are in the database
     * by running a SQL query
     *
     * @return an array of all the customers currently in the
     *         database and if there are no customers then
     *         the array will be empty
     */
	public static Customer[] getCustomers() {
	    ResultSet result = null;
	    List<Customer> custList = new ArrayList<Customer>();
		try{
		    DatabaseAccess.open();
		    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Customers");
		    result = stmt.executeQuery();
		    stmt.clearParameters();

            while (result.next()) {
                custList.add(new Customer(result.getInt("id"), result.getString("Name"),
                        result.getString("Email")));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            DatabaseAccess.close();
        }

        return custList.toArray(new Customer[custList.size()]);

	}

    /**
     * Gets all of the orders placed by the specified customer
     * by running a SQL query against the database
     *
     * @param c the customer whose orders to search for
     * @return an array of all the orders of a customer and if
     *         the customer does not have any orders then the
     *         array will be empty
     */
	public static Order[] getCustomerOrders (Customer c) {
	    ResultSet result = null;
	    int customerId = c.getCustomerID();
	    List<Order> orders = new ArrayList<Order>();

	    try{
	        DatabaseAccess.open();
	        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Orders o " +
                    "WHERE o.CustomerId = ?");
	        stmt.setInt(1, customerId);
	        result = stmt.executeQuery();
	        stmt.clearParameters();

	        // TODO: Need to updated date, total cost, lineitems after other methods created
	        while (result.next()){
	            orders.add(new Order(result.getInt("id"), new Date(), result.getString("Status"),
                        c, 0, null, result.getString("ShippingAddress"),
                        result.getString("BillingAddress"), result.getString("BillingInfo")));
            }
        }
        catch (SQLException e){
	        e.printStackTrace();
        }
        finally {
            DatabaseAccess.close();
        }

        return orders.toArray(new Order[orders.size()]);
	}

    /**
     * Gets all of the matching products given a user supplied
     * search query using SQL on the db
     *
     * @param query the user entered search term
     * @return an array of matching products for the query
     *         or if there are none then the array will be empty
     */
	public static Product[] searchProductReviews(String query) {
        ResultSet result = null;
        List<Product> products = new ArrayList<Product>();

	    try{
	        DatabaseAccess.open();
	        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ProductComments pc, Products p " +
                    "WHERE pc.Comment LIKE '%'?'%' AND p.id = pc.ProductId");
	        stmt.setString(1, query);
	        result = stmt.executeQuery();
	        stmt.clearParameters();

	        while (result.next()){
	            products.add(new Product(result.getInt("ProductId"), result.getInt("QtyInStock"),
                        result.getString("Name"), result.getString("Description"),
                        result.getDouble("Price"), 0,
                        (String[])result.getArray("Comment").getArray()));
            }
        }
        catch (SQLException e){
	        e.printStackTrace();
        }
        finally {
            DatabaseAccess.close();
        }
        return products.toArray(new Product[products.size()]);
	}
	                    
	public static void makeOrder(Customer c, LineItem [] lineItems) {
		// TODO: Insert data into your database.
		// Show an error message if you can not make the reservation.
		
		JOptionPane.showMessageDialog(null, "Create order for " + c.getName() + " for " + Integer.toString(lineItems.length) + " items.");
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
