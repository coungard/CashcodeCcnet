package BillToBill;

public class Application {
    private static final String portNameLinux = "/dev/ttyS0"; // для LINUX
    private static final String portNameWindows = "COM1"; // для WINDOWS

    public static void main(String[] args) throws Exception {
        CashcodeCcnet cashcode = new CashcodeCcnet(portNameLinux);
        cashcode.startAccept();
    }
}
