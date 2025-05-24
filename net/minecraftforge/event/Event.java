// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Event
{
    private boolean isCanceled;
    private final boolean isCancelable;
    private Result result;
    private final boolean hasResult;
    private static ListenerList listeners;
    private static final Map<Class, Map<Class, Boolean>> annotationMap;
    
    public Event() {
        this.isCanceled = false;
        this.result = Result.DEFAULT;
        this.setup();
        this.isCancelable = this.hasAnnotation(Cancelable.class);
        this.hasResult = this.hasAnnotation(HasResult.class);
    }
    
    private boolean hasAnnotation(final Class annotation) {
        final Class me = this.getClass();
        Map<Class, Boolean> list = Event.annotationMap.get(me);
        if (list == null) {
            list = new ConcurrentHashMap<Class, Boolean>();
            Event.annotationMap.put(me, list);
        }
        final Boolean cached = list.get(annotation);
        if (cached != null) {
            return cached;
        }
        for (Class cls = me; cls != Event.class; cls = cls.getSuperclass()) {
            if (cls.isAnnotationPresent(annotation)) {
                list.put(annotation, true);
                return true;
            }
        }
        list.put(annotation, false);
        return false;
    }
    
    public boolean isCancelable() {
        return this.isCancelable;
    }
    
    public boolean isCanceled() {
        return this.isCanceled;
    }
    
    public void setCanceled(final boolean cancel) {
        if (!this.isCancelable()) {
            throw new IllegalArgumentException("Attempted to cancel a uncancelable event");
        }
        this.isCanceled = cancel;
    }
    
    public boolean hasResult() {
        return this.hasResult;
    }
    
    public Result getResult() {
        return this.result;
    }
    
    public void setResult(final Result value) {
        this.result = value;
    }
    
    protected void setup() {
    }
    
    public ListenerList getListenerList() {
        return Event.listeners;
    }
    
    static {
        Event.listeners = new ListenerList();
        annotationMap = new ConcurrentHashMap<Class, Map<Class, Boolean>>();
    }
    
    public enum Result
    {
        DENY, 
        DEFAULT, 
        ALLOW;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface HasResult {
    }
}
