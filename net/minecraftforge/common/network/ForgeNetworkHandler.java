// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common.network;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraftforge.common.ForgeDummyContainer;
import cpw.mods.fml.common.network.NetworkModHandler;

public class ForgeNetworkHandler extends NetworkModHandler
{
    public ForgeNetworkHandler(final ForgeDummyContainer container) {
        super(container, container.getClass().getAnnotation(NetworkMod.class));
        this.configureNetworkMod(container);
    }
    
    @Override
    public boolean acceptVersion(final String version) {
        return true;
    }
}
