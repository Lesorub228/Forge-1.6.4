// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import cpw.mods.fml.common.FMLCommonHandler;

public class FakePlayer extends jv
{
    public FakePlayer(final abw world, final String name) {
        super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, name, new jw(world));
    }
    
    public void sendChatToPlayer(final String s) {
    }
    
    public boolean a(final int i, final String s) {
        return false;
    }
    
    public t b() {
        return new t(0, 0, 0);
    }
    
    public void a(final cv chatmessagecomponent) {
    }
    
    public void a(final ku par1StatBase, final int par2) {
    }
    
    public void openGui(final Object mod, final int modGuiId, final abw world, final int x, final int y, final int z) {
    }
    
    public boolean ar() {
        return true;
    }
    
    public boolean a(final uf player) {
        return false;
    }
    
    public void a(final nb source) {
    }
    
    public void l_() {
    }
    
    public void b(final int dim) {
    }
    
    public void a(final dp pkt) {
    }
}
