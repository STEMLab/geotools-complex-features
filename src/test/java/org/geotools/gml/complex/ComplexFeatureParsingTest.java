package org.geotools.gml.complex;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.data.DataUtilities;
import org.geotools.data.complex.config.EmfComplexFeatureReader;
import org.geotools.data.complex.config.FeatureTypeRegistry;
import org.geotools.feature.NameImpl;
import org.geotools.feature.complex.ComplexFeatureTypeRegistry;
import org.geotools.feature.complex.ComplexGMLConfiguration;
import org.geotools.feature.complex.NewXmlComplexFeatureParser;
import org.geotools.feature.type.ComplexFeatureTypeFactoryImpl;
import org.geotools.feature.type.FeatureTypeFactoryImpl;
import org.geotools.gml3.complex.GmlFeatureTypeRegistryConfiguration;
import org.geotools.xml.PullParser;
import org.geotools.xml.SchemaIndex;
import org.geotools.xml.resolver.SchemaCache;
import org.geotools.xml.resolver.SchemaResolver;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.Name;

/**
 * 
 */

/**
 * @author hgryoo
 *
 */
public class ComplexFeatureParsingTest {

    @Test
    public void SaxStyleParsingTest() throws Exception {
        //Schema download and resolve
        File cacheDirectory = new File(DataUtilities.urlToFile(ComplexFeatureParsingTest.class
                .getResource("/")), "/schemas");
        SchemaResolver resolver = new SchemaResolver( new SchemaCache(cacheDirectory, true));
        
        EmfComplexFeatureReader reader = EmfComplexFeatureReader.newInstance();
        reader.setResolver(resolver);
        //SchemaIndex schemaIndex = reader.parse(new URL("http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd"));
        
        SchemaIndex schemaIndex = reader.parse(getClass().getResource("indoorgmlcore.xsd"));
        
        FeatureTypeFactory factory = new ComplexFeatureTypeFactoryImpl();
        FeatureTypeRegistry registry = new FeatureTypeRegistry(factory,
                new GmlFeatureTypeRegistryConfiguration(null));
        registry.addSchemas(schemaIndex);

        InputStream in = getClass().getResourceAsStream("SMALL.gml");
       
        AttributeDescriptor descriptor = registry.getDescriptor(
                new NameImpl("http://www.opengis.net/indoorgml/1.0/core",":" ,"IndoorFeatures"), null);
        AttributeType type = descriptor.getType();
       
        Name descName = descriptor.getName();
        
        NewXmlComplexFeatureParser featureParser = new NewXmlComplexFeatureParser(
                in,
                (FeatureType)type, new QName(descName.getNamespaceURI(),
                        descName.getLocalPart()));
        
        Feature feature = featureParser.parse();
        
        if(feature == null) {
            throw new NullPointerException("feature parsing failed");
        }
    }
    
    @Test
    public void configurationStylePrasingTest() throws Exception {
        //Schema download and resolve
        File cacheDirectory = new File(DataUtilities.urlToFile(ComplexFeatureParsingTest.class
                .getResource("/")), "/schemas");
        SchemaResolver resolver = new SchemaResolver( new SchemaCache(cacheDirectory, true));
        
        EmfComplexFeatureReader reader = EmfComplexFeatureReader.newInstance();
        reader.setResolver(resolver);
        SchemaIndex schemaIndex = reader.parse(new URL("http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd"));
        
        FeatureTypeFactory factory = new FeatureTypeFactoryImpl();
        
        ComplexFeatureTypeRegistry registry = new ComplexFeatureTypeRegistry(factory,
                new GmlFeatureTypeRegistryConfiguration(null));
        registry.addSchemas(schemaIndex);

        InputStream in = getClass().getResourceAsStream("SMALL.gml");
        ComplexGMLConfiguration configuration = new ComplexGMLConfiguration();
        configuration.setFeatureTypeRegistry(registry);
        PullParser parser = new PullParser( configuration, in, new QName("http://www.opengis.net/indoorgml/1.0/core","IndoorFeatures") );
        
        int nfeatures = 0;
        
        List<Feature> fList = new ArrayList<Feature>();
        Feature f = null;
        while( ( f = (Feature) parser.parse() ) != null ) {
            fList.add(f);
        } 
        
        AttributeDescriptor descriptor = registry.getDescriptor(new NameImpl("http://www.opengis.net/indoorgml/1.0/core",":" ,"IndoorFeatures"), null);
        
        System.out.println();
    }

}
