// SPDX-License-Identifier: MIT

package srsc.packet;

/**
 *
 * @author atlas144
 */
public enum PayloadSize {
    COMMAND(0),
    BYTE(1),
    SHORT(2),
    INT(4);
    
    private final byte value;
    
    private PayloadSize(int value) {
        this.value = (byte) value;
    }
    
    public byte getValue() {
        return value;
    }
}
