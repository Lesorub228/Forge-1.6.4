// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.toposort;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.Map;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLLog;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class TopologicalSort
{
    public static <T> List<T> topologicalSort(final DirectedGraph<T> graph) {
        final DirectedGraph<T> rGraph = reverse(graph);
        final List<T> sortedResult = new ArrayList<T>();
        final Set<T> visitedNodes = new HashSet<T>();
        final Set<T> expandedNodes = new HashSet<T>();
        for (final T node : rGraph) {
            explore(node, rGraph, sortedResult, visitedNodes, expandedNodes);
        }
        return sortedResult;
    }
    
    public static <T> DirectedGraph<T> reverse(final DirectedGraph<T> graph) {
        final DirectedGraph<T> result = new DirectedGraph<T>();
        for (final T node : graph) {
            result.addNode(node);
        }
        for (final T from : graph) {
            for (final T to : graph.edgesFrom(from)) {
                result.addEdge(to, from);
            }
        }
        return result;
    }
    
    public static <T> void explore(final T node, final DirectedGraph<T> graph, final List<T> sortedResult, final Set<T> visitedNodes, final Set<T> expandedNodes) {
        if (!visitedNodes.contains(node)) {
            visitedNodes.add(node);
            for (final T inbound : graph.edgesFrom(node)) {
                explore(inbound, (DirectedGraph<Object>)graph, (List<Object>)sortedResult, (Set<Object>)visitedNodes, (Set<Object>)expandedNodes);
            }
            sortedResult.add(node);
            expandedNodes.add(node);
            return;
        }
        if (expandedNodes.contains(node)) {
            return;
        }
        FMLLog.severe("Mod Sorting failed.", new Object[0]);
        FMLLog.severe("Visting node %s", node);
        FMLLog.severe("Current sorted list : %s", sortedResult);
        FMLLog.severe("Visited set for this node : %s", visitedNodes);
        FMLLog.severe("Explored node set : %s", expandedNodes);
        final Sets.SetView<T> cycleList = (Sets.SetView<T>)Sets.difference((Set)visitedNodes, (Set)expandedNodes);
        FMLLog.severe("Likely cycle is in : %s", cycleList);
        throw new ModSortingException("There was a cycle detected in the input graph, sorting is not possible", (T)node, (Set<T>)cycleList);
    }
    
    public static class DirectedGraph<T> implements Iterable<T>
    {
        private final Map<T, SortedSet<T>> graph;
        private List<T> orderedNodes;
        
        public DirectedGraph() {
            this.graph = new HashMap<T, SortedSet<T>>();
            this.orderedNodes = new ArrayList<T>();
        }
        
        public boolean addNode(final T node) {
            if (this.graph.containsKey(node)) {
                return false;
            }
            this.orderedNodes.add(node);
            this.graph.put(node, new TreeSet<T>(new Comparator<T>() {
                @Override
                public int compare(final T o1, final T o2) {
                    return DirectedGraph.this.orderedNodes.indexOf(o1) - DirectedGraph.this.orderedNodes.indexOf(o2);
                }
            }));
            return true;
        }
        
        public void addEdge(final T from, final T to) {
            if (!this.graph.containsKey(from) || !this.graph.containsKey(to)) {
                throw new NoSuchElementException("Missing nodes from graph");
            }
            this.graph.get(from).add(to);
        }
        
        public void removeEdge(final T from, final T to) {
            if (!this.graph.containsKey(from) || !this.graph.containsKey(to)) {
                throw new NoSuchElementException("Missing nodes from graph");
            }
            this.graph.get(from).remove(to);
        }
        
        public boolean edgeExists(final T from, final T to) {
            if (!this.graph.containsKey(from) || !this.graph.containsKey(to)) {
                throw new NoSuchElementException("Missing nodes from graph");
            }
            return this.graph.get(from).contains(to);
        }
        
        public Set<T> edgesFrom(final T from) {
            if (!this.graph.containsKey(from)) {
                throw new NoSuchElementException("Missing node from graph");
            }
            return (Set<T>)Collections.unmodifiableSortedSet((SortedSet<Object>)this.graph.get(from));
        }
        
        @Override
        public Iterator<T> iterator() {
            return this.orderedNodes.iterator();
        }
        
        public int size() {
            return this.graph.size();
        }
        
        public boolean isEmpty() {
            return this.graph.isEmpty();
        }
        
        @Override
        public String toString() {
            return this.graph.toString();
        }
    }
}
