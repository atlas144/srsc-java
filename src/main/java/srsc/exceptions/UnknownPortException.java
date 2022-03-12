// SPDX-License-Identifier: MIT

package srsc.exceptions;

/**
 *
 * @author atlas144
 */
public class UnknownPortException extends Exception {
    
    public UnknownPortException(byte port) {
        super(String.format("Serial port %d does not exist!", port));
    }
    
}
