// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import com.google.common.collect.BiMap;
import java.util.Map;

public class RotationHelper
{
    private static final ForgeDirection[] UP_DOWN_AXES;
    private static final Map<BlockType, BiMap<Integer, ForgeDirection>> MAPPINGS;
    
    public static ForgeDirection[] getValidVanillaBlockRotations(final aqz block) {
        return (block instanceof anb || block instanceof apy || block instanceof aog || block instanceof aqx || block instanceof ark || block instanceof anm || block instanceof apv || block instanceof anu || block instanceof aqp || block instanceof ank || block instanceof aod || block instanceof aok || block instanceof aoy || block.cF == aqz.aN.cF || block.cF == aqz.aI.cF || block instanceof anz || block instanceof aqa || block instanceof anf || block instanceof aqf || block instanceof anp || block instanceof ari || block instanceof aou || block instanceof arm || block instanceof aqk || block instanceof amv) ? RotationHelper.UP_DOWN_AXES : ForgeDirection.VALID_DIRECTIONS;
    }
    
    public static boolean rotateVanillaBlock(final aqz block, final abw worldObj, final int x, final int y, final int z, final ForgeDirection axis) {
        if (worldObj.I) {
            return false;
        }
        if (axis == ForgeDirection.UP || axis == ForgeDirection.DOWN) {
            if (block instanceof anb || block instanceof apy || block instanceof aog || block instanceof aqx || block instanceof ark || block instanceof anm) {
                return rotateBlock(worldObj, x, y, z, axis, 3, BlockType.BED);
            }
            if (block instanceof aqa) {
                return rotateBlock(worldObj, x, y, z, axis, 15, BlockType.RAIL);
            }
            if (block instanceof apv || block instanceof anu) {
                return rotateBlock(worldObj, x, y, z, axis, 7, BlockType.RAIL_POWERED);
            }
            if (block instanceof aqp) {
                return rotateBlock(worldObj, x, y, z, axis, 3, BlockType.STAIR);
            }
            if (block instanceof ank || block instanceof aod || block instanceof aok || block instanceof aoy || block.cF == aqz.aN.cF) {
                return rotateBlock(worldObj, x, y, z, axis, 7, BlockType.CHEST);
            }
            if (block.cF == aqz.aI.cF) {
                return rotateBlock(worldObj, x, y, z, axis, 15, BlockType.SIGNPOST);
            }
            if (block instanceof anz) {
                return rotateBlock(worldObj, x, y, z, axis, 3, BlockType.DOOR);
            }
            if (block instanceof anf) {
                return rotateBlock(worldObj, x, y, z, axis, 7, BlockType.BUTTON);
            }
            if (block instanceof aqf || block instanceof anp) {
                return rotateBlock(worldObj, x, y, z, axis, 3, BlockType.REDSTONE_REPEATER);
            }
            if (block instanceof ari) {
                return rotateBlock(worldObj, x, y, z, axis, 3, BlockType.TRAPDOOR);
            }
            if (block instanceof aou) {
                return rotateBlock(worldObj, x, y, z, axis, 15, BlockType.MUSHROOM_CAP);
            }
            if (block instanceof arm) {
                return rotateBlock(worldObj, x, y, z, axis, 15, BlockType.VINE);
            }
            if (block instanceof aqk) {
                return rotateBlock(worldObj, x, y, z, axis, 7, BlockType.SKULL);
            }
            if (block instanceof amv) {
                return rotateBlock(worldObj, x, y, z, axis, 1, BlockType.ANVIL);
            }
        }
        if (block instanceof arj) {
            return rotateBlock(worldObj, x, y, z, axis, 12, BlockType.LOG);
        }
        if (block instanceof any || block instanceof ast || block instanceof asu || block instanceof aot) {
            return rotateBlock(worldObj, x, y, z, axis, 7, BlockType.DISPENSER);
        }
        if (block instanceof arg) {
            return rotateBlock(worldObj, x, y, z, axis, 15, BlockType.TORCH);
        }
        return block instanceof apb && rotateBlock(worldObj, x, y, z, axis, 7, BlockType.LEVER);
    }
    
    private static boolean rotateBlock(final abw worldObj, final int x, final int y, final int z, final ForgeDirection axis, final int mask, final BlockType blockType) {
        final int rotMeta = worldObj.h(x, y, z);
        if (blockType == BlockType.DOOR && (rotMeta & 0x8) == 0x8) {
            return false;
        }
        final int masked = rotMeta & ~mask;
        final int meta = rotateMetadata(axis, blockType, rotMeta & mask);
        if (meta == -1) {
            return false;
        }
        worldObj.b(x, y, z, (meta & mask) | masked, 3);
        return true;
    }
    
