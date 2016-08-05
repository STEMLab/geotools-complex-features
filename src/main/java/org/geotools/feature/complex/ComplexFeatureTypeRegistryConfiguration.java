/**
 * 
 */
package org.geotools.feature.complex;

import org.eclipse.xsd.XSDTypeDefinition;
import org.geotools.xml.complex.FeatureTypeRegistryConfiguration;

/**
 * @author hgryoo
 *
 */
public interface ComplexFeatureTypeRegistryConfiguration extends
        FeatureTypeRegistryConfiguration {

    Class<?> getBinding(XSDTypeDefinition typeDefinition);

}
