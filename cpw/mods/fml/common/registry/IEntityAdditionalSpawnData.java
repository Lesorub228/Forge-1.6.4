// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public interface IEntityAdditionalSpawnData
{
    void writeSpawnData(final ByteArrayDataOutput p0);
    
    void readSpawnData(final ByteArrayDataInput p0);
}
