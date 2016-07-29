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
    
    public void traverseType(PropertyType type) {
        
    }
    
    public Node traverseComplexType(ComplexType c, String parentName) {
        String cName = c.getName().getLocalPart();
        Node cNode = graph.addNode(UUID.randomUUID().toString());
        cNode.addAttribute("ui.class", "complex, invisible");
        cNode.addAttribute("ui.label", cName);
        
        Iterator<PropertyDescriptor> it = c.getDescriptors().iterator();
        while(it.hasNext()) {
            PropertyDescriptor desc = it.next();
            PropertyType type = desc.getType();
            
            String descName = desc.getName().getLocalPart();
            Node to = null;
            if(type instanceof FeatureType) {
                to = traverseFeatureType((FeatureType) type);
            } else if(type instanceof ComplexType) {
                to = traverseComplexType((ComplexType) type, cName);
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
            Edge e = graph.addEdge(UUID.randomUUID().toString(), cNode, to, true);
            e.addAttribute("ui.label", descName);
        }
        
        return cNode;
    }
    
    public Node traverseFeatureType(FeatureType ft) {
        String fName = ft.getName().getLocalPart();
        Node fNode = null;
        if(visited.containsKey(fName)) {
            fNode = visited.get(fName);
        } else {
            fNode = graph.addNode(fName);
            fNode.addAttribute("ui.class", "feature");
            fNode.addAttribute("ui.label", fName);
            visited.put(fName, fNode);
            
            Iterator<PropertyDescriptor> it = ft.getDescriptors().iterator();
            while(it.hasNext()) {
                PropertyDescriptor desc = it.next();
                PropertyType type = desc.getType();
                
                String descName = desc.getName().getLocalPart();
                Node to = null;
                if(type instanceof FeatureType) {
                    to = traverseFeatureType((FeatureType) type);
                } else if(type instanceof ComplexType) {
                    to = traverseComplexType((ComplexType) type, fName);
                } else {
                    String attrName = type.getName().getLocalPart();
                    to = graph.addNode(UUID.randomUUID().toString());
                    to.addAttribute("ui.label", attrName);
                }
                Edge e = graph.addEdge(UUID.randomUUID().toString(), fNode, to, true);
                e.addAttribute("ui.label", descName);
            }
        }
        return fNode;
    }
    
    class ClickListener implements ViewerListener {

        @Override
        public void viewClosed(String viewName) {
            // TODO Auto-generated method stub
            loop = false;
        }

        public void buttonPushed(String id) {
            Node n = graph.getNode(id);
            if(((String)n.getAttribute("ui.class")).contains("complex")) {
                n.addAttribute("ui.class", "complex, visible");
            }
            System.out.println("Button pushed on node "+id);
        }
    
        public void buttonReleased(String id) {
            Node n = graph.getNode(id);
            if(((String)n.getAttribute("ui.class")).contains("complex")) {
                n.addAttribute("ui.class", "complex, invisible");
            }
            System.out.println("Button released on node "+ graph.getNode(id).getAttribute("ui.label"));
        }
        
        
        
    }
}
