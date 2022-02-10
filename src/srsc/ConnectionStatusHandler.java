// SPDX-License-Identifier: MIT

package srsc;

/**
 *
 * @author atlas144
 */
public class ConnectionStatusHandler {
    
    private final Semaphore semaphore;
    private final byte[] acceptedCriticalIdentifiers;
    private boolean connected;
    private byte criticalPacketIdentifier;
    
    public ConnectionStatusHandler() {
        semaphore = new Semaphore(9);
        acceptedCriticalIdentifiers = new byte[10];
        connected = false;
        criticalPacketIdentifier = 0;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void registerAcceptedCriticalId(byte criticalPacketIdentifier) {
        acceptedCriticalIdentifiers[criticalPacketIdentifier % 10] = criticalPacketIdentifier;
    }
    
    public boolean isAcceptedCriticalIdKnown(byte criticalPacketIdentifier) {
        return acceptedCriticalIdentifiers[criticalPacketIdentifier % 10] == criticalPacketIdentifier;
    }

    public void connect() {
        connected = true;
    }

    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public byte useCriticalId() {
        return criticalPacketIdentifier++;
    }

    public void resetConnection() {
        semaphore.reset();
        
        for (byte acceptedCriticalIdentifier : acceptedCriticalIdentifiers) {
            acceptedCriticalIdentifier = (byte) 255;
        }
        
        connected = true;
        criticalPacketIdentifier = 0;
    }
    
}
