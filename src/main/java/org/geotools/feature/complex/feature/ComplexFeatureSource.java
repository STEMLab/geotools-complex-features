/**
 * 
 */
package org.geotools.feature.complex.feature;

import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.Set;

import org.geotools.data.DataAccess;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

/**
 * @author hgryoo
 *
 */
public class ComplexFeatureSource implements FeatureSource<FeatureType, Feature> {

    @Override
    public Name getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResourceInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataAccess<FeatureType, Feature> getDataStore() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addFeatureListener(FeatureListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeFeatureListener(FeatureListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public FeatureCollection<FeatureType, Feature> getFeatures(Filter filter)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeatureCollection<FeatureType, Feature> getFeatures(Query query)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeatureCollection<FeatureType, Feature> getFeatures()
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeatureType getSchema() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReferencedEnvelope getBounds() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReferencedEnvelope getBounds(Query query) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCount(Query query) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Set<Key> getSupportedHints() {
        // TODO Auto-generated method stub
        return null;
    }

}
