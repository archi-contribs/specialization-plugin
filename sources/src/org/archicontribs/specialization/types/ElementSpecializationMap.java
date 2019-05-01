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
public class SpecializationMap extends HashMap<String, List<SpecializationType>> {
    private static final long serialVersionUID = 1L;
    
    public SpecializationMap() {
    	super();
    }
    
    public SpecializationType getSpecializationType(String clazz, String name) {
    	List<SpecializationType> types = get(clazz);
    	if ( types != null ) {
    		for ( int index = 0 ; index < types.size() ; ++index ) {
    			if ( types.get(index).getName().equals(name) )
    				return types.get(index);
    		}
    	}
    	
    	return null;
    }
    
    public void addSpecializationType(String clazz, SpecializationType specializationType) {
    	List<SpecializationType> specializationTypes = get(clazz);
    	if ( specializationTypes == null ) {
    		specializationTypes = new ArrayList<SpecializationType>();
    		put(clazz, specializationTypes);
    	}
    	specializationTypes.add(specializationType);
    }
}
