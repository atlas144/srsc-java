// SPDX-License-Identifier: MIT

package srsc.packet;

import org.junit.Assert;
import org.junit.Test;
import srsc.exceptions.MissingPayloadException;

/**
 *
 * @author atlas144
 */
public class PacketTest {
    
    @Test
    public void testNoPayloadConstructorError() {
        PacketType testPacketType = new PacketType((byte) 0x00, PayloadSize.INT);
            
        Assert.assertThrows(MissingPayloadException.class, () -> {
            new Packet(testPacketType);
        });
    }
    
    @Test
    public void testGetPacketType() {
        PacketType testPacketType = null;
        Packet testPacket = null;
            
        try {
            testPacketType = new PacketType((byte) 0x00, PayloadSize.COMMAND);
            testPacket = new Packet(testPacketType);
        } catch (MissingPayloadException ex) {}
            
        Assert.assertEquals(testPacketType, testPacket.getPacketType());
    }
    
    @Test
    public void testGetPacketTypeIdentifier() {
        PacketType testPacketType = null;
        Packet testPacket = null;
            
        try {
            testPacketType = new PacketType((byte) 0x00, PayloadSize.COMMAND);
            testPacket = new Packet(testPacketType);
        } catch (MissingPayloadException ex) {}
            
        Assert.assertEquals(testPacketType.getPacketTypeIdentifier(), testPacket.getPacketTypeIdentifier());
    }
    
    @Test
    public void testGetPayloadSize() {
        PacketType testPacketType = null;
        Packet testPacket = null;
            
        try {
            testPacketType = new PacketType((byte) 0x00, PayloadSize.COMMAND);
            testPacket = new Packet(testPacketType);
        } catch (MissingPayloadException ex) {}
            
        Assert.assertEquals(testPacketType.getPayloadSize(), testPacket.getPayloadSize());
    }
    
    @Test
    public void testIsCritical() {
        final PacketType testPacketType = new PacketType((byte) 0x00, PayloadSize.INT, true);
        final int testPayload = 0x01020304;
        final Packet testPacket = new Packet(testPacketType, testPayload);
            
        Assert.assertTrue(testPacket.isCritical());
    }
    
    @Test
    public void testGetPayload() {
        final PacketType testPacketType = new PacketType((byte) 0x00, PayloadSize.INT);
        final int testPayload = 0x01020304;
        final Packet testPacket = new Packet(testPacketType, testPayload);
            
        Assert.assertEquals(testPayload, testPacket.getPayload());
    }
    
    @Test
    public void testGetBinaryPayload() {
        final PacketType testPacketType = new PacketType((byte) 0x00, PayloadSize.INT);
        final int testPayload = 0x01020304;
        final byte[] testBinaryPayload = {
            0x04,
            0x03,
            0x02,
            0x01
        };
        final Packet testPacket = new Packet(testPacketType, testPayload);
            
        Assert.assertArrayEquals(testBinaryPayload, testPacket.getBinaryPayload());
    }
    
}
