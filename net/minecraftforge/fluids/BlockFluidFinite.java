// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import java.util.Random;

public class BlockFluidFinite extends BlockFluidBase
{
    public BlockFluidFinite(final int id, final Fluid fluid, final akc material) {
        super(id, fluid, material);
    }
    
    @Override
    public int getQuantaValue(final acf world, final int x, final int y, final int z) {
        if (world.c(x, y, z)) {
            return 0;
        }
        if (world.a(x, y, z) != this.cF) {
            return -1;
        }
        final int quantaRemaining = world.h(x, y, z) + 1;
        return quantaRemaining;
    }
    
    @Override
    public boolean a(final int meta, final boolean fullHit) {
        return fullHit && meta == this.quantaPerBlock - 1;
    }
    
    @Override
    public int getMaxRenderHeightMeta() {
        return this.quantaPerBlock - 1;
    }
    
    public void a(final abw world, final int x, final int y, final int z, final Random rand) {
        boolean changed = false;
        final int prevRemaining;
        int quantaRemaining = prevRemaining = world.h(x, y, z) + 1;
        quantaRemaining = this.tryToFlowVerticallyInto(world, x, y, z, quantaRemaining);
        if (quantaRemaining < 1) {
            return;
        }
        if (quantaRemaining != prevRemaining) {
            changed = true;
            if (quantaRemaining == 1) {
                world.b(x, y, z, quantaRemaining - 1, 2);
                return;
            }
        }
        else if (quantaRemaining == 1) {
            return;
        }
        final int lowerthan = quantaRemaining - 1;
        if (this.displaceIfPossible(world, x, y, z - 1)) {
            world.c(x, y, z - 1, 0);
        }
        if (this.displaceIfPossible(world, x, y, z + 1)) {
            world.c(x, y, z + 1, 0);
        }
        if (this.displaceIfPossible(world, x - 1, y, z)) {
            world.c(x - 1, y, z, 0);
        }
        if (this.displaceIfPossible(world, x + 1, y, z)) {
            world.c(x + 1, y, z, 0);
        }
        final int north = this.getQuantaValueBelow((acf)world, x, y, z - 1, lowerthan);
        final int south = this.getQuantaValueBelow((acf)world, x, y, z + 1, lowerthan);
        final int west = this.getQuantaValueBelow((acf)world, x - 1, y, z, lowerthan);
        final int east = this.getQuantaValueBelow((acf)world, x + 1, y, z, lowerthan);
        int total = quantaRemaining;
        int count = 1;
        if (north >= 0) {
            ++count;
            total += north;
        }
        if (south >= 0) {
            ++count;
            total += south;
        }
        if (west >= 0) {
            ++count;
            total += west;
        }
        if (east >= 0) {
            ++count;
            total += east;
        }
        if (count == 1) {
            if (changed) {
                world.b(x, y, z, quantaRemaining - 1, 2);
            }
            return;
        }
        int each = total / count;
        int rem = total % count;
        if (north >= 0) {
            int newnorth = each;
            if (rem == count || (rem > 1 && rand.nextInt(count - rem) != 0)) {
                ++newnorth;
                --rem;
            }
            if (newnorth != north) {
                if (newnorth == 0) {
                    world.c(x, y, z - 1, 0);
                }
                else {
                    world.f(x, y, z - 1, this.cF, newnorth - 1, 2);
                }
                world.a(x, y, z - 1, this.cF, this.tickRate);
            }
            --count;
        }
        if (south >= 0) {
            int newsouth = each;
            if (rem == count || (rem > 1 && rand.nextInt(count - rem) != 0)) {
                ++newsouth;
                --rem;
            }
            if (newsouth != south) {
                if (newsouth == 0) {
                    world.c(x, y, z + 1, 0);
                }
                else {
                    world.f(x, y, z + 1, this.cF, newsouth - 1, 2);
                }
                world.a(x, y, z + 1, this.cF, this.tickRate);
            }
            --count;
        }
        if (west >= 0) {
            int newwest = each;
            if (rem == count || (rem > 1 && rand.nextInt(count - rem) != 0)) {
                ++newwest;
                --rem;
            }
            if (newwest != west) {
                if (newwest == 0) {
                    world.c(x - 1, y, z, 0);
                }
                else {
                    world.f(x - 1, y, z, this.cF, newwest - 1, 2);
                }
                world.a(x - 1, y, z, this.cF, this.tickRate);
            }
            --count;
        }
        if (east >= 0) {
            int neweast = each;
            if (rem == count || (rem > 1 && rand.nextInt(count - rem) != 0)) {
                ++neweast;
                --rem;
            }
            if (neweast != east) {
                if (neweast == 0) {
                    world.c(x + 1, y, z, 0);
                }
                else {
                    world.f(x + 1, y, z, this.cF, neweast - 1, 2);
                }
                world.a(x + 1, y, z, this.cF, this.tickRate);
            }
            --count;
        }
        if (rem > 0) {
            ++each;
        }
        world.b(x, y, z, each - 1, 2);
    }
    
