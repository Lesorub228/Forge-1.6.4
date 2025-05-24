// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import java.util.Random;
import cpw.mods.fml.common.TickType;

public interface BaseModProxy
{
    void modsLoaded();
    
    void load();
    
    String getName();
    
    String getPriorities();
    
    String getVersion();
    
    boolean doTickInGUI(final TickType p0, final boolean p1, final Object... p2);
    
    boolean doTickInGame(final TickType p0, final boolean p1, final Object... p2);
    
    void generateSurface(final abw p0, final Random p1, final int p2, final int p3);
    
    void generateNether(final abw p0, final Random p1, final int p2, final int p3);
    
    int addFuel(final int p0, final int p1);
    
    void takenFromCrafting(final uf p0, final ye p1, final mo p2);
    
    void takenFromFurnace(final uf p0, final ye p1);
    
    void onClientLogout(final cm p0);
    
    void onClientLogin(final uf p0);
    
    void serverDisconnect();
    
    void serverConnect(final ez p0);
    
    void receiveCustomPacket(final ea p0);
    
    void clientChat(final String p0);
    
    void onItemPickup(final uf p0, final ye p1);
    
    void serverCustomPayload(final ka p0, final ea p1);
    
    void serverChat(final ka p0, final String p1);
}
