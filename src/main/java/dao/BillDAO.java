package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Bill;

public class BillDAO extends AbstractDAO<Bill> {
    private static final String SELECT_ALL_QUERY = "SELECT * FROM bill";
    private static final String INSERT_QUERY = "INSERT INTO bill (order_id, client_id, product_id, value) VALUES (?, ?, ?, ?)";

    private static final String DELETE_QUERY = "DELETE FROM bill WHERE order_id = ?";
    @Override
    protected String getTableName() {
        return "bill";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "order_id";
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();

        try (ResultSet rs = executeSelectQuery(SELECT_ALL_QUERY)) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int clientId = rs.getInt("client_id");
                int productId = rs.getInt("product_id");
                int value = rs.getInt("value");
                Bill bill = new Bill(orderId, clientId, productId, value);
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bills;
    }



    public void insertBill(Bill bill) {
        executeInsertQuery(INSERT_QUERY, bill.orderId(), bill.clientId(), bill.productId(), bill.value());
    }
    public void deleteBill(Bill bill) {
        executeInsertQuery(DELETE_QUERY, bill.orderId());
    }



}
