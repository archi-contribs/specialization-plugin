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
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
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
import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.editor.propertysections.AbstractArchimatePropertySection;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;

public class SpecializationRelationshipLabelSection extends AbstractArchimatePropertySection {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationRelationshipLabelSection.class);

	ArchimateRelationshipEditPart relationshipEditPart = null;

    private Composite compoLabel;
    private Composite compoNoLabel;
    private Text txtLabelName;
    
    boolean mouseOverHelpButton = false;
    
    static final Image    HELP_ICON          = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/28x28/help.png"));
	
	/**
	 * Filter to show or reject this section depending on input value
	 */
	public static class Filter extends ObjectFilter {
		@Override
		protected boolean isRequiredType(Object object) {
			if ( object == null )
				return false;
			
            if ( !(object instanceof ArchimateRelationshipEditPart) )
                return false;
            
            logger.trace("showing label tab as the relationship has got a label");
            return true;
		}

		@Override
		protected Class<?> getAdaptableType() {
			return ArchimateRelationshipEditPart.class;
		}
	}
	
    /**
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
        lblNoLabel.setText("You must configure the view to allow labels replacement.");
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
                 e.gc.drawImage(HELP_ICON, 2, 2);
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
                 e.gc.drawImage(HELP_ICON, 2, 2);
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
    private ModifyListener labelModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            IArchimateRelationship concept = SpecializationRelationshipLabelSection.this.relationshipEditPart.getModel().getArchimateConcept();
            String value = ((Text)event.widget).getText();
            if ( value.isEmpty() ) value = null;        // null value allows to delete the property
            
            SpecializationPropertyCommand command = new SpecializationPropertyCommand(concept, "label", value);

            if ( command.canExecute() ) {
                CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
                compoundCommand.add(command);

                CommandStack stack = (CommandStack) concept.getArchimateModel().getAdapter(CommandStack.class);
                stack.execute(compoundCommand);
            }
        }
    };
	
	@Override
	protected Adapter getECoreAdapter() {
		return this.eAdapter;
	}

	@Override
	protected EObject getEObject() {
        if ( this.relationshipEditPart == null ) {
            logger.error("relationshipEditPart is null"); //$NON-NLS-1$
            return null;
        }

        return this.relationshipEditPart.getModel();
	}

	@Override
    protected void setElement(Object element) {
        this.relationshipEditPart = (ArchimateRelationshipEditPart)new Filter().adaptObject(element);
        if(this.relationshipEditPart == null) {
            logger.error("failed to get relationshipEditPart for " + element); //$NON-NLS-1$
        }

        refreshControls();
	}
	
	void refreshControls() {
	    logger.trace("Refreshing controls");
        
        if ( this.relationshipEditPart == null )
            return;
        
        if ( !SpecializationPlugin.mustReplaceLabel(this.relationshipEditPart.getModel()) ) {
            this.compoNoLabel.setVisible(true);
            this.compoLabel.setVisible(false);
            return;
        }
        
        this.compoNoLabel.setVisible(false);
        this.compoLabel.setVisible(true);
        
        this.txtLabelName.removeModifyListener(this.labelModifyListener);
        String labelName = SpecializationPlugin.getPropertyValue(this.relationshipEditPart.getModel().getArchimateConcept(), "label");
        this.txtLabelName.setText(labelName == null ? "" : labelName);
        this.txtLabelName.addModifyListener(this.labelModifyListener);
	}
}
