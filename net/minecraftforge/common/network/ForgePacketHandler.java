// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common.network;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.network.IPacketHandler;

public class ForgePacketHandler implements IPacketHandler
{
    @Override
    public void onPacketData(final cm network, final ea packet, final Player player) {
        final ForgePacket pkt = ForgePacket.readPacket(network, packet.c);
        if (pkt == null) {
            return;
        }
        pkt.execute(network, (uf)player);
    }
}
