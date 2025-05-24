// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

public interface IPlayerTracker
{
    void onPlayerLogin(final uf p0);
    
    void onPlayerLogout(final uf p0);
    
    void onPlayerChangedDimension(final uf p0);
    
    void onPlayerRespawn(final uf p0);
}
