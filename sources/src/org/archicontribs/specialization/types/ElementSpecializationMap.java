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
public class ElementSpecializationMap extends HashMap<String, List<ElementSpecialization>> {
    private static final long serialVersionUID = 1L;
    
    public ElementSpecializationMap() {
    	super();
    }
    
    public ElementSpecialization getElementSpecialization(String clazz, String name) {
    	List<ElementSpecialization> types = get(clazz);
    	if ( types != null ) {
    		for ( int index = 0 ; index < types.size() ; ++index ) {
    			if ( types.get(index).getSpecializationName().equals(name) )
    				return types.get(index);
    		}
    	}
    	
    	return null;
    }
    
    public void addElementSpecialization(String clazz, ElementSpecialization elementSpecialization) {
    	List<ElementSpecialization> elementSpecializationList = get(clazz);
    	if ( elementSpecializationList == null ) {
    		elementSpecializationList = new ArrayList<ElementSpecialization>();
    		put(clazz, elementSpecializationList);
    	}
    	elementSpecializationList.add(elementSpecialization);
    }
    
    public void removeElementSpecialization(String clazz, ElementSpecialization elementSpecialization) {
    	List<ElementSpecialization> elementSpecializationList = get(clazz);
    	if ( elementSpecializationList != null ) {
    		elementSpecializationList.remove(elementSpecialization);
    	}
    }
    
    public void removeElementSpecialization(String clazz, String elementName) {
    	List<ElementSpecialization> elementSpecializationList = get(clazz);
    	if ( elementSpecializationList != null ) {
        	ElementSpecialization elementSpecialization = getElementSpecialization(clazz, elementName);
        	if ( elementSpecialization != null )
        		elementSpecializationList.remove(elementSpecialization);
    	}
    }
}