    private static int rotateMetadata(final ForgeDirection axis, BlockType blockType, final int meta) {
        if (blockType == BlockType.RAIL || blockType == BlockType.RAIL_POWERED) {
            if (meta == 0 || meta == 1) {
                return ~meta & 0x1;
            }
            if (meta >= 2 && meta <= 5) {
                blockType = BlockType.RAIL_ASCENDING;
            }
            if (meta >= 6 && meta <= 9 && blockType == BlockType.RAIL) {
                blockType = BlockType.RAIL_CORNER;
            }
        }
        if (blockType == BlockType.SIGNPOST) {
            return (axis == ForgeDirection.UP) ? ((meta + 4) % 16) : ((meta + 12) % 16);
        }
        if (blockType == BlockType.LEVER && (axis == ForgeDirection.UP || axis == ForgeDirection.DOWN)) {
            switch (meta) {
                case 5: {
                    return 6;
                }
                case 6: {
                    return 5;
                }
                case 7: {
                    return 0;
                }
                case 0: {
                    return 7;
                }
            }
        }
        if (blockType == BlockType.MUSHROOM_CAP) {
            if (meta % 2 == 0) {
                blockType = BlockType.MUSHROOM_CAP_SIDE;
            }
            else {
                blockType = BlockType.MUSHROOM_CAP_CORNER;
            }
        }
        if (blockType == BlockType.VINE) {
            return meta << 1 | (meta & 0x8) >> 3;
        }
        final ForgeDirection orientation = metadataToDirection(blockType, meta);
        final ForgeDirection rotated = orientation.getRotation(axis);
        return directionToMetadata(blockType, rotated);
    }
    
    private static ForgeDirection metadataToDirection(final BlockType blockType, int meta) {
        if (blockType == BlockType.LEVER) {
            if (meta == 6) {
                meta = 5;
            }
            else if (meta == 0) {
                meta = 7;
            }
        }
        if (RotationHelper.MAPPINGS.containsKey(blockType)) {
            final BiMap<Integer, ForgeDirection> biMap = RotationHelper.MAPPINGS.get(blockType);
            if (biMap.containsKey((Object)meta)) {
                return (ForgeDirection)biMap.get((Object)meta);
            }
        }
        if (blockType == BlockType.TORCH) {
            return ForgeDirection.getOrientation(6 - meta);
        }
        if (blockType == BlockType.STAIR) {
            return ForgeDirection.getOrientation(5 - meta);
        }
        if (blockType == BlockType.CHEST || blockType == BlockType.DISPENSER || blockType == BlockType.SKULL) {
            return ForgeDirection.getOrientation(meta);
        }
        if (blockType == BlockType.BUTTON) {
            return ForgeDirection.getOrientation(6 - meta);
        }
        if (blockType == BlockType.TRAPDOOR) {
            return ForgeDirection.getOrientation(meta + 2).getOpposite();
        }
        return ForgeDirection.UNKNOWN;
    }
    
    private static int directionToMetadata(final BlockType blockType, ForgeDirection direction) {
        if ((blockType == BlockType.LOG || blockType == BlockType.ANVIL) && direction.offsetX + direction.offsetY + direction.offsetZ < 0) {
            direction = direction.getOpposite();
        }
        if (RotationHelper.MAPPINGS.containsKey(blockType)) {
            final BiMap<ForgeDirection, Integer> biMap = (BiMap<ForgeDirection, Integer>)RotationHelper.MAPPINGS.get(blockType).inverse();
            if (biMap.containsKey((Object)direction)) {
                return (int)biMap.get((Object)direction);
            }
        }
        if (blockType == BlockType.TORCH && direction.ordinal() >= 1) {
            return 6 - direction.ordinal();
        }
        if (blockType == BlockType.STAIR) {
            return 5 - direction.ordinal();
        }
        if (blockType == BlockType.CHEST || blockType == BlockType.DISPENSER || blockType == BlockType.SKULL) {
            return direction.ordinal();
        }
        if (blockType == BlockType.BUTTON && direction.ordinal() >= 2) {
            return 6 - direction.ordinal();
        }
        if (blockType == BlockType.TRAPDOOR) {
            return direction.getOpposite().ordinal() - 2;
        }
        return -1;
    }
    
