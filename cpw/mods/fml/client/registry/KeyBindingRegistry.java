// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client.registry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import cpw.mods.fml.common.TickType;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import com.google.common.collect.Sets;
import java.util.Set;

public class KeyBindingRegistry
{
    private static final KeyBindingRegistry INSTANCE;
    private Set<KeyHandler> keyHandlers;
    
    public KeyBindingRegistry() {
        this.keyHandlers = Sets.newLinkedHashSet();
    }
    
    public static void registerKeyBinding(final KeyHandler handler) {
        instance().keyHandlers.add(handler);
        if (!handler.isDummy) {
            TickRegistry.registerTickHandler(handler, Side.CLIENT);
        }
    }
    
    @Deprecated
    public static KeyBindingRegistry instance() {
        return KeyBindingRegistry.INSTANCE;
    }
    
    public void uploadKeyBindingsToGame(final aul settings) {
        final ArrayList<ats> harvestedBindings = Lists.newArrayList();
        for (final KeyHandler key : this.keyHandlers) {
            for (final ats kb : key.keyBindings) {
                harvestedBindings.add(kb);
            }
        }
        final ats[] modKeyBindings = harvestedBindings.toArray(new ats[harvestedBindings.size()]);
        final ats[] allKeys = new ats[settings.W.length + modKeyBindings.length];
        System.arraycopy(settings.W, 0, allKeys, 0, settings.W.length);
        System.arraycopy(modKeyBindings, 0, allKeys, settings.W.length, modKeyBindings.length);
        settings.W = allKeys;
        settings.a();
    }
    
    static {
        INSTANCE = new KeyBindingRegistry();
    }
    
    public abstract static class KeyHandler implements ITickHandler
    {
        protected ats[] keyBindings;
        protected boolean[] keyDown;
        protected boolean[] repeatings;
        private boolean isDummy;
        
        public KeyHandler(final ats[] keyBindings, final boolean[] repeatings) {
            assert keyBindings.length == repeatings.length : "You need to pass two arrays of identical length";
            this.keyBindings = keyBindings;
            this.repeatings = repeatings;
            this.keyDown = new boolean[keyBindings.length];
        }
        
        public KeyHandler(final ats[] keyBindings) {
            this.keyBindings = keyBindings;
            this.isDummy = true;
        }
        
        public ats[] getKeyBindings() {
            return this.keyBindings;
        }
        
        @Override
        public final void tickStart(final EnumSet<TickType> type, final Object... tickData) {
            this.keyTick(type, false);
        }
        
        @Override
        public final void tickEnd(final EnumSet<TickType> type, final Object... tickData) {
            this.keyTick(type, true);
        }
        
        private void keyTick(final EnumSet<TickType> type, final boolean tickEnd) {
            for (int i = 0; i < this.keyBindings.length; ++i) {
                final ats keyBinding = this.keyBindings[i];
                final int keyCode = keyBinding.d;
                final boolean state = (keyCode < 0) ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
                if (state != this.keyDown[i] || (state && this.repeatings[i])) {
                    if (state) {
                        this.keyDown(type, keyBinding, tickEnd, state != this.keyDown[i]);
                    }
                    else {
                        this.keyUp(type, keyBinding, tickEnd);
                    }
                    if (tickEnd) {
                        this.keyDown[i] = state;
                    }
                }
            }
        }
        
        public abstract void keyDown(final EnumSet<TickType> p0, final ats p1, final boolean p2, final boolean p3);
        
        public abstract void keyUp(final EnumSet<TickType> p0, final ats p1, final boolean p2);
        
        @Override
        public abstract EnumSet<TickType> ticks();
    }
}
