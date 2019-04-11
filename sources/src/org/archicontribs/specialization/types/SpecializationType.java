/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.types;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import lombok.Getter;
import lombok.Setter;

/**
 * This class stores the configuration of a specialization
 * 
 * @author Herve Jouin
 */
public class SpecializationType {
    @Getter private String name;
    @Getter @Setter private Image icon = null;
    @Getter @Setter private String iconSize = "";
    @Getter @Setter private String iconLocation = "";
    @Getter private List<Property> properties = new ArrayList<Property>();
    
    public SpecializationType(String name) {
        this.name = name;
    }
}