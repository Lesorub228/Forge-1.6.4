// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.ArrayList;

public class Property
{
    private String name;
    private String value;
    public String comment;
    private String[] values;
    private final boolean wasRead;
    private final boolean isList;
    private final Type type;
    private boolean changed;
    
    public Property() {
        this.changed = false;
        this.wasRead = false;
        this.type = null;
        this.isList = false;
    }
    
    public Property(final String name, final String value, final Type type) {
        this(name, value, type, false);
    }
    
    Property(final String name, final String value, final Type type, final boolean read) {
        this.changed = false;
        this.setName(name);
        this.value = value;
        this.type = type;
        this.wasRead = read;
        this.isList = false;
    }
    
    public Property(final String name, final String[] values, final Type type) {
        this(name, values, type, false);
    }
    
    Property(final String name, final String[] values, final Type type, final boolean read) {
        this.changed = false;
        this.setName(name);
        this.type = type;
        this.values = values;
        this.wasRead = read;
        this.isList = true;
    }
    
    public String getString() {
        return this.value;
    }
    
    public int getInt() {
        return this.getInt(-1);
    }
    
    public int getInt(final int _default) {
        try {
            return Integer.parseInt(this.value);
        }
        catch (final NumberFormatException e) {
            return _default;
        }
    }
    
    public boolean isIntValue() {
        try {
            Integer.parseInt(this.value);
            return true;
        }
        catch (final NumberFormatException e) {
            return false;
        }
    }
    
    public boolean getBoolean(final boolean _default) {
        if (this.isBooleanValue()) {
            return Boolean.parseBoolean(this.value);
        }
        return _default;
    }
    
    public boolean isBooleanValue() {
        return "true".equals(this.value.toLowerCase()) || "false".equals(this.value.toLowerCase());
    }
    
    public boolean isDoubleValue() {
        try {
            Double.parseDouble(this.value);
            return true;
        }
        catch (final NumberFormatException e) {
            return false;
        }
    }
    
    public double getDouble(final double _default) {
        try {
            return Double.parseDouble(this.value);
        }
        catch (final NumberFormatException e) {
            return _default;
        }
    }
    
    public String[] getStringList() {
        return this.values;
    }
    
    public int[] getIntList() {
        final ArrayList<Integer> nums = new ArrayList<Integer>();
        for (final String value : this.values) {
            try {
                nums.add(Integer.parseInt(value));
            }
            catch (final NumberFormatException ex) {}
        }
        final int[] primitives = new int[nums.size()];
        for (int i = 0; i < nums.size(); ++i) {
            primitives[i] = nums.get(i);
        }
        return primitives;
    }
    
    public boolean isIntList() {
        for (final String value : this.values) {
            try {
                Integer.parseInt(value);
            }
            catch (final NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    public boolean[] getBooleanList() {
        final ArrayList<Boolean> tmp = new ArrayList<Boolean>();
        for (final String value : this.values) {
            try {
                tmp.add(Boolean.parseBoolean(value));
            }
            catch (final NumberFormatException ex) {}
        }
        final boolean[] primitives = new boolean[tmp.size()];
        for (int i = 0; i < tmp.size(); ++i) {
            primitives[i] = tmp.get(i);
        }
        return primitives;
    }
    
    public boolean isBooleanList() {
        for (final String value : this.values) {
            if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                return false;
            }
        }
        return true;
    }
    
    public double[] getDoubleList() {
        final ArrayList<Double> tmp = new ArrayList<Double>();
        for (final String value : this.values) {
            try {
                tmp.add(Double.parseDouble(value));
            }
            catch (final NumberFormatException ex) {}
        }
        final double[] primitives = new double[tmp.size()];
        for (int i = 0; i < tmp.size(); ++i) {
            primitives[i] = tmp.get(i);
        }
        return primitives;
    }
    
    public boolean isDoubleList() {
        for (final String value : this.values) {
            try {
                Double.parseDouble(value);
            }
            catch (final NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean wasRead() {
        return this.wasRead;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public boolean isList() {
        return this.isList;
    }
    
    public boolean hasChanged() {
        return this.changed;
    }
    
    void resetChangedState() {
        this.changed = false;
    }
    
    public void set(final String value) {
        this.value = value;
        this.changed = true;
    }
    
    public void set(final String[] values) {
        this.values = values;
        this.changed = true;
    }
    
    public void set(final int value) {
        this.set(Integer.toString(value));
    }
    
    public void set(final boolean value) {
        this.set(Boolean.toString(value));
    }
    
    public void set(final double value) {
        this.set(Double.toString(value));
    }
    
    public enum Type
    {
        STRING, 
        INTEGER, 
        BOOLEAN, 
        DOUBLE;
        
        private static Type[] values;
        
        public static Type tryParse(final char id) {
            for (int x = 0; x < Type.values.length; ++x) {
                if (Type.values[x].getID() == id) {
                    return Type.values[x];
                }
            }
            return Type.STRING;
        }
        
        public char getID() {
            return this.name().charAt(0);
        }
        
        static {
            Type.values = new Type[] { Type.STRING, Type.INTEGER, Type.BOOLEAN, Type.DOUBLE };
        }
    }
}
