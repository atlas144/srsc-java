// SPDX-License-Identifier: MIT

package srsc.exceptions;

/**
 *
 * @author atlas144
 */
public class PayloadParsingException extends Exception {
    
    public PayloadParsingException() {
        super("Packet payload parsing failed!");
    }
    
}
