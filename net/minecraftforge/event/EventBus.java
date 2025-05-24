// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Set;
import java.lang.annotation.Annotation;
import com.google.common.reflect.TypeToken;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus
{
    private static int maxID;
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners;
    private final int busID;
    
    public EventBus() {
        this.listeners = new ConcurrentHashMap<Object, ArrayList<IEventListener>>();
        this.busID = EventBus.maxID++;
        ListenerList.resize(this.busID + 1);
    }
    
    public void register(final Object target) {
        if (this.listeners.containsKey(target)) {
            return;
        }
        final Set<? extends Class<?>> supers = TypeToken.of((Class)target.getClass()).getTypes().rawTypes();
        for (final Method method : target.getClass().getMethods()) {
            for (final Class<?> cls : supers) {
                try {
                    final Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    if (!real.isAnnotationPresent(ForgeSubscribe.class)) {
                        continue;
                    }
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1) {
                        throw new IllegalArgumentException("Method " + method + " has @ForgeSubscribe annotation, but requires " + parameterTypes.length + " arguments.  Event handler methods must require a single argument.");
                    }
                    final Class<?> eventType = parameterTypes[0];
                    if (!Event.class.isAssignableFrom(eventType)) {
                        throw new IllegalArgumentException("Method " + method + " has @ForgeSubscribe annotation, but takes a argument that is not a Event " + eventType);
                    }
                    this.register(eventType, target, method);
                    break;
                }
                catch (final NoSuchMethodException ex) {}
            }
        }
    }
    
    private void register(final Class<?> eventType, final Object target, final Method method) {
        try {
            final Constructor<?> ctr = eventType.getConstructor((Class<?>[])new Class[0]);
            ctr.setAccessible(true);
            final Event event = (Event)ctr.newInstance(new Object[0]);
            final ASMEventHandler listener = new ASMEventHandler(target, method);
            event.getListenerList().register(this.busID, listener.getPriority(), listener);
            ArrayList<IEventListener> others = this.listeners.get(target);
            if (others == null) {
                others = new ArrayList<IEventListener>();
                this.listeners.put(target, others);
            }
            others.add(listener);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void unregister(final Object object) {
        final ArrayList<IEventListener> list = this.listeners.remove(object);
        for (final IEventListener listener : list) {
            ListenerList.unregiterAll(this.busID, listener);
        }
    }
    
    public boolean post(final Event event) {
        final IEventListener[] arr$;
        final IEventListener[] listeners = arr$ = event.getListenerList().getListeners(this.busID);
        for (final IEventListener listener : arr$) {
            listener.invoke(event);
        }
        return event.isCancelable() && event.isCanceled();
    }
    
    static {
        EventBus.maxID = 0;
    }
}
