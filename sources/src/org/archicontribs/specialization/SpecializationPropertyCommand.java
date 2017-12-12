/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization;

import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

public class SpecializationPropertyCommand extends Command {
	protected enum actionType {Nothing, PropertyCreated, PropertyDeleted, PropertyUpdated};
	
    protected IProperties eObject;
	protected String      key;
	protected String      value;

	protected actionType  action;
	protected IProperty   property;
	protected String      oldValue;
	protected int         propertyIndex;
		
	/*
	 * Creates a new property
	 */
	public SpecializationPropertyCommand(IProperties eObject, String key, String value) {
	    this.eObject = eObject;
	    this.key = key;
	    this.value = value;
	}
	
    @Override
    public void execute() {
    	if ( !(eObject instanceof IProperties) ) {
    		action = actionType.Nothing;
    		return;
    	}
    	
    	// we search for the property
    	property = null;
    	for ( IProperty prop: ((IProperties)eObject).getProperties() ) {
    		if ( prop.getKey().equals(key)) {
    			property = prop;
    			break;
    		}
    	}
    	
    	if ( property == null ) {
    		// if the key does not exits yet, then we create it ... but only if the value is not null
    		if ( value == null ) {
    			action = actionType.Nothing;
    		} else {
	    		action = actionType.PropertyCreated;
	            property = IArchimateFactory.eINSTANCE.createProperty();
	            property.setKey(key);
	            property.setValue(value);
	            eObject.getProperties().add(property);
    		}
    	} else {
    		// else, we update the value ... but only if the value is not null
    		if ( value == null ) {
    			action = actionType.PropertyDeleted;
    			propertyIndex = eObject.getProperties().indexOf(property);
    			eObject.getProperties().remove(property);
    		} else {
    			action = actionType.PropertyUpdated;
    			oldValue = property.getValue();
    			property.setValue(value);
    		}
    	}
    }
    
    @Override
    public void undo() {
    	switch ( action ) {
    		case PropertyCreated:
    			// we remove the newly created property
    			eObject.getProperties().remove(property);
    			break;
    		case PropertyDeleted:
    			// we restore the deleted property
    			eObject.getProperties().add(propertyIndex, property);
    			break;
    		case PropertyUpdated:
    			// we restore the old value
    			property.setValue(oldValue);
    			break;
			case Nothing:
				// nothing to undo
				break;
    	}
    }
}