package org.archicontribs.specialization.types;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import com.archimatetool.model.IProperty;

import lombok.Getter;
import lombok.Setter;

public class SpecializationType {
    @Getter private String name;
    @Getter @Setter private Image icon = null;
    @Getter @Setter private String isonSize = null;
    @Getter @Setter private String iconLocation = null;
    @Getter private List<IProperty> properties = new ArrayList<IProperty>();
    
    public SpecializationType(String name) {
        this.name = name;
    }
    
    public void addProperty(IProperty prop) {
        this.properties.add(prop);
    }
}