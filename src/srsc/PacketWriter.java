// SPDX-License-Identifier: MIT

package srsc;

import com.fazecast.jSerialComm.SerialPort;
import srsc.packet.Packet;
import srsc.packet.PacketType;

/**
 *
 * @author atlas144
 */
public class PacketWriter {
    
    private final SerialPort port;
    private final ConnectionStatus connectionStatus;
    
    public PacketWriter(SerialPort port, ConnectionStatus connectionStatus) {
        this.port = port;
        this.connectionStatus = connectionStatus;
    }
    
    public void writePacket(PacketType packetType, int payload) {
        
    }
    
    public void writePacket(Packet packet) {
        
    }
    
}
