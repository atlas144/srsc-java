// SPDX-License-Identifier: MIT

package srsc;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import srsc.exceptions.SerialBufferFullException;
import srsc.packet.Packet;
import srsc.packet.PacketType;
import srsc.packet.PayloadSize;

/**
 *
 * @author atlas144
 */
public class PacketReaderTest {
    
    private SerialPort testSerialPortMock;
    private SerialPortEvent testSerialPortEventMock;
    private PacketWriter testPacketWriterMock;
    private ConnectionStatusHandler testConnectionStatusHandlerSpy;
    private PacketReader testPacketReaderSpy;
    
    private HashMap<Integer, PacketType> testPacketTypes;

    @Before
    public void setup() {
        testPacketTypes = new HashMap<>();
        
        testPacketTypes.put(0x00, new PacketType((byte) 0x00, PayloadSize.INT));
        testPacketTypes.put(0x01, new PacketType((byte) 0x01, PayloadSize.INT));
        testPacketTypes.put(0x02, new PacketType((byte) 0x02, PayloadSize.COMMAND));
        testPacketTypes.put(0x0a, new PacketType((byte) 0x0a, PayloadSize.INT));
        testPacketTypes.put(0x0b, new PacketType((byte) 0x0b, PayloadSize.INT, true));
        testPacketTypes.put(0x7f, new PacketType((byte) 0x7f, PayloadSize.INT));
        
        testSerialPortMock = Mockito.mock(SerialPort.class);
        testSerialPortEventMock = Mockito.mock(SerialPortEvent.class);
        testPacketWriterMock = Mockito.mock(PacketWriter.class);
        testConnectionStatusHandlerSpy = Mockito.spy(new ConnectionStatusHandler());
        testPacketReaderSpy = Mockito.spy(new PacketReader(testSerialPortMock, testConnectionStatusHandlerSpy, testPacketTypes, testPacketWriterMock));
    }

    @Test
    public void testGetListeningEvent() {
        Assert.assertEquals(testPacketReaderSpy.getListeningEvents(), SerialPort.LISTENING_EVENT_DATA_AVAILABLE);
    }

