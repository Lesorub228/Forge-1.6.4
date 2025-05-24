// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.server.command;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.ForgeTimeTracker;
import java.text.DecimalFormat;
import net.minecraft.server.MinecraftServer;

public class ForgeCommand extends z
{
    private MinecraftServer server;
    private static final DecimalFormat timeFormatter;
    
    public ForgeCommand(final MinecraftServer server) {
        this.server = server;
    }
    
    public String c() {
        return "forge";
    }
    
    public String c(final ad icommandsender) {
        return "commands.forge.usage";
    }
    
    public int a() {
        return 2;
    }
    
    public void b(final ad sender, final String[] args) {
        if (args.length == 0) {
            throw new bd("commands.forge.usage", new Object[0]);
        }
        if ("help".equals(args[0])) {
            throw new bd("commands.forge.usage", new Object[0]);
        }
        if ("tps".equals(args[0])) {
            this.displayTPS(sender, args);
        }
        else if ("tpslog".equals(args[0])) {
            this.doTPSLog(sender, args);
        }
        else {
            if (!"track".equals(args[0])) {
                throw new bd("commands.forge.usage", new Object[0]);
            }
            this.handleTracking(sender, args);
        }
    }
    
    private void handleTracking(final ad sender, final String[] args) {
        if (args.length != 3) {
            throw new bd("commands.forge.usage.tracking", new Object[0]);
        }
        final String type = args[1];
        final int duration = a(sender, args[2], 1, 60);
        if ("te".equals(type)) {
            this.doTurnOnTileEntityTracking(sender, duration);
            return;
        }
        throw new bd("commands.forge.usage.tracking", new Object[0]);
    }
    
    private void doTurnOnTileEntityTracking(final ad sender, final int duration) {
        ForgeTimeTracker.tileEntityTrackingDuration = duration;
        ForgeTimeTracker.tileEntityTracking = true;
        sender.a(cv.b("commands.forge.tracking.te.enabled", new Object[] { duration }));
    }
    
    private void doTPSLog(final ad sender, final String[] args) {
    }
    
    private void displayTPS(final ad sender, final String[] args) {
        int dim = 0;
        boolean summary = true;
        if (args.length > 1) {
            dim = a(sender, args[1]);
            summary = false;
        }
        if (summary) {
            for (final Integer dimId : DimensionManager.getIDs()) {
                final double worldTickTime = mean(this.server.worldTickTimes.get(dimId)) * 1.0E-6;
                final double worldTPS = Math.min(1000.0 / worldTickTime, 20.0);
                sender.a(cv.b("commands.forge.tps.summary", new Object[] { String.format("Dim %d", dimId), ForgeCommand.timeFormatter.format(worldTickTime), ForgeCommand.timeFormatter.format(worldTPS) }));
            }
            final double meanTickTime = mean(this.server.j) * 1.0E-6;
            final double meanTPS = Math.min(1000.0 / meanTickTime, 20.0);
            sender.a(cv.b("commands.forge.tps.summary", new Object[] { "Overall", ForgeCommand.timeFormatter.format(meanTickTime), ForgeCommand.timeFormatter.format(meanTPS) }));
        }
        else {
            final double worldTickTime2 = mean(this.server.worldTickTimes.get(dim)) * 1.0E-6;
            final double worldTPS2 = Math.min(1000.0 / worldTickTime2, 20.0);
            sender.a(cv.b("commands.forge.tps.summary", new Object[] { String.format("Dim %d", dim), ForgeCommand.timeFormatter.format(worldTickTime2), ForgeCommand.timeFormatter.format(worldTPS2) }));
        }
    }
    
    private static long mean(final long[] values) {
        long sum = 0L;
        for (final long v : values) {
            sum += v;
        }
        return sum / values.length;
    }
    
    static {
        timeFormatter = new DecimalFormat("########0.000");
    }
}
