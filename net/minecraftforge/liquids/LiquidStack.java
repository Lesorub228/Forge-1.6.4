// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Deprecated
public class LiquidStack
{
    public final int itemID;
    public int amount;
    public final int itemMeta;
    public by extra;
    private String textureSheet;
    @SideOnly(Side.CLIENT)
    private ms renderingIcon;
    
    public LiquidStack(final int itemID, final int amount) {
        this(itemID, amount, 0);
    }
    
    public LiquidStack(final yc item, final int amount) {
        this(item.cv, amount, 0);
    }
    
    public LiquidStack(final aqz block, final int amount) {
        this(block.cF, amount, 0);
    }
    
    public LiquidStack(final int itemID, final int amount, final int itemDamage) {
        this.textureSheet = "/terrain.png";
        this.itemID = itemID;
        this.amount = amount;
        this.itemMeta = itemDamage;
    }
    
    public LiquidStack(final int itemID, final int amount, final int itemDamage, final by nbt) {
        this(itemID, amount, itemDamage);
        if (nbt != null) {
            this.extra = (by)nbt.b();
        }
    }
    
    public by writeToNBT(final by nbt) {
        nbt.a("Amount", this.amount);
        nbt.a("Id", (short)this.itemID);
        nbt.a("Meta", (short)this.itemMeta);
        final String name = LiquidDictionary.findLiquidName(this);
        if (name != null) {
            nbt.a("LiquidName", name);
        }
        if (this.extra != null) {
            nbt.a("extra", (cl)this.extra);
        }
        return nbt;
    }
    
    public LiquidStack copy() {
        return new LiquidStack(this.itemID, this.amount, this.itemMeta, this.extra);
    }
    
    public boolean isLiquidEqual(final LiquidStack other) {
        return other != null && this.itemID == other.itemID && this.itemMeta == other.itemMeta && ((this.extra != null) ? this.extra.equals((Object)other.extra) : (other.extra == null));
    }
    
    public boolean containsLiquid(final LiquidStack other) {
        return this.isLiquidEqual(other) && this.amount >= other.amount;
    }
    
    public boolean isLiquidEqual(final ye other) {
        return other != null && ((this.itemID == other.d && this.itemMeta == other.k()) || this.isLiquidEqual(LiquidContainerRegistry.getLiquidForFilledItem(other)));
    }
    
    public ye asItemStack() {
        final ye stack = new ye(this.itemID, 1, this.itemMeta);
        if (this.extra != null) {
            stack.e = (by)this.extra.b();
        }
        return stack;
    }
    
    public static LiquidStack loadLiquidStackFromNBT(final by nbt) {
        if (nbt == null) {
            return null;
        }
        final String liquidName = nbt.i("LiquidName");
        int itemID = nbt.d("Id");
        int itemMeta = nbt.d("Meta");
        final LiquidStack liquid = LiquidDictionary.getCanonicalLiquid(liquidName);
        if (liquid != null) {
            itemID = liquid.itemID;
            itemMeta = liquid.itemMeta;
        }
        else if (yc.g[itemID] == null) {
            return null;
        }
        final int amount = nbt.e("Amount");
        final LiquidStack liquidstack = new LiquidStack(itemID, amount, itemMeta);
        if (nbt.b("extra")) {
            liquidstack.extra = nbt.l("extra");
        }
        return (liquidstack.itemID == 0) ? null : liquidstack;
    }
    
    public String getTextureSheet() {
        return this.textureSheet;
    }
    
    public LiquidStack setTextureSheet(final String textureSheet) {
        this.textureSheet = textureSheet;
        return this;
    }
    
    @SideOnly(Side.CLIENT)
    public ms getRenderingIcon() {
        if (this.itemID == aqz.G.cF) {
            return apc.b("water");
        }
        if (this.itemID == aqz.I.cF) {
            return apc.b("lava");
        }
        return this.renderingIcon;
    }
    
    @SideOnly(Side.CLIENT)
    public LiquidStack setRenderingIcon(final ms icon) {
        this.renderingIcon = icon;
        return this;
    }
    
    @Override
    public final int hashCode() {
        return 31 * this.itemMeta + this.itemID;
    }
    
    @Override
    public final boolean equals(final Object ob) {
        if (ob instanceof LiquidStack) {
            final LiquidStack ls = (LiquidStack)ob;
            return ls.itemID == this.itemID && ls.itemMeta == this.itemMeta && ((this.extra != null) ? this.extra.equals((Object)ls.extra) : (ls.extra == null));
        }
        return false;
    }
    
    public LiquidStack canonical() {
        return LiquidDictionary.getCanonicalLiquid(this);
    }
}
