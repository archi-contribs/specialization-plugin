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

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.propertysections.AbstractArchimatePropertySection;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimatePackage;

public class SpecializationElementLabelSection extends AbstractArchimatePropertySection {
	private static final SpecializationLogger logger = new SpecializationLogger(SpecializationElementLabelSection.class);

	private ArchimateElementEditPart elementEditPart = null;

    private Composite compoLabel;
    private Composite compoNoLabel;
	private Text txtLabelName;
	
	/**
	 * Filter to show or reject this section depending on input value
	 */
	public static class Filter extends ObjectFilter {
		@Override
		protected boolean isRequiredType(Object object) {
			if ( object == null )
				return false;
			
            logger.trace(object.getClass().getSimpleName()+" -> filter : "+(object instanceof ArchimateElementEditPart));
            if ( !(object instanceof ArchimateElementEditPart) )
                return false;
            
            logger.trace("showing label tab as the element has got a label");
            return true;
		}

		@Override
		protected Class<?> getAdaptableType() {
			return ArchimateElementEditPart.class;
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
	    compoNoLabel = new Composite(parent, SWT.NONE);
        compoNoLabel.setForeground(parent.getForeground());
        compoNoLabel.setBackground(parent.getBackground());
        compoNoLabel.setLayout(new FormLayout());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        compoNoLabel.setLayoutData(fd);

        Label lblNoLabel = new Label(compoNoLabel, SWT.NONE);
        lblNoLabel.setText("You must configure the view to allow labels replacement.");
        lblNoLabel.setForeground(parent.getForeground());
        lblNoLabel.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblNoLabel.setLayoutData(fd);

        /* ********************************************************* */
        compoLabel = new Composite(parent, SWT.NONE);
        compoLabel.setForeground(parent.getForeground());
        compoLabel.setBackground(parent.getBackground());
        compoLabel.setLayout(new FormLayout());
        fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        compoLabel.setLayoutData(fd);

		Label lblLabelName = new Label(compoLabel, SWT.NONE);
		lblLabelName.setText("Label :");
		lblLabelName.setForeground(compoLabel.getForeground());
		lblLabelName.setBackground(compoLabel.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblLabelName.setLayoutData(fd);
        
        txtLabelName = new Text(compoLabel, SWT.BORDER);
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
            IArchimateElement concept = elementEditPart.getModel().getArchimateConcept();
            if ( value.isEmpty() )
                SpecializationPlugin.deleteProperty(concept, "label");
            else
                SpecializationPlugin.setProperty(concept, "label", value);
            // we force the label to refresh on the graphical object
            elementEditPart.getModel().getArchimateConcept().setName(elementEditPart.getModel().getArchimateConcept().getName());
        }
    };
	
	@Override
	protected Adapter getECoreAdapter() {
		return eAdapter;
	}

	@Override
	protected EObject getEObject() {
        if ( elementEditPart == null ) {
            logger.error("elementEditPart is null"); //$NON-NLS-1$
            return null;
        }

        return elementEditPart.getModel();
	}

	protected void setElement(Object element) {
        elementEditPart = (ArchimateElementEditPart)new Filter().adaptObject(element);
        if(elementEditPart == null) {
            logger.error("failed to get elementEditPart for " + element); //$NON-NLS-1$
        }

        refreshControls();
	}
	
	private void refreshControls() {
	    logger.trace("Refreshing controls");
        
        if ( elementEditPart == null )
            return;
        
        if ( !SpecializationPlugin.mustReplaceLabel(elementEditPart.getModel()) ) {
            compoNoLabel.setVisible(true);
            compoLabel.setVisible(false);
            return;
        }
        
        compoNoLabel.setVisible(false);
        compoLabel.setVisible(true);
        
        txtLabelName.removeModifyListener(labelModifyListener);
        String labelName = SpecializationPlugin.getPropertyValue(elementEditPart.getModel().getArchimateConcept(), "label");
        txtLabelName.setText(labelName == null ? "" : labelName);
        txtLabelName.addModifyListener(labelModifyListener);
	}
}
