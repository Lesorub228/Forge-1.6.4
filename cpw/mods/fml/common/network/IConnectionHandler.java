// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import net.minecraft.server.MinecraftServer;

public interface IConnectionHandler
{
    void playerLoggedIn(final Player p0, final ez p1, final cm p2);
    
    String connectionReceived(final jy p0, final cm p1);
    
    void connectionOpened(final ez p0, final String p1, final int p2, final cm p3);
    
    void connectionOpened(final ez p0, final MinecraftServer p1, final cm p2);
    
    void connectionClosed(final cm p0);
    
    void clientLoggedIn(final ez p0, final cm p1, final ep p2);
}
