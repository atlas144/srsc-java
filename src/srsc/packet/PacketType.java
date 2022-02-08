// SPDX-License-Identifier: MIT

package srsc.packet;

/**
 *
 * @author atlas144
 */
public class PacketType {
    private final byte packetTypeIdentifier;
    private final PayloadSize payloadSize;
    private final boolean critical;
    
    public PacketType(byte packetTypeIdentifier, PayloadSize payloadSize, boolean critical) {
        this.packetTypeIdentifier = packetTypeIdentifier;
        this.payloadSize = payloadSize;
        this.critical = critical;
    }

    public byte getPacketTypeIdentifier() {
        return packetTypeIdentifier;
    }

    public PayloadSize getPayloadSize() {
        return payloadSize;
    }

    public boolean isCritical() {
        return critical;
    }

}
