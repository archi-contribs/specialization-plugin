package org.archicontribs.specialization.types;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.propertysections.SpecializationModelSection;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.archimatetool.model.IArchimatePackage;

import lombok.Getter;
import lombok.Setter;

public class ComponentLabel extends Label {
    static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);
    
    @Getter @Setter boolean selected;

    ComponentLabel(Composite parent, int type, @SuppressWarnings("rawtypes") Class clazz) {
        super(parent,type);
        this.setSize(100,  100);
        this.setToolTipText(clazz.getSimpleName());
        this.setImage(ArchimateIcons.getImage(getElementClassname()));

        this.addPaintListener(new PaintListener() {
            @Override public void paintControl(PaintEvent event)
            {
                if ( ComponentLabel.this.selected ) {
                    ComponentLabel.this.setBackground(SpecializationPlugin.GREY_COLOR);
                    //event.gc.drawRoundRectangle(0, 0, 16, 16, 2, 2);
                } else
                    ComponentLabel.this.setBackground(ArchimateIcons.getColor(getElementClassname()));
            }
        });
        
        this.addListener(SWT.MouseUp, new Listener() {
            @Override public void handleEvent(Event event) {
                ComponentLabel.this.selected = true;
                redraw();
            }
        });
    }
    
    public void select() {
        this.notifyListeners(SWT.MouseUp, new Event());
    }


    public String getElementClassname() {
        return this.getToolTipText().replaceAll(" ",  "");
    }
    
    public EClass getEClass() {
    	return (EClass)IArchimatePackage.eINSTANCE.getEClassifier(this.getToolTipText());
    }
}
