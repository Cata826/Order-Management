package presentation;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBills();
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

}
