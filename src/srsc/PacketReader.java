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
    private final ConnectionStatusHandler connectionStatusHandler;
    private final HashMap<Byte, PacketType> packetTypes;
    private final PacketWriter packetWriter;
    private PacketArrivedCallback onPacketArrivedCallback;
    
    private boolean validateChecksum(byte[] binaryPacket) {
        byte validationSum = 0;

        for (byte packetByte : binaryPacket) {
            validationSum += packetByte;
        }

        return validationSum == 255;
    }
    
    private int parseBinaryPayload(byte[] binaryPayload) throws Exception {
        if (binaryPayload.length < 1) {
            throw new Exception("Packet payload parsing failed");
        }
        
        int payload = binaryPayload[0];

        for (byte i = 1; i < binaryPayload.length; i++) {
            payload += binaryPayload[i] * (256 * i);
        }
        
        return payload;
    }
    
    private Packet buildPacket(byte[] binaryPacket) throws Exception {
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
        connectionStatusHandler.getSemaphore().setSize(packet.getPayload() / SRSC.MAX_PACKET_SIZE);
        connectionStatusHandler.resetConnection();
        System.out.println("Arrived CONNECT packet");

        try {
            packetWriter.writePacket(packetTypes.get(0x01), 0);
        } catch (Exception ex) {}
    }
    
    private void processConnackPacket(Packet packet) {
        connectionStatusHandler.getSemaphore().setSize(packet.getPayload() / SRSC.MAX_PACKET_SIZE);
        connectionStatusHandler.resetConnection();
        System.out.println("Arrived CONNACK packet");
    }
    
    private void processBinaryPacket(byte[] binaryPacket) {
        PacketType packetType = packetTypes.get(binaryPacket[0]);
        
        if (packetType == null) {
            System.out.println("Arrived packet with unknown type!");
            return;
        } else if (!connectionStatusHandler.isConnected() && packetType.getPacketTypeIdentifier() > 0x01) {
            return;
        }
        
        if (packetType.getPacketTypeIdentifier() == 0x02) {
            System.out.println("Arrived ACCEPTACK packet");
            connectionStatusHandler.getSemaphore().increase();

            return;
        }

        if (!validateChecksum(binaryPacket)) {
            System.out.println("Arrived corrupted packet!");

            return;
        }

        if (packetType.isCritical()) {                
            if (connectionStatusHandler.isAcceptedCriticalIdKnown(binaryPacket[2])) {
                return;
            } else {
                connectionStatusHandler.registerAcceptedCriticalId(binaryPacket[2]);
            }
        }

        try {
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
                    packetWriter.writeAcceptackPacket();
                    break;
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
    
    public PacketReader(SerialPort port, ConnectionStatusHandler connectionStatusHandler, HashMap<Byte, PacketType> packetTypes, PacketWriter packetWriter) {
        this.port = port;
        this.connectionStatusHandler = connectionStatusHandler;
        this.packetTypes = packetTypes;
        this.packetWriter = packetWriter;
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
