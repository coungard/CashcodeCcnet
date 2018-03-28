package BillToBill;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.Arrays;

class Packet {

    private static final int POLYNOMIAL = 0x8408;
    private SerialPort serialPort = CashcodeCcnet.serialPort;

    private int getCRC16(int[] data) {
        int crc = 0;

        for (int i = 0; i < data.length; i++) {
            crc ^= data[i] & 0xFF;

            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc = POLYNOMIAL ^ crc >>> 1;
                } else {
                    crc >>>= 1;
                }
            }
        }
        return crc;
    }

    void sendPacket(int[] packet) throws SerialPortException, InterruptedException {
        serialPort.writeIntArray(packet);
        pause();
    }

    private void pause() throws InterruptedException {
        Thread.sleep(350);
    }

    int[] formPacket(int command, int[] data) {
        int length = data.length + 6;
        int[] commandArr = new int[256];
        commandArr[0] = 0x02;   //sync
        commandArr[1] = 0x03;   //valid address
        commandArr[2] = length; //length
        commandArr[3] = command; //command

        // заполняем массив команды значениями из data[], начиная с 4 индекса.
        if (data.length != 0) {
            int i = 4, d=0;
            while (d != data.length) {
                commandArr[i] = data[d];
                i+=1;
                d+=1;
            }
        }
        int[] crcPacket =  Arrays.copyOfRange(commandArr, 0, length-2);
        int crcValue = getCRC16(crcPacket);
        commandArr[length-1] = (crcValue >> 8 ) & 0xFF;
        commandArr[length-2] = crcValue  & 0xFF;
        int[] result = Arrays.copyOfRange(commandArr, 0, length);
        return result;
    }
}