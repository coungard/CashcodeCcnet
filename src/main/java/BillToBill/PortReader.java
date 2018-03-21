package BillToBill;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.Arrays;

// данный Порт reader должен успешно обрабатывать кол-во входных байтов(судя по документации)
class PortReader implements SerialPortEventListener {

    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0) {
            int[] receivedData = new int[0];
            try {
                receivedData = CashcodeCcnet.serialPort.readIntArray();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            // логирование пакета
            System.out.println(Arrays.toString(receivedData));
        }
    }
}