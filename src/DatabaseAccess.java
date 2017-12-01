import java.sql.*;
import java.util.Date;
import java.util.Properties;

import javax.swing.JOptionPane;


public class DatabaseAccess {

	private static Connection conn;

	/** Opens a connection to the database using the given settings. */
	public static void open() throws Exception {
		// Make sure the JDBC driver is loaded.
		String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		Class.forName(driverClassName).newInstance();

		// Open a connection to our database.
        conn = DriverManager.getConnection("jdbc:sqlserver://info340a-au17.ischool.uw.edu;database=P2_Group8;",
                "ericpeng", "sql-1636149");
	}

	/** Closes the connection to the database. */
	public static void close() throws SQLException {
		conn.close();
		conn = null;
	}
	

	public static Order [] GetPendingOrders() {
		// TODO:  Query the database and retrieve the information.
		// resultset.findcolumn(string col)
		PreparedStatement pending = conn.preparedStatement("SELECT * FROM Orders o WHERE o.Status = 'Processing'");
	    ResultSet order = pending.executeQuery();
	    Order[] a = new Order[];
	    if (order.next()) {
	    	while(order.next()) {
		    	a.push(new Order(order.getInt("OrderID"), order.getDate("OrderDate"),
		    			order.getString(order.toString()), order.getCustomer("Customer"),
		    			order.getDouble("TotalCost"),order.getLineItem[](LineItems),
		    			order.getString("ShippingAddress"), order.getString("BillingAddress"),
		    			order.getString("BillingInfo")));
		    }
	    }
	    order.close();
		return a;
	}
	
	public static Product[] GetProducts() throws Exception{
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products");

		ResultSet result = stmt.executeQuery();
		ResultSetMetaData data = result.getMetaData();
        while (result.next()){
            for(int k = 1; k < data.getColumnCount(); k++){
                if (k > 1) System.out.print(",  ");
                String columnValue = result.getString(k);
                System.out.print(columnValue + " " + data.getColumnName(k));
            }
            System.out.println();
        }
        return new Product [] {};
	}

	public static void main(String [] args) throws Exception{
	    DatabaseAccess.open();
	    DatabaseAccess.GetProducts();
	    DatabaseAccess.close();
		//DatabaseAccess access = new DatabaseAccess();
		//access.GetProducts();
        //access.open();
		//DatabaseAccess.GetProducts();
	}

	public static Order GetOrderDetails(int OrderID) {
		// TODO:  Query the database to get the flight information as well as all 
		// the reservations.
		
		// get the order details and store them in a Order object
		Order o = new Order();
		String query = "SELECT * FROM Order o\n"  +
                   "JOIN Customer c ON o.CustomerId = c.id\n" + "WHERE o.id = ?";
		PreparedStatement searchOrder = conn.prepareStatement(query);
	    searchOrder.setInt(1, OrderID);
	    ResultSet rs = searchOrder.executeQuery();
	    while (rs.next()) {
	    	o.OrderID = OrderID;
			o.Customer = new Customer();
			o.Customer.CustomerID = rs.getInt("CustomerId");
			o.Customer.Name = rs.getString("Name");
			o.Customer.Email = rs.getString("Email")
			// o.OrderDate = new Date();
			o.Status = rs.getString("Status");
			// o.TotalCost = 520.20;
			o.BillingAddress = rs.getString("BillingAddress");
			o.BillingInfo	 = rs.getString("BillingInfo");
			o.ShippingAddress= rs.getString("ShippingAddress");
	    }
	    rs.close();
		
		// get all the LineItems and store them in a LineItem[]
	    ArrayList<LineItem> l = new ArrayList<LineItem>();
		String query = "SELECT * FROM LineItem\n"  +
                    "WHERE OrderId = ?";
		PreparedStatement searchOrder = conn.prepareStatement(query);
	    searchOrder.setInt(1, OrderID);
	    ResultSet rs = searchOrder.executeQuery();
	    wwhile (rs.next()) {
	        results.add(new Flight(rs.getInt("fid"), rs.getInt("year"), 
	                rs.getInt("month_id"), rs.getInt("day_of_month"),
	                rs.getString("name"), rs.getString("flight_num"),
	                    rs.getString("origin_city"),
	                    rs.getString("dest_city"),
	                    (int)rs.getFloat("actual_time"))
	              );
	            }
	          rs.close();

	    rs.close();
		LineItem li = new LineItem();
		li.Order = o;
		li.PricePaid = 540.00;
		li.Product = new Product();
		li.Product.Description = "A great product.";
		li.Product.Name = "Computer Mouse";
		li.Quantity = 2;
		
		// link the LineItem[] with the Order
		o.LineItems = new LineItem[] {li};
		// return the Order
		return o;
	}

	public static Product GetProductDetails (int ProductID) {
		Product p = new Product();
		p.Description = "A great monitor";
		p.Name = "Monitor, 19 in";
		p.InStock = 10;
		p.Price = 196;
		p.ProductID = ProductID;
		p.UserComments = new String [] { "I bought this product last year and it's still the best monitor I've had.", "After 6 months the color started going out, not sure if it was just mine or all of them" };
		
		return p;
		
	}
	
	public static Customer [] GetCustomers () {
		// TODO:  Query the database to retrieve a list of customers.
		
		// DUMMY VALUES FOLLOW
		Customer c1 = new Customer();
		c1.CustomerID = 1;
		c1.Email = "k@u";
		c1.Name = "Kevin Fleming";
		
		Customer c2 = new Customer();
		c2.CustomerID = 2;
		c2.Email = "k@u";
		c2.Name = "Niki Cassaro";

		Customer c3 = new Customer();
		c3.CustomerID = 3;
		c3.Email = "k@u";
		c3.Name = "Ava Fleming";
		
		return new Customer [] { c1, c2, c3 };
	}
	
	public static Order [] GetCustomerOrders (Customer c) {
		Order o = new Order();
		o.OrderID = 1;
		o.Customer = new Customer();
		o.Customer.CustomerID = 1;
		o.Customer.Name = "Kevin";
		o.Customer.Email = "kevin@pathology.washington.edu";
		o.OrderDate = new Date();
		o.Status = "ORDERED";
		o.TotalCost = 520.20;
		o.BillingAddress = "1959 NE Pacific St, Seattle, WA 98195";
		o.BillingInfo	 = "PO 12345";
		o.ShippingAddress= "1959 NE Pacific St, Seattle, WA 98195";

		return new Order [] { o };
	}
	
	public static Product [] SearchProductReviews(String query) {
		// DUMMY VALUES
		Product p = new Product();
		p.Description = "A great monitor";
		p.Name = "Monitor, 19 in";
		p.InStock = 10;
		p.Price = 196;
		p.ProductID = 1;
		p.Relavance = 0.7;
		return new Product [] { p} ;
	}
	                    
	public static void MakeOrder(Customer c, LineItem [] LineItems) {
		// TODO: Insert data into your database.
		// Show an error message if you can not make the reservation.
		
		JOptionPane.showMessageDialog(null, "Create order for " + c.Name + " for " + Integer.toString(LineItems.length) + " items.");
	}
}
