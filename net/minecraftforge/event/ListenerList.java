// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

public class ListenerList
{
    private static ArrayList<ListenerList> allLists;
    private static int maxSize;
    private ListenerList parent;
    private ListenerListInst[] lists;
    
    public ListenerList() {
        this.lists = new ListenerListInst[0];
        ListenerList.allLists.add(this);
        this.resizeLists(ListenerList.maxSize);
    }
    
    public ListenerList(final ListenerList parent) {
        this.lists = new ListenerListInst[0];
        ListenerList.allLists.add(this);
        this.parent = parent;
        this.resizeLists(ListenerList.maxSize);
    }
    
    public static void resize(final int max) {
        if (max <= ListenerList.maxSize) {
            return;
        }
        for (final ListenerList list : ListenerList.allLists) {
            list.resizeLists(max);
        }
        ListenerList.maxSize = max;
    }
    
    public void resizeLists(final int max) {
        if (this.parent != null) {
            this.parent.resizeLists(max);
        }
        if (this.lists.length >= max) {
            return;
        }
        final ListenerListInst[] newList = new ListenerListInst[max];
        int x;
        for (x = 0; x < this.lists.length; ++x) {
            newList[x] = this.lists[x];
        }
        while (x < max) {
            if (this.parent != null) {
                newList[x] = new ListenerListInst(this.parent.getInstance(x));
            }
            else {
                newList[x] = new ListenerListInst();
            }
            ++x;
        }
        this.lists = newList;
    }
    
    public static void clearBusID(final int id) {
        for (final ListenerList list : ListenerList.allLists) {
            list.lists[id].dispose();
        }
    }
    
    protected ListenerListInst getInstance(final int id) {
        return this.lists[id];
    }
    
    public IEventListener[] getListeners(final int id) {
        return this.lists[id].getListeners();
    }
    
    public void register(final int id, final EventPriority priority, final IEventListener listener) {
        this.lists[id].register(priority, listener);
    }
    
    public void unregister(final int id, final IEventListener listener) {
        this.lists[id].unregister(listener);
    }
    
    public static void unregiterAll(final int id, final IEventListener listener) {
        for (final ListenerList list : ListenerList.allLists) {
            list.unregister(id, listener);
        }
    }
    
    static {
        ListenerList.allLists = new ArrayList<ListenerList>();
        ListenerList.maxSize = 0;
    }
    
    private class ListenerListInst
    {
        private boolean rebuild;
        private IEventListener[] listeners;
        private ArrayList<ArrayList<IEventListener>> priorities;
        private ListenerListInst parent;
        
        private ListenerListInst() {
            this.rebuild = true;
            final int count = EventPriority.values().length;
            this.priorities = new ArrayList<ArrayList<IEventListener>>(count);
            for (int x = 0; x < count; ++x) {
                this.priorities.add(new ArrayList<IEventListener>());
            }
        }
        
        public void dispose() {
            for (final ArrayList<IEventListener> listeners : this.priorities) {
                listeners.clear();
            }
            this.priorities.clear();
            this.parent = null;
            this.listeners = null;
        }
        
        private ListenerListInst(final ListenerList list, final ListenerListInst parent) {
            this(list);
            this.parent = parent;
        }
        
        public ArrayList<IEventListener> getListeners(final EventPriority priority) {
            final ArrayList<IEventListener> ret = new ArrayList<IEventListener>(this.priorities.get(priority.ordinal()));
            if (this.parent != null) {
                ret.addAll(this.parent.getListeners(priority));
            }
            return ret;
        }
        
        public IEventListener[] getListeners() {
            if (this.shouldRebuild()) {
                this.buildCache();
            }
            return this.listeners;
        }
        
        protected boolean shouldRebuild() {
            return this.rebuild || (this.parent != null && this.parent.shouldRebuild());
        }
        
        private void buildCache() {
            if (this.parent != null && this.parent.shouldRebuild()) {
                this.parent.buildCache();
            }
            final ArrayList<IEventListener> ret = new ArrayList<IEventListener>();
            for (final EventPriority value : EventPriority.values()) {
                ret.addAll(this.getListeners(value));
            }
            this.listeners = ret.toArray(new IEventListener[ret.size()]);
            this.rebuild = false;
        }
        
        public void register(final EventPriority priority, final IEventListener listener) {
            this.priorities.get(priority.ordinal()).add(listener);
            this.rebuild = true;
        }
        
        public void unregister(final IEventListener listener) {
            for (final ArrayList<IEventListener> list : this.priorities) {
                if (list.remove(listener)) {
                    this.rebuild = true;
                }
            }
        }
    }
}