    public int tryToFlowVerticallyInto(final abw world, final int x, final int y, final int z, final int amtToInput) {
        final int otherY = y + this.densityDir;
        if (otherY < 0 || otherY >= world.R()) {
            world.i(x, y, z);
            return 0;
        }
        int amt = this.getQuantaValueBelow((acf)world, x, otherY, z, this.quantaPerBlock);
        if (amt >= 0) {
            amt += amtToInput;
            if (amt > this.quantaPerBlock) {
                world.f(x, otherY, z, this.cF, this.quantaPerBlock - 1, 3);
                world.a(x, otherY, z, this.cF, this.tickRate);
                return amt - this.quantaPerBlock;
            }
            if (amt > 0) {
                world.f(x, otherY, z, this.cF, amt - 1, 3);
                world.a(x, otherY, z, this.cF, this.tickRate);
                world.i(x, y, z);
                return 0;
            }
            return amtToInput;
        }
        else {
            final int density_other = BlockFluidBase.getDensity((acf)world, x, otherY, z);
            if (density_other != Integer.MAX_VALUE) {
                if (this.densityDir < 0) {
                    if (density_other < this.density) {
                        final int bId = world.a(x, otherY, z);
                        final BlockFluidBase block = (BlockFluidBase)aqz.s[bId];
                        final int otherData = world.h(x, otherY, z);
                        world.f(x, otherY, z, this.cF, amtToInput - 1, 3);
                        world.f(x, y, z, bId, otherData, 3);
                        world.a(x, otherY, z, this.cF, this.tickRate);
                        world.a(x, y, z, bId, block.a(world));
                        return 0;
                    }
                }
                else if (density_other > this.density) {
                    final int bId = world.a(x, otherY, z);
                    final BlockFluidBase block = (BlockFluidBase)aqz.s[bId];
                    final int otherData = world.h(x, otherY, z);
                    world.f(x, otherY, z, this.cF, amtToInput - 1, 3);
                    world.f(x, y, z, bId, otherData, 3);
                    world.a(x, otherY, z, this.cF, this.tickRate);
                    world.a(x, y, z, bId, block.a(world));
                    return 0;
                }
                return amtToInput;
            }
            if (this.displaceIfPossible(world, x, otherY, z)) {
                world.f(x, otherY, z, this.cF, amtToInput - 1, 3);
                world.a(x, otherY, z, this.cF, this.tickRate);
                world.i(x, y, z);
                return 0;
            }
            return amtToInput;
        }
    }
    
    public FluidStack drain(final abw world, final int x, final int y, final int z, final boolean doDrain) {
        return null;
    }
    
    public boolean canDrain(final abw world, final int x, final int y, final int z) {
        return false;
    }
}
