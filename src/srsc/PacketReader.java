// SPDX-License-Identifier: MIT

package srsc;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.util.Arrays;
import java.util.HashMap;
import srsc.packet.Packet;
import srsc.packet.PacketType;
import srsc.packet.PayloadSize;

/**
 *
 * @author atlas144
 */
public class PacketReader implements SerialPortDataListener {

    private final SerialPort port;
    private final HashMap<Byte, PacketType> packetTypes;
    private final byte[] acceptedCriticalPackets;
    private final Semaphore semaphore;
    private final SRSC srsc;
    private PacketArrivedCallback onPacketArrivedCallback;
    
    private boolean validateChecksum(byte[] binaryPacket) {
        byte validationSum = 0;

        for (byte i = 0; i < binaryPacket.length; i++) {
          validationSum += binaryPacket[i];
        }

        return validationSum == 255;
    }
    
    private int parseBinaryPayload(byte[] binaryPayload) {
        int payload = 0;

        for (byte i = 0; i < binaryPayload.length; i++) {
            payload += binaryPayload[i] * (255 * i);
        }
        
        return payload;
    }
    
    private Packet buildPacket(byte[] binaryPacket) throws IllegalArgumentException {
        Packet packet;
        PacketType packetType = packetTypes.get(binaryPacket[0]);
        
        if (packetType.getPayloadSize() != PayloadSize.COMMAND) {
            int payloadOffset = packetType.isCritical() ? 3 : 2;
            int payload = parseBinaryPayload(Arrays.copyOfRange(binaryPacket, payloadOffset, payloadOffset + packetType.getPayloadSize().getValue()));
            
            packet = new Packet(packetType, payload);
        } else {
            packet = new Packet(packetType);
        }
        
        return packet;
    }
    
    private void processConnectPacket(Packet packet) {
        semaphore.setSize(packet.getPayload());
        semaphore.reset();
        
        for (byte acceptedCriticalPacket : acceptedCriticalPackets) {
            acceptedCriticalPacket = (byte) 255;
        }
        
        System.out.println("Arrived CONNECT packet");
        srsc.writePacket(packetTypes.get(0x01), 0);
    }
    
    private void processConnackPacket(Packet packet) {
        semaphore.setSize(packet.getPayload());
        semaphore.reset();
        
        for (byte acceptedCriticalPacket : acceptedCriticalPackets) {
            acceptedCriticalPacket = (byte) 255;
        }
        
        System.out.println("Arrived CONNACK packet");
    }
    
    private void processBinaryPacket(byte[] binaryPacket) {
        PacketType packetType = packetTypes.get(binaryPacket[0]);
        
        if (packetType != null) {
            if (packetType.getPacketTypeIdentifier() == 0x02) {
                System.out.println("Arrived ACCEPTACK packet");
                semaphore.increase();

                return;
            }

            if (!validateChecksum(binaryPacket)) {
                System.out.println("Arrived corrupted packet!");

                return;
            }
            
            if (packetType.isCritical()) {                
                if (acceptedCriticalPackets[binaryPacket[2] % 10] == binaryPacket[2]) {
                    return;
                } else {
                    acceptedCriticalPackets[binaryPacket[2] % 10] = binaryPacket[2];
                }
            }

            Packet packet = buildPacket(binaryPacket);

            switch (packetType.getPacketTypeIdentifier()) {
                case 0x00:
                    processConnectPacket(packet);
                    break;
                case 0x01:
                    processConnackPacket(packet);
                    break;
                default:
                    onPacketArrivedCallback.onPacketArrived(packet);
                    break;
            }
        } else {
            System.out.println("Arrived packet with unknown type!");
        }
    }
    
    public PacketReader(SerialPort port, HashMap<Byte, PacketType> packetTypes, Semaphore semaphore, SRSC srsc) {
        this.port = port;
        this.packetTypes = packetTypes;
        acceptedCriticalPackets = new byte[10];
        this.semaphore = semaphore;
        this.srsc = srsc;
        onPacketArrivedCallback = (Packet packet) -> {};
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
        }
        
        byte[] binaryPacket = new byte[port.bytesAvailable()];
        
        port.readBytes(binaryPacket, binaryPacket.length);
        processBinaryPacket(binaryPacket);
    }
    
    public void registerOnPacketArrivedCallback(PacketArrivedCallback callback) {
        onPacketArrivedCallback = callback;
    }
    
}
