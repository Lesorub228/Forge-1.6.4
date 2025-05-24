// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

public enum ForgeDirection
{
    DOWN(0, -1, 0), 
    UP(0, 1, 0), 
    NORTH(0, 0, -1), 
    SOUTH(0, 0, 1), 
    WEST(-1, 0, 0), 
    EAST(1, 0, 0), 
    UNKNOWN(0, 0, 0);
    
    public final int offsetX;
    public final int offsetY;
    public final int offsetZ;
    public final int flag;
    public static final ForgeDirection[] VALID_DIRECTIONS;
    public static final int[] OPPOSITES;
    public static final int[][] ROTATION_MATRIX;
    
    private ForgeDirection(final int x, final int y, final int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        this.flag = 1 << this.ordinal();
    }
    
    public static ForgeDirection getOrientation(final int id) {
        if (id >= 0 && id < ForgeDirection.VALID_DIRECTIONS.length) {
            return ForgeDirection.VALID_DIRECTIONS[id];
        }
        return ForgeDirection.UNKNOWN;
    }
    
    public ForgeDirection getOpposite() {
        return getOrientation(ForgeDirection.OPPOSITES[this.ordinal()]);
    }
    
    public ForgeDirection getRotation(final ForgeDirection axis) {
        return getOrientation(ForgeDirection.ROTATION_MATRIX[axis.ordinal()][this.ordinal()]);
    }
    
    static {
        VALID_DIRECTIONS = new ForgeDirection[] { ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };
        OPPOSITES = new int[] { 1, 0, 3, 2, 5, 4, 6 };
        ROTATION_MATRIX = new int[][] { { 0, 1, 4, 5, 3, 2, 6 }, { 0, 1, 5, 4, 2, 3, 6 }, { 5, 4, 2, 3, 0, 1, 6 }, { 4, 5, 2, 3, 1, 0, 6 }, { 2, 3, 1, 0, 4, 5, 6 }, { 3, 2, 0, 1, 4, 5, 6 }, { 0, 1, 2, 3, 4, 5, 6 } };
    }
}
