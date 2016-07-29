/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotools.feature.complex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.AttributeImpl;
import org.opengis.feature.Attribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeType;

/**
 * @author Adam Brown (Curtin University of Technology)
 * @author Hyung-Gyu Ryoo (Pusan National University)
 *
 */
public class HrefMap {

    /**
     * This is a mapping which links string gml:ids to an attribute. It's used
     * to keep track of an id'd attributes so that we can refer back to them if
     * they're referred to further down the input stream by a href.
     */
    private Map<String, Attribute> discoveredComplexAttributes = new HashMap<String, Attribute>();

    /**
     * The placeholder complex attributes object maintains a record of
     * incomplete attributes that relate to a particular hrefed id.
     */
    private Map<String, ArrayList<Attribute>> placeholderComplexAttributes = new HashMap<String, ArrayList<Attribute>>();
    
    public void register(String id, Attribute value) {
        // Add the value to the discoveredComplexAttributes object:
        discoveredComplexAttributes.put(id, value);

        // Check whether anything is waiting for this attribute and, if so,
        // populate them.
        if (placeholderComplexAttributes.containsKey(id)) {
                for (Attribute placeholderComplexAttribute : this.placeholderComplexAttributes
                                .get(id)) {
                        placeholderComplexAttribute.setValue(value.getValue());
                }
        }
    }

    public Attribute resolveHref(String href, AttributeType expectedType) {
        if(href.startsWith("#")) {
            String hrefId = href.substring(1);
            
            if (discoveredComplexAttributes.containsKey(hrefId)) {
                return discoveredComplexAttributes.get(hrefId);
            } else {
             // If not, then we create a placeholderComplexAttribute instead:
                Attribute placeholderComplexAttribute = new AttributeImpl(
                                Collections.<Property> emptyList(), expectedType, null);

                // I must maintain a reference back to this object so that I can
                // change it once its target is found:
                if (!placeholderComplexAttributes.containsKey(hrefId)) {
                        placeholderComplexAttributes.put(hrefId,
                                        new ArrayList<Attribute>());
                }

                // Adding it to a list allows us to have multiple hrefs pointing
                // to the same target.
                placeholderComplexAttributes.get(hrefId).add(
                                placeholderComplexAttribute);
                return placeholderComplexAttribute;
            }
        } else {
                // TODO remote href
                throw new UnsupportedOperationException("remote href is not supported yet");
        }
    }
}
