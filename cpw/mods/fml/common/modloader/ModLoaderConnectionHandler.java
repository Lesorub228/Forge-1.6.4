// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.network.IConnectionHandler;

public class ModLoaderConnectionHandler implements IConnectionHandler
{
    private BaseModProxy mod;
    
    public ModLoaderConnectionHandler(final BaseModProxy mod) {
        this.mod = mod;
    }
    
    @Override
    public void playerLoggedIn(final Player player, final ez netHandler, final cm manager) {
        this.mod.onClientLogin((uf)player);
    }
    
    @Override
    public String connectionReceived(final jy netHandler, final cm manager) {
        return null;
    }
    
    @Override
    public void connectionOpened(final ez netClientHandler, final String server, final int port, final cm manager) {
        ModLoaderHelper.sidedHelper.clientConnectionOpened(netClientHandler, manager, this.mod);
    }
    
    @Override
    public void connectionClosed(final cm manager) {
        if (ModLoaderHelper.sidedHelper == null || !ModLoaderHelper.sidedHelper.clientConnectionClosed(manager, this.mod)) {
            this.mod.serverDisconnect();
            this.mod.onClientLogout(manager);
        }
    }
    
    @Override
    public void clientLoggedIn(final ez nh, final cm manager, final ep login) {
        this.mod.serverConnect(nh);
    }
    
    @Override
    public void connectionOpened(final ez netClientHandler, final MinecraftServer server, final cm manager) {
        ModLoaderHelper.sidedHelper.clientConnectionOpened(netClientHandler, manager, this.mod);
    }
}
