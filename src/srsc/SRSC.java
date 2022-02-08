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
