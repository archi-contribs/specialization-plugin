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
        ComponentLabel componentLabel = new ComponentLabel(parent, SWT.NONE, clazz);
        this.allComponentLabels.add(componentLabel);
        
        componentLabel.addListener(SWT.MouseUp, new Listener() {
            @Override public void handleEvent(Event event) {
                for ( ComponentLabel lbl: ExclusiveComponentLabels.this.allComponentLabels ) {
                    if ( lbl.isSelected() && !lbl.equals(event.widget) ) {
                        lbl.setSelected(false);
                        lbl.redraw();
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
    
    public boolean isDisposed() {
    	for ( ComponentLabel lbl: this.allComponentLabels )
            if ( lbl.isDisposed() )
                return true;
        return false;
    }
}
