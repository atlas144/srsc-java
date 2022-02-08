// SPDX-License-Identifier: MIT

package srsc;

import srsc.packet.PacketType;
import srsc.packet.Packet;
import com.fazecast.jSerialComm.SerialPort;

/**
 *
 * @author atlas144
 */
public class SRSC {
    
    private final SerialPort port;
    
    public SRSC(byte port) {
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
    
}
