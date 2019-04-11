/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class stores the list of specializations
 * 
 * @author Herve Jouin
 */
public class SpecializationsList extends HashMap<String, List<SpecializationType>> {
    private static final long serialVersionUID = 1L;
    
    public SpecializationsList() {
    }
    
    public void addSpecializationForClass(String clazz) {
        if ( get(clazz) == null )
            put(clazz, new ArrayList<SpecializationType>());
    }
}
