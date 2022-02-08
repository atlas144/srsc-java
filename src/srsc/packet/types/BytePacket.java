// SPDX-License-Identifier: MIT

package srsc.packet.types;

import srsc.packet.*;

/**
 *
 * @author atlas144
 */
public class BytePacket extends PayloadPacket {
    
    private final byte payload;
    
    public BytePacket(PacketType packetType, byte payload) {
        super(packetType);
        this.payload = payload;
        
        for (byte i = 0; i < binaryPayload.length; i++) {
            binaryPayload[i] = (byte) (payload >> (i * 8));
        }
    }

    public byte getPayload() {
        return payload;
    }
    
}
