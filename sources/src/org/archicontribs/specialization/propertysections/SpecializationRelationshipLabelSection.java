/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.editor.propertysections.AbstractArchimatePropertySection;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;

public class SpecializationRelationshipLabelSection extends AbstractArchimatePropertySection {
	private static final SpecializationLogger logger = new SpecializationLogger(SpecializationRelationshipLabelSection.class);

	private ArchimateRelationshipEditPart relationshipEditPart = null;

    private Composite compoIcon;
    private Composite compoNoIcon;
    private Label lblNoIcon;
    private Text txtLabelName;
	
	/**
	 * Filter to show or reject this section depending on input value
	 */
	public static class Filter extends ObjectFilter {
		@Override
		protected boolean isRequiredType(Object object) {
			if ( object == null )
				return false;
			
            logger.trace(object.getClass().getSimpleName()+" -> filter : "+(object instanceof ArchimateRelationshipEditPart));
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
	    compoNoIcon = new Composite(parent, SWT.NONE);
        compoNoIcon.setForeground(parent.getForeground());
        compoNoIcon.setBackground(parent.getBackground());
        compoNoIcon.setLayout(new FormLayout());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        compoNoIcon.setLayoutData(fd);

        lblNoIcon = new Label(compoNoIcon, SWT.NONE);
        lblNoIcon.setText("You must configure the view to allow labels replacement.");
        lblNoIcon.setForeground(parent.getForeground());
        lblNoIcon.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblNoIcon.setLayoutData(fd);

        /* ********************************************************* */
        compoIcon = new Composite(parent, SWT.NONE);
        compoIcon.setForeground(parent.getForeground());
        compoIcon.setBackground(parent.getBackground());
        compoIcon.setLayout(new FormLayout());
        fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        compoIcon.setLayoutData(fd);

		Label lblLabelName = new Label(compoIcon, SWT.NONE);
		lblLabelName.setText("Label :");
		lblLabelName.setForeground(compoIcon.getForeground());
		lblLabelName.setBackground(compoIcon.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblLabelName.setLayoutData(fd);
        
        txtLabelName = new Text(compoIcon, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblLabelName, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblLabelName, 35);
        fd.right = new FormAttachment(0, 500);
        txtLabelName.setLayoutData(fd);
        txtLabelName.addModifyListener(labelModifyListener);
	}
	
    /**
     * Called when the label name is changed in the txtLabelName text widget
     */
    private ModifyListener labelModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            Text text = (Text)event.widget;
            String value = text.getText();
            IArchimateRelationship relationship = relationshipEditPart.getModel().getArchimateRelationship();
            if ( value.isEmpty() )
                SpecializationPlugin.deleteProperty(relationship, "label");
            else
                SpecializationPlugin.setProperty(relationship, "label", value);
            // we force the label to refresh on the graphical object
            relationshipEditPart.getModel().getArchimateRelationship().setName(relationshipEditPart.getModel().getArchimateRelationship().getName());
        }
    };
	
	@Override
	protected Adapter getECoreAdapter() {
		return eAdapter;
	}

	@Override
	protected EObject getEObject() {
        if ( relationshipEditPart == null ) {
            logger.error("relationshipEditPart is null"); //$NON-NLS-1$
            return null;
        }

        return relationshipEditPart.getModel();
	}

	protected void setElement(Object element) {
        relationshipEditPart = (ArchimateRelationshipEditPart)new Filter().adaptObject(element);
        if(relationshipEditPart == null) {
            logger.error("failed to get relationshipEditPart for " + element); //$NON-NLS-1$
        }

        refreshControls();
	}
	
	private void refreshControls() {
	    logger.trace("Refreshing controls");
        
        if ( relationshipEditPart == null )
            return;
        
        if ( !SpecializationPlugin.mustReplaceIcon(relationshipEditPart.getModel()) ) {
            compoNoIcon.setVisible(true);
            compoIcon.setVisible(false);
            return;
        }
        
        compoNoIcon.setVisible(false);
        compoIcon.setVisible(true);
        
        txtLabelName.removeModifyListener(labelModifyListener);
        String labelName = SpecializationPlugin.getPropertyValue(relationshipEditPart.getModel().getArchimateConcept(), "label");
        txtLabelName.setText(labelName == null ? "" : labelName);
        txtLabelName.addModifyListener(labelModifyListener);
	}
}
