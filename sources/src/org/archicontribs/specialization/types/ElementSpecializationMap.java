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
		if ( (clazz != null) && !clazz.isEmpty() && (name != null) && !name.isEmpty() ) {
			List<ElementSpecialization> types = get(clazz);
			if ( types != null ) {
				for ( int index = 0 ; index < types.size() ; ++index ) {
					if ( types.get(index).getSpecializationName().equals(name) )
						return types.get(index);
				}
			}
		}
		return null;
	}

	public void addElementSpecialization(String clazz, ElementSpecialization elementSpecialization) {
		if ( (clazz != null) && !clazz.isEmpty() && (elementSpecialization != null) ) {
			List<ElementSpecialization> elementSpecializationList = get(clazz);
			if ( elementSpecializationList == null ) {
				elementSpecializationList = new ArrayList<ElementSpecialization>();
				put(clazz, elementSpecializationList);
			}
			elementSpecializationList.add(elementSpecialization);
		}
	}

	public void removeElementSpecialization(String clazz, ElementSpecialization elementSpecialization) {
		if ( (clazz != null) && !clazz.isEmpty() && (elementSpecialization != null) ) {
			List<ElementSpecialization> elementSpecializationList = get(clazz);
			if ( elementSpecializationList != null ) {
				elementSpecializationList.remove(elementSpecialization);
			}
		}
	}

	public void removeElementSpecialization(String clazz, String elementName) {
		if ( (clazz != null) && !clazz.isEmpty() && (elementName != null) && !elementName.isEmpty() ) {
			List<ElementSpecialization> elementSpecializationList = get(clazz);
			if ( elementSpecializationList != null ) {
				ElementSpecialization elementSpecialization = getElementSpecialization(clazz, elementName);
				if ( elementSpecialization != null )
					elementSpecializationList.remove(elementSpecialization);
			}
		}
	}

	public static ElementSpecializationMap getFromArchimateModel(IArchimateModel model) {
		if ( model == null || model.getMetadata() == null)
			return null;

		IProperty specializationsMetadata = model.getMetadata().getEntry(SpecializationPlugin.SPECIALIZATION_PROPERTY_KEY);
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
			String specializationName = null;
			ElementSpecializationMap elementSpecializationMap = null;

			IArchimateConcept concept = null;

			if ( obj instanceof IArchimateConcept )
				concept = (IArchimateConcept)obj;
			else if ( obj instanceof IDiagramModelArchimateObject )
				concept = ((IDiagramModelArchimateObject)obj).getArchimateConcept();
			else {
				logger.error(SpecializationPlugin.getDebugName(concept)+" should be an ArchimateConcept or an ArchimateObject !");
				return null;
			}

			// no specialization for objects that do not have an id
			if ( concept.getId() == null )
				return null;

			// if concept is not in a model, it means it may be in the SpecializationodelSection
			if ( concept.getArchimateModel() == null ) {
				specializationName = SpecializationModelSection.getSelectedSpecializationName();
				if ( SpecializationModelSection.INSTANCE != null )
					elementSpecializationMap = getFromArchimateModel(SpecializationModelSection.INSTANCE.getCurrentModel());
			} else {
				for ( IProperty prop: concept.getProperties() ) {
					if ( SpecializationPlugin.SPECIALIZATION_PROPERTY_KEY.equals(prop.getKey()) ) {
						specializationName = prop.getValue();
						break;
					}
				}
				elementSpecializationMap = getFromArchimateModel(concept.getArchimateModel());
			}
			
			if ( elementSpecializationMap != null )
				return elementSpecializationMap.getElementSpecialization(concept.getClass().getSimpleName().replaceAll(" ",  ""), specializationName);
		}
		return null;
	}
}
