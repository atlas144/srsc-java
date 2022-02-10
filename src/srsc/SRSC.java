// SPDX-License-Identifier: MIT

package srsc;

import srsc.packet.PacketType;
import com.fazecast.jSerialComm.SerialPort;
import java.util.HashMap;
import srsc.packet.PayloadSize;

/**
 *
 * @author atlas144
 */
public class SRSC {
    
    private final SerialPort port;
    private final ConnectionStatusHandler connectionStatusHandler;
    private final HashMap<Byte, PacketType> packetTypes;
    private final PacketReader packetReader;
    private final PacketWriter packetWriter;
    
    public static final byte MAX_PACKET_SIZE = 7;
    
    public SRSC(byte port) {
        connectionStatusHandler = new ConnectionStatusHandler();
        packetTypes = new HashMap<>();
        final SerialPort[] ports = SerialPort.getCommPorts();
        
        if (port >= ports.length) {
            throw new IndexOutOfBoundsException("Given serial port number is not valid!");
        } else {
            this.port = ports[port];
        }
        
        packetWriter = new PacketWriter(this.port, connectionStatusHandler);
        packetReader = new PacketReader(this.port, connectionStatusHandler, packetTypes, packetWriter);
        
        packetTypes.put((byte) 0x00, new PacketType((byte) 0x00, PayloadSize.INT));
        packetTypes.put((byte) 0x01, new PacketType((byte) 0x01, PayloadSize.INT));
        packetTypes.put((byte) 0x02, new PacketType((byte) 0x02, PayloadSize.COMMAND));
    }
    
    public void begin() {
        port.openPort();
        port.addDataListener(packetReader);
           
        Thread connectionThread = new Thread(() -> {
            try {
                System.out.print("Connecting");

                while (!connectionStatusHandler.isConnected()) {    
                    try {
                        System.out.print(".");
                        packetWriter.writePacket(packetTypes.get(0x00), 0);
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        System.out.println("\nOposite serial buffer is full - waiting for 5000 ms");
                        Thread.sleep(5000);
                    }
                }
                
                System.out.println();
            } catch (InterruptedException ex) {}
        });
        
        connectionThread.start();
    }
    
    public void writePacket(byte packetType, int payload) throws Exception {
        PacketType packetTypeObject = packetTypes.get(packetType);
        
        if (packetTypeObject == null) {
            throw new Exception(String.format("Packet type %d is nod defined!", packetType));
        }
        
        if (packetTypeObject.isCritical()) {
            for (int i = 0; i < 5; i++) {
                packetWriter.writePacket(packetTypeObject, connectionStatusHandler.useCriticalId(), payload);
            }
        } else {
            packetWriter.writePacket(packetTypeObject, payload);
        }
    }
    
    public void writePacket(byte packetType) throws Exception {
        PacketType packetTypeObject = packetTypes.get(packetType);
        
        if (packetTypeObject == null) {
            throw new Exception(String.format("Packet type %d is nod defined!", packetType));
        }
        
        if (packetTypeObject.isCritical()) {
            for (int i = 0; i < 5; i++) {
                packetWriter.writePacket(packetTypeObject, connectionStatusHandler.useCriticalId());
            }
        } else {
            packetWriter.writePacket(packetTypeObject);
        }
    }
    
    public void definePacketType(byte packetTypeIdentifier, PayloadSize payloadSize, boolean isCritical) {        
        packetTypes.put(packetTypeIdentifier, new PacketType(packetTypeIdentifier, payloadSize, isCritical));
    }
    
    public void definePacketType(byte packetTypeIdentifier, PayloadSize payloadSize) {
        definePacketType(packetTypeIdentifier, payloadSize, false);
    }
    
    public void registerOnPacketArrivedCallback(PacketArrivedCallback callback) {
        packetReader.registerOnPacketArrivedCallback(callback);
    }
    
}
