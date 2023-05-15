package presentation;
import model.Order;
import dao.OrderDAO;
import model.*;
import dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OrderGUI extends JFrame {
    private List<Product> productList;
    private JTable productTable;
   // private BillDAO billDao;
    private JButton refreshButton;
    private ProductGUI gus =new ProductGUI();
    private JTextField orderIdTextField;
    private JTextField clientTextField;
    private JTextField productTextField;
    private JTextField quantityTextField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO =new ProductDAO();

    public OrderGUI() {
        setTitle("Order Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 300));
        initComponents();
        pack();
        setLocationRelativeTo(null);
        orderDAO = new OrderDAO();
      //  billDAO = new BillDAO();
        refreshOrderTable();

    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JLabel orderIdLabel = new JLabel("Order ID:");
        orderIdTextField = new JTextField();
        JLabel clientLabel = new JLabel("Client:");
        clientTextField = new JTextField();
        JLabel productLabel = new JLabel("Product:");
        productTextField = new JTextField();
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityTextField = new JTextField();

        inputPanel.add(orderIdLabel);
        inputPanel.add(orderIdTextField);
        inputPanel.add(clientLabel);
        inputPanel.add(clientTextField);
        inputPanel.add(productLabel);
        inputPanel.add(productTextField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityTextField);

        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            addOrder();

                                        }
                                    });
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshOrderTable();
            }
        });
        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOrder();
            }
        });

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Order ID");
        tableModel.addColumn("Client");
        tableModel.addColumn("Product");
        tableModel.addColumn("Quantity");

        orderTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(orderTable);

        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

private void addOrder() {
    int orderId = Integer.parseInt(orderIdTextField.getText());
    int clientId = Integer.parseInt(clientTextField.getText());
    int productId = Integer.parseInt(productTextField.getText());
    int quantity = Integer.parseInt(quantityTextField.getText());

    Product product = productDAO.getProductById(productId);
    if (product == null) {
        JOptionPane.showMessageDialog(this, "Product not found.", "Add Order", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int availableStock = product.getStock();
    if (availableStock < quantity) {
        JOptionPane.showMessageDialog(this, "Insufficient stock.", "Add Order", JOptionPane.WARNING_MESSAGE);
        return;
    }
    product.setStock(availableStock-quantity);
    productDAO.updateProduct(product);
    gus.refreshProductTable();

    Order order = new Order(orderId, clientId, productId, quantity);
    orderDAO.insertOrder(order);


    int updatedStock = availableStock - quantity;

    productDAO.updateProductStock(productId, updatedStock);

    refreshOrderTable();
    gus.refreshProductTable();

    // Calculate the bill value based on product price and quantity
    int value = (int)product.getPrice() * quantity;
    Bill bill = new Bill(orderId, clientId, productId, value);

    BillDAO billDAO = new BillDAO();
    billDAO.insertBill(bill);



    orderIdTextField.setText("");
    clientTextField.setText("");
    productTextField.setText("");
    quantityTextField.setText("");

}
    private void updateOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            String orderIdString = orderIdTextField.getText();
            String clientString = clientTextField.getText();
            String productString = productTextField.getText();
            String quantityString = quantityTextField.getText();

            if (orderIdString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Order ID.", "Update Order", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int orderId;
            try {
                orderId = Integer.parseInt(orderIdString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Order ID format. Please enter a valid integer.", "Update Order", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (clientString.isEmpty() || productString.isEmpty() || quantityString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all the fields.", "Update Order", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int client, product, quantity;
            try {
                client = Integer.parseInt(clientString);
                product = Integer.parseInt(productString);
                quantity = Integer.parseInt(quantityString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input format. Please enter valid integers.", "Update Order", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Order order = orderDAO.getOrderById(orderId);
            if (order != null) {
                int currentQuantity = order.getQuantity();

                order.setClient(client);
                order.setProduct(product);
                order.setQuantity(quantity);

                orderDAO.updateOrder(order);
                refreshOrderTable();

                int quantityDifference = quantity - currentQuantity;
                Product productToUpdate = productDAO.getProductById(product);
                if (productToUpdate != null) {
                    int currentStock = productToUpdate.getStock();
                    int updatedStock = currentStock - quantityDifference;
                    productToUpdate.setStock(updatedStock);
                    productDAO.updateProduct(productToUpdate);
                    gus.refreshProductTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found.", "Update Order", JOptionPane.WARNING_MESSAGE);
                }

                orderIdTextField.setText("");
                clientTextField.setText("");
                productTextField.setText("");
                quantityTextField.setText("");

                orderTable.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this, "Order not found.", "Update Order", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order from the table to update.", "Update Order", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void deleteOrder() {
    int selectedRow = orderTable.getSelectedRow();
    if (selectedRow != -1) {
        int orderId = Integer.parseInt(orderIdTextField.getText());
        Order order = orderDAO.getOrderById(orderId);
        if (order != null) {
            int confirmDialog = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this order?", "Delete Order", JOptionPane.YES_NO_OPTION);
            if (confirmDialog == JOptionPane.YES_OPTION) {
                int productId = order.getProduct();
                int quantity = order.getQuantity();

                orderDAO.deleteOrder(orderId);
                refreshOrderTable();

                orderIdTextField.setText("");
                clientTextField.setText("");
                productTextField.setText("");
                quantityTextField.setText("");

                orderTable.clearSelection();

                Product product = productDAO.getProductById(productId);
                if (product != null) {
                    int currentStock = product.getStock();
                    int updatedStock = currentStock + quantity;
                    product.setStock(updatedStock);
                    productDAO.updateProduct(product);
                    gus.refreshProductTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found.", "Delete Order", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Order not found.", "Delete Order", JOptionPane.WARNING_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select an order from the table to delete.", "Delete Order", JOptionPane.WARNING_MESSAGE);
    }
}

    private void refreshOrderTable() {
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        model.setRowCount(0);

        try {
            List<Order> orders = orderDAO.getAllOrders();

            for (Order order : orders) {
                Object[] row = {order.getOrderId(), order.getClient(), order.getProduct(), order.getQuantity()};
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

