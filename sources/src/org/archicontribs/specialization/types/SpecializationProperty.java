package org.archicontribs.specialization.types;

import lombok.Getter;
import lombok.Setter;

public class SpecializationProperty {
    @Getter @Setter private String name;
    @Getter @Setter private String value;
    
    public SpecializationProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
