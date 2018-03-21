package BillToBill;

import jssc.SerialPort;
import jssc.SerialPortException;

public class CashcodeCcnet extends Packet {

    private volatile boolean isLoop = false;
    static SerialPort serialPort;

    CashcodeCcnet(String portName) {
        serialPort = new SerialPort(portName);
    }

    void startAccept() throws SerialPortException, InterruptedException {
        serialPort.openPort();

        serialPort.setParams(SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

        sendReset();

        /*
            здесь указаны значения для каждого номинала банкноты. Судя по документации,
            порядок от меньшей банкноты к большей зависит от прошивки. Если в купюроприемник
            вбита российская прошивка, то банкноты должны быть упорядочены так :
            10 рублей -   4 (00100) 2 бит
            50 рублей -   8 (01000) 3 бит
            100 рублей - 16 (10000) 4 бит   и т.д.
            ...
         */
        int cash = 4+8+16+32+64+128;
        sendEnableBillTypes(cash);
        startPollingLoop();
    }


    public boolean stop() throws SerialPortException {
        stopPolling();
        boolean result = serialPort.isOpened() ? serialPort.closePort() : false;
        return result;
    }

    public void startPollingLoop() throws SerialPortException, InterruptedException {
        isLoop = true;
        while (isLoop) {
            sendPoll();
            sendDescription();
            //TODO if (...) sendReturn();

            //TODO    интуитивное решение :
            //TODO  int[] temp = formPacket[0x31, new int[]{});
            //TODO  if temp[5] =

            // банкнота номиналом в 100 RUB , должна занимать 4 бит ячейки в своем
            // диапазоне. Предполагаю, что тут необходимо выболнить отправку формы
            // formPacket(0x31, new int[]{}), то есть выполнить команду GET STATUS,
            // которая должна вернуть массив байтов, ГДЕ ОДИН ИЗ ИНДЕКСОВ как раз и
            // содержит номинал в 100 RUB, этот же индекс находится в диапазоне Z1-Z3
            // но я так и не понял, в каком порядке будет индексироваться выходной массив
            // и с какими значениями......
        }
    }

    public void stopPolling() {
        isLoop = false;
    }

    // описание банкноты
    public void sendDescription() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x41, new int[]{}));
    }

    // команда подтверждения
    public void sendNsc() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0xFF,new int[]{}));
    }

    // команда ..не подтверждения
    public void sendAsc() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x00,new int[]{}));
    }

    // команда от контроллера для складывания банкнот в депонировании в кассету(если я правильно перевел)
    public void sendStack() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x35,new int[]{}));
    }

    // что-то вроде перезагрузки
    public void sendReset() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x30,new int[]{}));
    }

    // запрос статуса купюроприемника
    public void sendStatus() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x31,new int[]{}));
    }

    // установка безопасного режима
    public void sendSecurity(int value) throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x32,new int[]{0,0,value}));
    }

    // демонстрация доступных видов купюр для оплаты
    public void sendEnableBillTypes(int value) throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x34,new int[]{0,0,0xFC,0,0,0}));
    }

    // возврат купюры
    public void sendReturn() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x36,new int[]{}));
    }

    // запрос статуса активности купюроприемника
    public void sendPoll() throws SerialPortException, InterruptedException {
        sendPacket(formPacket(0x33,new int[]{}));
    }
}
