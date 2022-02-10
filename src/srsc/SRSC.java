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
    private final ConnectionStatus connectionStatus;
    private final HashMap<Byte, PacketType> packetTypes;
    private final Semaphore semaphore;
    private final PacketReader packetReader;
    private final PacketWriter packetWriter;
    
    public static final byte MAX_PACKET_SIZE = 7;
    
    public SRSC(byte port) {
        connectionStatus = new ConnectionStatus();
        packetTypes = new HashMap<>();
        semaphore = new Semaphore(64);
        final SerialPort[] ports = SerialPort.getCommPorts();
        
        if (port >= ports.length) {
            throw new IndexOutOfBoundsException("Given serial port number is not valid!");
        } else {
            this.port = ports[port];
        }
        
        packetWriter = new PacketWriter(this.port, connectionStatus);
        packetReader = new PacketReader(this.port, connectionStatus, packetTypes, semaphore, packetWriter);
        
        packetTypes.put((byte) 0, new PacketType((byte) 0, PayloadSize.INT));
        packetTypes.put((byte) 1, new PacketType((byte) 1, PayloadSize.INT));
        packetTypes.put((byte) 2, new PacketType((byte) 2, PayloadSize.COMMAND));
    }
    
    public void begin() {
        port.openPort();
                
        try {
            while (!connectionStatus.isConnected()) {    
                packetWriter.writePacket(packetTypes.get(0x00), 0);
                Thread.sleep(500);
            }
        } catch (InterruptedException ex) {}
    }
    
    public void writePacket(PacketType packetType, int payload) {
        for (int i = 0; i < (packetType.isCritical() ? 5 : 1); i++) {
            packetWriter.writePacket(packetType, payload);
        }
    }
    
    public void writePacket(PacketType packetType) throws Exception {
        for (int i = 0; i < (packetType.isCritical() ? 5 : 1); i++) {
            packetWriter.writePacket(packetType);
        }
    }
    
    public void definePacketType(byte packetTypeIdentifier, PayloadSize payloadSize, boolean isCritical) throws Exception {
        if (packetTypes.containsKey(packetTypeIdentifier)) {
            throw new Exception("This packet type is already defined!");
        }
        
        packetTypes.put(packetTypeIdentifier, new PacketType(packetTypeIdentifier, payloadSize, isCritical));
    }
    
    public void definePacketType(byte packetTypeIdentifier, PayloadSize payloadSize) throws Exception {
        definePacketType(packetTypeIdentifier, payloadSize, false);
    }
    
    public void registerOnPacketArrivedCallback(PacketArrivedCallback callback) {
        packetReader.registerOnPacketArrivedCallback(callback);
    }
    
}
