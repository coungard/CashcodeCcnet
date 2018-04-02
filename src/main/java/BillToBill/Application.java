package BillToBill;

import javax.swing.*;

public class Application {
    private static String portName = JOptionPane.showInputDialog("Введите название используемого com-порта");

    public static void main(String[] args) throws Exception {
        CashcodeCcnet cashcode = new CashcodeCcnet(portName);
        cashcode.startAccept();
    }
}
