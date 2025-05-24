// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common.network;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraftforge.fluids.FluidIdMapPacket;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.network.IConnectionHandler;

public class ForgeConnectionHandler implements IConnectionHandler
{
    @Override
    public void playerLoggedIn(final Player player, final ez netHandler, final cm manager) {
        final ea[] fluidPackets = ForgePacket.makePacketSet(new FluidIdMapPacket());
        for (int i = 0; i < fluidPackets.length; ++i) {
            PacketDispatcher.sendPacketToPlayer((ey)fluidPackets[i], player);
        }
    }
    
    @Override
    public String connectionReceived(final jy netHandler, final cm manager) {
        return null;
    }
    
    @Override
    public void connectionOpened(final ez netClientHandler, final String server, final int port, final cm manager) {
    }
    
    @Override
    public void connectionOpened(final ez netClientHandler, final MinecraftServer server, final cm manager) {
    }
    
    @Override
    public void connectionClosed(final cm manager) {
    }
    
    @Override
    public void clientLoggedIn(final ez clientHandler, final cm manager, final ep login) {
    }
}
