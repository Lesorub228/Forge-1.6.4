// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.server;

import java.util.Iterator;
import java.util.Arrays;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import java.lang.ref.WeakReference;
import java.util.Map;

public class ForgeTimeTracker
{
    public static boolean tileEntityTracking;
    public static int tileEntityTrackingDuration;
    public static long tileEntityTrackingTime;
    private Map<asp, int[]> tileEntityTimings;
    private Map<nn, int[]> entityTimings;
    private static final ForgeTimeTracker INSTANCE;
    private WeakReference<asp> tile;
    private WeakReference<nn> entity;
    private long timing;
    
    private ForgeTimeTracker() {
        final MapMaker mm = new MapMaker();
        mm.weakKeys();
        this.tileEntityTimings = mm.makeMap();
        this.entityTimings = mm.makeMap();
    }
    
    private void trackTileStart(final asp tileEntity, final long nanoTime) {
        if (ForgeTimeTracker.tileEntityTrackingTime == 0L) {
            ForgeTimeTracker.tileEntityTrackingTime = nanoTime;
        }
        else if (ForgeTimeTracker.tileEntityTrackingTime + ForgeTimeTracker.tileEntityTrackingDuration < nanoTime) {
            ForgeTimeTracker.tileEntityTracking = false;
            ForgeTimeTracker.tileEntityTrackingTime = 0L;
            return;
        }
        this.tile = new WeakReference<asp>(tileEntity);
        this.timing = nanoTime;
    }
    
    private void trackTileEnd(final asp tileEntity, final long nanoTime) {
        if (this.tile == null || this.tile.get() != tileEntity) {
            this.tile = null;
            return;
        }
        int[] timings = this.tileEntityTimings.get(tileEntity);
        if (timings == null) {
            timings = new int[101];
            this.tileEntityTimings.put(tileEntity, timings);
        }
        final int[] array = timings;
        final int n = 100;
        final int n2 = (timings[100] + 1) % 100;
        array[n] = n2;
        final int idx = n2;
        timings[idx] = (int)(nanoTime - this.timing);
    }
    
    public static ImmutableMap<asp, int[]> getTileTimings() {
        return ForgeTimeTracker.INSTANCE.buildImmutableTileEntityTimingMap();
    }
    
    private ImmutableMap<asp, int[]> buildImmutableTileEntityTimingMap() {
        final ImmutableMap.Builder<asp, int[]> builder = (ImmutableMap.Builder<asp, int[]>)ImmutableMap.builder();
        for (final Map.Entry<asp, int[]> entry : this.tileEntityTimings.entrySet()) {
            builder.put((Object)entry.getKey(), (Object)Arrays.copyOfRange(entry.getValue(), 0, 100));
        }
        return (ImmutableMap<asp, int[]>)builder.build();
    }
    
    public static void trackStart(final asp tileEntity) {
        if (!ForgeTimeTracker.tileEntityTracking) {
            return;
        }
        ForgeTimeTracker.INSTANCE.trackTileStart(tileEntity, System.nanoTime());
    }
    
    public static void trackEnd(final asp tileEntity) {
        if (!ForgeTimeTracker.tileEntityTracking) {
            return;
        }
        ForgeTimeTracker.INSTANCE.trackTileEnd(tileEntity, System.nanoTime());
    }
    
    public static void trackStart(final nn par1Entity) {
    }
    
    public static void trackEnd(final nn par1Entity) {
    }
    
    static {
        INSTANCE = new ForgeTimeTracker();
    }
}
