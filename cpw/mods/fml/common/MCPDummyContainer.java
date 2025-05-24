// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import com.google.common.eventbus.EventBus;

public class MCPDummyContainer extends DummyModContainer
{
    public MCPDummyContainer(final ModMetadata metadata) {
        super(metadata);
    }
    
    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        return true;
    }
}
