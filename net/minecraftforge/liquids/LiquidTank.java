// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

@Deprecated
public class LiquidTank implements ILiquidTank
{
    private LiquidStack liquid;
    private int capacity;
    private int tankPressure;
    private asp tile;
    
    public LiquidTank(final int capacity) {
        this(null, capacity);
    }
    
    public LiquidTank(final int liquidId, final int quantity, final int capacity) {
        this(new LiquidStack(liquidId, quantity), capacity);
    }
    
    public LiquidTank(final int liquidId, final int quantity, final int capacity, final asp tile) {
        this(liquidId, quantity, capacity);
        this.tile = tile;
    }
    
    public LiquidTank(final LiquidStack liquid, final int capacity) {
        this.liquid = liquid;
        this.capacity = capacity;
    }
    
    public LiquidTank(final LiquidStack liquid, final int capacity, final asp tile) {
        this(liquid, capacity);
        this.tile = tile;
    }
    
    @Override
    public LiquidStack getLiquid() {
        return this.liquid;
    }
    
    @Override
    public int getCapacity() {
        return this.capacity;
    }
    
    public void setLiquid(final LiquidStack liquid) {
        this.liquid = liquid;
    }
    
    public void setCapacity(final int capacity) {
        this.capacity = capacity;
    }
    
    @Override
    public int fill(final LiquidStack resource, final boolean doFill) {
        if (resource == null || resource.itemID <= 0) {
            return 0;
        }
        if (this.liquid == null || this.liquid.itemID <= 0) {
            if (resource.amount <= this.capacity) {
                if (doFill) {
                    this.liquid = resource.copy();
                }
                return resource.amount;
            }
            if (doFill) {
                this.liquid = resource.copy();
                this.liquid.amount = this.capacity;
                if (this.tile != null) {
                    LiquidEvent.fireEvent(new LiquidEvent.LiquidFillingEvent(this.liquid, this.tile.k, this.tile.l, this.tile.m, this.tile.n, this));
                }
            }
            return this.capacity;
        }
        else {
            if (!this.liquid.isLiquidEqual(resource)) {
                return 0;
            }
            final int space = this.capacity - this.liquid.amount;
            if (resource.amount <= space) {
                if (doFill) {
                    final LiquidStack liquid = this.liquid;
                    liquid.amount += resource.amount;
                }
                return resource.amount;
            }
            if (doFill) {
                this.liquid.amount = this.capacity;
            }
            return space;
        }
    }
    
    @Override
    public LiquidStack drain(final int maxDrain, final boolean doDrain) {
        if (this.liquid == null || this.liquid.itemID <= 0) {
            return null;
        }
        if (this.liquid.amount <= 0) {
            return null;
        }
        int used = maxDrain;
        if (this.liquid.amount < used) {
            used = this.liquid.amount;
        }
        if (doDrain) {
            final LiquidStack liquid = this.liquid;
            liquid.amount -= used;
        }
        final LiquidStack drained = new LiquidStack(this.liquid.itemID, used, this.liquid.itemMeta);
        if (this.liquid.amount <= 0) {
            this.liquid = null;
        }
        if (doDrain && this.tile != null) {
            LiquidEvent.fireEvent(new LiquidEvent.LiquidDrainingEvent(drained, this.tile.k, this.tile.l, this.tile.m, this.tile.n, this));
        }
        return drained;
    }
    
    @Override
    public int getTankPressure() {
        return this.tankPressure;
    }
    
    public void setTankPressure(final int pressure) {
        this.tankPressure = pressure;
    }
    
    public String getLiquidName() {
        return (this.liquid != null) ? LiquidDictionary.findLiquidName(this.liquid) : null;
    }
    
    public boolean containsValidLiquid() {
        return LiquidDictionary.findLiquidName(this.liquid) != null;
    }
    
    public by writeToNBT(final by nbt) {
        if (this.containsValidLiquid()) {
            this.liquid.writeToNBT(nbt);
        }
        else {
            nbt.a("emptyTank", "");
        }
        return nbt;
    }
    
    public LiquidTank readFromNBT(final by nbt) {
        if (!nbt.b("emptyTank")) {
            final LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbt);
            if (liquid != null) {
                this.setLiquid(liquid);
            }
        }
        return this;
    }
}
