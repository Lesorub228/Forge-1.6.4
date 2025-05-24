// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client.modloader;

import com.google.common.primitives.Booleans;
import java.util.Arrays;
import com.google.common.collect.ObjectArrays;
import cpw.mods.fml.common.TickType;
import java.util.EnumSet;
import java.util.List;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class ModLoaderKeyBindingHandler extends KeyBindingRegistry.KeyHandler
{
    private ModLoaderModContainer modContainer;
    private List<ats> helper;
    private boolean[] active;
    private boolean[] mlRepeats;
    private boolean[] armed;
    
    public ModLoaderKeyBindingHandler() {
        super(new ats[0], new boolean[0]);
        this.active = new boolean[0];
        this.mlRepeats = new boolean[0];
        this.armed = new boolean[0];
    }
    
    void setModContainer(final ModLoaderModContainer modContainer) {
        this.modContainer = modContainer;
    }
    
    public void fireKeyEvent(final ats kb) {
        ((BaseMod)this.modContainer.getMod()).keyboardEvent(kb);
    }
    
    @Override
    public void keyDown(final EnumSet<TickType> type, final ats kb, final boolean end, final boolean repeats) {
        if (!end) {
            return;
        }
        final int idx = this.helper.indexOf(kb);
        if (type.contains(TickType.CLIENT)) {
            this.armed[idx] = true;
        }
        if (this.armed[idx] && type.contains(TickType.RENDER) && (!this.active[idx] || this.mlRepeats[idx])) {
            this.fireKeyEvent(kb);
            this.active[idx] = true;
            this.armed[idx] = false;
        }
    }
    
    @Override
    public void keyUp(final EnumSet<TickType> type, final ats kb, final boolean end) {
        if (!end) {
            return;
        }
        final int idx = this.helper.indexOf(kb);
        this.active[idx] = false;
    }
    
    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT, TickType.RENDER);
    }
    
    @Override
    public String getLabel() {
        return this.modContainer.getModId() + " KB " + this.keyBindings[0].d;
    }
    
    void addKeyBinding(final ats binding, final boolean repeats) {
        this.keyBindings = (ats[])ObjectArrays.concat((Object[])this.keyBindings, (Object)binding);
        Arrays.fill(this.repeatings = new boolean[this.keyBindings.length], true);
        this.active = new boolean[this.keyBindings.length];
        this.armed = new boolean[this.keyBindings.length];
        this.mlRepeats = Booleans.concat(new boolean[][] { this.mlRepeats, { repeats } });
        this.keyDown = new boolean[this.keyBindings.length];
        this.helper = Arrays.asList(this.keyBindings);
    }
}
