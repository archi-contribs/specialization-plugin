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

public class ComponentLabel extends Label {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);

	@Getter boolean selected;

	ComponentLabel(Composite parent, int type, @SuppressWarnings("rawtypes") Class clazz) {
		super(parent,type);
		this.setSize(100,  100);
		this.setToolTipText(clazz.getSimpleName());
		this.setImage(ArchimateIcons.getImage(getElementClassname()));

		this.addPaintListener(new PaintListener() {
			@Override public void paintControl(PaintEvent event) {
				ComponentLabel.this.setBackground(ComponentLabel.this.selected ? SpecializationPlugin.GREY_COLOR : ArchimateIcons.getColor(getElementClassname()));
			}
		});

		this.addListener(SWT.MouseUp, this.mouseUpListener);
	}

	@Override protected void checkSubclass () {
		// do nothing
	}

	public void select() {
		setSelected(true);
	}

	public void unelect() {
		setSelected(false);
	}

	public void setSelected(boolean selected) {
		ComponentLabel.this.selected = selected;
		redraw();
	}


	public String getElementClassname() {
		return this.getToolTipText().replaceAll(" ",  "");
	}

	public EClass getEClass() {
		return (EClass)IArchimatePackage.eINSTANCE.getEClassifier(this.getToolTipText());
	}
	
	Listener mouseUpListener = new Listener() {
		@Override public void handleEvent(Event event) {
			select();
		}
	};
}
