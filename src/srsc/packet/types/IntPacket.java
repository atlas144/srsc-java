// SPDX-License-Identifier: MIT

package srsc.packet.types;

import srsc.packet.PacketType;
import srsc.packet.PayloadPacket;

/**
 *
 * @author atlas144
 */
public class IntPacket extends PayloadPacket {
    
    private final int payload;
    
    public IntPacket(PacketType packetType, int payload) {
        super(packetType);
        this.payload = payload;
        
        for (byte i = 0; i < binaryPayload.length; i++) {
            binaryPayload[i] = (byte) (payload >> (i * 8));
        }
    }

    public int getPayload() {
        return payload;
    }
    
}
