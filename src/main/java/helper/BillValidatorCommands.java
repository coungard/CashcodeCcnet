package helper;

import java.util.HashMap;

public class BillValidatorCommands {

    public HashMap<String, byte[]> commands;

    public BillValidatorCommands() {
        commands = new HashMap<>();

        commands.put("ACK", new byte[]{0x02, 0x03, 0x06, 0x00, (byte) 0xC2, (byte) 0x82});
        commands.put("Reset", new byte[]{0x02, 0x03, 0x06, 0x30, 0x41, (byte)0xB3});
        commands.put("GetStatus", new byte[]{0x02, 0x03, 0x06, 0x31, (byte)0xC8, (byte)0xA2});
        commands.put("SetSecurity", new byte[]{0x02, 0x03, 0x06, 0x32, 0x53, (byte)0x90});
        commands.put("Poll", new byte[]{0x02, 0x03, 0x06, 0x33, (byte)0xDA, (byte)0x81});
        commands.put("EnableBillTypes", new byte[]{0x02, 0x03, 0x0C, 0x34, (byte)0xFF, (byte)0xFF,
                (byte)0xFF, 0x00, 0x00, 0x00, (byte)0xB5, (byte)0xC1});
        commands.put("EnableBillTypesEscrow", new byte[]{0x02, 0x03, 0x0C, 0x34, (byte)0xFF, (byte)0xFF,
                (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFE, (byte)0xF7});
        commands.put("DisableBillTypes", new byte[]{0x02, 0x03, 0x0C, 0x34, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x17, 0x0C});
        commands.put("Stack", new byte[]{0x02, 0x03, 0x06, 0x35, (byte)0xEC, (byte)0xE4});
        commands.put("Return", new byte[]{0x02, 0x03, 0x06, 0x36, 0x77, (byte)0xD6});
        commands.put("Identification", new byte[]{0x02, 0x03, 0x06, 0x37, (byte)0xFE, (byte)0xC7});
        commands.put("Hold", new byte[]{0x02, 0x03, 0x06, 0x38, 0x09, 0x3F});
        commands.put("CassetteStatus", new byte[]{0x02, 0x03, 0x06, 0x3B, (byte)0x92, 0x0D});
        commands.put("Dispense", new byte[]{0x02, 0x03, 0x06, 0x3C, 0x2D, 0x79});
        commands.put("Unload", new byte[]{0x02, 0x03, 0x06, 0x3D, (byte)0xA4, 0x68});
        commands.put("EscrowCassetteStatus", new byte[]{0x02, 0x03, 0x06, 0x3E, 0x3F, 0x5A});
        commands.put("EscrowCassetteUnload", new byte[]{0x02, 0x03, 0x06, 0x3F, (byte)0xB6, 0x4B});
        commands.put("SetCassetteType", new byte[]{0x02, 0x03, 0x06, 0x40, (byte)0xC6, (byte)0xC0});
        commands.put("GetBillTable", new byte[]{0x02, 0x03, 0x06, 0x41, 0x4F, (byte)0xD1});
        commands.put("Download", new byte[]{0x02, 0x03, 0x06, 0x50, 0x47, (byte)0xD0});

    }
}

