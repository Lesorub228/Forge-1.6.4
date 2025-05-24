// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState;
import net.minecraft.server.MinecraftServer;

public class FMLServerAboutToStartEvent extends FMLStateEvent
{
    private MinecraftServer server;
    
    public FMLServerAboutToStartEvent(final Object... data) {
        super(data);
        this.server = (MinecraftServer)data[0];
    }
    
    @Override
    public LoaderState.ModState getModState() {
        return LoaderState.ModState.AVAILABLE;
    }
    
    public MinecraftServer getServer() {
        return this.server;
    }
}
