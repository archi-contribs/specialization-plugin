/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.types;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * This class stores the configuration of a specialization
 * 
 * @author Herve Jouin
 */
public class ElementSpecialization {
    private String specializationName;
    @Getter @Setter private int figure = 0;       // 0 or 1 if the alternate figure is selected
    private String label = "";
    private String iconName = "";
    private String iconSize = "";
    private String iconLocation = "";
    private List<SpecializationProperty> properties = new ArrayList<SpecializationProperty>();
    
    public ElementSpecialization(String name) {
        this.specializationName = name;
    }
   
    // specializationName
    public String getSpecializationName() {
    	return (this.specializationName == null ? "" : this.specializationName);
    }
    
    public void setSpecializationName(String specializationName) {
    	this.specializationName = (specializationName == null ? "" : specializationName);
    }
    
    // label
    public String getLabel() {
    	return (this.label == null ? "" : this.label);
    }
    
    public void setLabel(String label) {
    	this.label = (label == null ? "" : label);
    }
    
    // iconName
    public String getIconName() {
    	return (this.iconName == null ? "" : this.iconName);
    }
    
    public void setIconName(String iconName) {
    	this.iconName = (iconName == null ? "" : iconName);
    }
    
    // iconSize
    public String getIconSize() {
    	return (this.iconSize == null ? "" : this.iconSize);
    }
    
    public void setIconSize(String iconSize) {
    	this.iconSize = (iconSize == null ? "" : iconSize);
    }
    
    // iconLocation
    public String getIconLocation() {
    	return (this.iconLocation == null ? "" : this.iconLocation);
    }
    
    public void setIconLocation(String iconLocation) {
    	this.iconLocation = (iconLocation == null ? "" : iconLocation);
    }
    
    // properties
    public List<SpecializationProperty> getProperties() {
    	if ( this.properties == null )
    		this.properties = new ArrayList<SpecializationProperty>();
    	return this.properties;
    }
    
    public void setProperties(List<SpecializationProperty> properties) {
    	this.properties = (properties == null ? new ArrayList<SpecializationProperty>() : properties);
    }
}