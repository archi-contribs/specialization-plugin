/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.SpecializationPropertyCommand;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.editor.propertysections.AbstractArchimatePropertySection;
import com.archimatetool.editor.propertysections.ITabbedLayoutConstants;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModel;

public class SpecializationDiagramModelSection extends AbstractArchimatePropertySection {
	private static final SpecializationLogger logger = new SpecializationLogger(SpecializationDiagramModelSection.class);

	private IDiagramModel diagramModel;

	private Label lblReplaceIcons;
	private Button btnIconsYes;
	private Button btnIconsNo;
	private Button btnIconsDefault;

	private Label lblReplaceLabels;
	private Button btnLabelsYes;
	private Button btnLabelsNo;
	private Button btnLabelsDefault;
	
	/**
	 * Filter to show or reject this section depending on input value
	 */
	public static class Filter extends ObjectFilter {
		@Override
		protected boolean isRequiredType(Object object) {
			if ( object == null )
				logger.trace("null -> filter : "+(object instanceof IArchimateDiagramModel));
			else
				logger.trace(object.getClass().getSimpleName()+" -> filter : "+(object instanceof IArchimateDiagramModel));
			return object instanceof IArchimateDiagramModel;
		}

		@Override
		protected Class<?> getAdaptableType() {
			return IArchimateDiagramModel.class;
		}
	}

	/**
	 * Create the controls
	 */
	@Override
	protected void createControls(Composite parent) {
		parent.setLayout(new FormLayout());

		lblReplaceIcons = new Label(parent, SWT.NONE);
		lblReplaceIcons.setText("Replace icons");
		lblReplaceIcons.setForeground(parent.getForeground());
		lblReplaceIcons.setBackground(parent.getBackground());
		lblReplaceIcons.setFont(parent.getFont());
		lblReplaceIcons.setEnabled(false);
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 27);
		lblReplaceIcons.setLayoutData(fd);

