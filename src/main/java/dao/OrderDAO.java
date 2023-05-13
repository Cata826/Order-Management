package dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Order;

/**
 *
 */
public class OrderDAO extends AbstractDAO<Order> {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM `order`";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM `order` WHERE orderId = ?";
    private static final String INSERT_QUERY = "INSERT INTO `order` (orderId, clientId, productId, quantity) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE `order` SET clientId = ?, productId = ?, quantity = ? WHERE orderId = ?";
    private static final String DELETE_QUERY = "DELETE FROM `order` WHERE orderId = ?";

    @Override
    protected String getTableName() {
        return "`order`";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "orderId";
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        try (ResultSet rs = executeSelectQuery(SELECT_ALL_QUERY)) {
            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                int client = rs.getInt("clientId");
                int product = rs.getInt("productId");
                int quantity = rs.getInt("quantity");
                Order order = new Order(orderId, client, product, quantity);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public Order getOrderById(int orderId) {
        try (ResultSet rs = executeSelectQuery(SELECT_BY_ID_QUERY, orderId)) {
            if (rs.next()) {
                int client = rs.getInt("clientId");
                int product = rs.getInt("productId");
                int quantity = rs.getInt("quantity");
                return new Order(orderId, client, product, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void insertOrder(Order order) {
        executeInsertQuery(INSERT_QUERY, order.getOrderId(), order.getClient(), order.getProduct(), order.getQuantity());
    }

    public void updateOrder(Order order) {
        executeUpdateQuery(UPDATE_QUERY, order.getClient(), order.getProduct(), order.getQuantity(), order.getOrderId());
    }

    public void deleteOrder(int orderId) {
        executeDeleteQuery(DELETE_QUERY, orderId);
    }
}
