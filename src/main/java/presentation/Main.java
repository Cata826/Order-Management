package presentation;
import javax.swing.*;
import java.awt.*;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        ClientGUI clientGUI = new ClientGUI();
        OrderGUI orderGUI = new OrderGUI();
        ProductGUI productGUI = new ProductGUI();
        BillGUI billGUI = new BillGUI();
        billGUI.setVisible(true);

        JFrame frame = new JFrame("Order Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 3));
        frame.add(clientGUI.getContentPane());
        frame.add(orderGUI.getContentPane());
        frame.add(productGUI.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }
}