    static {
        UP_DOWN_AXES = new ForgeDirection[] { ForgeDirection.UP, ForgeDirection.DOWN };
        MAPPINGS = new HashMap<BlockType, BiMap<Integer, ForgeDirection>>();
        BiMap<Integer, ForgeDirection> biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(3);
        biMap.put((Object)0, (Object)ForgeDirection.UP);
        biMap.put((Object)4, (Object)ForgeDirection.EAST);
        biMap.put((Object)8, (Object)ForgeDirection.SOUTH);
        RotationHelper.MAPPINGS.put(BlockType.LOG, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(4);
        biMap.put((Object)0, (Object)ForgeDirection.SOUTH);
        biMap.put((Object)1, (Object)ForgeDirection.WEST);
        biMap.put((Object)2, (Object)ForgeDirection.NORTH);
        biMap.put((Object)3, (Object)ForgeDirection.EAST);
        RotationHelper.MAPPINGS.put(BlockType.BED, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(4);
        biMap.put((Object)2, (Object)ForgeDirection.EAST);
        biMap.put((Object)3, (Object)ForgeDirection.WEST);
        biMap.put((Object)4, (Object)ForgeDirection.NORTH);
        biMap.put((Object)5, (Object)ForgeDirection.SOUTH);
        RotationHelper.MAPPINGS.put(BlockType.RAIL_ASCENDING, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(4);
        biMap.put((Object)6, (Object)ForgeDirection.WEST);
        biMap.put((Object)7, (Object)ForgeDirection.NORTH);
        biMap.put((Object)8, (Object)ForgeDirection.EAST);
        biMap.put((Object)9, (Object)ForgeDirection.SOUTH);
        RotationHelper.MAPPINGS.put(BlockType.RAIL_CORNER, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(6);
        biMap.put((Object)1, (Object)ForgeDirection.EAST);
        biMap.put((Object)2, (Object)ForgeDirection.WEST);
        biMap.put((Object)3, (Object)ForgeDirection.SOUTH);
        biMap.put((Object)4, (Object)ForgeDirection.NORTH);
        biMap.put((Object)5, (Object)ForgeDirection.UP);
        biMap.put((Object)7, (Object)ForgeDirection.DOWN);
        RotationHelper.MAPPINGS.put(BlockType.LEVER, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(4);
        biMap.put((Object)0, (Object)ForgeDirection.WEST);
        biMap.put((Object)1, (Object)ForgeDirection.NORTH);
        biMap.put((Object)2, (Object)ForgeDirection.EAST);
        biMap.put((Object)3, (Object)ForgeDirection.SOUTH);
        RotationHelper.MAPPINGS.put(BlockType.DOOR, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(4);
        biMap.put((Object)0, (Object)ForgeDirection.NORTH);
        biMap.put((Object)1, (Object)ForgeDirection.EAST);
        biMap.put((Object)2, (Object)ForgeDirection.SOUTH);
        biMap.put((Object)3, (Object)ForgeDirection.WEST);
        RotationHelper.MAPPINGS.put(BlockType.REDSTONE_REPEATER, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(4);
        biMap.put((Object)1, (Object)ForgeDirection.EAST);
        biMap.put((Object)3, (Object)ForgeDirection.SOUTH);
        biMap.put((Object)7, (Object)ForgeDirection.NORTH);
        biMap.put((Object)9, (Object)ForgeDirection.WEST);
        RotationHelper.MAPPINGS.put(BlockType.MUSHROOM_CAP_CORNER, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(4);
        biMap.put((Object)2, (Object)ForgeDirection.NORTH);
        biMap.put((Object)4, (Object)ForgeDirection.WEST);
        biMap.put((Object)6, (Object)ForgeDirection.EAST);
        biMap.put((Object)8, (Object)ForgeDirection.SOUTH);
        RotationHelper.MAPPINGS.put(BlockType.MUSHROOM_CAP_SIDE, biMap);
        biMap = (BiMap<Integer, ForgeDirection>)HashBiMap.create(2);
        biMap.put((Object)0, (Object)ForgeDirection.SOUTH);
        biMap.put((Object)1, (Object)ForgeDirection.EAST);
        RotationHelper.MAPPINGS.put(BlockType.ANVIL, biMap);
    }
    
    private enum BlockType
    {
        LOG, 
        DISPENSER, 
        BED, 
        RAIL, 
        RAIL_POWERED, 
        RAIL_ASCENDING, 
        RAIL_CORNER, 
        TORCH, 
        STAIR, 
        CHEST, 
        SIGNPOST, 
        DOOR, 
        LEVER, 
        BUTTON, 
        REDSTONE_REPEATER, 
        TRAPDOOR, 
        MUSHROOM_CAP, 
        MUSHROOM_CAP_CORNER, 
        MUSHROOM_CAP_SIDE, 
        VINE, 
        SKULL, 
        ANVIL;
    }
}
