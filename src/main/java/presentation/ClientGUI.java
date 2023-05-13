package presentation;
import connection.*;
import dao.*;
import model.Client;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ClientGUI extends JFrame {
    private JTextField idTextField;
    private JTextField nameTextField;
    private JTextField addressTextField;
    private JTextField contactTextField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private ClientDAO clientDAO;
    private ConnectionFactory connectionFactory;

    public ClientGUI() {
        setTitle("Client Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 300));
        initComponents();
        pack();
        setLocationRelativeTo(null);
        clientDAO = new ClientDAO();
        refreshClientTable();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        JLabel idLabel = new JLabel("ID:");
        idTextField = new JTextField();
        JLabel nameLabel = new JLabel("Name:");
        nameTextField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        addressTextField = new JTextField();
        JLabel contactLabel = new JLabel("Contact:");
        contactTextField = new JTextField();

        inputPanel.add(idLabel);
        inputPanel.add(idTextField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameTextField);
        inputPanel.add(addressLabel);
        inputPanel.add(addressTextField);
        inputPanel.add(contactLabel);
        inputPanel.add(contactTextField);

        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClient();
            }
        });

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClient();
            }
        });

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteClient();
            }
        });


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Address");
        tableModel.addColumn("Contact");

        clientTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(clientTable);

        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private void addClient() {
        int id = Integer.parseInt(idTextField.getText());
        String name = nameTextField.getText();
        String address = addressTextField.getText();
        String contact = contactTextField.getText();

        Client client = new Client(id, name, address, contact);
        clientDAO.insertClient(client);

        refreshClientTable();

        idTextField.setText("");
        nameTextField.setText("");
        addressTextField.setText("");
        contactTextField.setText("");
    }

    private void updateClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow != -1) {
            String idString = idTextField.getText();
            String name = nameTextField.getText();
            String contact = contactTextField.getText();
            String address = addressTextField.getText();

            if (idString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ID.", "Update Client", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a valid integer.", "Update Client", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all the fields.", "Update Client", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Client client = clientDAO.getClientById(id);
            if (client != null) {
                client.setName(name);
                client.setContact(contact);
                client.setAddress(address);

                clientDAO.updateClient(client);
                refreshClientTable();

                idTextField.setText("");
                nameTextField.setText("");
                contactTextField.setText("");
                addressTextField.setText("");

                clientTable.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this, "Client not found.", "Update Client", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a client from the table to update.", "Update Client", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void deleteClient() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(idTextField.getText());
            Client client = clientDAO.getClientById(id);
            if (client != null) {
                int confirmDialog = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this client?", "Delete Client", JOptionPane.YES_NO_OPTION);
                if (confirmDialog == JOptionPane.YES_OPTION) {
                    clientDAO.deleteClient(id);
                    refreshClientTable();

                    idTextField.setText("");
                    nameTextField.setText("");
                    contactTextField.setText("");
                    addressTextField.setText("");

                    clientTable.clearSelection();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Client not found.", "Delete Client", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a client from the table to delete.", "Delete Client", JOptionPane.WARNING_MESSAGE);
        }
    }



    private void refreshClientTable() {
        DefaultTableModel model = (DefaultTableModel) clientTable.getModel();
        model.setRowCount(0);

        try {
            List<Client> clients = clientDAO.getAllClients();

            for (Client client : clients) {
                Object[] row = {client.getClientId(), client.getName(), client.getContact(), client.getAddress()};
                model.addRow(row);
            }
        } finally {

        }
    }

        }



