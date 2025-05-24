// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client.registry;

import cpw.mods.fml.common.registry.GameRegistry;

public class ClientRegistry
{
    public static void registerTileEntity(final Class<? extends asp> tileEntityClass, final String id, final bje specialRenderer) {
        GameRegistry.registerTileEntity(tileEntityClass, id);
        bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer);
    }
    
    public static void bindTileEntitySpecialRenderer(final Class<? extends asp> tileEntityClass, final bje specialRenderer) {
        bjd.a.m.put(tileEntityClass, specialRenderer);
        specialRenderer.a(bjd.a);
    }
}
