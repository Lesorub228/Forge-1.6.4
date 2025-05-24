// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import com.google.common.base.Splitter;
import java.io.IOException;
import java.io.BufferedWriter;
import com.google.common.collect.ImmutableMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;

public class ConfigCategory implements Map<String, Property>
{
    private String name;
    private String comment;
    private ArrayList<ConfigCategory> children;
    private Map<String, Property> properties;
    public final ConfigCategory parent;
    private boolean changed;
    
    public ConfigCategory(final String name) {
        this(name, null);
    }
    
    public ConfigCategory(final String name, final ConfigCategory parent) {
        this.children = new ArrayList<ConfigCategory>();
        this.properties = new TreeMap<String, Property>();
        this.changed = false;
        this.name = name;
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ConfigCategory) {
            final ConfigCategory cat = (ConfigCategory)obj;
            return this.name.equals(cat.name) && this.children.equals(cat.children);
        }
        return false;
    }
    
    public String getQualifiedName() {
        return getQualifiedName(this.name, this.parent);
    }
    
    public static String getQualifiedName(final String name, final ConfigCategory parent) {
        return (parent == null) ? name : (parent.getQualifiedName() + "." + name);
    }
    
    public ConfigCategory getFirstParent() {
        return (this.parent == null) ? this : this.parent.getFirstParent();
    }
    
    public boolean isChild() {
        return this.parent != null;
    }
    
    public Map<String, Property> getValues() {
        return (Map<String, Property>)ImmutableMap.copyOf((Map)this.properties);
    }
    
    public void setComment(final String comment) {
        this.comment = comment;
    }
    
    public boolean containsKey(final String key) {
        return this.properties.containsKey(key);
    }
    
    public Property get(final String key) {
        return this.properties.get(key);
    }
    
    private void write(final BufferedWriter out, final String... data) throws IOException {
        this.write(out, true, data);
    }
    
    private void write(final BufferedWriter out, final boolean new_line, final String... data) throws IOException {
        for (int x = 0; x < data.length; ++x) {
            out.write(data[x]);
        }
        if (new_line) {
            out.write(Configuration.NEW_LINE);
        }
    }
    
    public void write(final BufferedWriter out, final int indent) throws IOException {
        final String pad0 = this.getIndent(indent);
        final String pad2 = this.getIndent(indent + 1);
        final String pad3 = this.getIndent(indent + 2);
        this.write(out, pad0, "####################");
        this.write(out, pad0, "# ", this.name);
        if (this.comment != null) {
            this.write(out, pad0, "#===================");
            final Splitter splitter = Splitter.onPattern("\r?\n");
            for (final String line : splitter.split((CharSequence)this.comment)) {
                this.write(out, pad0, "# ", line);
            }
        }
        this.write(out, pad0, "####################", Configuration.NEW_LINE);
        if (!Configuration.allowedProperties.matchesAllOf((CharSequence)this.name)) {
            this.name = '\"' + this.name + '\"';
        }
        this.write(out, pad0, this.name, " {");
        final Property[] props = this.properties.values().toArray(new Property[this.properties.size()]);
        for (int x = 0; x < props.length; ++x) {
            final Property prop = props[x];
            if (prop.comment != null) {
                if (x != 0) {
                    out.newLine();
                }
                final Splitter splitter2 = Splitter.onPattern("\r?\n");
                for (final String commentLine : splitter2.split((CharSequence)prop.comment)) {
                    this.write(out, pad2, "# ", commentLine);
                }
            }
            String propName = prop.getName();
            if (!Configuration.allowedProperties.matchesAllOf((CharSequence)propName)) {
                propName = '\"' + propName + '\"';
            }
            if (prop.isList()) {
                final char type = prop.getType().getID();
                this.write(out, pad2, String.valueOf(type), ":", propName, " <");
                for (final String line2 : prop.getStringList()) {
                    this.write(out, pad3, line2);
                }
                this.write(out, pad2, " >");
            }
            else if (prop.getType() == null) {
                this.write(out, pad2, propName, "=", prop.getString());
            }
            else {
                final char type = prop.getType().getID();
                this.write(out, pad2, String.valueOf(type), ":", propName, "=", prop.getString());
            }
        }
        for (final ConfigCategory child : this.children) {
            child.write(out, indent + 1);
        }
        this.write(out, pad0, "}", Configuration.NEW_LINE);
    }
    
    private String getIndent(final int indent) {
        final StringBuilder buf = new StringBuilder("");
        for (int x = 0; x < indent; ++x) {
            buf.append("    ");
        }
        return buf.toString();
    }
    
    public boolean hasChanged() {
        if (this.changed) {
            return true;
        }
        for (final Property prop : this.properties.values()) {
            if (prop.hasChanged()) {
                return true;
            }
        }
        return false;
    }
    
    void resetChangedState() {
        this.changed = false;
        for (final Property prop : this.properties.values()) {
            prop.resetChangedState();
        }
    }
    
    @Override
    public int size() {
        return this.properties.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.properties.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.properties.containsValue(value);
    }
    
    @Override
    public Property get(final Object key) {
        return this.properties.get(key);
    }
    
    @Override
    public Property put(final String key, final Property value) {
        this.changed = true;
        return this.properties.put(key, value);
    }
    
    @Override
    public Property remove(final Object key) {
        this.changed = true;
        return this.properties.remove(key);
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends Property> m) {
        this.changed = true;
        this.properties.putAll(m);
    }
    
    @Override
    public void clear() {
        this.changed = true;
        this.properties.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return this.properties.keySet();
    }
    
    @Override
    public Collection<Property> values() {
        return this.properties.values();
    }
    
    @Override
    public Set<Entry<String, Property>> entrySet() {
        return (Set<Entry<String, Property>>)ImmutableSet.copyOf((Collection)this.properties.entrySet());
    }
    
    public Set<ConfigCategory> getChildren() {
        return (Set<ConfigCategory>)ImmutableSet.copyOf((Collection)this.children);
    }
    
    public void removeChild(final ConfigCategory child) {
        if (this.children.contains(child)) {
            this.children.remove(child);
            this.changed = true;
        }
    }
}
