// SPDX-License-Identifier: MIT

package srsc;

import srsc.packet.PacketType;
import com.fazecast.jSerialComm.SerialPort;
import java.util.HashMap;
import srsc.exceptions.MissingPayloadException;
import srsc.exceptions.SerialBufferFullException;
import srsc.exceptions.UnknownPacketTypeException;
import srsc.exceptions.UnknownPortException;
import srsc.packet.PayloadSize;

/**
 * SRSC protocol communication API.
 *
 * @author atlas144
 */
public class SRSC {
    
    private final SerialPort port;
    private final ConnectionStatusHandler connectionStatusHandler;
    private final HashMap<Integer, PacketType> packetTypes;
    private final PacketReader packetReader;
    private final PacketWriter packetWriter;
    
    /**
     * Maximal transmitted packet size (protocol defines as 7 b).
     */
    public static final byte MAX_PACKET_SIZE = 7;
    
    /**
     * Builds SRSC API object.
     * @param port serial port number to which the communication opponent 
     * is connected 
     * @throws UnknownPortException thrown if it is not an active serial port 
     * number
     */
    public SRSC(byte port) throws UnknownPortException {
        connectionStatusHandler = new ConnectionStatusHandler();
        packetTypes = new HashMap<>();
        final SerialPort[] ports = SerialPort.getCommPorts();
        
        if (port >= ports.length) {
            throw new UnknownPortException(port);
        } else {
            this.port = ports[port];
        }
        
        packetWriter = new PacketWriter(this.port, connectionStatusHandler);
        packetReader = new PacketReader(this.port, connectionStatusHandler, packetTypes, packetWriter);
        
        packetTypes.put(0x00, new PacketType((byte) 0x00, PayloadSize.INT));
        packetTypes.put(0x01, new PacketType((byte) 0x01, PayloadSize.INT));
        packetTypes.put(0x02, new PacketType((byte) 0x02, PayloadSize.COMMAND));
    }
    
    /**
     * Starts connection with opponent.
     */
    public void begin() {
        port.openPort();
        port.addDataListener(packetReader);
           
        Thread connectionThread = new Thread(() -> {
            try {
                System.out.print("Connecting");

                while (!connectionStatusHandler.isConnected()) {    
                    try {
                        System.out.println("Sending CONNECT packet");
                        packetWriter.writePacket(packetTypes.get(0x00), 0);
                        Thread.sleep(1000);
                    } catch (SerialBufferFullException ex) {
                        System.out.println("\nOposite serial buffer is full - waiting for 5000 ms");
                        Thread.sleep(5000);
                    }
                }
                
                System.out.println();
            } catch (InterruptedException ex) {}
        });
        
        connectionThread.start();
    }
    
    /**
     * Sends packet with <i>packetType</i> and <i>payload</i>.
     * @param packetType type of packet. It must be registered first.
     * @param payload the message the packet carries. It must be a number that 
     * is less than or equal to the payload size for the packet type.
     * @throws UnknownPacketTypeException thrown if packet type has not been 
     * registered yet
     * @throws SerialBufferFullException thrown if the serial buffer 
     * on the opponent side is full (the packet was probably not delivered 
     * - no more messages should be sent)
     */
    public void writePacket(int packetType, int payload) throws UnknownPacketTypeException, SerialBufferFullException {
        PacketType packetTypeObject = packetTypes.get(packetType);
        
        if (packetTypeObject == null) {
            throw new UnknownPacketTypeException(packetType);
        }
        
        if (packetTypeObject.isCritical()) {
            for (int i = 0; i < 5; i++) {
                packetWriter.writePacket(packetTypeObject, connectionStatusHandler.useCriticalId(), payload);
            }
        } else {
            packetWriter.writePacket(packetTypeObject, payload);
        }
    }
    
    /**
     * Sends command packet with <i>packetType</i> (with no payload).
     * @param packetType type of packet. It must be registered first.
     * @throws UnknownPacketTypeException thrown if packet type has not been 
     * registered yet
     * @throws SerialBufferFullException thrown if the serial buffer 
     * on the opponent side is full (the packet was probably not delivered 
     * - no more messages should be sent)
     * @throws MissingPayloadException thrown if the given packet type requires 
     * payload
     */
    public void writePacket(int packetType) throws UnknownPacketTypeException, SerialBufferFullException, MissingPayloadException {
        PacketType packetTypeObject = packetTypes.get(packetType);
        
        if (packetTypeObject == null) {
            throw new UnknownPacketTypeException(packetType);
        }
        
        if (packetTypeObject.isCritical()) {
            for (int i = 0; i < 5; i++) {
                packetWriter.writePacket(packetTypeObject, connectionStatusHandler.useCriticalId());
            }
        } else {
            packetWriter.writePacket(packetTypeObject);
        }
    }
    
    /**
     * Defines new packet type.
     * @param packetTypeIdentifier identifier of the packet (see protocol 
     * definition)
     * @param payloadSize 0/1/2/4 bytes
     * @param isCritical says whether to treat the packet as critical
     */
    public void definePacketType(int packetTypeIdentifier, PayloadSize payloadSize, boolean isCritical) {        
        packetTypes.put(packetTypeIdentifier, new PacketType((byte) packetTypeIdentifier, payloadSize, isCritical));
    }
    
    /**
     * Defines new non-critical packet type.
     * @param packetTypeIdentifier identifier of the packet (see protocol 
     * definition)
     * @param payloadSize 0/1/2/4 bytes
     */
    public void definePacketType(byte packetTypeIdentifier, PayloadSize payloadSize) {
        definePacketType(packetTypeIdentifier, payloadSize, false);
    }
    
    /**
     * Registers a callback that is called when a new packet is received.
     * @param callback function that processes the received packet
     */
    public void registerOnPacketArrivedCallback(PacketArrivedCallback callback) {
        packetReader.registerOnPacketArrivedCallback(callback);
    }
    
}
