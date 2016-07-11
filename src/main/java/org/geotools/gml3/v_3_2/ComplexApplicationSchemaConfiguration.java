/**
 * 
 */
package org.geotools.gml3.v_3_2;

import org.geotools.feature.complex.ComplexFeatureTypeRegistry;
import org.geotools.gml3.v3_2.GML;
import org.geotools.gml3.v3_2.GMLConfiguration;
import org.geotools.gml3.v3_2.complex.binding.ComplexFeatureTypeBinding;
import org.geotools.xml.Configuration;
import org.geotools.xs.XSConfiguration;
import org.picocontainer.MutablePicoContainer;


/**
 * @author hgryoo
 *
 */
public class ComplexApplicationSchemaConfiguration extends Configuration {
    /**
     * Creates a new configuration.
     * 
     * @generated
     */     
    public ComplexApplicationSchemaConfiguration(String namespace, String schemaLocation) {
        super(new ApplicationSchemaXSD(namespace, schemaLocation));
        addDependency(new XSConfiguration());
        addDependency(new GMLConfiguration());
    }
    
    private ComplexFeatureTypeRegistry registry;
    public void setFeatureTypeRegistry(ComplexFeatureTypeRegistry registry) {
        this.registry = registry;
    }
    
    protected void registerBindings(MutablePicoContainer container) {
        //Types
        container.unregisterComponent(GML.AbstractFeatureType);
        container.registerComponentImplementation(GML.AbstractFeatureType,
                ComplexFeatureTypeBinding.class);
    }
    
    @Override
    protected void configureContext(MutablePicoContainer container) {
        super.configureContext(container);
        //container.registerComponentInstance(new FeatureCache());
        container.registerComponentInstance(registry);
    }
}
