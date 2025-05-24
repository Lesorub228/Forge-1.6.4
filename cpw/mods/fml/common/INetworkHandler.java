// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

public interface INetworkHandler
{
    boolean onChat(final Object... p0);
    
    void onPacket250Packet(final Object... p0);
    
    void onServerLogin(final Object p0);
}
