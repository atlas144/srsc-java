// SPDX-License-Identifier: MIT

package srsc;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.util.HashMap;
import srsc.exceptions.SerialBufferFullException;
import srsc.packet.Packet;
import srsc.packet.PacketType;

/**
 *
 * @author atlas144
 */
public class PacketReader implements SerialPortDataListener {

    private final SerialPort port;
    private final ConnectionStatusHandler connectionStatusHandler;
    private final HashMap<Integer, PacketType> packetTypes;
    private final PacketWriter packetWriter;
    private PacketArrivedCallback onPacketArrivedCallback;
    
    private void processConnectPacket(Packet packet) {
        connectionStatusHandler.getSemaphore().setSize(packet.getPayload() / SRSC.MAX_PACKET_SIZE);
        connectionStatusHandler.resetConnection();
        System.out.println("Arrived CONNECT packet");

        try {
            packetWriter.writePacket(packetTypes.get(0x01), 0);
        } catch (SerialBufferFullException ex) {}
    }
    
    private void processConnackPacket(Packet packet) {
        connectionStatusHandler.getSemaphore().setSize(packet.getPayload() / SRSC.MAX_PACKET_SIZE);
        connectionStatusHandler.resetConnection();
        System.out.println("Arrived CONNACK packet");
    }
    
    private void processAcceptackPacket() {
        connectionStatusHandler.getSemaphore().increase();
        System.out.println("Arrived ACCEPTACK packet");
    }
    
    private void processBinaryPacket(byte[] binaryPacket) {
        PacketType packetType = packetTypes.get((int) binaryPacket[0]);
        
        if (packetType != null) {
            if (connectionStatusHandler.isConnected() || packetType.getPacketTypeIdentifier() < 0x02) {
                if (packetType.getPacketTypeIdentifier() == 0x02) {
                    processAcceptackPacket();
                } else {
                    if (PacketProcessor.validateChecksum(binaryPacket)) {
                        if (packetType.isCritical()) {                
                            if (connectionStatusHandler.isAcceptedCriticalIdKnown(binaryPacket[2])) {
                                return;
                            } else {
                                connectionStatusHandler.registerAcceptedCriticalId(binaryPacket[2]);
                            }
                        }
                        
                        Packet packet = PacketProcessor.buildPacketObject(binaryPacket, packetTypes);

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
                    } else {
                        System.out.println("Arrived corrupted packet!");
                    }
                }
            }
        } else {
            System.out.println("Arrived packet with unknown type!");
        }
    }
    
    public PacketReader(SerialPort port, ConnectionStatusHandler connectionStatusHandler, HashMap<Integer, PacketType> packetTypes, PacketWriter packetWriter) {
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
