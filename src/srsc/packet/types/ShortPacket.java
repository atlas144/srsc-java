// SPDX-License-Identifier: MIT

package srsc.packet.types;

import srsc.packet.PacketType;
import srsc.packet.PayloadPacket;

/**
 *
 * @author atlas144
 */
public class ShortPacket extends PayloadPacket {
    
    private final short payload;
    
    public ShortPacket(PacketType packetType, short payload) {
        super(packetType);
        this.payload = payload;
        
        for (byte i = 0; i < binaryPayload.length; i++) {
            binaryPayload[i] = (byte) (payload >> (i * 8));
        }
    }

    public short getPayload() {
        return payload;
    }
    
}
