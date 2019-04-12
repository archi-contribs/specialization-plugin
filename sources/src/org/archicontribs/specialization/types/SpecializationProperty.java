package org.archicontribs.specialization.types;

import lombok.Getter;
import lombok.Setter;

public class Property {
    @Getter @Setter private String name;
    @Getter @Setter private String value;
    
    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
