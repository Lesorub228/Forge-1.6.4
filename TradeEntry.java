// 
// Decompiled by Procyon v0.6.0
// 

@Deprecated
public class TradeEntry
{
    @Deprecated
    public final int id;
    @Deprecated
    public float chance;
    @Deprecated
    public boolean buying;
    @Deprecated
    public int min;
    @Deprecated
    public int max;
    
    @Deprecated
    public TradeEntry(final int id, final float chance, final boolean buying, final int min, final int max) {
        this.min = 0;
        this.max = 0;
        this.id = id;
        this.chance = chance;
        this.buying = buying;
        this.min = min;
        this.max = max;
    }
    
    @Deprecated
    public TradeEntry(final int id, final float chance, final boolean buying) {
        this(id, chance, buying, 0, 0);
    }
}
