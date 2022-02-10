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
    private final Semaphore semaphore;
    
    private byte countChecksum(Packet packet, byte id) {
        byte checksum = (byte) (packet.getPacketTypeIdentifier() + id);
        
        for (byte binaryByte : packet.getBinaryPayload()) {
            checksum += binaryByte;
        }
        
        return (byte) ~checksum;
    }
    
    public PacketWriter(SerialPort port, Semaphore semaphore) {
        this.port = port;
        this.semaphore = semaphore;
    }
    
    public void writePacket(Packet packet, byte id) throws Exception {
        byte payloadOffset = 2;
        byte binaryPacketSize = (byte) (payloadOffset + (packet.isCritical() ? 1 : 0) + packet.getPacketType().getPayloadSize().getValue());
        byte[] binaryPacket = new byte[binaryPacketSize];
        
        binaryPacket[0] = packet.getPacketTypeIdentifier();
        binaryPacket[1] = countChecksum(packet, id);
        
        if (packet.isCritical()) {
            binaryPacket[2] = id;
            payloadOffset = 3;
        }
        
        System.arraycopy(packet.getBinaryPayload(), 0, binaryPacket, payloadOffset, packet.getPayloadSize().getValue());
        
        semaphore.decrease();
        port.writeBytes(binaryPacket, binaryPacketSize);
    }
    
    public void writePacket(Packet packet) throws Exception {
        writePacket(packet, (byte) 0);
    }
    
    public void writePacket(PacketType packetType, byte id, int payload) throws Exception {
        writePacket(new Packet(packetType, payload), id);
    }
    
    public void writePacket(PacketType packetType, int payload) throws Exception {
        writePacket(new Packet(packetType, payload));
    }
    
    public void writePacket(PacketType packetType, byte id) throws Exception {
        writePacket(new Packet(packetType), id);
    }
    
    public void writePacket(PacketType packetType) throws Exception {
        writePacket(new Packet(packetType));
    }
    
}
