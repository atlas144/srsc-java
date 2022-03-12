// SPDX-License-Identifier: MIT

package srsc.exceptions;

/**
 *
 * @author atlas144
 */
public class UnknownPacketTypeException extends Exception {
    
    public UnknownPacketTypeException(int packetType) {
        super(String.format("Packet type %d is nod defined!", packetType));
    }
    
}
