// SPDX-License-Identifier: MIT

package srsc;

import srsc.exceptions.SerialBufferFullException;

/**
 *
 * @author atlas144
 */
public class Semaphore {
    
    private int size;
    private int counter;
    
    public Semaphore(int size) {
        this.size = size;
        counter = size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getSpace() {
        return counter;
    }

    public void reset() {
        counter = size;
    }
    
    public void increase() {
        if (counter < size) counter++;
    }
    
    public void decrease() throws SerialBufferFullException {
        if (counter <= 0) {
            throw new SerialBufferFullException();
        }
        
        counter--;
    }
    
}
