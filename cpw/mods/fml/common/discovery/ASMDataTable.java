// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery;

import java.util.Set;
import java.util.Iterator;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.HashMultimap;
import java.util.List;
import cpw.mods.fml.common.ModContainer;
import java.util.Map;
import com.google.common.collect.SetMultimap;

public class ASMDataTable
{
    private SetMultimap<String, ASMData> globalAnnotationData;
    private Map<ModContainer, SetMultimap<String, ASMData>> containerAnnotationData;
    private List<ModContainer> containers;
    private SetMultimap<String, ModCandidate> packageMap;
    
    public ASMDataTable() {
        this.globalAnnotationData = (SetMultimap<String, ASMData>)HashMultimap.create();
        this.containers = Lists.newArrayList();
        this.packageMap = (SetMultimap<String, ModCandidate>)HashMultimap.create();
    }
    
    public SetMultimap<String, ASMData> getAnnotationsFor(final ModContainer container) {
        if (this.containerAnnotationData == null) {
            final ImmutableMap.Builder<ModContainer, SetMultimap<String, ASMData>> mapBuilder = (ImmutableMap.Builder<ModContainer, SetMultimap<String, ASMData>>)ImmutableMap.builder();
            for (final ModContainer cont : this.containers) {
                final Multimap<String, ASMData> values = (Multimap<String, ASMData>)Multimaps.filterValues((Multimap)this.globalAnnotationData, (Predicate)new ModContainerPredicate(cont));
                mapBuilder.put((Object)cont, (Object)ImmutableSetMultimap.copyOf((Multimap)values));
            }
            this.containerAnnotationData = (Map<ModContainer, SetMultimap<String, ASMData>>)mapBuilder.build();
        }
        return this.containerAnnotationData.get(container);
    }
    
    public Set<ASMData> getAll(final String annotation) {
        return this.globalAnnotationData.get((Object)annotation);
    }
    
    public void addASMData(final ModCandidate candidate, final String annotation, final String className, final String objectName, final Map<String, Object> annotationInfo) {
        this.globalAnnotationData.put((Object)annotation, (Object)new ASMData(candidate, annotation, className, objectName, annotationInfo));
    }
    
    public void addContainer(final ModContainer container) {
        this.containers.add(container);
    }
    
    public void registerPackage(final ModCandidate modCandidate, final String pkg) {
        this.packageMap.put((Object)pkg, (Object)modCandidate);
    }
    
    public Set<ModCandidate> getCandidatesFor(final String pkg) {
        return this.packageMap.get((Object)pkg);
    }
    
    public static final class ASMData implements Cloneable
    {
        private ModCandidate candidate;
        private String annotationName;
        private String className;
        private String objectName;
        private Map<String, Object> annotationInfo;
        
        public ASMData(final ModCandidate candidate, final String annotationName, final String className, final String objectName, final Map<String, Object> info) {
            this.candidate = candidate;
            this.annotationName = annotationName;
            this.className = className;
            this.objectName = objectName;
            this.annotationInfo = info;
        }
        
        public ModCandidate getCandidate() {
            return this.candidate;
        }
        
        public String getAnnotationName() {
            return this.annotationName;
        }
        
        public String getClassName() {
            return this.className;
        }
        
        public String getObjectName() {
            return this.objectName;
        }
        
        public Map<String, Object> getAnnotationInfo() {
            return this.annotationInfo;
        }
        
        public ASMData copy(final Map<String, Object> newAnnotationInfo) {
            try {
                final ASMData clone = (ASMData)this.clone();
                clone.annotationInfo = newAnnotationInfo;
                return clone;
            }
            catch (final CloneNotSupportedException e) {
                throw new RuntimeException("Unpossible", e);
            }
        }
    }
    
    private static class ModContainerPredicate implements Predicate<ASMData>
    {
        private ModContainer container;
        
        public ModContainerPredicate(final ModContainer container) {
            this.container = container;
        }
        
        public boolean apply(final ASMData data) {
            return this.container.getSource().equals(data.candidate.getModContainer());
        }
    }
}
