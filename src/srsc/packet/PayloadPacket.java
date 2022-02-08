// SPDX-License-Identifier: MIT

package srsc.packet;

/**
 *
 * @author atlas144
 */
public abstract class PayloadPacket extends Packet {
    
    protected final byte[] binaryPayload;
    
    public PayloadPacket(PacketType packetType) {
        super(packetType);
        
        binaryPayload = new byte[packetType.getPayloadSize().getValue()];
    }
    
    public byte[] getBinaryPayload() {
        return binaryPayload;
    }
    
}
