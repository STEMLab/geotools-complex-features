/**
 * 
 */
package org.graphstream.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

/**
 * @author hgryoo
 *
 */
public class GraphVisualization {

    private Graph graph;
    private SpriteManager sman;
    private Viewer viewer;
    
    private Map<String, Node> visited = new HashMap<String, Node>();
    
    private Map<Node, PropertyType> types = new HashMap<Node, PropertyType>();
    
    private ClickListener listener = new ClickListener();
    
    ViewerPipe fromViewer;
    
    protected boolean loop = true;
    
    public GraphVisualization(FeatureType topLevelType) {
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        graph = new SingleGraph("feature graph", false, true);
        graph.addAttribute("ui.stylesheet", "url(' "+getClass().getResource("style.css").toString() +"')");
        sman = new SpriteManager(graph);
        
        traverseFeatureType(topLevelType);
        
        viewer = graph.display(true);
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(listener);
        fromViewer.addSink(graph);
        
        while(loop) {
            fromViewer.pump();
        }
    }
    
    public Node traverseComplexType(ComplexType c) {
        String cName = c.getName().getLocalPart();
        
        Node cNode = graph.addNode(UUID.randomUUID().toString());
        types.put(cNode, c);
        
        cNode.addAttribute("ui.class", "complex, invisible");
        cNode.addAttribute("ui.label", cName);
        
        Iterator<PropertyDescriptor> it = c.getDescriptors().iterator();
        while(it.hasNext()) {
            PropertyDescriptor desc = it.next();
            PropertyType type = desc.getType();
            
            String descName = desc.getName().getLocalPart();
            
            Node to = traverseNextType(type);
            Edge e = graph.addEdge(UUID.randomUUID().toString(), cNode, to, true);
            e.addAttribute("ui.label", descName);
        }
        
        return cNode;
    }
    
    public Node traverseNextType(PropertyType type) {
        Node to = null;
        if(type instanceof FeatureType) {
            to = traverseFeatureType((FeatureType) type);
        } else if(type instanceof ComplexType) {
            to = traverseComplexType((ComplexType) type);
        } else if(type instanceof AssociationType) {
            to = traverseAssociationType((AssociationType) type);
        } else {
            String attrName = type.getName().getLocalPart();
            to = graph.addNode(UUID.randomUUID().toString());
            to.addAttribute("ui.label", attrName);
            
            if(type instanceof GeometryType) {
               to.addAttribute("ui.class", "geometry, invisible");
            } else {
               to.addAttribute("ui.class", "attribute, invisible");
            }
        }
        return to;
    }
    
    public Node traverseAssociationType(AssociationType t) {
        String name = t.getName().getLocalPart();
        Node from = graph.addNode(UUID.randomUUID().toString());
        from.addAttribute("ui.class", "association, invisible");
        from.addAttribute("ui.label", name);
        
        AttributeType related = t.getRelatedType();
        Node to = traverseNextType(related);
        
        Edge e = graph.addEdge(UUID.randomUUID().toString(), from, to, true);
        e.addAttribute("ui.class", "association");
        
        return from;
    }
    
    public Node traverseFeatureType(FeatureType ft) {
        String fName = ft.getName().getLocalPart();
        Node fNode = null;
        if(visited.containsKey(fName)) {
            fNode = visited.get(fName);
        } else {
            fNode = graph.addNode(fName);
            types.put(fNode, ft);
            
            fNode.addAttribute("ui.class", "feature");
            fNode.addAttribute("ui.label", fName);
            visited.put(fName, fNode);
            
            Iterator<PropertyDescriptor> it = ft.getDescriptors().iterator();
            while(it.hasNext()) {
                PropertyDescriptor desc = it.next();
                PropertyType type = desc.getType();
                
                String descName = desc.getName().getLocalPart();
                Node to = traverseNextType(type);
                Edge e = graph.addEdge(UUID.randomUUID().toString(), fNode, to, true);
                e.addAttribute("ui.label", descName);
            }
        }
        return fNode;
    }
    
    class ClickListener implements ViewerListener {

        @Override
        public void viewClosed(String viewName) {
            loop = false;
        }

        public void buttonPushed(String id) {
            Node n = graph.getNode(id);
            
            PropertyType type = types.get(n);
            System.out.println(type);
            
            String clazz = ((String)n.getAttribute("ui.class"));
            if(clazz != null && clazz.contains("invisible")) {
                String[] st = clazz.split(",");
                n.addAttribute("ui.class", st[0] + ", visible");
            }
        }
    
        public void buttonReleased(String id) {
            Node n = graph.getNode(id);
            
            String clazz = ((String)n.getAttribute("ui.class"));
            if(clazz != null && clazz.contains("visible")) {
                String[] st = clazz.split(",");
                n.addAttribute("ui.class", st[0] + ", invisible");
            }
        }
    }
}