    @Test
    public void testArrivedUnwatchedEvent() {
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_TIMED_OUT);
        
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Mockito.verify(testConnectionStatusHandlerSpy, Mockito.never()).isConnected();
    }
    
    @Test
    public void testProcessGeneralNoncriticalPacket() {
        PacketArrivedCallback testPacketArrivedCallback = Mockito.mock(PacketArrivedCallback.class);
        
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(6);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x0a;
            buffer[1] = 0x55;
            buffer[2] = 0x10;
            buffer[3] = 0x20;
            buffer[4] = 0x30;
            buffer[5] = 0x40;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);

        testConnectionStatusHandlerSpy.connect();
        testPacketReaderSpy.registerOnPacketArrivedCallback(testPacketArrivedCallback);
        
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Mockito.verify(testPacketArrivedCallback).onPacketArrived(Mockito.any(Packet.class));
    }
    
    @Test
    public void testProcessGeneralCriticalPacket() {
        PacketArrivedCallback testPacketArrivedCallback = Mockito.mock(PacketArrivedCallback.class);
        
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(7);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x0b;
            buffer[1] = 0x04;
            buffer[2] = 0x10;
            buffer[3] = 0x20;
            buffer[4] = 0x30;
            buffer[5] = 0x40;
            buffer[6] = 0x50;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);

        testConnectionStatusHandlerSpy.connect();
        testPacketReaderSpy.registerOnPacketArrivedCallback(testPacketArrivedCallback);
        
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Mockito.verify(testConnectionStatusHandlerSpy).registerAcceptedCriticalId((byte) 0x10);
        Mockito.verify(testPacketArrivedCallback).onPacketArrived(Mockito.any(Packet.class));
    }
    
    @Test
    public void testProcessGeneralRepeatingCriticalPacket() {
        PacketArrivedCallback testPacketArrivedCallback = Mockito.mock(PacketArrivedCallback.class);
        
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(7);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x0b;
            buffer[1] = 0x04;
            buffer[2] = 0x10;
            buffer[3] = 0x20;
            buffer[4] = 0x30;
            buffer[5] = 0x40;
            buffer[6] = 0x50;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);

        testConnectionStatusHandlerSpy.connect();
        testPacketReaderSpy.registerOnPacketArrivedCallback(testPacketArrivedCallback);
        
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Mockito.verify(testConnectionStatusHandlerSpy).registerAcceptedCriticalId((byte) 0x10);
        Mockito.verify(testPacketArrivedCallback).onPacketArrived(Mockito.any(Packet.class));
    }
    
    @Test
    public void testProcessConnectPacket() {
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(6);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x00;
            buffer[1] = 0x5f;
            buffer[2] = 0x10;
            buffer[3] = 0x20;
            buffer[4] = 0x30;
            buffer[5] = 0x40;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);
        
        Assert.assertFalse(testConnectionStatusHandlerSpy.isConnected());
        
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Assert.assertTrue(testConnectionStatusHandlerSpy.isConnected());
        Mockito.verify(testConnectionStatusHandlerSpy).getSemaphore(); //0x40302010
        Assert.assertEquals(153842251, testConnectionStatusHandlerSpy.getSemaphore().getSize());
        Mockito.verify(testConnectionStatusHandlerSpy).resetConnection();
        try {
            Mockito.verify(testPacketWriterMock).writePacket(testPacketTypes.get(0x01), 0);
        } catch (SerialBufferFullException ex) {}
    }
    
    @Test
    public void testProcessConnackPacket() {
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(6);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x01;
            buffer[1] = 0x5e;
            buffer[2] = 0x10;
            buffer[3] = 0x20;
            buffer[4] = 0x30;
            buffer[5] = 0x40;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);
        
        Assert.assertFalse(testConnectionStatusHandlerSpy.isConnected());
        
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Assert.assertTrue(testConnectionStatusHandlerSpy.isConnected());
        Mockito.verify(testConnectionStatusHandlerSpy).getSemaphore(); //0x40302010
        Assert.assertEquals(153842251, testConnectionStatusHandlerSpy.getSemaphore().getSize());
        Mockito.verify(testConnectionStatusHandlerSpy).resetConnection();
    }
    
    @Test
    public void testProcessAcceptackPacket() {
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(1);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x02;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);

        testConnectionStatusHandlerSpy.connect();
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Mockito.verify(testConnectionStatusHandlerSpy).getSemaphore();
    }
    
    @Test
    public void testUnknownTypePacket() {
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(6);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x0f;
            buffer[1] = 0x50;
            buffer[2] = 0x10;
            buffer[3] = 0x20;
            buffer[4] = 0x30;
            buffer[5] = 0x40;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);
        
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Mockito.verify(testConnectionStatusHandlerSpy, Mockito.never()).isConnected();
    }
    
    @Test
    public void testGeneralPacketBeforeConnection() {
        PacketArrivedCallback testPacketArrivedCallback = Mockito.mock(PacketArrivedCallback.class);
        
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(6);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x0a;
            buffer[1] = 0x55;
            buffer[2] = 0x10;
            buffer[3] = 0x20;
            buffer[4] = 0x30;
            buffer[5] = 0x40;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);
        
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Mockito.verify(testPacketArrivedCallback, Mockito.never()).onPacketArrived(Mockito.any(Packet.class));
    }
    
    @Test
    public void testGeneralPacketChecksumFailed() {
        Mockito.when(testSerialPortMock.bytesAvailable()).thenReturn(6);
        Mockito.when(testSerialPortMock.readBytes(Mockito.any(byte[].class), Mockito.anyLong())).thenAnswer((iom) -> {
            byte[] buffer = iom.getArgument(0, byte[].class);
            
            buffer[0] = 0x0a;
            buffer[1] = 0x0;
            buffer[2] = 0x10;
            buffer[3] = 0x20;
            buffer[4] = 0x30;
            buffer[5] = 0x40;
            
            return buffer.length;
        });
        Mockito.when(testSerialPortEventMock.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_AVAILABLE);
        
        testConnectionStatusHandlerSpy.connect();
        testPacketReaderSpy.serialEvent(testSerialPortEventMock);
        
        Mockito.verify(testPacketWriterMock, Mockito.never()).writeAcceptackPacket();
    }
    
}
