// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import java.util.Iterator;
import java.util.Collection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.TickType;
import java.util.EnumSet;
import cpw.mods.fml.common.ITickHandler;

public class BaseModTicker implements ITickHandler
{
    private BaseModProxy mod;
    private EnumSet<TickType> ticks;
    private boolean clockTickTrigger;
    private boolean sendGuiTicks;
    
    BaseModTicker(final BaseModProxy mod, final boolean guiTicker) {
        this.mod = mod;
        this.ticks = EnumSet.of(TickType.WORLDLOAD);
        this.sendGuiTicks = guiTicker;
    }
    
    BaseModTicker(final EnumSet<TickType> ticks, final boolean guiTicker) {
        this.ticks = ticks;
        this.sendGuiTicks = guiTicker;
    }
    
    @Override
    public void tickStart(final EnumSet<TickType> types, final Object... tickData) {
        this.tickBaseMod(types, false, tickData);
    }
    
    @Override
    public void tickEnd(final EnumSet<TickType> types, final Object... tickData) {
        this.tickBaseMod(types, true, tickData);
    }
    
    private void tickBaseMod(final EnumSet<TickType> types, final boolean end, final Object... tickData) {
        if (FMLCommonHandler.instance().getSide().isClient() && (this.ticks.contains(TickType.CLIENT) || this.ticks.contains(TickType.WORLDLOAD))) {
            final EnumSet cTypes = EnumSet.copyOf(types);
            if ((end && types.contains(TickType.CLIENT)) || types.contains(TickType.WORLDLOAD)) {
                this.clockTickTrigger = true;
                cTypes.remove(TickType.CLIENT);
                cTypes.remove(TickType.WORLDLOAD);
            }
            if (end && this.clockTickTrigger && types.contains(TickType.RENDER)) {
                this.clockTickTrigger = false;
                cTypes.remove(TickType.RENDER);
                cTypes.add(TickType.CLIENT);
            }
            this.sendTick(cTypes, end, tickData);
        }
        else {
            this.sendTick(types, end, tickData);
        }
    }
    
    private void sendTick(final EnumSet<TickType> types, final boolean end, final Object... tickData) {
        for (final TickType type : types) {
            if (!this.ticks.contains(type)) {
                continue;
            }
            boolean keepTicking = true;
            if (this.sendGuiTicks) {
                keepTicking = this.mod.doTickInGUI(type, end, tickData);
            }
            else {
                keepTicking = this.mod.doTickInGame(type, end, tickData);
            }
            if (keepTicking) {
                continue;
            }
            this.ticks.remove(type);
            this.ticks.removeAll(type.partnerTicks());
        }
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return this.clockTickTrigger ? EnumSet.of(TickType.RENDER) : this.ticks;
    }
    
    @Override
    public String getLabel() {
        return this.mod.getClass().getSimpleName();
    }
    
    public void setMod(final BaseModProxy mod) {
        this.mod = mod;
    }
}
