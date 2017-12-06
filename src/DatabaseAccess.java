import java.sql.*;
import java.util.*;
import java.util.Date;
import javax.swing.JOptionPane;


public class DatabaseAccess {

    private static Connection conn;

    private static PreparedStatement beginTxnStmt;
    private static PreparedStatement commitTxnStmt;
    private static PreparedStatement abortTxnStmt;

    /**
     * Opens a connection to the database using the given settings.
     */
    public static void open() {
        // Make sure the JDBC driver is loaded.
        try {
            String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            Class.forName(driverClassName).newInstance();

            // Open a connection to our database.
            conn = DriverManager.getConnection("jdbc:sqlserver://info340a-au17.ischool.uw.edu;database=P2_Group8;",
                    "ericpeng", "sql-1636149");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the connection to the database.
     */
    public static void close() {
        try {
            conn.close();
            //conn = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Order[] getPendingOrders() {

        ResultSet result = null;
        List<Order> orders = new ArrayList<Order>();

        try {
            DatabaseAccess.open();
            DatabaseAccess.beginTransaction();

            PreparedStatement lineItem = conn.prepareStatement("SELECT p.Name, p.Description," +
                    " p.QtyInStock, p.Price, " +
                    "p.Description, l.OrderId, l.PricePaid, l.ProductId,\n" +
                    "l.Quantity, pc.Comment, o.BillingAddress, o.CustomerId,\n" +
                    "o.BillingInfo, o.id, o.ShippingAddress,\n" +
                    "o.Status, o.ShippingAddress, c.Email, c.Name as 'CName'\n" +
                    "FROM LineItems l JOIN Products p ON p.id = l.ProductId \n" +
                    "JOIN ProductComments pc ON l.ProductId = pc.ProductId JOIN Orders o ON o.id = l.OrderId \n" +
                    "JOIN Customers c ON c.id = o.CustomerId\n" +
                    "WHERE o.Status = 'Processing'");

            ResultSet liResult = lineItem.executeQuery();

            Set<LineItem> allLineItems = new HashSet<>();

            while (liResult.next()){

                boolean createO = true;
                boolean createL = true;

                for (Order order : orders) {
                    if (order.getOrderID() == liResult.getInt("OrderId")){
                        createO = false;
                    }
                }

                if (createO) {
                   Order o = new Order(liResult.getInt("OrderId"), new Date(),
                           liResult.getString("Status"),
                            new Customer(liResult.getInt("CustomerId"),
                                    liResult.getString("CName"),
                                    liResult.getString("Email")),
                           liResult.getDouble("Price") * liResult.getInt("Quantity"),
                            new LineItem[]{}, liResult.getString("ShippingAddress"),
                           liResult.getString("BillingAddress"),
                            liResult.getString("BillingInfo"));
                    orders.add(o);
                }

                for (LineItem item : allLineItems) {
                    if (item.getOrder().getOrderID() == liResult.getInt("OrderId") &&
                            item.getProduct().getProductID() == liResult.getInt("ProductId")){
                        createL = false;
                    }
                }

                if (createL) {
                    LineItem li = new LineItem(new Product(liResult.getInt("ProductId"),
                            liResult.getInt("QtyInStock"), liResult.getString("Name"),
                            liResult.getString("Description"),
                            liResult.getDouble("Price"), 0,
                            new String[]{liResult.getString("Comment")}),
                            new Order(liResult.getInt("OrderId")),
                            liResult.getInt("Quantity"), liResult.getDouble("PricePaid"));

                    allLineItems.add(li);
                }
            }

            for (LineItem item : allLineItems) {
                for (Order order : orders) {

                    if (item.getOrder().getOrderID() == order.getOrderID()){
                        List<LineItem> lineItems = new ArrayList<>(Arrays.asList(order.getLineItems()));
                        lineItems.add(item);
                        order.setLineItems(lineItems.toArray(new LineItem[lineItems.size()]));
                        item.setOrder(order);
                    }
                }
            }
            DatabaseAccess.commitTransaction();
        }

        catch (SQLException e) {
            try {
                DatabaseAccess.rollbackTransaction();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseAccess.close();
        }

        return orders.toArray(new Order[orders.size()]);
    }

    /**
     * Gets all of the products currently being stored in the database
     * by running a SQL query
     *
     * @return an array of all of the products in the database and if
     * there are no products then the array will be empty
     */
    public static Product[] getProducts() {
        ResultSet result = null;

        List<Product> prod = new ArrayList<Product>();

        try {
            DatabaseAccess.open();
            DatabaseAccess.beginTransaction();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products p, ProductComments pc " +
                    "WHERE p.id = pc.ProductId");
            result = stmt.executeQuery();
            stmt.clearParameters();

            while (result.next()) {
                boolean addFlag = true;

                for (Product p : prod){
                    if (p.getProductID() == result.getInt("id")){
                        addFlag = false;
                    }
                }

                if (addFlag) {
                    prod.add(new Product(result.getInt("id"), result.getInt("QtyInStock"),
                            result.getString("Name"), result.getString("Description"),
                            result.getDouble("Price"), 0, new String[]{result.getString("Comment")}));
                }
            }

            DatabaseAccess.commitTransaction();
        }catch (SQLException e) {
            try {
                DatabaseAccess.rollbackTransaction();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseAccess.close();
        }

        return prod.toArray(new Product[prod.size()]);
    }

    public static Order getOrderDetails(int orderID) {
        Order o = null;

        try {
            DatabaseAccess.open();
            DatabaseAccess.beginTransaction();

            List<LineItem> items = new ArrayList<LineItem>();

            PreparedStatement searchLineItem = conn.prepareStatement("SELECT * FROM LineItems li " +
                    "JOIN Products p ON p.id = li.ProductId\n" +
                    "INNER JOIN ProductComments pc ON pc.ProductId = p.id\n" +
                    "WHERE li.OrderId = ?");

            searchLineItem.setInt(1, orderID);
            ResultSet result = searchLineItem.executeQuery();

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Orders o " +
                    "JOIN Customers c ON c.id = o.CustomerId " +
                    "WHERE o.id = ?");

            stmt.setInt(1, orderID);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()){
                o = new Order(resultSet.getInt("id"), new Date(), resultSet.getString("Status"),
                        new Customer(resultSet.getInt("CustomerId"), resultSet.getString("Name"),
                                resultSet.getString("Email")), 0, new LineItem[]{},
                        resultSet.getString("ShippingAddress"),
                        resultSet.getString("BillingAddress"),
                        resultSet.getString("BillingInfo"));
            }

            double total = 0;

            while (result.next()) {
                boolean addNew = true;
                double paid = result.getDouble("PricePaid");
                int quantity = result.getInt("Quantity");

                total += paid * quantity;

                for (LineItem item : items){
                    if (item.getProduct().getProductID() == result.getInt("ProductId")){
                        addNew = false;
                    }
                }

                if (addNew) {
                    items.add(new LineItem(new Product(result.getInt("ProductId"), result.getInt("QtyInStock"),
                            result.getString("Name"), result.getString("Description"),
                            result.getDouble("Price"), 0,
                            new String[]{result.getString("Comment")}), o, quantity, paid));
                }
            }

            o.setTotalCost(total);
            o.setLineItems(items.toArray(new LineItem[items.size()]));

            DatabaseAccess.commitTransaction();
        } catch (SQLException e) {
            try {
                DatabaseAccess.rollbackTransaction();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseAccess.close();
        }

        return o;
    }

    /**
     * Gets the product details by running a SQL query on the database
     *
     * @param productID the product id to find in the db
     * @return a Product object from the query or if the product id does
     * not exist then null is returned
     */
    public static Product getProductDetails(int productID) {
        Product p = null;
        //System.out.println(productID);
        try {
            DatabaseAccess.open();
            DatabaseAccess.beginTransaction();

            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT pc.ProductId, p.Name, p.Description, p.Price, p.QtyInStock, pc.Comment " +
                            "FROM ProductComments pc, Products p " +
                            "WHERE p.id = pc.id AND p.id = ?");

            stmt.setInt(1, productID);

            ResultSet result = stmt.executeQuery();
            stmt.clearParameters();

            while (result.next()) {
                p = new Product(productID,
                        result.getInt("QtyInStock"),
                        result.getString("Name"),
                        result.getString("Description"),
                        result.getDouble("Price"),
                        0,
                        new String[]{result.getString("Comment")});
            }
            DatabaseAccess.commitTransaction();
        } catch (SQLException e) {
            try {
                DatabaseAccess.rollbackTransaction();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseAccess.close();
        }
        return p;
    }

    /**
     * Gets all of the customers that are in the database
     * by running a SQL query
     *
     * @return an array of all the customers currently in the
     * database and if there are no customers then
     * the array will be empty
     */
    public static Customer[] getCustomers() {
        ResultSet result = null;
        List<Customer> custList = new ArrayList<Customer>();
        try {
            DatabaseAccess.open();
            DatabaseAccess.beginTransaction();

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Customers");
            result = stmt.executeQuery();
            stmt.clearParameters();

            while (result.next()) {
                custList.add(new Customer(result.getInt("id"), result.getString("Name"),
                        result.getString("Email")));
            }
            DatabaseAccess.commitTransaction();

        } catch (SQLException e) {
            try{
                DatabaseAccess.rollbackTransaction();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
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
     * the customer does not have any orders then the
     * array will be empty
     */
    public static Order[] getCustomerOrders(Customer c) {
        ResultSet result = null;
        int customerId = c.getCustomerID();
        List<Order> orders = new ArrayList<Order>();

        try {
            DatabaseAccess.open();
            DatabaseAccess.beginTransaction();

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Orders o " +
                    "WHERE o.CustomerId = ?");
            stmt.setInt(1, customerId);
            result = stmt.executeQuery();
            stmt.clearParameters();

            while (result.next()) {
                orders.add(new Order(result.getInt("id"), new Date(), result.getString("Status"),
                        c, 0, null, result.getString("ShippingAddress"),
                        result.getString("BillingAddress"), result.getString("BillingInfo")));
            }

            DatabaseAccess.commitTransaction();
         } catch (SQLException e) {
            try {
                DatabaseAccess.rollbackTransaction();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
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
     * or if there are none then the array will be empty
     */
    public static Product[] searchProductReviews(String query) {
        ResultSet result = null;
        List<Product> products = new ArrayList<Product>();

        try {
            DatabaseAccess.open();
            DatabaseAccess.beginTransaction();

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ProductComments pc, Products p " +
                    "WHERE pc.Comment LIKE ? AND p.id = pc.ProductId");
            stmt.setString(1, "%" + query + "%");
            result = stmt.executeQuery();
            stmt.clearParameters();

            while (result.next()) {
                products.add(new Product(result.getInt("ProductId"), result.getInt("QtyInStock"),
                        result.getString("Name"), result.getString("Description"),
                        result.getDouble("Price"), 0,
                        new String[]{result.getString("Comment")}));
            }

            DatabaseAccess.commitTransaction();
        } catch (SQLException e) {
            try {
                DatabaseAccess.rollbackTransaction();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseAccess.close();
        }
        return products.toArray(new Product[products.size()]);
    }

    public static void makeOrder(Customer c, LineItem[] lineItems) {

        try {
            DatabaseAccess.open();
            DatabaseAccess.beginTransaction();

            //Order o;
            double totalPrice = 0;
            int qtyRequested = 0;

            for (int k = 0; k < lineItems.length; k++) {
                qtyRequested = lineItems[k].getQuantity();

                int productId = lineItems[k].getProduct().getProductID();
                totalPrice += lineItems[k].getPricePaid();

                //int orderId = lineItems[k].getOrder().getOrderID();

                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products p " +
                        "WHERE p.id = ?");
                stmt.setInt(1, productId);

                ResultSet result = stmt.executeQuery();

                int qtyAvailable = 0;

                while (result.next()) {
                    qtyAvailable = result.getInt("QtyInStock");
                }

                if (qtyAvailable >= qtyRequested) {
                    //System.out.println("Can be created");

                    PreparedStatement updateStock = conn.prepareStatement("UPDATE Products " +
                            "SET QtyInStock = ? WHERE id = ?");
                    updateStock.setInt(1, qtyAvailable - qtyRequested);
                    updateStock.setInt(2, productId);
                    updateStock.executeUpdate();

                } else {
                    JOptionPane.showMessageDialog(null, "Could not create new order", "Error", JOptionPane.ERROR_MESSAGE);
                    DatabaseAccess.rollbackTransaction();
                }

            }


            PreparedStatement p = conn.prepareStatement("SELECT * FROM Orders o " +
                    "WHERE o.CustomerId = ?");
            p.setInt(1, c.getCustomerID());
            ResultSet resultSet = p.executeQuery();

            String shipping = "";
            String billing = "";
            String info = "";

            while (resultSet.next()){
                billing = resultSet.getString("BillingAddress");
                shipping = resultSet.getString("ShippingAddress");
                info = resultSet.getString("BillingInfo");
            }

            PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO Orders " +
                    "VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

            java.sql.Date d = new java.sql.Date(new Date().getTime());
            stmt1.setInt(1, c.getCustomerID());
            stmt1.setString(2, "Processing");
            stmt1.setString(3, billing);
            stmt1.setString(4, shipping);
            stmt1.setString(5, info);

            stmt1.execute();

            ResultSet key = stmt1.getGeneratedKeys();

            int insertKey = 0;

            while(key.next() && key != null){
                insertKey = key.getInt(1);
            }

            for (int k = 0; k < lineItems.length; k++){
                PreparedStatement updateLineItem = conn.prepareStatement("INSERT INTO LineItems " +
                        "VALUES (?, ?, ?, ?)");
                updateLineItem.setInt(1, insertKey);
                updateLineItem.setInt(2, lineItems[k].getProduct().getProductID());
                updateLineItem.setInt(3, lineItems[k].getQuantity());
                updateLineItem.setDouble(4, lineItems[k].getProduct().getPrice() * lineItems[k].getQuantity());
                updateLineItem.execute();
            }

            DatabaseAccess.commitTransaction();

            JOptionPane.showMessageDialog(null, "Create order for " + c.getName() + " for " +
                    Integer.toString(lineItems.length) + " items.");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DatabaseAccess.close();
        }
    }

    /**
     * Puts the connection into a new transaction.
     */
    private static void beginTransaction() throws SQLException {
        beginTxnStmt = conn.prepareStatement(
                "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;");
        conn.setAutoCommit(false);  // do not commit until explicitly requested
        beginTxnStmt.executeUpdate();
    }

    /**
     * Commits the current transaction.
     */
    private static void commitTransaction() throws SQLException {
        commitTxnStmt = conn.prepareStatement("COMMIT TRANSACTION");
        commitTxnStmt.executeUpdate();
        conn.setAutoCommit(true);  // go back to one transaction per statement
    }

    /**
     * Aborts the current transaction.
     */
    private static void rollbackTransaction() throws SQLException {
        abortTxnStmt = conn.prepareStatement("ROLLBACK TRANSACTION");
        abortTxnStmt.executeUpdate();
        conn.setAutoCommit(true);  // go back to one transaction per statement
    }
}
