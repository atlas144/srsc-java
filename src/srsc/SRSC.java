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
 *
 * @author atlas144
 */
public class SRSC {
    
    private final SerialPort port;
    private final ConnectionStatusHandler connectionStatusHandler;
    private final HashMap<Byte, PacketType> packetTypes;
    private final PacketReader packetReader;
    private final PacketWriter packetWriter;
    
    public static final byte MAX_PACKET_SIZE = 7;
    
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
        
        packetTypes.put((byte) 0x00, new PacketType((byte) 0x00, PayloadSize.INT));
        packetTypes.put((byte) 0x01, new PacketType((byte) 0x01, PayloadSize.INT));
        packetTypes.put((byte) 0x02, new PacketType((byte) 0x02, PayloadSize.COMMAND));
    }
    
    public void begin() {
        port.openPort();
        port.addDataListener(packetReader);
           
        Thread connectionThread = new Thread(() -> {
            try {
                System.out.print("Connecting");

                while (!connectionStatusHandler.isConnected()) {    
                    try {
                        System.out.print(".");
                        packetWriter.writePacket(packetTypes.get(0x00), 0);
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        System.out.println("\nOposite serial buffer is full - waiting for 5000 ms");
                        Thread.sleep(5000);
                    }
                }
                
                System.out.println();
            } catch (InterruptedException ex) {}
        });
        
        connectionThread.start();
    }
    
    public void writePacket(byte packetType, int payload) throws UnknownPacketTypeException, SerialBufferFullException {
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
    
    public void writePacket(byte packetType) throws UnknownPacketTypeException, SerialBufferFullException, MissingPayloadException {
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
    
    public void definePacketType(byte packetTypeIdentifier, PayloadSize payloadSize, boolean isCritical) {        
        packetTypes.put(packetTypeIdentifier, new PacketType(packetTypeIdentifier, payloadSize, isCritical));
    }
    
    public void definePacketType(byte packetTypeIdentifier, PayloadSize payloadSize) {
        definePacketType(packetTypeIdentifier, payloadSize, false);
    }
    
    public void registerOnPacketArrivedCallback(PacketArrivedCallback callback) {
        packetReader.registerOnPacketArrivedCallback(callback);
    }
    
}
