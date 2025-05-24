// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import com.google.common.base.Objects;
import com.google.common.collect.HashMultiset;
import cpw.mods.fml.common.ModContainer;
import com.google.common.collect.Multiset;
import java.util.Map;

public class ItemData
{
    private static Map<String, Multiset<String>> modOrdinals;
    private final String modId;
    private final String itemType;
    private final int itemId;
    private final int ordinal;
    private String forcedModId;
    private String forcedName;
    
    public ItemData(final yc item, final ModContainer mc) {
        this.itemId = item.cv;
        if (item.getClass().equals(zh.class)) {
            this.itemType = aqz.s[this.getItemId()].getClass().getName();
        }
        else {
            this.itemType = item.getClass().getName();
        }
        this.modId = mc.getModId();
        if (!ItemData.modOrdinals.containsKey(mc.getModId())) {
            ItemData.modOrdinals.put(mc.getModId(), (Multiset<String>)HashMultiset.create());
        }
        this.ordinal = ItemData.modOrdinals.get(mc.getModId()).add((Object)this.itemType, 1);
    }
    
    public ItemData(final by tag) {
        this.modId = tag.i("ModId");
        this.itemType = tag.i("ItemType");
        this.itemId = tag.e("ItemId");
        this.ordinal = tag.e("ordinal");
        this.forcedModId = (tag.b("ForcedModId") ? tag.i("ForcedModId") : null);
        this.forcedName = (tag.b("ForcedName") ? tag.i("ForcedName") : null);
    }
    
    public String getItemType() {
        return (this.forcedName != null) ? this.forcedName : this.itemType;
    }
    
    public String getModId() {
        return (this.forcedModId != null) ? this.forcedModId : this.modId;
    }
    
    public int getOrdinal() {
        return this.ordinal;
    }
    
    public int getItemId() {
        return this.itemId;
    }
    
    public by toNBT() {
        final by tag = new by();
        tag.a("ModId", this.modId);
        tag.a("ItemType", this.itemType);
        tag.a("ItemId", this.itemId);
        tag.a("ordinal", this.ordinal);
        if (this.forcedModId != null) {
            tag.a("ForcedModId", this.forcedModId);
        }
        if (this.forcedName != null) {
            tag.a("ForcedName", this.forcedName);
        }
        return tag;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(new Object[] { this.itemId, this.ordinal });
    }
    
    @Override
    public boolean equals(final Object obj) {
        try {
            final ItemData other = (ItemData)obj;
            return Objects.equal((Object)this.getModId(), (Object)other.getModId()) && Objects.equal((Object)this.getItemType(), (Object)other.getItemType()) && Objects.equal((Object)this.itemId, (Object)other.itemId) && (this.isOveridden() || Objects.equal((Object)this.ordinal, (Object)other.ordinal));
        }
        catch (final ClassCastException cce) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Item %d, Type %s, owned by %s, ordinal %d, name %s, claimedModId %s", this.itemId, this.itemType, this.modId, this.ordinal, this.forcedName, this.forcedModId);
    }
    
    public boolean mayDifferByOrdinal(final ItemData rightValue) {
        return Objects.equal((Object)this.getItemType(), (Object)rightValue.getItemType()) && Objects.equal((Object)this.getModId(), (Object)rightValue.getModId());
    }
    
    public boolean isOveridden() {
        return this.forcedName != null;
    }
    
    public void setName(final String name, final String modId) {
        if (name == null) {
            this.forcedName = null;
            this.forcedModId = null;
            return;
        }
        String localModId;
        if ((localModId = modId) == null) {
            localModId = Loader.instance().activeModContainer().getModId();
        }
        if (ItemData.modOrdinals.get(localModId).count((Object)name) > 0) {
            FMLLog.severe("The mod %s is attempting to redefine the item at id %d with a non-unique name (%s.%s)", Loader.instance().activeModContainer(), this.itemId, localModId, name);
            throw new LoaderException();
        }
        ItemData.modOrdinals.get(localModId).add((Object)name);
        this.forcedModId = modId;
        this.forcedName = name;
    }
    
    static {
        ItemData.modOrdinals = Maps.newHashMap();
    }
}
