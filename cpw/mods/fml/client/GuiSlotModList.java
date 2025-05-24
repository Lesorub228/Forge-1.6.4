// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import java.util.ArrayList;

public class GuiSlotModList extends GuiScrollingList
{
    private GuiModList parent;
    private ArrayList<ModContainer> mods;
    
    public GuiSlotModList(final GuiModList parent, final ArrayList<ModContainer> mods, final int listWidth) {
        super(parent.getMinecraftInstance(), listWidth, parent.h, 32, parent.h - 65 + 4, 10, 35);
        this.parent = parent;
        this.mods = mods;
    }
    
    @Override
    protected int getSize() {
        return this.mods.size();
    }
    
    @Override
    protected void elementClicked(final int var1, final boolean var2) {
        this.parent.selectModIndex(var1);
    }
    
    @Override
    protected boolean isSelected(final int var1) {
        return this.parent.modIndexSelected(var1);
    }
    
    @Override
    protected void drawBackground() {
        this.parent.e();
    }
    
    @Override
    protected int getContentHeight() {
        return this.getSize() * 35 + 1;
    }
    
    @Override
    protected void drawSlot(final int listIndex, final int var2, final int var3, final int var4, final bfq var5) {
        final ModContainer mc = this.mods.get(listIndex);
        if (Loader.instance().getModState(mc) == LoaderState.ModState.DISABLED) {
            this.parent.getFontRenderer().b(this.parent.getFontRenderer().a(mc.getName(), this.listWidth - 10), this.left + 3, var3 + 2, 16720418);
            this.parent.getFontRenderer().b(this.parent.getFontRenderer().a(mc.getDisplayVersion(), this.listWidth - 10), this.left + 3, var3 + 12, 16720418);
            this.parent.getFontRenderer().b(this.parent.getFontRenderer().a("DISABLED", this.listWidth - 10), this.left + 3, var3 + 22, 16720418);
        }
        else {
            this.parent.getFontRenderer().b(this.parent.getFontRenderer().a(mc.getName(), this.listWidth - 10), this.left + 3, var3 + 2, 16777215);
            this.parent.getFontRenderer().b(this.parent.getFontRenderer().a(mc.getDisplayVersion(), this.listWidth - 10), this.left + 3, var3 + 12, 13421772);
            this.parent.getFontRenderer().b(this.parent.getFontRenderer().a((mc.getMetadata() != null) ? mc.getMetadata().getChildModCountString() : "Metadata not found", this.listWidth - 10), this.left + 3, var3 + 22, 13421772);
        }
    }
}
