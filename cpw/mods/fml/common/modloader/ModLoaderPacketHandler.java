// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.network.IPacketHandler;

public class ModLoaderPacketHandler implements IPacketHandler
{
    private BaseModProxy mod;
    
    public ModLoaderPacketHandler(final BaseModProxy mod) {
        this.mod = mod;
    }
    
    @Override
    public void onPacketData(final cm manager, final ea packet, final Player player) {
        if (player instanceof jv) {
            this.mod.serverCustomPayload(((jv)player).a, packet);
        }
        else {
            ModLoaderHelper.sidedHelper.sendClientPacket(this.mod, packet);
        }
    }
}
