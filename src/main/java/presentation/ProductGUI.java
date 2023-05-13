package presentation;

import model.Product;
import dao.ProductDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ProductGUI extends JFrame {
    private JTextField idTextField;
    private JTextField nameTextField;
    private JTextField priceTextField;
    private JTextField stockTextField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private JTable productTable;
    private DefaultTableModel tableModel;
    private List<Product> productList;
    private ProductDAO productDAO;

    public ProductGUI() {
        setTitle("Product Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 300));
        initComponents();
        pack();
        setLocationRelativeTo(null);
        productDAO = new ProductDAO();
        productList = new ArrayList<>();
        refreshProductTable();
    }

    void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        JLabel idLabel = new JLabel("ID:");
        idTextField = new JTextField();
        JLabel nameLabel = new JLabel("Name:");
        nameTextField = new JTextField();
        JLabel priceLabel = new JLabel("Price:");
        priceTextField = new JTextField();
        JLabel stockLabel = new JLabel("Stock:");
        stockTextField = new JTextField();

        inputPanel.add(idLabel);
        inputPanel.add(idTextField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameTextField);
        inputPanel.add(priceLabel);
        inputPanel.add(priceTextField);
        inputPanel.add(stockLabel);
        inputPanel.add(stockTextField);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshProductTable();
            }
        });

        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
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
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Stock");

        productTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(productTable);

        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private void addProduct() {
        int id = Integer.parseInt(idTextField.getText());
        String name = nameTextField.getText();
        double price = Double.parseDouble(priceTextField.getText());
        int stock = Integer.parseInt(stockTextField.getText());

        Product product = new Product(id, name, price, stock);
        productDAO.insertProduct(product);

        refreshProductTable();

        idTextField.setText("");
        nameTextField.setText("");
        priceTextField.setText("");
        stockTextField.setText("");
    }

    void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            String idString = idTextField.getText();
            String name = nameTextField.getText();
            String priceString = priceTextField.getText();
            String stockString = stockTextField.getText();


            if (idString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ID.", "Update Product", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a valid integer.", "Update Product", JOptionPane.WARNING_MESSAGE);
                return;
            }


            if (name.isEmpty() || priceString.isEmpty() || stockString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all the fields.", "Update Product", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double price;
            int stock;
            try {
                price = Double.parseDouble(priceString);
                stock = Integer.parseInt(stockString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid price or stock format. Please enter valid numbers.", "Update Product", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Product product = productDAO.getProductById(id);
            if (product != null) {
                product.setName(name);
                product.setPrice(price);
                product.setStock(stock);

                productDAO.updateProduct(product);
                refreshProductTable();

                idTextField.setText("");
                nameTextField.setText("");
                priceTextField.setText("");
                stockTextField.setText("");

                productTable.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.", "Update Product", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product from the table to update.", "Update Product", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            String idString = idTextField.getText();

            if (idString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ID.", "Delete Product", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a valid integer.", "Delete Product", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Product product = productDAO.getProductById(id);
            if (product != null) {
                int confirmDialog = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Delete Product", JOptionPane.YES_NO_OPTION);
                if (confirmDialog == JOptionPane.YES_OPTION) {
                    productDAO.deleteProduct(id);
                    refreshProductTable();

                    idTextField.setText("");
                    nameTextField.setText("");
                    priceTextField.setText("");
                    stockTextField.setText("");

                    productTable.clearSelection();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.", "Delete Product", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product from the table to delete.", "Delete Product", JOptionPane.WARNING_MESSAGE);
        }
    }


    void refreshProductTable() {
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);

        try {
            productList = productDAO.getAllProducts();

            for (Product product : productList) {
                Object[] row = {product.getProductId(), product.getName(), product.getPrice(), product.getStock()};
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error while retrieving product data.", "Refresh Product Table", JOptionPane.ERROR_MESSAGE);
        }
    }





}

