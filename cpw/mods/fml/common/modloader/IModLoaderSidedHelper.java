// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.network.EntitySpawnPacket;

public interface IModLoaderSidedHelper
{
    void finishModLoading(final ModLoaderModContainer p0);
    
    Object getClientGui(final BaseModProxy p0, final uf p1, final int p2, final int p3, final int p4, final int p5);
    
    nn spawnEntity(final BaseModProxy p0, final EntitySpawnPacket p1, final EntityRegistry.EntityRegistration p2);
    
    void sendClientPacket(final BaseModProxy p0, final ea p1);
    
    void clientConnectionOpened(final ez p0, final cm p1, final BaseModProxy p2);
    
    boolean clientConnectionClosed(final cm p0, final BaseModProxy p1);
}
