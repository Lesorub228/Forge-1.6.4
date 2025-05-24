// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.IChatListener;

public class ModLoaderChatListener implements IChatListener
{
    private BaseModProxy mod;
    
    public ModLoaderChatListener(final BaseModProxy mod) {
        this.mod = mod;
    }
    
    @Override
    public dm serverChat(final ez handler, final dm message) {
        this.mod.serverChat((ka)handler, message.a);
        return message;
    }
    
    @Override
    public dm clientChat(final ez handler, final dm message) {
        this.mod.clientChat(message.a);
        return message;
    }
}
