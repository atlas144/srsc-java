// SPDX-License-Identifier: MIT

package srsc;

import org.junit.Assert;
import org.junit.Test;
import srsc.exceptions.SerialBufferFullException;

/**
 *
 * @author atlas144
 */
public class SemaphoreTest {
    
    @Test
    public void testGetSize() {
        final int testSize = 2;
        final Semaphore semaphore = new Semaphore(testSize);
        
        Assert.assertEquals(testSize, semaphore.getSize());
    }
    
    @Test
    public void testSetSize() {
        final int testInitialSize = 1;
        final int testSize = 2;
        final Semaphore semaphore = new Semaphore(testInitialSize);
        
        semaphore.setSize(testSize);
        
        Assert.assertEquals(testSize, semaphore.getSize());
    }
    
    @Test
    public void testSetSizeUnlimited() {
        final int testInitialSize = 1;
        final int testSize = 0;
        final Semaphore semaphore = new Semaphore(testInitialSize);
        
        semaphore.setSize(testSize);
        
        Assert.assertEquals(testSize, semaphore.getSize());
    }
    
    @Test
    public void testGetSpace() {
        final int testSpace = 2;
        final Semaphore semaphore = new Semaphore(testSpace);
        
        Assert.assertEquals(testSpace, semaphore.getSpace());
    }
    
    @Test
    public void testDecrease() {
        final int testSpace = 5;
        final int decrease = 3;
        final Semaphore semaphore = new Semaphore(testSpace);
        
        for (int i = 0; i < decrease; i++) {
            try {
                semaphore.decrease();
            } catch (SerialBufferFullException ex) {}
        }
        
        Assert.assertEquals(testSpace - decrease, semaphore.getSpace());
    }
    
    @Test
    public void testDecreaseUnlimited() {
        final int testSpace = 0;
        final int decrease = 3;
        final Semaphore semaphore = new Semaphore(testSpace);
        
        for (int i = 0; i < decrease; i++) {
            try {
                semaphore.decrease();
            } catch (SerialBufferFullException ex) {}
        }
        
        Assert.assertEquals(testSpace, semaphore.getSpace());
    }
    
    @Test
    public void testDecreaseWithEmptyCounter() {
        final int testSpace = 3;
        final int decrease = 3;
        final Semaphore semaphore = new Semaphore(testSpace);
        
        for (int i = 0; i < decrease; i++) {
            try {
                semaphore.decrease();
            } catch (SerialBufferFullException ex) {}
        }
        
        Assert.assertThrows(SerialBufferFullException.class, () -> {
            semaphore.decrease();
        });
    }
    
    @Test
    public void testIncrease() {
        final int testSpace = 5;
        final int decrease = 3;
        final int increase = 2;
        final Semaphore semaphore = new Semaphore(testSpace);
        
        for (int i = 0; i < decrease; i++) {
            try {
                semaphore.decrease();
            } catch (SerialBufferFullException ex) {}
        }
        
        for (int i = 0; i < increase; i++) {
            semaphore.increase();
        }
        
        Assert.assertEquals(testSpace - decrease + increase, semaphore.getSpace());
    }
    
    @Test
    public void testIncreaseUnlimited() {
        final int testSpace = 0;
        final int decrease = 3;
        final int increase = 2;
        final Semaphore semaphore = new Semaphore(testSpace);
        
        for (int i = 0; i < decrease; i++) {
            try {
                semaphore.decrease();
            } catch (SerialBufferFullException ex) {}
        }
        
        for (int i = 0; i < increase; i++) {
            semaphore.increase();
        }
        
        Assert.assertEquals(testSpace, semaphore.getSpace());
    }
    
    @Test
    public void testIncreaseWithFullCounter() {
        final int testSpace = 5;
        final Semaphore semaphore = new Semaphore(testSpace);
        
        semaphore.increase();
        
        Assert.assertEquals(testSpace, semaphore.getSpace());
    }
    
    @Test
    public void testReset() {
        final int testSpace = 5;
        final int decrease = 3;
        final Semaphore semaphore = new Semaphore(testSpace);
        
        for (int i = 0; i < decrease; i++) {
            try {
                semaphore.decrease();
            } catch (SerialBufferFullException ex) {}
        }
        
        semaphore.reset();
        
        Assert.assertEquals(testSpace, semaphore.getSpace());
    }
    
}
