/**
 * 
 */
package org.geotools.gml3.v3_2.complex.binding;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.geotools.feature.AttributeImpl;
import org.geotools.feature.ComplexFeatureBuilder;
import org.geotools.feature.NameImpl;
import org.geotools.feature.complex.ComplexFeatureTypeRegistry;
import org.geotools.gml2.FeatureTypeCache;
import org.geotools.util.Converters;
import org.geotools.util.logging.Logging;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

/**
 * @author hgryoo
 *
 */
public class GMLComplexParsingUtils {
    /**
     * logging instance
     */
    static Logger LOGGER = Logging.getLogger( "org.geotools.gml" );
    
    public static Feature parseFeature(ElementInstance instance, Node node, Object value,
            FeatureTypeCache ftCache, ComplexFeatureTypeRegistry registry) throws Exception {

        //get the definition of the element
        XSDElementDeclaration decl = instance.getElementDeclaration();
        
        if(decl.getName().equalsIgnoreCase("Transition")) {
            System.out.println(decl);
        }
        
        FeatureType fType = null;
        if (!decl.isAbstract()) {
            XSDTypeDefinition def = decl.getTypeDefinition();
            Name name = new NameImpl(decl.getTargetNamespace(), decl.getName()); 
            Name typeName = new NameImpl(def.getTargetNamespace(), def.getName());
            
            fType = ftCache.get(typeName);
            if(fType == null) {
                AttributeDescriptor descriptor;
                descriptor = registry.getDescriptor(name, null, decl);
                fType = (FeatureType) descriptor.getType();
                ftCache.put(fType);
                //fType = featureType(decl, bwFactory, null, new FeatureTypeFactoryImpl());
            }
        } else {
            Name name = new NameImpl(node.getComponent().getNamespace(), node
                    .getComponent().getName());
            
            fType = ftCache.get(name);
            if(fType == null) {
                AttributeDescriptor descriptor = registry.getDescriptor(name, null);
                fType = (FeatureType) descriptor.getType();
                ftCache.put(fType);
            }
        }
        
      //fid
        String fid = (String) node.getAttributeValue("fid");

        if (fid == null) {
            //look for id
            fid = (String) node.getAttributeValue("id");
        }
        
        return GMLComplexParsingUtils.feature(fType, fid, node);
    }
    
    public static Feature feature(FeatureType type, String fid, Node node) {
        
        ComplexFeatureBuilder featureBuilder = new ComplexFeatureBuilder(type);
        
        Collection<PropertyDescriptor> descriptors = type.getDescriptors();
        for(Iterator<PropertyDescriptor> it = descriptors.iterator(); it.hasNext();) {
            PropertyDescriptor propDesc = it.next();
            PropertyType propType = propDesc.getType();
            
            List propValues = node.getChildValues(propDesc.getName().getLocalPart());
            for(Object values : propValues) {
                Class<?> binding = propType.getBinding();
                
                if ((values != null) && !propType.getBinding().isAssignableFrom(values.getClass())) {
                    //type mismatch, to try convert
                    Object converted = Converters.convert(values, propType.getBinding());
    
                    if (converted != null) {
                        values = converted;
                    }
                }
                
                if(values != null) {
                    if(!Property.class.isAssignableFrom(values.getClass())) {
                        featureBuilder.append(propDesc.getName(), 
                                new AttributeImpl(values, 
                                        (AttributeDescriptor) propDesc,
                                        null));
                    } else {
                        featureBuilder.append(propDesc.getName(),
                                (Property) values);
                    }
                }
            }
        }
        
        
        return featureBuilder.buildFeature(fid);
    }
    
}
