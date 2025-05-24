// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.FMLCommonHandler;

public class PacketDispatcher
{
    public static ea getPacket(final String type, final byte[] data) {
        return new ea(type, data);
    }
    
    public static void sendPacketToServer(final ey packet) {
        FMLCommonHandler.instance().getSidedDelegate().sendPacket(packet);
    }
    
    public static void sendPacketToPlayer(final ey packet, final Player player) {
        if (player instanceof jv) {
            ((jv)player).a.b(packet);
        }
    }
    
    public static void sendPacketToAllAround(final double X, final double Y, final double Z, final double range, final int dimensionId, final ey packet) {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            server.af().a(X, Y, Z, range, dimensionId, packet);
        }
        else {
            FMLLog.fine("Attempt to send packet to all around without a server instance available", new Object[0]);
        }
    }
    
    public static void sendPacketToAllInDimension(final ey packet, final int dimId) {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            server.af().a(packet, dimId);
        }
        else {
            FMLLog.fine("Attempt to send packet to all in dimension without a server instance available", new Object[0]);
        }
    }
    
    public static void sendPacketToAllPlayers(final ey packet) {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            server.af().a(packet);
        }
        else {
            FMLLog.fine("Attempt to send packet to all in dimension without a server instance available", new Object[0]);
        }
    }
    
    public static dr getTinyPacket(final Object mod, final short tag, final byte[] data) {
        final NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mod);
        return new dr((short)nmh.getNetworkId(), tag, data);
    }
}
