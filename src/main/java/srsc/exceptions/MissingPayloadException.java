// SPDX-License-Identifier: MIT

package srsc.exceptions;

/**
 *
 * @author atlas144
 */
public class MissingPayloadException extends Exception {
    
    public MissingPayloadException(byte packetType) {
        super(String.format("Packet with type %d must have payload!", packetType));
    }
    
}
