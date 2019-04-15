package org.archicontribs.specialization.types;

import java.util.ArrayList;
import java.util.List;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.propertysections.SpecializationModelSection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import lombok.Getter;

public class ExclusiveComponentLabels {
    static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);
    
    @Getter List<ComponentLabel> allComponentLabels = new ArrayList<ComponentLabel>();
    
    public ComponentLabel add(Composite parent, @SuppressWarnings("rawtypes") Class clazz) {
        ComponentLabel componentLabel = new ComponentLabel(parent, clazz);
        this.allComponentLabels.add(componentLabel);
        
        componentLabel.getLabel().addListener(SWT.MouseUp, new Listener() {
            @Override public void handleEvent(Event event) {
                for ( ComponentLabel lbl: ExclusiveComponentLabels.this.allComponentLabels ) {
                    if ( lbl.isSelected() && !lbl.getLabel().equals(event.widget) ) {
                        lbl.setSelected(false);
                        lbl.label.redraw();
                    }
                }
            }
        });
        
        return componentLabel;
    }
    
    public ComponentLabel getSelected() {
        for ( ComponentLabel lbl: this.allComponentLabels )
            if ( lbl.isSelected() )
                return lbl;
        return null;
    }
}
