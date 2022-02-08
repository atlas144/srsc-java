// SPDX-License-Identifier: MIT

package srsc;

import srsc.packet.Packet;

/**
 *
 * @author atlas144
 */
public interface PacketArrivedCallback {
    
    public void onPacketArrived(Packet packet);
    
}
