// SPDX-License-Identifier: MIT

package srsc;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author atlas144
 */
public class ConnectionStatusHandlerTest {
    
    @Test
    public void testGetSemaphore() {
        final ConnectionStatusHandler connectionStatusHandler = new ConnectionStatusHandler();
        
        Assert.assertNotNull(connectionStatusHandler.getSemaphore());
    }
    
    @Test
    public void testAcceptedCriticalIdKnown() {
        final ConnectionStatusHandler connectionStatusHandler = new ConnectionStatusHandler();
        
        connectionStatusHandler.registerAcceptedCriticalId((byte) 5);
        
        Assert.assertTrue(connectionStatusHandler.isAcceptedCriticalIdKnown((byte) 5));
    }
    
    @Test
    public void testAcceptedCriticalIdNotKnown() {
        final ConnectionStatusHandler connectionStatusHandler = new ConnectionStatusHandler();
        
        Assert.assertFalse(connectionStatusHandler.isAcceptedCriticalIdKnown((byte) 5));
    }
    
    @Test
    public void testConnect() {
        final ConnectionStatusHandler connectionStatusHandler = new ConnectionStatusHandler();
                
        connectionStatusHandler.connect();
        
        Assert.assertTrue(connectionStatusHandler.isConnected());
    }
    
    @Test
    public void testDisconnect() {
        final ConnectionStatusHandler connectionStatusHandler = new ConnectionStatusHandler();

        connectionStatusHandler.connect();
        connectionStatusHandler.disconnect();
        
        Assert.assertFalse(connectionStatusHandler.isConnected());
    }
    
    @Test
    public void testUseCriticalIdCommon() {
        final int testCounter = 5;
        final ConnectionStatusHandler connectionStatusHandler = new ConnectionStatusHandler();

        for (int i = 0; i < testCounter; i++) {
            connectionStatusHandler.useCriticalId();
        }
        
        Assert.assertEquals(testCounter, connectionStatusHandler.useCriticalId());
    }
    
    @Test
    public void testUseCriticalIdRestart() {
        final int testCounter = 256;
        final ConnectionStatusHandler connectionStatusHandler = new ConnectionStatusHandler();

        for (int i = 0; i < testCounter; i++) {
            connectionStatusHandler.useCriticalId();
        }
        
        Assert.assertEquals(0, connectionStatusHandler.useCriticalId());
    }
    
    @Test
    public void testResetConnection() {
        final ConnectionStatusHandler connectionStatusHandler = new ConnectionStatusHandler();
        
        connectionStatusHandler.disconnect();
        connectionStatusHandler.useCriticalId();
        connectionStatusHandler.resetConnection();
        
        Assert.assertTrue(connectionStatusHandler.isConnected());
        Assert.assertEquals(0, connectionStatusHandler.useCriticalId());
    }
    
}
