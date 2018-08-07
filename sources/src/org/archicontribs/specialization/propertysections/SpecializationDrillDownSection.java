/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import java.util.Comparator;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.SpecializationPropertyCommand;
import org.archicontribs.specialization.diagram.ArchimateDiagramEditor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IProperty;

public class SpecializationDrillDownSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationDrillDownSection.class);

	ArchimateElementEditPart elementEditPart = null;

    private Composite compoDrilldown;
	Combo comboDrilldown;
	
    boolean mouseOverHelpButton = false;
    
    static final Image    HELP_ICON          = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/28x28/help.png"));
	
	/**
	 * Filter to show or reject this section depending on input value
	 */
	public static class Filter extends ObjectFilter {
		@Override
		protected boolean isRequiredType(Object object) {
            if ( object != null && object instanceof ArchimateElementEditPart ) {
                logger.trace("Showing Drill down tab as the object is an element");
                return true;
            }
            return false;
		}

		@Override
		protected Class<?> getAdaptableType() {
			return ArchimateElementEditPart.class;
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
	    this.compoDrilldown = new Composite(parent, SWT.NONE);
        this.compoDrilldown.setForeground(parent.getForeground());
        this.compoDrilldown.setBackground(parent.getBackground());
        this.compoDrilldown.setLayout(new FormLayout());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.compoDrilldown.setLayoutData(fd);

		Label label = new Label(this.compoDrilldown, SWT.NONE);
		label.setText("Please select the view to drill down to:");
		label.setForeground(this.compoDrilldown.getForeground());
		label.setBackground(this.compoDrilldown.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        label.setLayoutData(fd);
        
        this.comboDrilldown = new Combo(this.compoDrilldown, SWT.BORDER | SWT.READ_ONLY);
        this.comboDrilldown.setFont(new Font(this.comboDrilldown.getDisplay(), "consolas", this.compoDrilldown.getFont().getFontData()[0].getHeight(), SWT.NONE));
        fd = new FormData();
        fd.top = new FormAttachment(label, 0, SWT.CENTER);
        fd.left = new FormAttachment(label, 20);
        fd.right = new FormAttachment(100, -20);
        this.comboDrilldown.setLayoutData(fd);
        this.comboDrilldown.addModifyListener(this.comboModifyListener);
        
        Label btnHelp = new Label(this.compoDrilldown, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(label, 10);
        fd.bottom = new FormAttachment(label, 40, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(0, 40);
        btnHelp.setLayoutData(fd);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { SpecializationDrillDownSection.this.mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { SpecializationDrillDownSection.this.mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( SpecializationDrillDownSection.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceLabel.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceLabel.html"); } });
        
        Label helpLbl = new Label(this.compoDrilldown, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp, 5);
        helpLbl.setLayoutData(fd);
	}
	
    /**
     * Called when the combo value is changed
     */
    ModifyListener comboModifyListener = new ModifyListener() {
        @SuppressWarnings("synthetic-access")
        @Override
        public void modifyText(ModifyEvent event) {
            IArchimateElement concept = SpecializationDrillDownSection.this.elementEditPart.getModel().getArchimateConcept();
            String value = ((Combo)event.widget).getText();
            String viewId = null;
            if ( !value.isEmpty() ) {
                // value is under the form "name (id)" so we must get the id
                String[] splittedValue = value.split("[\\(\\)]");
                if ( splittedValue != null && splittedValue.length != 0 )
                    viewId = splittedValue[splittedValue.length-1];
            }
            getCommandStack().execute(new SpecializationPropertyCommand(concept, ArchimateDiagramEditor.getDrilldownPropertyName(), viewId, SpecializationDrillDownSection.this.eAdapter));
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
                if( property.getKey().equals(ArchimateDiagramEditor.getDrilldownPropertyName()) )
                    refreshControls();
            }
        }
        
        @Override
        public void setTarget(Notifier n) {
        	if ( n == null )
        		return;
        	
            if ( n instanceof IDiagramModelArchimateObject) {
                super.setTarget(((IDiagramModelArchimateObject)n).getArchimateConcept());
                refreshControls();
            }
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
        if ( this.elementEditPart == null ) {
            logger.trace("elementEditPart is null"); //$NON-NLS-1$
            return null;
        }
        return this.elementEditPart.getModel();
	}

	@Override
    protected void setElement(Object element) {
        this.elementEditPart = (ArchimateElementEditPart)new Filter().adaptObject(element);

        logger.trace("Setting element to "+this.elementEditPart);

        refreshControls();
	}
	
	void refreshControls() {
		if ( this.comboDrilldown == null || this.comboDrilldown.isDisposed() )
			return;
		
        if ( this.elementEditPart == null ) {
            logger.trace("Not refreshing controls as elementEditPart is null");
            this.comboDrilldown.removeModifyListener(this.comboModifyListener);
            this.comboDrilldown.setText("");
            this.comboDrilldown.addModifyListener(this.comboModifyListener);
            return;
        }

        logger.trace("Refreshing controls");
        
        IArchimateModel model = this.elementEditPart.getModel().getArchimateModel();
        String viewId = SpecializationPlugin.getPropertyValue(this.elementEditPart.getModel().getArchimateConcept(), ArchimateDiagramEditor.getDrilldownPropertyName());
        String selectedComboEntry = "";
        
        this.comboDrilldown.removeModifyListener(this.comboModifyListener);
        this.comboDrilldown.removeAll();
        this.comboDrilldown.add("");

        EList<IDiagramModel> allViews = model.getDiagramModels();
        allViews.sort(this.nameComparator);
        int nameLength = 0;
        
        // first, we calculate the longer view name
        for ( IDiagramModel view: allViews ) {
            if ( view.getName().length() > nameLength )
                nameLength = view.getName().length();
        }
        
        //then we fill in the combo, aligning the view IDs
        for ( IDiagramModel view: allViews ) {
            String entry = String.format("%-"+nameLength+"s (%s)",view.getName(), view.getId());
            this.comboDrilldown.add(entry);
            if ( viewId != null && view.getId().equals(viewId) )
                selectedComboEntry = entry;
        }
        this.comboDrilldown.setText(selectedComboEntry);
        
        this.comboDrilldown.addModifyListener(this.comboModifyListener);
	}
	
	private Comparator<IDiagramModel> nameComparator = new Comparator<IDiagramModel>() {
	    @Override public int compare(IDiagramModel view1, IDiagramModel view2) {
	        return view1.getName().compareTo(view2.getName());
	    }
	};
}