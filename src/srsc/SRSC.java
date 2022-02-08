// SPDX-License-Identifier: MIT

package srsc;

import srsc.packet.PacketType;
import srsc.packet.Packet;
import com.fazecast.jSerialComm.SerialPort;
import java.util.HashMap;
import srsc.packet.PayloadSize;

/**
 *
 * @author atlas144
 */
public class SRSC {
    
    private final SerialPort port;
    private final HashMap<Byte, PacketType> packetTypes;
    
    public SRSC(byte port) {
        packetTypes = new HashMap<>();
        final SerialPort[] ports = SerialPort.getCommPorts();
        
        if (port >= ports.length) {
            throw new IndexOutOfBoundsException("Given serial port number is not valid!");
        } else {
            this.port = ports[port];
        }
        
        packetTypes.put((byte) 0, new PacketType((byte) 0, PayloadSize.INT));
        packetTypes.put((byte) 1, new PacketType((byte) 1, PayloadSize.INT));
        packetTypes.put((byte) 2, new PacketType((byte) 2, PayloadSize.COMMAND));
        packetTypes.put((byte) 64, new PacketType((byte) 64, PayloadSize.BYTE));
        packetTypes.put((byte) 65, new PacketType((byte) 65, PayloadSize.SHORT));
        packetTypes.put((byte) 66, new PacketType((byte) 66, PayloadSize.INT));
        packetTypes.put((byte) 67, new PacketType((byte) 67, PayloadSize.BYTE, true));
        packetTypes.put((byte) 68, new PacketType((byte) 68, PayloadSize.SHORT, true));
        packetTypes.put((byte) 69, new PacketType((byte) 69, PayloadSize.INT, true));
    }
    
    public void begin() {
        port.openPort();
    }
    
    public Packet readPacket() {
        
    }
    
    public void writePacket(PacketType packetType, int payload) {
        
    }
    
    public void writePacket(PacketType packetType, short payload) {
        
    }
    
    public void writePacket(PacketType packetType, byte payload) {
        
    }
    
    public void writePacket(PacketType packetType) {
        
    }
    
    public void writeBinaryPacket(PacketType packetType, byte[] binaryPayload) {
        
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
    
}
