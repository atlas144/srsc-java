// SPDX-License-Identifier: MIT

package srsc.packet;

/**
 *
 * @author atlas144
 */
public abstract class Packet {
    
    protected final PacketType packetType;
    
    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public byte getPacketTypeIdentifier() {
        return packetType.getPacketTypeIdentifier();
    }

    public PayloadSize getPayloadSize() {
        return packetType.getPayloadSize();
    }
    
    public boolean isCritical() {
        return packetType.isCritical();
    }
    
}
