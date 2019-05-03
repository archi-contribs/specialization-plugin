/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.propertysections.SpecializationModelSection;
import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IProperty;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


/**
 * This class stores the list of specializations
 * 
 * @author Herve Jouin
 */
public class ElementSpecializationMap extends HashMap<String, List<ElementSpecialization>> {
	static final SpecializationLogger logger = new SpecializationLogger(ElementSpecializationMap.class);
	
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
    
	public static ElementSpecializationMap getFromArchimateModel(IArchimateModel model) {
		if ( model == null || model.getMetadata() == null)
			return null;
		
		IProperty specializationsMetadata = model.getMetadata().getEntry(SpecializationPlugin.METADATA_KEY);
		if ( specializationsMetadata != null ) {
			try {
				Gson gson = new Gson();
				return gson.fromJson(specializationsMetadata.getValue(), ElementSpecializationMap.class);
			} catch (JsonSyntaxException e) {
				SpecializationPlugin.popup(Level.FATAL, "An exception occured while retrieving the specialization metadata from the model.",e);
				//TODO: store the exception somewhere and deactivate the plugin for that model
				//TODO: add an option to erase the specializations metadata and startup again from an empty configuration
			}
		}
		
		return null;
	}
    
	/**
	 * Retrieves the ElementSpecialization associated to the IArchimateConcept concept
	 * @param concept the ArchimateConcept for which the ElementSpecialization must be retrieved
	 */
	public static ElementSpecialization getElementSpecialization(EObject obj) {
		if ( obj != null ) {
			String clazz = null;
			String specializationName = null;
			ElementSpecializationMap elementSpecializationMap = null;
			
			IArchimateConcept concept = null;
			
			if ( obj instanceof IArchimateConcept )
				concept = (IArchimateConcept)obj;
			else if ( obj instanceof IDiagramModelArchimateObject )
				concept = ((IDiagramModelArchimateObject)obj).getArchimateConcept();
			else {
				logger.error("Object should be an ArchimateConcept or an ArchimateObject !");
				return null;
			}
			
			// no specialization for objects that do don have an id
			if ( concept.getId() == null )
				return null;
			
			String traceMessage;
			if ( concept.getArchimateModel() == null ) {
				// if no object is provided, we return the icon name from the SpecializationModelSection page
				traceMessage = "getting the icon name from the SpecializationModelSection page";
				clazz = SpecializationModelSection.getSelectedClass();
				specializationName = SpecializationModelSection.getSelectedSpecializationName();
				elementSpecializationMap = getFromArchimateModel(SpecializationModelSection.INSTANCE.getCurrentModel());
			} else {
				traceMessage = "getting the icon name from the concept properties";
				for ( IProperty prop: concept.getProperties() ) {
					if ( prop.getKey().equals(SpecializationPlugin.PROPERTY_KEY) ) {
						clazz = concept.getClass().getSimpleName().replaceAll(" ",  "");
						specializationName = prop.getValue();
						elementSpecializationMap = getFromArchimateModel(concept.getArchimateModel());
						break;
					}
				}
			}
	
			ElementSpecialization elementSpecialization = null;
			if ( elementSpecializationMap != null )
				elementSpecialization = elementSpecializationMap.getElementSpecialization(clazz, specializationName);
			
			logger.trace(traceMessage+" ("+(elementSpecialization == null ? "null" : elementSpecialization.getIconName())+")");
			
			return elementSpecialization;
		}
		return null;
	}
}
