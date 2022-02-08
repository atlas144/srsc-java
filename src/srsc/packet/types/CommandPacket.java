// SPDX-License-Identifier: MIT

package srsc.packet.types;

import srsc.packet.Packet;
import srsc.packet.PacketType;
import srsc.packet.PayloadSize;

/**
 *
 * @author atlas144
 */
public class CommandPacket extends Packet {
    
    public CommandPacket(PacketType packetType) throws IllegalArgumentException {
        super(packetType);
        
        if (packetType.getPayloadSize() != PayloadSize.COMMAND) {
            throw new IllegalArgumentException("Packets that are not of type COMMAND (do not have length 0) must have payload!");
        }
    }
    
}
