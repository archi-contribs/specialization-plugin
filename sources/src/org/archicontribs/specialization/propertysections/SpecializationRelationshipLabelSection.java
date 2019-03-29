/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.SpecializationPropertyCommand;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IProperty;

public class SpecializationRelationshipLabelSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationRelationshipLabelSection.class);

	ArchimateRelationshipEditPart relationshipEditPart = null;

    private Composite compoLabel;
    private Composite compoNoLabel;
    Text txtLabelName;
    
    boolean mouseOverHelpButton = false;
	
	/**
	 * Filter to show or reject this section depending on input value
	 */
	public static class Filter extends ObjectFilter {
		@Override
		protected boolean isRequiredType(Object object) {
			if ( object != null && object instanceof ArchimateRelationshipEditPart ) {
	            logger.trace("Showing label tab.");
	            return true;
			}
            return false;
		}

		@Override
		protected Class<?> getAdaptableType() {
			return ArchimateRelationshipEditPart.class;
		}
	}
    
    @Override
    protected void setLayout(Composite parent) {
       parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, shouldUseExtraSpace()));
       
       parent.setLayout(new FormLayout());
    }

	/**
	 * Create the controls
	 */
	@Override
	protected void createControls(Composite parent) {
	    this.compoNoLabel = new Composite(parent, SWT.NONE);
        this.compoNoLabel.setForeground(parent.getForeground());
        this.compoNoLabel.setBackground(parent.getBackground());
        this.compoNoLabel.setLayout(new FormLayout());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.compoNoLabel.setLayoutData(fd);

        Label lblNoLabel = new Label(this.compoNoLabel, SWT.NONE);
        lblNoLabel.setText("You must configure the view or the model to allow labels replacement.");
        lblNoLabel.setForeground(parent.getForeground());
        lblNoLabel.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblNoLabel.setLayoutData(fd);
        
        Label btnHelp = new Label(this.compoNoLabel, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblNoLabel, 25);
        fd.bottom = new FormAttachment(lblNoLabel, 55, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 20);
        fd.right = new FormAttachment(0, 50);
        btnHelp.setLayoutData(fd);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { SpecializationRelationshipLabelSection.this.mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { SpecializationRelationshipLabelSection.this.mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( SpecializationRelationshipLabelSection.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(SpecializationPlugin.HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceLabel.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceLabel.html"); } });
        
        Label helpLbl = new Label(this.compoNoLabel, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp, 5);
        helpLbl.setLayoutData(fd);

        /* ********************************************************* */
        this.compoLabel = new Composite(parent, SWT.NONE);
        this.compoLabel.setForeground(parent.getForeground());
        this.compoLabel.setBackground(parent.getBackground());
        this.compoLabel.setLayout(new FormLayout());
        fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.compoLabel.setLayoutData(fd);

		Label lblLabelName = new Label(this.compoLabel, SWT.NONE);
		lblLabelName.setText("Label :");
		lblLabelName.setForeground(this.compoLabel.getForeground());
		lblLabelName.setBackground(this.compoLabel.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblLabelName.setLayoutData(fd);
        
        this.txtLabelName = new Text(this.compoLabel, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblLabelName, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblLabelName, 35);
        fd.right = new FormAttachment(0, 500);
        this.txtLabelName.setLayoutData(fd);
        this.txtLabelName.addModifyListener(this.labelModifyListener);
        
        Label btnHelp2 = new Label(this.compoLabel, SWT.NONE);
        btnHelp2.setForeground(parent.getForeground());
        btnHelp2.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblLabelName, 10);
        fd.bottom = new FormAttachment(lblLabelName, 40, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(0, 40);
        btnHelp2.setLayoutData(fd);
        btnHelp2.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { SpecializationRelationshipLabelSection.this.mouseOverHelpButton = true; btnHelp2.redraw(); } });
        btnHelp2.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { SpecializationRelationshipLabelSection.this.mouseOverHelpButton = false; btnHelp2.redraw(); } });
        btnHelp2.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( SpecializationRelationshipLabelSection.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(SpecializationPlugin.HELP_ICON, 2, 2);
            }
        });
        btnHelp2.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceLabel.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceLabel.html"); } });
        
        helpLbl = new Label(this.compoLabel, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp2, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp2, 5);
        helpLbl.setLayoutData(fd);
	}
	
    /**
     * Called when the label name is changed in the txtLabelName text widget
     */
    ModifyListener labelModifyListener = new ModifyListener() {
        @SuppressWarnings("synthetic-access")
        @Override
        public void modifyText(ModifyEvent event) {
            IArchimateRelationship concept = SpecializationRelationshipLabelSection.this.relationshipEditPart.getModel().getArchimateConcept();
            String value = ((Text)event.widget).getText();
            if ( value.isEmpty() ) value = null;        // null value allows to delete the property
            getCommandStack().execute(new SpecializationPropertyCommand(concept, "label", value, SpecializationRelationshipLabelSection.this.eAdapter));
        }
    };
	
	@Override
	protected Adapter getECoreAdapter() {
		return this.eAdapter;
	}
	   
    /**
     * Adapter to listen to changes made elsewhere (including Undo/Redo commands)
     * This one is a EContentAdapter to listen to child IProperty changes
     */
    private Adapter eAdapter = new EContentAdapter() {
        @Override
        public void notifyChanged(Notification msg) {
            if ( msg.getNotifier() instanceof IProperty ) {
                IProperty property = (IProperty)msg.getNotifier();
                if( property.getKey().equals("label") )
                    refreshControls();
            }
        }
        
        @Override
        public void setTarget(Notifier n) {
        	if ( n == null )
        		return;
        	
            if ( n instanceof IDiagramModelArchimateObject)
                super.setTarget(((IDiagramModelArchimateObject)n).getArchimateConcept());
            else
                super.setTarget(n);
        }
        
        @Override
        public void unsetTarget(Notifier n) {
        	if ( n == null )
        		return;
        	
        	if ( n instanceof IDiagramModelArchimateObject)
                super.unsetTarget(((IDiagramModelArchimateObject)n).getArchimateConcept());
            else
                super.unsetTarget(n);
        }
    };

	@Override
	protected EObject getEObject() {
        if ( this.relationshipEditPart == null )
            return null;
        return this.relationshipEditPart.getModel();
	}

	@Override
    protected void setElement(Object element) {
        this.relationshipEditPart = (ArchimateRelationshipEditPart)new Filter().adaptObject(element);

        logger.trace("Setting relationship to "+this.relationshipEditPart);
        
        refreshControls();
	}
	
	void refreshControls() {
		if ( this.txtLabelName == null || this.txtLabelName.isDisposed() )
			return;
		
        if ( this.relationshipEditPart == null ) {
            logger.trace("Not refreshing controls as relationshipEditPart is null");
            this.txtLabelName.removeModifyListener(this.labelModifyListener);
            this.txtLabelName.setText("");
            this.txtLabelName.addModifyListener(this.labelModifyListener);
            return;
        }

        logger.trace("Refreshing controls");
        
        if ( !SpecializationPlugin.mustReplaceLabel(this.relationshipEditPart.getModel()) ) {
            this.compoNoLabel.setVisible(true);
            this.compoLabel.setVisible(false);
            return;
        }
        
        this.compoNoLabel.setVisible(false);
        this.compoLabel.setVisible(true);
        
        String labelName = SpecializationPlugin.getPropertyValue(this.relationshipEditPart.getModel().getArchimateConcept(), "label");
        if ( labelName == null ) labelName = "";
        
        if ( !this.txtLabelName.getText().equals(labelName) ) {
            this.txtLabelName.removeModifyListener(this.labelModifyListener);
            this.txtLabelName.setText(labelName);
            this.txtLabelName.setSelection(labelName.length());
            this.txtLabelName.addModifyListener(this.labelModifyListener);
            // we reset the element's name to force the diagram to refresh it
            this.relationshipEditPart.getModel().getArchimateConcept().setName(this.relationshipEditPart.getModel().getArchimateConcept().getName());
        }
	}
}