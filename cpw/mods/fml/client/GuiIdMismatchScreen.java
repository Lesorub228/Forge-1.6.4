// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import java.util.Iterator;
import java.util.Map;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.ItemData;
import com.google.common.collect.MapDifference;
import java.util.List;

public class GuiIdMismatchScreen extends aux
{
    private List<String> missingIds;
    private List<String> mismatchedIds;
    private boolean allowContinue;
    
    public GuiIdMismatchScreen(final MapDifference<Integer, ItemData> idDifferences, final boolean allowContinue) {
        super((awe)null, "ID mismatch", "Should I continue?", 1);
        this.missingIds = Lists.newArrayList();
        this.mismatchedIds = Lists.newArrayList();
        this.a = (awe)this;
        for (final Map.Entry<Integer, ItemData> entry : idDifferences.entriesOnlyOnLeft().entrySet()) {
            this.missingIds.add(String.format("ID %d from Mod %s is missing", entry.getValue().getItemId(), entry.getValue().getModId(), entry.getValue().getItemType()));
        }
        for (final Map.Entry<Integer, MapDifference.ValueDifference<ItemData>> entry2 : idDifferences.entriesDiffering().entrySet()) {
            final ItemData world = (ItemData)entry2.getValue().leftValue();
            final ItemData game = (ItemData)entry2.getValue().rightValue();
            this.mismatchedIds.add(String.format("ID %d is mismatched between world and game", world.getItemId()));
        }
        this.allowContinue = allowContinue;
    }
    
    public void a(final boolean choice, final int par2) {
        FMLClientHandler.instance().callbackIdDifferenceResponse(choice);
    }
    
    public void a(final int par1, final int par2, final float par3) {
        this.e();
        if (!this.allowContinue && this.i.size() == 2) {
            this.i.remove(0);
        }
        int offset = Math.max(85 - (this.missingIds.size() + this.mismatchedIds.size()) * 10, 30);
        this.a(this.o, "Forge Mod Loader has found ID mismatches", this.g / 2, 10, 16777215);
        this.a(this.o, "Complete details are in the log file", this.g / 2, 20, 16777215);
        int maxLines = 20;
        for (final String s : this.missingIds) {
            this.a(this.o, s, this.g / 2, offset, 15658734);
            offset += 10;
            if (--maxLines < 0) {
                break;
            }
            if (offset >= this.h - 30) {
                break;
            }
        }
        if (maxLines > 0 && offset < this.h - 30) {
            for (final String s : this.mismatchedIds) {
                this.a(this.o, s, this.g / 2, offset, 15658734);
                offset += 10;
                if (--maxLines < 0) {
                    break;
                }
                if (offset >= this.h - 30) {
                    break;
                }
            }
        }
        if (this.allowContinue) {
            this.a(this.o, "Do you wish to continue loading?", this.g / 2, this.h - 30, 16777215);
        }
        else {
            this.a(this.o, "You cannot connect to this server", this.g / 2, this.h - 30, 16777215);
        }
        for (int var4 = 0; var4 < this.i.size(); ++var4) {
            final aut var5 = this.i.get(var4);
            var5.e = this.h - 20;
            if (!this.allowContinue) {
                var5.d = this.g / 2 - 75;
                var5.f = bkb.a("gui.done");
            }
            var5.a(this.f, par1, par2);
        }
    }
}
