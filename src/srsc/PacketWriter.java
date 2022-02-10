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
    
    private byte countChecksum(Packet packet, byte id) {
        byte checksum = (byte) (packet.getPacketTypeIdentifier() + id);
        
        for (byte binaryByte : packet.getBinaryPayload()) {
            checksum += binaryByte;
        }
        
        return (byte) ~checksum;
    }
    
    public PacketWriter(SerialPort port, ConnectionStatus connectionStatus) {
        this.port = port;
        this.connectionStatus = connectionStatus;
    }
    
    public void writePacket(Packet packet, byte id) {
        byte binaryPacketSize = (byte) (2 + (packet.isCritical() ? 1 : 0) + packet.getPacketType().getPayloadSize().getValue());
        byte[] binaryPacket = new byte[binaryPacketSize];
        byte payloadOffset = 2;
        
        binaryPacket[0] = packet.getPacketTypeIdentifier();
        binaryPacket[1] = countChecksum(packet, id);
        
        if (packet.isCritical()) {
            binaryPacket[2] = id;
            payloadOffset = 3;
        }
        
        System.arraycopy(packet.getBinaryPayload(), 0, binaryPacket, payloadOffset, packet.getPayloadSize().getValue());
        
        port.writeBytes(binaryPacket, binaryPacketSize);
    }
    
    public void writePacket(Packet packet) {
        writePacket(packet, (byte) 0);
    }
    
    public void writePacket(PacketType packetType, byte id, int payload) {
        writePacket(new Packet(packetType, payload), id);
    }
    
    public void writePacket(PacketType packetType, int payload) {
        writePacket(new Packet(packetType, payload));
    }
    
    public void writePacket(PacketType packetType, byte id) throws Exception {
        writePacket(new Packet(packetType), id);
    }
    
    public void writePacket(PacketType packetType) throws Exception {
        writePacket(new Packet(packetType));
    }
    
}
