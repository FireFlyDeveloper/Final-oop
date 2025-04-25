package com.oop.Genesys;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import models.Customer;
import models.Customer2;
import models.SQLquery;

public class CRUD {
    // Xata PostgreSQL connection details
    private static final String DB_URL = "jdbc:postgresql://ap-southeast-2.sql.xata.sh:5432/water";
    private static final String USER = "egeos8";
    private static final String PASSWORD = "xau_mGMpF5pB6HuN7vaDt4O8AKChHOAtA0vp3";
    
    public CRUD() {
        createPriceTable();
        // Uncomment the following line if you want to auto-create the customer table as well
        // createCustomerTable();
    }
    
    // Initialize PostgreSQL driver
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found");
            e.printStackTrace();
        }
    }

    // Establish database connection with SSL
    private Connection connect() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("sslmode", "require"); // Required for Xata
        props.setProperty("ssl", "true");
        
        return DriverManager.getConnection(DB_URL, props);
    }

    // Test database connection
    public boolean testConnection() {
        try (Connection conn = connect()) {
            return conn.isValid(2); // 2 second timeout
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    // ========== PRICE TABLE OPERATIONS ========== //

    public void createPriceTable() {
        String query = "CREATE TABLE IF NOT EXISTS price ("
                     + "id SERIAL PRIMARY KEY, "
                     + "item VARCHAR(255) NOT NULL, "
                     + "quantity INT NOT NULL, "
                     + "price DECIMAL(10, 2) NOT NULL"
                     + ")";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
            System.out.println("Price table created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating price table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean createPrice(SQLquery sql) {
        String query = "INSERT INTO price (item, quantity, price) VALUES (?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
             
            statement.setString(1, sql.name); 
            statement.setInt(2, sql.number);
            statement.setDouble(3, sql.price);
            
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error creating price: " + e.getMessage());
            return false;
        }
    }

    public List<SQLquery> readPrice() {
        String query = "SELECT * FROM price";
        List<SQLquery> items = new ArrayList<>();
        
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String item = resultSet.getString("item");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                // Pass price as a double instead of casting it to int
                SQLquery itemObj = new SQLquery(id, item, quantity, (int) price);
                items.add(itemObj);
            }
        } catch (SQLException e) {
            System.err.println("Error reading prices: " + e.getMessage());
        }
        return items;
    }

    public boolean updatePrice(SQLquery sql) {
        String query = "UPDATE price SET item = ?, quantity = ?, price = ? WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
             
            statement.setString(1, sql.name);
            statement.setInt(2, sql.number);
            statement.setDouble(3, sql.price);
            statement.setInt(4, sql.id);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating price: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePrice(int id) {
        String query = "DELETE FROM price WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
             
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting price: " + e.getMessage());
            return false;
        }
    }
    
    // ========== CUSTOMER TABLE OPERATIONS ========== //

    public void createCustomerTable() {
        String query = "CREATE TABLE IF NOT EXISTS customer ("
                     + "id SERIAL PRIMARY KEY, "
                     + "name VARCHAR(255) NOT NULL, "
                     + "number VARCHAR(255) NOT NULL, "
                     + "address VARCHAR(255) NOT NULL, "
                     + "itemName VARCHAR(255) NOT NULL, "
                     + "itemPrice INT NOT NULL, "
                     + "itemNumber INT NOT NULL, "
                     + "confirmed BOOLEAN NOT NULL"
                     + ")";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
            System.out.println("Customer table created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating customer table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean createCustomer(Customer customer) {
        String query = "INSERT INTO customer (name, number, address, itemName, itemPrice, itemNumber, confirmed) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, customer.name);
            statement.setString(2, customer.number);
            statement.setString(3, customer.address);
            statement.setString(4, customer.itemName);
            statement.setInt(5, customer.itemPrice);
            statement.setInt(6, customer.itemNumber);
            statement.setBoolean(7, customer.confirmed);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return false;
        }
    }

    public List<Customer2> readCustomers() {
        String query = "SELECT * FROM customer";
        List<Customer2> customers = new ArrayList<>();
        
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String number = resultSet.getString("number");
                String address = resultSet.getString("address");
                String itemName = resultSet.getString("itemName");
                int itemPrice = resultSet.getInt("itemPrice");
                int itemNumber = resultSet.getInt("itemNumber");
                boolean confirmed = resultSet.getBoolean("confirmed");

                Customer2 customer = new Customer2(id, name, number, address, itemName, itemPrice, itemNumber, confirmed);
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error reading customers: " + e.getMessage());
        }
        return customers;
    }

    public boolean updateCustomer(Customer2 customer) {
        String query = "UPDATE customer SET name = ?, number = ?, address = ?, itemName = ?, " +
                      "itemPrice = ?, itemNumber = ?, confirmed = ? WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, customer.name);
            statement.setString(2, customer.number);
            statement.setString(3, customer.address);
            statement.setString(4, customer.itemName);
            statement.setInt(5, customer.itemPrice);
            statement.setInt(6, customer.itemNumber);
            statement.setBoolean(7, customer.confirmed);
            statement.setInt(8, customer.id);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCustomer(int id) {
        String query = "DELETE FROM customer WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }
}
