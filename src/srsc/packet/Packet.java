// SPDX-License-Identifier: MIT

package srsc.packet;

/**
 *
 * @author atlas144
 */
public class Packet {
    
    private final PacketType packetType;
    private final int payload;
    private final byte[] binaryPayload;
    
    public Packet(PacketType packetType, int payload) {
        this.packetType = packetType;
        this.payload = payload;
        binaryPayload = new byte[packetType.getPayloadSize().getValue()];
        
        for (byte i = 0; i < binaryPayload.length; i++) {
            binaryPayload[i] = (byte) (payload >> (i * 8));
        }
    }
    
    public Packet(PacketType packetType) throws IllegalArgumentException {
        this(packetType, 0);
        
        if (packetType.getPayloadSize() == PayloadSize.COMMAND) {
            throw new IllegalArgumentException(String.format("Packet with type %d must have payload!", packetType.getPacketTypeIdentifier()));
        }
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

    public int getPayload() {
        return payload;
    }
    
    public byte[] getBinaryPayload() {
        return binaryPayload;
    }
    
}
