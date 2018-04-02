package BillToBill;

import java.util.HashMap;

public class ErrorsHandler {
    public HashMap<Integer, String> errorsList;
    public static int lastError;

    public ErrorsHandler() {
        errorsList = new HashMap<>();

        errorsList.put(100000, "Неизвестная ошибка");

        errorsList.put(100010, "Ошибка открытия Com-порта");
        errorsList.put(100020, "Com-порт не открыт");
        errorsList.put(100030, "Ошибка отпраки команды включения купюроприемника.");
        errorsList.put(100040, "Ошибка отпраки команды включения купюроприемника. От купюроприемника не получена команда POWER UP.");
        errorsList.put(100050, "Ошибка отпраки команды включения купюроприемника. От купюроприемника не получена команда ACK.");
        errorsList.put(100060, "Ошибка отпраки команды включения купюроприемника. От купюроприемника не получена команда INITIALIZE.");
        errorsList.put(100070, "Ошибка проверки статуса купюроприемника. Cтекер снят.");
        errorsList.put(100080, "Ошибка проверки статуса купюроприемника. Стекер переполнен.");
        errorsList.put(100090, "Ошибка проверки статуса купюроприемника. В валидаторе застряла купюра.");
        errorsList.put(100100, "Ошибка проверки статуса купюроприемника. В стекере застряла купюра.");
        errorsList.put(100110, "Ошибка проверки статуса купюроприемника. Фальшивая купюра.");
        errorsList.put(100120, "Ошибка проверки статуса купюроприемника. Предыдущая купюра еще не попала в стек и находится в механизме распознавания.");

        errorsList.put(100130, "Ошибка работы купюроприемника. Сбой при работе механизма стекера.");
        errorsList.put(100140, "Ошибка работы купюроприемника. Сбой в скорости передачи купюры в стекер.");
        errorsList.put(100150, "Ошибка работы купюроприемника. Сбой передачи купюры в стекер.");
        errorsList.put(100160, "Ошибка работы купюроприемника. Сбой механизма выравнивания купюр.");
        errorsList.put(100170, "Ошибка работы купюроприемника. Сбой в работе стекера.");
        errorsList.put(100180, "Ошибка работы купюроприемника. Сбой в работе оптических сенсоров.");
        errorsList.put(100190, "Ошибка работы купюроприемника. Сбой работы канала индуктивности.");
        errorsList.put(100200, "Ошибка работы купюроприемника. Сбой в работе канала проверки заполняемости стекера.");

        // Ошибки распознования купюры
        errorsList.put(0x60, "Rejecting due to Insertion");
        errorsList.put(0x61, "Rejecting due to Magnetic");
        errorsList.put(0x62, "Rejecting due to Remained bill in head");
        errorsList.put(0x63, "Rejecting due to Multiplying");
        errorsList.put(0x64, "Rejecting due to Conveying");
        errorsList.put(0x65, "Rejecting due to Identification1");
        errorsList.put(0x66, "Rejecting due to Verification");
        errorsList.put(0x67, "Rejecting due to Optic");
        errorsList.put(0x68, "Rejecting due to Inhibit");
        errorsList.put(0x69, "Rejecting due to Capacity");
        errorsList.put(0x6A, "Rejecting due to Operation");
        errorsList.put(0x6C, "Rejecting due to Length");
    }

    public boolean checkError(int[] resultData) {

        boolean IsError = false;

        //Если не получили от купюроприемника третий байт равный 30Н (ILLEGAL COMMAND )
        if (resultData[3] == 0x30) {
            lastError = 100040;
            IsError = true;
        }
        //Если не получили от купюроприемника третий байт равный 41Н (Drop Cassette Full)
        else if (resultData[3] == 0x41) {
            lastError = 100080;
            IsError = true;
        }
        //Если не получили от купюроприемника третий байт равный 42Н (Drop Cassette out of position)
        else if (resultData[3] == 0x42) {
            lastError = 100070;
            IsError = true;
        }
        //Если не получили от купюроприемника третий байт равный 43Н (Validator Jammed)
        else if (resultData[3] == 0x43) {
            lastError = 100090;
            IsError = true;
        }
        //Если не получили от купюроприемника третий байт равный 44Н (Drop Cassette Jammed)
        else if (resultData[3] == 0x44) {
            lastError = 100100;
            IsError = true;
        }
        //Если не получили от купюроприемника третий байт равный 45Н (Cheated)
        else if (resultData[3] == 0x45) {
            lastError = 100110;
            IsError = true;
        }
        //Если не получили от купюроприемника третий байт равный 46Н (Pause)
        else if (resultData[3] == 0x46) {
            lastError = 100120;
            IsError = true;
        }
        //Если не получили от купюроприемника третий байт равный 47Н (Generic Failure codes)
        else if (resultData[3] == 0x47) {
            if (resultData[4] == 0x50) {
                lastError = 100130;
            }        // Stack Motor Failure
            else if (resultData[4] == 0x51) {
                lastError = 100140;
            }   // Transport Motor Speed Failure
            else if (resultData[4] == 0x52) {
                lastError = 100150;
            }   // Transport Motor Failure
            else if (resultData[4] == 0x53) {
                lastError = 100160;
            }   // Aligning Motor Failure
            else if (resultData[4] == 0x54) {
                lastError = 100170;
            }   // Initial Cassette Status Failure
            else if (resultData[4] == 0x55) {
                lastError = 100180;
            }   // Optic Canal Failure
            else if (resultData[4] == 0x56) {
                lastError = 100190;
            }   // Magnetic Canal Failure
            else if (resultData[4] == 0x5F) {
                lastError = 100200;
            }   // Capacitance Canal Failure
            IsError = true;
        }

        return IsError;
    }
}

