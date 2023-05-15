package dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Client;

/**
 *
 *
 */
public class ClientDAO extends AbstractDAO<Client> {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM client";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM client WHERE clientId = ?";
    private static final String INSERT_QUERY = "INSERT INTO client (clientId, name, address,contact) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE client SET name = ?, address = ?, contact = ? WHERE clientId = ?";
    private static final String DELETE_QUERY = "DELETE FROM client WHERE clientId = ?";

    @Override
    protected String getTableName() {
        return "client";
    }

    @Override
    protected String getPrimaryKeyColumnName() {
        return "clientId";
    }

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();

        try (ResultSet rs = executeSelectQuery(SELECT_ALL_QUERY)) {
            while (rs.next()) {
                int id = rs.getInt("clientId");
                String name = rs.getString("name");
                String contact = rs.getString("address");
                String address = rs.getString("contact");
                Client client = new Client(id, name, contact, address);
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public Client getClientById(int id) {
        try (ResultSet rs = executeSelectQuery(SELECT_BY_ID_QUERY, id)) {
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("address");
                String address = rs.getString("contact");
                return new Client(id, name, email, address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void insertClient(Client client) {
        executeInsertQuery(INSERT_QUERY, client.getClientId(), client.getName(), client.getContact(), client.getAddress());
    }

    public void updateClient(Client client) {
        executeUpdateQuery(UPDATE_QUERY, client.getName(), client.getContact(), client.getAddress(), client.getClientId());
    }

    public void deleteClient(int clientId) {
        executeDeleteQuery(DELETE_QUERY, clientId);
    }
}
