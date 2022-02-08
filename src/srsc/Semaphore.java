// SPDX-License-Identifier: MIT

package srsc;

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
        counter++;
    }
    
    public void decrease() {
        counter--;
    }
    
}
