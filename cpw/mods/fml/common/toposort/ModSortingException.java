// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.toposort;

import java.util.Set;

public class ModSortingException extends RuntimeException
{
    private SortingExceptionData sortingExceptionData;
    
    public <T> ModSortingException(final String string, final T node, final Set<T> visitedNodes) {
        super(string);
        this.sortingExceptionData = new SortingExceptionData((T)node, (Set<T>)visitedNodes);
    }
    
    public <T> SortingExceptionData<T> getExceptionData() {
        return this.sortingExceptionData;
    }
    
    public class SortingExceptionData<T>
    {
        private T firstBadNode;
        private Set<T> visitedNodes;
        
        public SortingExceptionData(final T node, final Set<T> visitedNodes) {
            this.firstBadNode = node;
            this.visitedNodes = visitedNodes;
        }
        
        public T getFirstBadNode() {
            return this.firstBadNode;
        }
        
        public Set<T> getVisitedNodes() {
            return this.visitedNodes;
        }
    }
}
