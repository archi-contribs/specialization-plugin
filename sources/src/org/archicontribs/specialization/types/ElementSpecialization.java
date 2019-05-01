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
    @Getter @Setter private String name;
    @Getter @Setter private Image icon = null;
    @Getter @Setter private int figure = 0;       // 0 or 1 if the alternate figure is selected 
    @Getter @Setter private String iconSize = "";
    @Getter @Setter private String iconLocation = "";
    @Getter private List<SpecializationProperty> properties = new ArrayList<SpecializationProperty>();
    
    public SpecializationType(String name) {
        this.name = name;
    }
}