/**
 * 
 */
package org.geotools.feature.complex.feature;

import java.io.IOException;
import java.util.List;

import org.geotools.data.DataAccess;
import org.geotools.data.FeatureSource;
import org.geotools.data.ServiceInfo;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

/**
 * @author hgryoo
 *
 */
public class ComplexFeatureDataAccess implements DataAccess<FeatureType, Feature> {

    @Override
    public ServiceInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createSchema(FeatureType featureType) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateSchema(Name typeName, FeatureType featureType)
            throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeSchema(Name typeName) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Name> getNames() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeatureType getSchema(Name name) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeatureSource<FeatureType, Feature> getFeatureSource(Name typeName)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }

}
