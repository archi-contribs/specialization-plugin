package org.archicontribs.specialization.types;

import java.util.ArrayList;
import java.util.List;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.propertysections.SpecializationModelSection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.archimatetool.model.IArchimateModel;

import lombok.Getter;
import lombok.Setter;

public class ExclusiveComponentLabels {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);

	@Getter @Setter IArchimateModel model = null;

	@Getter List<Listener> listeners = new ArrayList<Listener>();
	@Getter List<ComponentLabel> allComponentLabels = new ArrayList<ComponentLabel>();

	public ComponentLabel add(Composite parent, @SuppressWarnings("rawtypes") Class clazz) {
		ComponentLabel componentLabel = new ComponentLabel(parent, SWT.NONE, clazz);
		this.allComponentLabels.add(componentLabel);

		componentLabel.removeListener(SWT.MouseUp, componentLabel.mouseUpListener);
		componentLabel.addListener(SWT.MouseUp, this.selectComponentLabelListener);

		// to avoid memory leak
		parent.addDisposeListener(new DisposeListener() {
			@Override public void widgetDisposed(DisposeEvent e) {
				componentLabel.dispose();
			}
		});

		return componentLabel;
	}

	public boolean isDisposed() {
		for ( ComponentLabel lbl: this.allComponentLabels )
			if ( lbl.isDisposed() )
				return true;
		return false;
	}

	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}
	
	public ComponentLabel getSelectedComponentLabel() {
		for ( ComponentLabel componentLabel: this.allComponentLabels ) {
			if ( componentLabel.isSelected() )
				return componentLabel;
		}
		return null;
	}

	/**
	This event is fired when an element class is selected in the componentLabels diagram
	*/
	Listener selectComponentLabelListener = new Listener() {
		@Override public void handleEvent(Event event) {
			if ( event.widget != null ) {
				for ( ComponentLabel componentLabel: ExclusiveComponentLabels.this.allComponentLabels )
					componentLabel.setSelected(componentLabel.equals(event.widget));

				for (Listener listener: ExclusiveComponentLabels.this.listeners)
					listener.handleEvent(event);
			}
		}
	};
}
