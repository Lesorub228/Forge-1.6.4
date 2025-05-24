// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import java.util.Random;

public class BlockFluidClassic extends BlockFluidBase
{
    protected boolean[] isOptimalFlowDirection;
    protected int[] flowCost;
    protected FluidStack stack;
    
    public BlockFluidClassic(final int id, final Fluid fluid, final akc material) {
        super(id, fluid, material);
        this.isOptimalFlowDirection = new boolean[4];
        this.flowCost = new int[4];
        this.stack = new FluidStack(fluid, 1000);
    }
    
    public BlockFluidClassic setFluidStack(final FluidStack stack) {
        this.stack = stack;
        return this;
    }
    
    public BlockFluidClassic setFluidStackAmount(final int amount) {
        this.stack.amount = amount;
        return this;
    }
    
    @Override
    public int getQuantaValue(final acf world, final int x, final int y, final int z) {
        if (world.a(x, y, z) == 0) {
            return 0;
        }
        if (world.a(x, y, z) != this.cF) {
            return -1;
        }
        final int quantaRemaining = this.quantaPerBlock - world.h(x, y, z);
        return quantaRemaining;
    }
    
    @Override
    public boolean a(final int meta, final boolean fullHit) {
        return fullHit && meta == 0;
    }
    
    @Override
    public int getMaxRenderHeightMeta() {
        return 0;
    }
    
    @Override
    public int getLightValue(final acf world, final int x, final int y, final int z) {
        if (this.maxScaledLight == 0) {
            return super.getLightValue(world, x, y, z);
        }
        final int data = this.quantaPerBlock - world.h(x, y, z) - 1;
        return (int)(data / this.quantaPerBlockFloat * this.maxScaledLight);
    }
    
    public void a(final abw world, final int x, final int y, final int z, final Random rand) {
        int quantaRemaining = this.quantaPerBlock - world.h(x, y, z);
        int expQuanta = -101;
        if (quantaRemaining < this.quantaPerBlock) {
            final int y2 = y - this.densityDir;
            if (world.a(x, y2, z) == this.cF || world.a(x - 1, y2, z) == this.cF || world.a(x + 1, y2, z) == this.cF || world.a(x, y2, z - 1) == this.cF || world.a(x, y2, z + 1) == this.cF) {
                expQuanta = this.quantaPerBlock - 1;
            }
            else {
                int maxQuanta = -100;
                maxQuanta = this.getLargerQuanta((acf)world, x - 1, y, z, maxQuanta);
                maxQuanta = this.getLargerQuanta((acf)world, x + 1, y, z, maxQuanta);
                maxQuanta = this.getLargerQuanta((acf)world, x, y, z - 1, maxQuanta);
                maxQuanta = this.getLargerQuanta((acf)world, x, y, z + 1, maxQuanta);
                expQuanta = maxQuanta - 1;
            }
            if (expQuanta != quantaRemaining) {
                if ((quantaRemaining = expQuanta) <= 0) {
                    world.i(x, y, z);
                }
                else {
                    world.b(x, y, z, this.quantaPerBlock - expQuanta, 3);
                    world.a(x, y, z, this.cF, this.tickRate);
                    world.f(x, y, z, this.cF);
                }
            }
        }
        else if (quantaRemaining >= this.quantaPerBlock) {
            world.b(x, y, z, 0, 2);
        }
        if (this.canDisplace((acf)world, x, y + this.densityDir, z)) {
            this.flowIntoBlock(world, x, y + this.densityDir, z, 1);
            return;
        }
        int flowMeta = this.quantaPerBlock - quantaRemaining + 1;
        if (flowMeta >= this.quantaPerBlock) {
            return;
        }
        if (this.isSourceBlock((acf)world, x, y, z) || !this.isFlowingVertically((acf)world, x, y, z)) {
            if (world.a(x, y - this.densityDir, z) == this.cF) {
                flowMeta = 1;
            }
            final boolean[] flowTo = this.getOptimalFlowDirections(world, x, y, z);
            if (flowTo[0]) {
                this.flowIntoBlock(world, x - 1, y, z, flowMeta);
            }
            if (flowTo[1]) {
                this.flowIntoBlock(world, x + 1, y, z, flowMeta);
            }
            if (flowTo[2]) {
                this.flowIntoBlock(world, x, y, z - 1, flowMeta);
            }
            if (flowTo[3]) {
                this.flowIntoBlock(world, x, y, z + 1, flowMeta);
            }
        }
    }
    
    public boolean isFlowingVertically(final acf world, final int x, final int y, final int z) {
        return world.a(x, y + this.densityDir, z) == this.cF || (world.a(x, y, z) == this.cF && this.canFlowInto(world, x, y + this.densityDir, z));
    }
    
