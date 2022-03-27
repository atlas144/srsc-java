// SPDX-License-Identifier: MIT

package srsc;

import java.util.Arrays;
import java.util.HashMap;
import srsc.exceptions.MissingPayloadException;
import srsc.exceptions.PayloadParsingException;
import srsc.packet.Packet;
import srsc.packet.PacketType;
import srsc.packet.PayloadSize;

/**
 *
 * @author atlas144
 */
public class PacketProcessor {
    
    public static boolean validateChecksum(byte[] binaryPacket) {
        int validationSum = 0;

        for (byte packetByte : binaryPacket) {
            validationSum += Byte.toUnsignedInt(packetByte);
        }

        return (validationSum & 0xff) == 0xff;
    }
    
    public static byte[] parsePayload(int payload, PayloadSize payloadSize) {
        byte[] binaryPayload = new byte[payloadSize.getValue()];

        for (byte i = 0; i < binaryPayload.length; i++) {
            binaryPayload[i] = (byte) ((payload >> (8 * i)) & 0xff);
        }
        
        return binaryPayload;
    }
    
    public static int parseBinaryPayload(byte[] binaryPayload) {        
        int payload = 0;

        for (byte i = 0; i < binaryPayload.length; i++) {
            payload |= binaryPayload[i] << (8 * i);
        }

        return payload;
    }

    public static Packet buildPacketObject(byte[] binaryPacket, HashMap<Integer, PacketType> packetTypes) {
        Packet packet = null;
        PacketType packetType = packetTypes.get((int) binaryPacket[0]);
        
        if (packetType.getPayloadSize() != PayloadSize.COMMAND) {
            int payloadOffset = packetType.isCritical() ? 3 : 2;
            int payload = parseBinaryPayload(Arrays.copyOfRange(binaryPacket, payloadOffset, payloadOffset + packetType.getPayloadSize().getValue()));
            
            packet = new Packet(packetType, payload);
        } else {
            try {
                packet = new Packet(packetType);
            } catch (MissingPayloadException exception) {}
        }
        
        return packet;
    }

}
