package BillToBill;

import jssc.*;

import java.util.Arrays;

public class CashcodeCcnet extends ErrorsHandler {

    private static int[] receivedData;

    private static final int POLYNOMIAL = 0x08408;  // полином для считывания CRC16
    private static final int POLL_TIMEOUT = 200;    // Тайм-аут ожидания ответа от считывателя
    private static final int EVENT_WAIT_HANDLER_TIMEOUT = 10000;

    private volatile boolean isLoop = false;
    private static boolean returnBill = false; // эта булевая переменная нужна для дальнейшей обработки
    private static boolean isPowerUp = false;
    private boolean isEnableBills =false;

    private SerialPort serialPort;

    public CashcodeCcnet(String portName) {
        serialPort = new SerialPort(portName);
    }


    public void startAccept() throws SerialPortException, InterruptedException {
        serialPort.openPort();

        serialPort.setParams(SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

        powerUpBillValidator();
        startPollingLoop();
    }

    private int powerUpBillValidator() throws SerialPortException, InterruptedException {
        // POWER UP
        lastError = 100000;
        sendPoll();

        // Проверим результат
        if (checkError(receivedData)) {
            sendNak();
            throw new RuntimeException(errorsList.get(lastError));
        }

        // Иначе отправляем сигнал подтверждения
        sendAck();
        // RESET
        sendReset();

        //Если не получили от купюроприемника сигнала ACK
        if (receivedData[3] != 0x00) {
            lastError = 100050;
            return lastError;
        }

        // INITIALIZE
        // Далее снова опрашиваем купюроприемник
        sendPoll();

        if (checkError(receivedData)) {
            sendNak();
            throw new RuntimeException(errorsList.get(lastError));
        }

        // Иначе отправляем сигнал подтверждения
        sendAck();

        // GET STATUS
        sendStatus();

        // Команда GET STATUS возвращает 6 байт ответа. Если все равны 0, то статус ok и можно работать дальше, иначе ошибка
        if (receivedData[3] != 0x00 || receivedData[4] != 0x00 || receivedData[5] != 0x00 ||
                receivedData[6] != 0x00 || receivedData[7] != 0x00 || receivedData[8] != 0x00) {
            lastError = 100070; //Cтекер снят
            throw new RuntimeException(errorsList.get(lastError));
        }

        sendAck();

        // SET_SECURITY (в тестовом примере отправляет 3 байта (0 0 0)
        sendSecurity(0);

        //Если не получили от купюроприемника сигнала ACK
        if (receivedData[3] != 0x00) {
            lastError = 100050; // нет ACK
            return lastError;
        }

        // IDENTIFICATION
        sendIdentification();
        sendAck();

        // POLL
        // Далее снова опрашиваем купюроприемник. Должны получить команду INITIALIZE
        sendPoll();

        // Проверим результат
        if (checkError(receivedData)) {
            sendNak();
            throw new RuntimeException(errorsList.get(lastError));
        }

        // Иначе отправляем сигнал подтверждения
        sendAck();

        // POLL
        // Далее снова опрашиваем купюроприемник. Должны получить команду UNIT DISABLE
        sendPoll();

        // Проверим результат
        if (checkError(receivedData)) {
            sendNak();
            throw new RuntimeException(errorsList.get(lastError));
        }

        // Иначе отправляем сигнал подтверждения
        sendAck();

        isPowerUp = true;
        return lastError;
    }

    public void startPollingLoop() throws SerialPortException, InterruptedException {
        isLoop = true;
        while (isLoop) {
            // отпавить команду POLL
            sendPoll();
            // Если четвертый бит не Idling (незанятый), то идем дальше
            if (receivedData[3] != 0x14) {
                // ACCEPTING
                //Если получили ответ 15H (Accepting)
                if (receivedData[3] == 0x15) {
                    // Подтверждаем
                    sendAck();
                }

                // ESCROW POSITION
                // Если четвертый бит 1Сh (Rejecting), то купюроприемник не распознал купюру
                else if (receivedData[3] == 0x1C) {
                    // Принялии какую-то купюру
                    sendAck();

                    //TODO...
                }

                // ESCROW POSITION
                // купюра распознана
                else if (receivedData[3] == 0x80) {
                    // Подтветждаем
                    sendAck();

                    //TODO... Событие, что купюра в процессе отправки в стек

                    // Если программа отвечает возвратом, то на возврат
                    if (returnBill) {
                        // RETURN
                        // Если программа отказывает принимать купюру, отправим RETURN
                        sendReturn();
                        returnBill = false;
                    } else {
                        // STACK
                        // Если равпознали, отправим купюру в стек (STACK)
                        sendStack();
                    }
                }

                // STACKING
                // Если четвертый бит 17h, следовательно идет процесс отправки купюры в стек (STACKING)
                else if (receivedData[3] == 0x17) {
                    sendAck();
                }

                // Bill stacked
                // Если четвертый бит 81h, следовательно, купюра попала в стек
                else if (receivedData[3] == 0x81) {
                    // Подтветждаем
                    sendAck();

                    //TODO...
                }

                // RETURNING
                // Если четвертый бит 18h, следовательно идет процесс возврата
                else if (receivedData[3] == 0x18) {
                    sendAck();
                }

                // BILL RETURNING
                // Если четвертый бит 82h, следовательно купюра возвращена
                else if (receivedData[3] == 0x82) {
                    sendAck();
                }

                // Drop Cassette out of position
                // Снят купюроотстойник
                else if (receivedData[3] == 0x42) {
                    //TODO...
                    System.out.println("Снят купюроотстойник");
                }

                // Initialize
                // Кассета вставлена обратно на место
                else if (receivedData[3] == 0x13) {
                    //TODO...
                    System.out.println("Кассета вставлена обратно на место");
                }
            }
        }
    }

    // Включение режима приема купюр
    public int EnableBillValidator() throws SerialPortException, InterruptedException {

        isEnableBills = true;

        // отправить команду ENABLE BILL TYPES (в тестовом примере отправляет 6 байт  (255 255 255 0 0 0)
        sendEnableBillTypes();
        //функция удержания ? я не до конца понимаю что это
        sendEnableBillTypesEscrow();
        //Если не получили от купюроприемника сигнала ACK
        if (receivedData[3] != 0x00) {
            lastError = 100050;
            throw new RuntimeException(errorsList.get(lastError));
        }

        // Далее снова опрашиваем купюроприемник
        sendPoll();
        // Проверим результат
        if (checkError(receivedData)) {
            sendNak();
            throw new RuntimeException(errorsList.get(lastError));
        }

        // Иначе отправляем сигнал подтверждения
        sendAck();

        return lastError;
    }


    private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {

            if (event.isRXCHAR() && event.getEventValue() > 0) {

                try {
                    receivedData = serialPort.readIntArray();

                    if (receivedData.length == 0 || !checkCrc(receivedData)) {
                        throw new RuntimeException("Несоответствие контрольной суммы полученного сообщения. " +
                                "Возможно устройство не подключено к COM-порту. Проверьте настройки подключения.");
                    }
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
                // логируем исходящие данные
                System.out.println("Исходящие данные от com-port : " + Arrays.toString(receivedData));
            }
        }

    }

    public boolean stop() throws SerialPortException {
        stopPolling();
        return serialPort.isOpened() && serialPort.closePort();
    }

    private void stopPolling() {
        isLoop = false;
    }

    private void sendNak() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0xFF, new int[]{}));
    }

    private void sendAck() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x00, new int[]{}));
    }

    public void sendStack() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x35, new int[]{}));
    }

    private void sendReset() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x30, new int[]{}));
    }

    private void sendIdentification() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x37, new int[]{}));
    }

    private void sendStatus() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x31, new int[]{}));
    }

    private void sendSecurity(int value) throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x32, new int[]{0, 0, value}));
    }

    private void sendEnableBillTypes() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x34, new int[]{0xFF, 0xFF, 0xFF, 0, 0, 0}));
    }

    private void sendEnableBillTypesEscrow() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x34, new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF}));
    }

    private void sendReturn() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x36, new int[]{}));
    }

    private void sendPoll() throws SerialPortException, InterruptedException {
        sendPacket(formPacket((byte) 0x33, new int[]{}));
    }

    private void sendPacket(int[] packet) throws SerialPortException, InterruptedException {
        serialPort.writeIntArray(packet);
        Thread.sleep(100);
    }

    private int[] formPacket(int command, int[] data) {
        int length = data.length + 6;
        int[] commandArr = new int[256];
        commandArr[0] = 0x02;   //sync
        commandArr[1] = 0x03;   //valid address
        commandArr[2] = length; //length
        commandArr[3] = command; //command

        if (data.length != 0) {
            int i = 4, d = 0;
            while (d != data.length) {
                commandArr[i] = data[d];
                i += 1;
                d += 1;
            }
        }
        int[] crcPacket = Arrays.copyOfRange(commandArr, 0, length - 2);
        int crcValue = getCrc16(crcPacket);
        commandArr[length - 1] = (crcValue >> 8) & 0xFF;
        commandArr[length - 2] = crcValue & 0xFF;
        int[] res = Arrays.copyOfRange(commandArr, 0, length);
        return res;
    }

    private int getCrc16(int[] arr) {
        int i, tmpCrc = 0;
        byte j;
        for (i = 0; i <= arr.length - 1; i++) {
            tmpCrc ^= arr[i];
            for (j = 0; j <= 7; j++) {
                if ((tmpCrc & 0x0001) != 0) {
                    tmpCrc >>= 1;
                    tmpCrc ^= POLYNOMIAL;
                } else {
                    tmpCrc >>= 1;
                }
            }
        }
        return tmpCrc;
    }

    private boolean checkCrc(int[] buffer) {
        boolean result = true;
        int length = buffer.length;

        int[] oldCRC = new int[]{length - 2, length - 1};

        int newCRC16 = getCrc16(Arrays.copyOfRange(buffer, 0, length - 2));
        int[] newCRC = new int[]{newCRC16 & 0xFF, (newCRC16 >> 8) & 0xFF};

        for (int i = 0; i < 2; i++) {
            if (oldCRC[i] != newCRC[i]) {
                result = false;
                break;
            }
        }
        return result;
    }

}
