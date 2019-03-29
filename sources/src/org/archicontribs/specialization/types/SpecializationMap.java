package org.archicontribs.specialization.types;

import java.util.List;

import lombok.Getter;

@SuppressWarnings("rawtypes")
public class SpecializationMap {
    @Getter Class classTospecialize;
    @Getter List<String> specializations;
}