		Composite compoReplaceIcons = new Composite(parent, SWT.NONE);
		compoReplaceIcons.setBackground(parent.getBackground());
		compoReplaceIcons.setLayout(new RowLayout(SWT.VERTICAL));
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, ITabbedLayoutConstants.STANDARD_LABEL_WIDTH);
		compoReplaceIcons.setLayoutData(fd);

		btnIconsYes = new Button(compoReplaceIcons, SWT.RADIO);
		btnIconsYes.setBackground(parent.getBackground());
		btnIconsYes.setForeground(parent.getForeground());
		btnIconsYes.setFont(parent.getFont());
		btnIconsYes.setText("yes");
		btnIconsYes.setSelection(false);
		btnIconsYes.setEnabled(false);
		btnIconsYes.addSelectionListener(replaceIconsListener);

		btnIconsNo = new Button(compoReplaceIcons, SWT.RADIO);
		btnIconsNo.setBackground(parent.getBackground());
		btnIconsNo.setForeground(parent.getForeground());
		btnIconsNo.setFont(parent.getFont());
		btnIconsNo.setText("no");
		btnIconsNo.setSelection(false);
		btnIconsNo.setEnabled(false);
		btnIconsNo.addSelectionListener(replaceIconsListener);

		btnIconsDefault = new Button(compoReplaceIcons, SWT.RADIO);
		btnIconsDefault.setBackground(parent.getBackground());
		btnIconsDefault.setForeground(parent.getForeground());
		btnIconsDefault.setFont(parent.getFont());
		btnIconsDefault.setText("use default");
		btnIconsDefault.setSelection(false);
		btnIconsDefault.setEnabled(false);
		btnIconsDefault.addSelectionListener(replaceIconsListener);


		lblReplaceLabels = new Label(parent, SWT.NONE);
		lblReplaceLabels.setText("Replace Labels");
		lblReplaceLabels.setForeground(parent.getForeground());
		lblReplaceLabels.setBackground(parent.getBackground());
		lblReplaceLabels.setFont(parent.getFont());
		lblReplaceLabels.setEnabled(false);
		fd = new FormData();
		fd.top = new FormAttachment(compoReplaceIcons, 5);
		fd.left = new FormAttachment(0, 27);
		lblReplaceLabels.setLayoutData(fd);

		Composite compoReplaceLabels = new Composite(parent, SWT.NONE);
		compoReplaceLabels.setBackground(parent.getBackground());
		compoReplaceLabels.setLayout(new RowLayout(SWT.VERTICAL));
		fd = new FormData();
		fd.top = new FormAttachment(compoReplaceIcons, 5);
		fd.left = new FormAttachment(0, ITabbedLayoutConstants.STANDARD_LABEL_WIDTH);
		compoReplaceLabels.setLayoutData(fd);

		btnLabelsYes = new Button(compoReplaceLabels, SWT.RADIO);
		btnLabelsYes.setBackground(parent.getBackground());
		btnLabelsYes.setForeground(parent.getForeground());
		btnLabelsYes.setFont(parent.getFont());
		btnLabelsYes.setText("yes");
		btnLabelsYes.setSelection(false);
		btnLabelsYes.setEnabled(false);
		btnLabelsYes.addSelectionListener(replaceLabelsListener);

		btnLabelsNo = new Button(compoReplaceLabels, SWT.RADIO);
		btnLabelsNo.setBackground(parent.getBackground());
		btnLabelsNo.setForeground(parent.getForeground());
		btnLabelsNo.setFont(parent.getFont());
		btnLabelsNo.setText("no");
		btnLabelsNo.setSelection(false);
		btnLabelsNo.setEnabled(false);
		btnLabelsNo.addSelectionListener(replaceLabelsListener);

		btnLabelsDefault = new Button(compoReplaceLabels, SWT.RADIO);
		btnLabelsDefault.setBackground(parent.getBackground());
		btnLabelsDefault.setForeground(parent.getForeground());
		btnLabelsDefault.setFont(parent.getFont());
		btnLabelsDefault.setText("use default");
		btnLabelsDefault.setSelection(false);
		btnLabelsDefault.setEnabled(false);
		btnLabelsDefault.addSelectionListener(replaceLabelsListener);
	}
	
	/*
     * Adapter to listen to changes made elsewhere (including Undo/Redo commands)
     */
    private Adapter eAdapter = new AdapterImpl() {
        @Override
        public void notifyChanged(Notification msg) {
            Object feature = msg.getFeature();
            // Diagram Name event (Undo/Redo and here!)
            if(feature == IArchimatePackage.Literals.PROPERTIES__PROPERTIES) {
            	refreshControls();
            }
        }
    };

	@Override
	protected Adapter getECoreAdapter() {
		return eAdapter;
	}

	@Override
	protected EObject getEObject() {
		return diagramModel;
	}

	protected void setElement(Object element) {
		diagramModel = (IDiagramModel)new Filter().adaptObject(element);
		if(diagramModel == null) {
			logger.error("failed to get element for " + element); //$NON-NLS-1$
		}
		
		refreshControls();
	}
	
	private void refreshControls() {
		boolean mustReplaceIconsInAllViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews").length() == 0;
		boolean mustReplaceLabelsInAllViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews").length() == 0;

		boolean yes;
		boolean no;

		String propValue = SpecializationPlugin.getPropertyValue(diagramModel, "must replace icons");
		if ( propValue != null )
			propValue = propValue.toLowerCase();
		yes = SpecializationPlugin.areEqual(propValue, "yes");
		no = SpecializationPlugin.areEqual(propValue, "no");

		btnIconsYes.setSelection(yes);
		btnIconsNo.setSelection(no);
		btnIconsDefault.setSelection(!yes && !no);

		lblReplaceIcons.setEnabled(!mustReplaceIconsInAllViews);
		btnIconsYes.setEnabled(!mustReplaceIconsInAllViews);
		btnIconsNo.setEnabled(!mustReplaceIconsInAllViews);
		btnIconsDefault.setEnabled(!mustReplaceIconsInAllViews);

		propValue = SpecializationPlugin.getPropertyValue(diagramModel, "must replace labels");
		if ( propValue != null )
			propValue = propValue.toLowerCase();
		yes = SpecializationPlugin.areEqual(propValue, "yes");
		no = SpecializationPlugin.areEqual(propValue, "no");

		btnLabelsYes.setSelection(yes);
		btnLabelsNo.setSelection(no);
		btnLabelsDefault.setSelection(!yes && !no);

		lblReplaceLabels.setEnabled(!mustReplaceLabelsInAllViews);
		btnLabelsYes.setEnabled(!mustReplaceLabelsInAllViews);
		btnLabelsNo.setEnabled(!mustReplaceLabelsInAllViews);
		btnLabelsDefault.setEnabled(!mustReplaceLabelsInAllViews);
	}


	private SelectionListener replaceIconsListener = new SelectionAdapter () {
		public void widgetSelected(SelectionEvent event) {
			if ( diagramModel == null ) 
				return;

			Button button = ((Button) event.widget);
			if ( !button.getSelection() )
				return;
			
			String value = null;
			if ( button.equals(btnIconsYes) )
				value = "yes";
			else if ( button.equals(btnIconsNo) )
				value = "no";
			
			SpecializationPropertyCommand command = new SpecializationPropertyCommand(diagramModel, "must replace icons", value);
			
            if ( command.canExecute() ) {
    			CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
            	compoundCommand.add(command);

    		    CommandStack stack = (CommandStack) diagramModel.getArchimateModel().getAdapter(CommandStack.class);
    		    stack.execute(compoundCommand);
    		    logger.trace("Setting property \"must replace icons\" to "+value);
            }
		};
	};

	private SelectionListener replaceLabelsListener = new SelectionAdapter () {
		public void widgetSelected(SelectionEvent event) {
			if ( diagramModel == null ) 
				return;

			Button button = ((Button) event.widget);
			if ( !button.getSelection() )
				return;

			String value = null;
			if ( button.equals(btnLabelsYes) )
				value = "yes";
			else if ( button.equals(btnLabelsNo) )
				value = "no";
			
			SpecializationPropertyCommand command = new SpecializationPropertyCommand(diagramModel, "must replace labels", value);
			
            if ( command.canExecute() ) {
    			CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
            	compoundCommand.add(command);

    		    CommandStack stack = (CommandStack) diagramModel.getAdapter(CommandStack.class);
    		    stack.execute(compoundCommand);
    		    logger.trace("Setting property \"must replace labels\" to "+value);
            }
		};
	};
}
