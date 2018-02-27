/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

public class SpecializationPropertyCommand extends Command {
	protected enum actionType {Nothing, PropertyCreated, PropertyDeleted, PropertyUpdated}
	
    protected IProperties eObject;
	protected String      key;
	protected String      value;

	protected actionType  action;
	protected IProperty   property;
	protected String      oldValue;
	protected int         propertyIndex;
	
	protected Adapter adapter;
		
	/*
	 * Creates a new property
	 */
	public SpecializationPropertyCommand(IProperties eObject, String key, String value) {
	    this.eObject = eObject;
	    this.key = key;
	    this.value = value;
	    this.adapter = null;
	    setLabel("set property "+key);
	}
	
	/*
     * Creates a new property and set the concept's adapter
     */
    public SpecializationPropertyCommand(IProperties eObject, String key, String value, Adapter adapter) {
        this.eObject = eObject;
        this.key = key;
        this.value = value;
        this.adapter = adapter;
        setLabel("set property "+key);
    }
	
    @Override
    public void execute() {
    	if ( !(this.eObject != null) ) {
    		this.action = actionType.Nothing;
    		return;
    	}
    	
    	// we search for the property
    	this.property = null;
    	for ( IProperty prop: this.eObject.getProperties() ) {
    		if ( prop.getKey().equals(this.key)) {
    			this.property = prop;
    			break;
    		}
    	}
    	
    	if ( this.property == null ) {
    		// if the key does not exits yet, then we create it ... but only if the value is not null
    		if ( this.value == null ) {
    			this.action = actionType.Nothing;
    		} else {
	    		this.action = actionType.PropertyCreated;
	            this.property = IArchimateFactory.eINSTANCE.createProperty();
	            this.property.setKey(this.key);
	            this.property.setValue(this.value);
	            this.eObject.getProperties().add(this.property);
	            this.property.eNotify(new ENotificationImpl((InternalEObject) this.property, Notification.ADD, EcorePackage.EATTRIBUTE, this.property.getValue(), null));
    		}
    	} else {
    		// else, we update the value ... but only if the value is not null
    		if ( this.value == null ) {
    			this.action = actionType.PropertyDeleted;
    			this.propertyIndex = this.eObject.getProperties().indexOf(this.property);
    			this.eObject.getProperties().remove(this.property);
    			this.property.eNotify(new ENotificationImpl((InternalEObject) this.property, Notification.REMOVE, EcorePackage.EATTRIBUTE, null, this.property.getValue()));
    		} else {
    			this.action = actionType.PropertyUpdated;
    			this.oldValue = this.property.getValue();
    			this.property.setValue(this.value);
    		}
    	}
    	
        if ( this.adapter != null )
            this.adapter.setTarget(this.eObject);
    	
    	// if the eObject is an element or a relationship, then we reset their name to force the diagrams to refresh them
    	if ( this.eObject instanceof IArchimateElement || this.eObject instanceof IArchimateRelationship )
    	    ((INameable)this.eObject).setName(((INameable)this.eObject).getName());
    }
    
    @Override
    public void undo() {
    	switch ( this.action ) {
    		case PropertyCreated:
    			// we remove the newly created property
    			this.eObject.getProperties().remove(this.property);
    			this.property.eNotify(new ENotificationImpl((InternalEObject) this.property, Notification.REMOVE, EcorePackage.EATTRIBUTE, null, this.property.getValue()));
    			break;
    		case PropertyDeleted:
    			// we restore the deleted property
    			this.eObject.getProperties().add(this.propertyIndex, this.property);
    			this.property.eNotify(new ENotificationImpl((InternalEObject) this.property, Notification.ADD, EcorePackage.EATTRIBUTE, this.property.getValue(), null));
    			break;
    		case PropertyUpdated:
    			// we restore the old value
    			this.property.setValue(this.oldValue);
    			break;
			case Nothing:
				// nothing to undo
				break;
            default: // will never be here
                break;
    	}
    	
        // if the eObject is an element or a relationship, then we reset their name to force the diagrams to refresh them
        if ( this.eObject instanceof IArchimateElement || this.eObject instanceof IArchimateRelationship )
            ((INameable)this.eObject).setName(((INameable)this.eObject).getName());
    }
    
    @Override
    public void dispose() {
        this.eObject = null;
        this.key = null;
        this.value = null;

        this.action = null;
        this.property = null;
        this.oldValue = null;
        this.propertyIndex = 0;
    }
}