package dao;
import connection.ConnectionFactory;
import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ProductDAO extends AbstractDAO<Product> {
    private ConnectionFactory connectionFactory;
    private static final String SELECT_ALL_QUERY = "SELECT * FROM product";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM product WHERE productId = ?";
    private static final String INSERT_QUERY = "INSERT INTO product (productId, name, price, stock) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE product SET name = ?, price = ?, stock = ? WHERE productId = ?";
    private static final String DELETE_QUERY = "DELETE FROM product WHERE productId = ?";

    public ProductDAO() {
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        try (ResultSet rs = executeSelectQuery(SELECT_ALL_QUERY)) {
            while (rs.next()) {
                int id = rs.getInt("productId");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                int stock = rs.getInt("stock");
                Product product = new Product(id, name, price, stock);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public Product getProductById(int id) {
        try (ResultSet rs = executeSelectQuery(SELECT_BY_ID_QUERY, id)) {
            if (rs.next()) {
                String name = rs.getString("name");
                int price = rs.getInt("price");
                int stock = rs.getInt("stock");
                return new Product(id, name, price, stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateProductStock(int productId, int newStock) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionFactory.getConnection();
            String query = "UPDATE product SET stock = ? WHERE productId = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, newStock);
            statement.setInt(2, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
    }
    public void insertProduct(Product product) {
        executeInsertQuery(INSERT_QUERY, product.getProductId(), product.getName(), product.getPrice(), product.getStock());
    }

    public void updateProduct(Product product) {
        executeUpdateQuery(UPDATE_QUERY, product.getName(), product.getPrice(), product.getStock(), product.getProductId());
    }

    public void deleteProduct(int productId) {
        executeDeleteQuery(DELETE_QUERY, productId);
    }

    @Override
    protected String getTableName() {
        return "product";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "productId";
    }



}
