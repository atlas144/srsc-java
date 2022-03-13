// SPDX-License-Identifier: MIT

package srsc.packet;

import srsc.PacketProcessor;
import srsc.exceptions.MissingPayloadException;

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
        binaryPayload = PacketProcessor.parsePayload(payload, packetType.getPayloadSize());
    }
    
    public Packet(PacketType packetType) throws MissingPayloadException {
        this(packetType, 0);
        
        if (packetType.getPayloadSize() != PayloadSize.COMMAND) {
            throw new MissingPayloadException(packetType.getPacketTypeIdentifier());
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
