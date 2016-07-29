/**
 * 
 */
package org.geotools.gml3.v3_2;

import org.geotools.feature.complex.ComplexFeatureTypeRegistry;
import org.geotools.feature.complex.HrefMap;
import org.geotools.gml3.v3_2.GML;
import org.geotools.gml3.v3_2.GMLConfiguration;
import org.geotools.gml3.v3_2.complex.binding.ComplexFeatureTypeBinding;
import org.geotools.xml.Configuration;
import org.geotools.xml.resolver.SchemaResolver;
import org.geotools.xml.xLink.XLinkSchema.Href;
import org.geotools.xs.XSConfiguration;
import org.picocontainer.MutablePicoContainer;


/**
 * @author hgryoo
 *
 */
public class ComplexApplicationSchemaConfiguration extends Configuration {
    
    /**
     * Original (unresolved) schema location.
     */
    private final String originalSchemaLocation;

    /**
     * Creates a new configuration.
     * 
     * @generated
     */
    /*
    public ComplexApplicationSchemaConfiguration(String namespace, String schemaLocation) {
        super(new ApplicationSchemaXSD(namespace, schemaLocation));
        addDependency(new XSConfiguration());
        addDependency(new GMLConfiguration());
    }
    */
    
    /**
     * Because we do not know the dependent GML {@link Configuration} until runtime, it must be
     * specified as a constructor argument.
     * 
     * @param namespace
     *            the namespace URI
     * @param schemaLocation
     *            URL giving canonical schema location
     * @param resolver
     */
    public ComplexApplicationSchemaConfiguration(String namespace, String schemaLocation,
            SchemaResolver resolver) {
        super(new ComplexAppSchemaXSD(namespace, schemaLocation, resolver));
        addDependency(new XSConfiguration());
        addDependency(new GMLConfiguration());
        originalSchemaLocation = schemaLocation;
        ((ComplexAppSchemaXSD) getXSD()).setConfiguration(this);
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
        container.registerComponentInstance(new HrefMap());
        container.registerComponentInstance(registry);
        //container.registerComponentInstance(new FeatureTypeCache());
    }
    
    /**
     * Get the original (unresolved) schema location.
     * 
     * @return the schema location
     */
    public String getSchemaLocation() {
        return originalSchemaLocation;
    }

    /**
     * Allow late addition of a dependency such as GML.
     * 
     * @see org.geotools.xml.Configuration#addDependency(org.geotools.xml.Configuration)
     */
    @Override
    public void addDependency(Configuration dependency) {
        super.addDependency(dependency);
    }
}
