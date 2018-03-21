package BillToBill;

public class Application {
    private static final String portName = "/dev/tty50"; // значение подставлено на уровне интуиции

    public static void main(String[] args) throws Exception {
        CashcodeCcnet cashcode = new CashcodeCcnet(portName);
        cashcode.startAccept();
    }
}