    public boolean isSourceBlock(final acf world, final int x, final int y, final int z) {
        return world.a(x, y, z) == this.cF && world.h(x, y, z) == 0;
    }
    
    protected boolean[] getOptimalFlowDirections(final abw world, final int x, final int y, final int z) {
        for (int side = 0; side < 4; ++side) {
            this.flowCost[side] = 1000;
            int x2 = x;
            final int y2 = y;
            int z2 = z;
            switch (side) {
                case 0: {
                    --x2;
                    break;
                }
                case 1: {
                    ++x2;
                    break;
                }
                case 2: {
                    --z2;
                    break;
                }
                case 3: {
                    ++z2;
                    break;
                }
            }
            if (this.canFlowInto((acf)world, x2, y2, z2)) {
                if (!this.isSourceBlock((acf)world, x2, y2, z2)) {
                    if (this.canFlowInto((acf)world, x2, y2 + this.densityDir, z2)) {
                        this.flowCost[side] = 0;
                    }
                    else {
                        this.flowCost[side] = this.calculateFlowCost(world, x2, y2, z2, 1, side);
                    }
                }
            }
        }
        int min = this.flowCost[0];
        for (int side2 = 1; side2 < 4; ++side2) {
            if (this.flowCost[side2] < min) {
                min = this.flowCost[side2];
            }
        }
        for (int side2 = 0; side2 < 4; ++side2) {
            this.isOptimalFlowDirection[side2] = (this.flowCost[side2] == min);
        }
        return this.isOptimalFlowDirection;
    }
    
    protected int calculateFlowCost(final abw world, final int x, final int y, final int z, final int recurseDepth, final int side) {
        int cost = 1000;
        for (int adjSide = 0; adjSide < 4; ++adjSide) {
            if ((adjSide != 0 || side != 1) && (adjSide != 1 || side != 0) && (adjSide != 2 || side != 3)) {
                if (adjSide != 3 || side != 2) {
                    int x2 = x;
                    final int y2 = y;
                    int z2 = z;
                    switch (adjSide) {
                        case 0: {
                            --x2;
                            break;
                        }
                        case 1: {
                            ++x2;
                            break;
                        }
                        case 2: {
                            --z2;
                            break;
                        }
                        case 3: {
                            ++z2;
                            break;
                        }
                    }
                    if (this.canFlowInto((acf)world, x2, y2, z2)) {
                        if (!this.isSourceBlock((acf)world, x2, y2, z2)) {
                            if (this.canFlowInto((acf)world, x2, y2 + this.densityDir, z2)) {
                                return recurseDepth;
                            }
                            if (recurseDepth < 4) {
                                final int min = this.calculateFlowCost(world, x2, y2, z2, recurseDepth + 1, adjSide);
                                if (min < cost) {
                                    cost = min;
                                }
                            }
                        }
                    }
                }
            }
        }
        return cost;
    }
    
    protected void flowIntoBlock(final abw world, final int x, final int y, final int z, final int meta) {
        if (meta < 0) {
            return;
        }
        if (this.displaceIfPossible(world, x, y, z)) {
            world.f(x, y, z, this.cF, meta, 3);
        }
    }
    
    protected boolean canFlowInto(final acf world, final int x, final int y, final int z) {
        if (world.c(x, y, z)) {
            return true;
        }
        final int bId = world.a(x, y, z);
        if (bId == this.cF) {
            return true;
        }
        if (this.displacementIds.containsKey(bId)) {
            return this.displacementIds.get(bId);
        }
        final akc material = aqz.s[bId].cU;
        if (material.c() || material == akc.h || material == akc.i || material == akc.D) {
            return false;
        }
        final int density = BlockFluidBase.getDensity(world, x, y, z);
        return density == Integer.MAX_VALUE || this.density > density;
    }
    
    protected int getLargerQuanta(final acf world, final int x, final int y, final int z, final int compare) {
        final int quantaRemaining = this.getQuantaValue(world, x, y, z);
        if (quantaRemaining <= 0) {
            return compare;
        }
        return (quantaRemaining >= compare) ? quantaRemaining : compare;
    }
    
    public FluidStack drain(final abw world, final int x, final int y, final int z, final boolean doDrain) {
        if (!this.isSourceBlock((acf)world, x, y, z)) {
            return null;
        }
        if (doDrain) {
            world.i(x, y, z);
        }
        return this.stack.copy();
    }
    
    public boolean canDrain(final abw world, final int x, final int y, final int z) {
        return this.isSourceBlock((acf)world, x, y, z);
    }
}
