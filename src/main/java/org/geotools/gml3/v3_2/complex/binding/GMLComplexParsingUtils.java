/**
 * 
 */
package org.geotools.gml3.v3_2.complex.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.geotools.data.complex.config.Types;
import org.geotools.feature.AttributeBuilder;
import org.geotools.feature.AttributeImpl;
import org.geotools.feature.ComplexFeatureBuilder;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.LenientFeatureFactoryImpl;
import org.geotools.feature.NameImpl;
import org.geotools.feature.complex.ComplexFeatureTypeRegistry;
import org.geotools.feature.complex.HrefMap;
import org.geotools.gml2.FeatureTypeCache;
import org.geotools.util.Converters;
import org.geotools.util.logging.Logging;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
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
            FeatureTypeCache ftCache, ComplexFeatureTypeRegistry registry, HrefMap hrefMap) throws Exception {

        //get the definition of the element
        XSDElementDeclaration decl = instance.getElementDeclaration();
        
        FeatureType fType = null;
        if (!decl.isAbstract()) {
            XSDTypeDefinition def = decl.getTypeDefinition();
            Name name = new NameImpl(decl.getTargetNamespace(), decl.getName()); 
            Name typeName = new NameImpl(def.getTargetNamespace(), def.getName());
            
            //fType = ftCache.get(typeName);
            //if(fType == null) {
                AttributeDescriptor descriptor;
                descriptor = registry.getDescriptor(name, null, decl);
                fType = (FeatureType) descriptor.getType();
                //ftCache.put(fType);
                //fType = featureType(decl, bwFactory, null, new FeatureTypeFactoryImpl());
            //}
        } else {
            Name name = new NameImpl(node.getComponent().getNamespace(), node
                    .getComponent().getName());
            
            //fType = ftCache.get(name);
            //if(fType == null) {
                AttributeDescriptor descriptor = registry.getDescriptor(name, null, decl);
                fType = (FeatureType) descriptor.getType();
                //ftCache.put(fType);
            //}
        }
        
      //fid
        String fid = (String) node.getAttributeValue("fid");

        if (fid == null) {
            //look for id
            fid = (String) node.getAttributeValue("id");
        }
        
        //we make fid automatically.
        if (fid == null) {
            fid = UUID.randomUUID().toString();
        }
        
        return GMLComplexParsingUtils.feature(fType, fid, node, hrefMap);
    }
    
    public static Feature feature(FeatureType type, String fid, Node node, HrefMap map) {
        
        ComplexFeatureBuilder featureBuilder = new ComplexFeatureBuilder(type);
        
        Collection<PropertyDescriptor> descriptors = type.getDescriptors();
        for(Iterator<PropertyDescriptor> it = descriptors.iterator(); it.hasNext();) {
            PropertyDescriptor propDesc = it.next();
            PropertyType propType = propDesc.getType();
            
            List propValues = node.getChildValues(propDesc.getName().getLocalPart());
            for(Object value : propValues) {
                Class<?> binding = propType.getBinding();
                
                if(propType instanceof ComplexType) {
                    AttributeBuilder attributeBuilder = new AttributeBuilder(
                            new LenientFeatureFactoryImpl());
                    attributeBuilder.setType((AttributeType) propType);
                    
                    if (propType.getBinding() == Collection.class
                            && Types.isSimpleContentType(type)) {
                        ArrayList<Property> list = new ArrayList<Property>();
                    }
                    
                } else if (propType instanceof AttributeType) {
                    if(propType instanceof GeometryType) {
                        featureBuilder.append(propDesc.getName(),
                                new GeometryAttributeImpl(value,
                                        (GeometryDescriptor)propDesc, null));
                    } else {
                        Object converted = Converters.convert(value, binding);
                        
                        if (converted != null) {
                            value = converted;
                        }
                        
                        featureBuilder.append(propDesc.getName(),
                                new AttributeImpl(value,
                                        (AttributeDescriptor)propDesc, null));
                    }
                }
/*                
                if ((values != null) && !binding.isAssignableFrom(values.getClass())) {
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
                }*/
            }
        }
        
        Feature f = featureBuilder.buildFeature(fid);
        map.register(fid, f);
        return f;
    }
}
