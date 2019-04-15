package org.archicontribs.specialization.types;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.propertysections.SpecializationModelSection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import lombok.Getter;
import lombok.Setter;

public class ComponentLabel {
    static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);
    
    @Getter Label label;
    @Getter @Setter boolean selected;

    ComponentLabel(Composite parent, @SuppressWarnings("rawtypes") Class clazz) {
        this.label = new Label(parent, SWT.NONE);
        this.label.setSize(100,  100);
        this.label.setToolTipText(clazz.getSimpleName());
        this.label.setImage(ArchimateIcons.getImage(getElementClassname()));

        this.label.addPaintListener(new PaintListener() {
            @Override public void paintControl(PaintEvent event)
            {
                if ( ComponentLabel.this.selected ) {
                    ComponentLabel.this.label.setBackground(SpecializationPlugin.GREY_COLOR);
                    //event.gc.drawRoundRectangle(0, 0, 16, 16, 2, 2);
                } else
                    ComponentLabel.this.label.setBackground(ArchimateIcons.getColor(getElementClassname()));
            }
        });
        
        this.label.addListener(SWT.MouseUp, new Listener() {
            @Override public void handleEvent(Event event) {
                ComponentLabel.this.selected = true;
                redraw();
            }
        });
    }
    
    public void select() {
        this.label.notifyListeners(SWT.MouseUp, new Event());
    }


    public String getElementClassname() {
        return this.label.getToolTipText().replaceAll(" ",  "");
    }

    public void redraw() {
        this.label.redraw();
    }
}
