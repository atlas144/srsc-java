// SPDX-License-Identifier: MIT

package srsc;

import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import srsc.exceptions.PayloadParsingException;
import srsc.packet.PacketType;
import srsc.packet.PayloadSize;

/**
 *
 * @author atlas144
 */
public class PacketProcessorTest {
    
    @Test
    public void testConstructor() {
        final PacketProcessor testPacketProcessor = new PacketProcessor();
        
        Assert.assertNotNull(testPacketProcessor);
    }
    
    @Test
    public void testValidateChecksumSuccess() {
        final byte[] testBinaryPacket = {
            0x01,
            0x00,
            0x40,
            0x00,
            0x00,
            0x00
        };
        
        byte checksum = 0x00;
        
        for (byte packetByte : testBinaryPacket) {
            checksum += Byte.toUnsignedInt(packetByte);
        }
        
        testBinaryPacket[1] = (byte) ~checksum;
        
        Assert.assertTrue(PacketProcessor.validateChecksum(testBinaryPacket));
    }
    
    @Test
    public void testValidateChecksumFail() {
        final byte[] testBinaryPacket = {
            0x01,
            0x00,
            0x40,
            0x00,
            0x00,
            0x00
        };
        
        Assert.assertFalse(PacketProcessor.validateChecksum(testBinaryPacket));
    }
    
    @Test
    public void testParsePayload() {
        final int testPayload = 0x01020304;
        final byte[] testBinaryPayload = {
            0x04,
            0x03,
            0x02,
            0x01
        };
        
        Assert.assertArrayEquals(testBinaryPayload, PacketProcessor.parsePayload(testPayload, PayloadSize.INT));
    }
    
    @Test
    public void testParseBinaryPayload() {
        try {
            final byte[] testBinaryPayload =  {
                0x04,
                0x03,
                0x02,
                0x01
            };
            final int testPayload = 0x01020304;
            
            Assert.assertEquals(testPayload, PacketProcessor.parseBinaryPayload(testBinaryPayload));
        } catch (PayloadParsingException ex) {}
    }
    
    @Test
    public void testParseBinaryPayloadMissingPayload() {
        final byte[] testBinaryPayload = new byte[0];

        Assert.assertThrows(PayloadParsingException.class, () -> {
            PacketProcessor.parseBinaryPayload(testBinaryPayload);
        });
    }
    
    @Test
    public void testBuildPacketObject() {
        try {
            final HashMap<Integer, PacketType> testPacketTypes = new HashMap<>();
            final byte[] testBinaryPacket = {
                0x01,
                0x00,
                0x40,
                0x00,
                0x00,
                0x00
            };
            
            testPacketTypes.put(0x01, new PacketType((byte) 0x01, PayloadSize.INT));

            byte checksum = 0x00;
            
            for (byte packetByte : testBinaryPacket) {
                checksum += Byte.toUnsignedInt(packetByte);
            }
            
            testBinaryPacket[1] = (byte) ~checksum;
            
            Assert.assertNotNull(PacketProcessor.buildPacketObject(testBinaryPacket, testPacketTypes));
        } catch (PayloadParsingException ex) {}
    }
    
    @Test
    public void testBuildPacketObjectCommand() {
        try {
            final HashMap<Integer, PacketType> testPacketTypes = new HashMap<>();
            final byte[] testBinaryPacket = {
                0x05,
                0x00
            };
            
            testPacketTypes.put(0x05, new PacketType((byte) 0x05, PayloadSize.COMMAND));
                        
            byte checksum = 0x00;
            
            for (byte packetByte : testBinaryPacket) {
                checksum += Byte.toUnsignedInt(packetByte);
            }
            
            testBinaryPacket[1] = (byte) ~checksum;
            
            Assert.assertNotNull(PacketProcessor.buildPacketObject(testBinaryPacket, testPacketTypes));
        } catch (PayloadParsingException ex) {}
    }
    
    @Test
    public void testBuildPacketObjectCritical() {
        try {
            final HashMap<Integer, PacketType> testPacketTypes = new HashMap<>();
            final byte[] testBinaryPacket = {
                0x06,
                0x00,
                0x40,
                0x40,
                0x00,
                0x00,
                0x00
            };
            
            testPacketTypes.put(0x06, new PacketType((byte) 0x06, PayloadSize.INT, true));
            
            byte checksum = 0x00;
            
            for (byte packetByte : testBinaryPacket) {
                checksum += Byte.toUnsignedInt(packetByte);
            }
            
            testBinaryPacket[1] = (byte) ~checksum;
            
            Assert.assertNotNull(PacketProcessor.buildPacketObject(testBinaryPacket, testPacketTypes));
        } catch (PayloadParsingException ex) {}
    }

}
