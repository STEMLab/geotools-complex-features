package org.geotools.gml.complex;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.data.DataUtilities;
import org.geotools.data.complex.config.EmfComplexFeatureReader;
import org.geotools.data.complex.config.FeatureTypeRegistry;
import org.geotools.feature.NameImpl;
import org.geotools.feature.complex.ComplexFeatureTypeRegistry;
import org.geotools.feature.complex.NewXmlComplexFeatureParser;
import org.geotools.feature.type.ComplexFeatureTypeFactoryImpl;
import org.geotools.feature.type.FeatureTypeFactoryImpl;
import org.geotools.gml3.complex.GmlFeatureTypeRegistryConfiguration;
import org.geotools.gml3.v3_2.ComplexApplicationSchemaConfiguration;
import org.geotools.xml.AppSchemaConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.PullParser;
import org.geotools.xml.SchemaIndex;
import org.geotools.xml.Schemas;
import org.geotools.xml.resolver.SchemaCache;
import org.geotools.xml.resolver.SchemaResolver;
import org.graphstream.ui.GraphVisualization;
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
        
        Configuration configuration = new AppSchemaConfiguration("http://www.opengis.net/indoorgml/1.0/core",
                "http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd", resolver);
        //SchemaIndex schemaIndex = null;
        try {
            //schemaIndex = Schemas.findSchemas(configuration);
        
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
        } finally {
            if (schemaIndex != null) {
                schemaIndex.destroy();
            }
        }
    }
    
    @Test
    public void configurationStyleParsingTest() throws Exception {
        //Schema download and resolve
        File cacheDirectory = new File(DataUtilities.urlToFile(ComplexFeatureParsingTest.class
                .getResource("/")), "/schemas");
        SchemaResolver resolver = new SchemaResolver( new SchemaCache(cacheDirectory, true));
        
        ComplexApplicationSchemaConfiguration conf = new ComplexApplicationSchemaConfiguration("http://www.opengis.net/indoorgml/1.0/core",
                "http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd", resolver);
        
        SchemaIndex schemaIndex = null;
        try {
            schemaIndex = Schemas.findSchemas(conf);

            ComplexFeatureTypeRegistry registry = new ComplexFeatureTypeRegistry(new FeatureTypeFactoryImpl(),
                    new GmlFeatureTypeRegistryConfiguration(null));
            registry.addSchemas(schemaIndex);
            
            conf.setFeatureTypeRegistry(registry);
            InputStream in = getClass().getResourceAsStream("SMALL.gml");
            PullParser parser = new PullParser( conf, in, new QName("http://www.opengis.net/indoorgml/1.0/core","IndoorFeatures") );
             
            List<Feature> fList = new ArrayList<Feature>();
            Feature f = null;
            while( ( f = (Feature) parser.parse() ) != null ) {
                fList.add(f);
            }
            
            AttributeDescriptor descriptor = registry.getDescriptor(new NameImpl("http://www.opengis.net/indoorgml/1.0/core",":" ,"CellSpace"), null);
            FeatureType topType = (FeatureType) descriptor.getType();
            
            System.out.println();
            
        } finally {
            if (schemaIndex != null) {
                schemaIndex.destroy();
            }
        }
    }

    @Test
    public void registryTest() throws Exception {
        //Schema download and resolve
        File cacheDirectory = new File(DataUtilities.urlToFile(ComplexFeatureParsingTest.class
                .getResource("/")), "/schemas");
        SchemaResolver resolver = new SchemaResolver( new SchemaCache(cacheDirectory, true));
        
        ComplexApplicationSchemaConfiguration conf = new ComplexApplicationSchemaConfiguration("http://www.opengis.net/indoorgml/1.0/core",
                "http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd", resolver);
        
        SchemaIndex schemaIndex = null;
        try {
            schemaIndex = Schemas.findSchemas(conf);

            ComplexFeatureTypeRegistry registry = new ComplexFeatureTypeRegistry(new FeatureTypeFactoryImpl(),
                    new GmlFeatureTypeRegistryConfiguration(null));
            registry.addSchemas(schemaIndex);
            
            AttributeDescriptor descriptor = registry.getDescriptor(new NameImpl("http://www.opengis.net/indoorgml/1.0/core",":" ,"CellSpace"), null);
            FeatureType fType = (FeatureType) descriptor.getType();
            
            System.out.println();
            
        } finally {
            if (schemaIndex != null) {
                schemaIndex.destroy();
            }
        }
    }
    
    private Feature parseFeatureType() throws Exception {
        File cacheDirectory = new File(DataUtilities.urlToFile(ComplexFeatureParsingTest.class
                .getResource("/")), "/schemas");
        SchemaResolver resolver = new SchemaResolver( new SchemaCache(cacheDirectory, true));
        
        EmfComplexFeatureReader reader = EmfComplexFeatureReader.newInstance();
        reader.setResolver(resolver);
        
        SchemaIndex schemaIndex = reader.parse(getClass().getResource("indoorgmlcore.xsd"));
        Feature f;
        try {
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
            
            f = featureParser.parse();
            
            if(f == null) {
                throw new NullPointerException("feature parsing failed");
            }
        } finally {
            if (schemaIndex != null) {
                schemaIndex.destroy();
            }
        }
        return f;
    }
    
    @Test
    public void testVisualization() throws Exception {
        
        Feature f = parseFeatureType();
        FeatureType fType = f.getType();
        
        GraphVisualization v = new GraphVisualization(fType);
                System.out.println();
    }
    
}
