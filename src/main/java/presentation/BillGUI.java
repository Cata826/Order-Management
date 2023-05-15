package presentation;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.BillDAO;
import model.Bill;

public class BillGUI extends JFrame {

    private BillDAO billDAO;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel totalLabel;

    public BillGUI() {
        billDAO = new BillDAO();

        setTitle("Bill Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Order ID");
        tableModel.addColumn("Client ID");
        tableModel.addColumn("Product ID");
        tableModel.addColumn("Value");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton loadButton = new JButton("Load Bills");
        JButton deleteButton = new JButton("Delete Bill");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBills();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedBill();
            }
        });

        JButton calculateTotalButton = new JButton("Calculate Total");
        calculateTotalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalValue = calculateTotalValue();
                totalLabel.setText("Total Value: " + totalValue);
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(calculateTotalButton);

        totalLabel = new JLabel("Total Value: 0");

        JPanel totalPanel = new JPanel();
        totalPanel.add(totalLabel);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(totalPanel, BorderLayout.NORTH);
    }

    private void loadBills() {
        List<Bill> bills = billDAO.getAllBills();
        tableModel.setRowCount(0);

        for (Bill bill : bills) {
            Object[] row = { bill.orderId(), bill.clientId(), bill.productId(), bill.value() };
            tableModel.addRow(row);
        }
    }

    private int calculateTotalValue() {
        int totalValue = 0;
        int valueColumnIndex = 3;
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            int value = Integer.parseInt(tableModel.getValueAt(row, valueColumnIndex).toString());
            totalValue += value;
        }

        return totalValue;
    }
    private void deleteSelectedBill() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            Bill bill = new Bill(orderId, 0, 0, 0);

            int confirmDialog = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this bill?", "Delete Bill", JOptionPane.YES_NO_OPTION);
            if (confirmDialog == JOptionPane.YES_OPTION) {
                billDAO.deleteBill(bill);
                loadBills();
                calculateTotalValue();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill from the table to delete.", "Delete Bill", JOptionPane.WARNING_MESSAGE);
        }

    }




}
