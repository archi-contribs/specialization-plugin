/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.commands.SpecializationPropertyCommand;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IFolder;

public class SpecializationFolderSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationFolderSection.class);

	IFolder folder;

	private Label lblIconInfo;
	private Label lblReplaceIcons;
	Button btnIconsYes;
	Button btnIconsNo;
	private Button btnIconsDefault;
	
    boolean mouseOverHelpButton = false;
	
	/**
	 * Filter to show or reject this section depending on input value
	 */
	public static class Filter extends ObjectFilter {
		@Override
		protected boolean isRequiredType(Object object) {
			if ( object == null )
				return false;
			
			return (object instanceof IFolder) && !(object instanceof IArchimateModel);
		}

		@Override
		protected Class<?> getAdaptableType() {
			return IFolder.class;
		}
	}

	/**
	 * Create the controls
	 */
	@Override
	protected void createControls(Composite parent) {
		parent.setLayout(new FormLayout());

	      
        boolean mustUseIconProperty = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").length() == 0;

        this.lblIconInfo = new Label(parent, SWT.NONE);
        if ( mustUseIconProperty )
            this.lblIconInfo.setText("Icons in model tree: the preference states to use properties.");
        else 
            this.lblIconInfo.setText("Icons in model tree: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree")+" replace icons.");
        this.lblIconInfo.setForeground(parent.getForeground());
        this.lblIconInfo.setBackground(parent.getBackground());
        this.lblIconInfo.setFont(parent.getFont());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 5);
        fd.left = new FormAttachment(0, 10);
        this.lblIconInfo.setLayoutData(fd);
        
        this.lblReplaceIcons = new Label(parent, SWT.NONE);
		this.lblReplaceIcons.setText("Replace icons in this folder:");
		this.lblReplaceIcons.setForeground(parent.getForeground());
		this.lblReplaceIcons.setBackground(parent.getBackground());
		this.lblReplaceIcons.setFont(parent.getFont());
		this.lblReplaceIcons.setEnabled(false);
		fd = new FormData();
        fd.top = new FormAttachment(this.lblIconInfo, 5);
        fd.left = new FormAttachment(0, 10);
		this.lblReplaceIcons.setLayoutData(fd);

		Composite compoReplaceIcons = new Composite(parent, SWT.NONE);
		compoReplaceIcons.setBackground(parent.getBackground());
		compoReplaceIcons.setLayout(new RowLayout(SWT.VERTICAL));
		fd = new FormData();
        fd.top = new FormAttachment(this.lblReplaceIcons, 0, SWT.TOP);
        fd.left = new FormAttachment(this.lblReplaceIcons, 20);
		compoReplaceIcons.setLayoutData(fd);

		this.btnIconsYes = new Button(compoReplaceIcons, SWT.RADIO);
		this.btnIconsYes.setBackground(parent.getBackground());
		this.btnIconsYes.setForeground(parent.getForeground());
		this.btnIconsYes.setFont(parent.getFont());
		this.btnIconsYes.setText("yes");
		this.btnIconsYes.setSelection(false);
		this.btnIconsYes.setEnabled(false);
		this.btnIconsYes.addSelectionListener(this.replaceIconsListener);

		this.btnIconsNo = new Button(compoReplaceIcons, SWT.RADIO);
		this.btnIconsNo.setBackground(parent.getBackground());
		this.btnIconsNo.setForeground(parent.getForeground());
		this.btnIconsNo.setFont(parent.getFont());
		this.btnIconsNo.setText("no");
		this.btnIconsNo.setSelection(false);
		this.btnIconsNo.setEnabled(false);
		this.btnIconsNo.addSelectionListener(this.replaceIconsListener);

		this.btnIconsDefault = new Button(compoReplaceIcons, SWT.RADIO);
		this.btnIconsDefault.setBackground(parent.getBackground());
		this.btnIconsDefault.setForeground(parent.getForeground());
		this.btnIconsDefault.setFont(parent.getFont());
		this.btnIconsDefault.setText("use model properties");
		this.btnIconsDefault.setSelection(false);
		this.btnIconsDefault.setEnabled(false);
		this.btnIconsDefault.addSelectionListener(this.replaceIconsListener);

		Label btnHelp = new Label(parent, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(compoReplaceIcons, 25);
        fd.bottom = new FormAttachment(compoReplaceIcons, 55, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(0, 50);
        btnHelp.setLayoutData(fd);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { SpecializationFolderSection.this.mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { SpecializationFolderSection.this.mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( SpecializationFolderSection.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(SpecializationPlugin.HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/specializeFolder.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/specializeFolder.html"); } });
        
        Label helpLbl = new Label(parent, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp, 5);
        helpLbl.setLayoutData(fd);
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
		return this.eAdapter;
	}

	@Override
	protected EObject getEObject() {
		return this.folder;
	}

	@Override
    protected void setElement(Object element) {
		this.folder = (IFolder)new Filter().adaptObject(element);
		if(this.folder == null) {
			logger.error("failed to get element for " + element); //$NON-NLS-1$
		}
		
		refreshControls();
	}
	
	void refreshControls() {
        boolean yes;
        boolean no;
        boolean mustUseIconProperty = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").length() == 0;

        if ( mustUseIconProperty ) {
            this.lblIconInfo.setText("Icons in model tree: the preference states to use properties.");
            String propValue = SpecializationPlugin.getPropertyValue(this.folder, "must replace icons");
            if ( propValue != null )
                propValue = propValue.toLowerCase();
            yes = SpecializationPlugin.areEqual(propValue, "yes");
            no = SpecializationPlugin.areEqual(propValue, "no");

            this.btnIconsYes.setSelection(yes);
            this.btnIconsNo.setSelection(no);
            this.btnIconsDefault.setSelection(!yes && !no);
        } else { 
            this.lblIconInfo.setText("Icons in model tree: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree")+" replace icons.");
            yes = SpecializationPlugin.areEqual(SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").toLowerCase(), "always");
            this.btnIconsYes.setSelection(yes);
            this.btnIconsNo.setSelection(!yes);
            this.btnIconsDefault.setSelection(false);
        }
        this.lblReplaceIcons.setEnabled(mustUseIconProperty);
        this.btnIconsYes.setEnabled(mustUseIconProperty);
        this.btnIconsNo.setEnabled(mustUseIconProperty);
        this.btnIconsDefault.setEnabled(mustUseIconProperty);
	}


	private SelectionListener replaceIconsListener = new SelectionAdapter () {
		@Override
        public void widgetSelected(SelectionEvent event) {
			if ( SpecializationFolderSection.this.folder == null ) 
				return;

			Button button = ((Button) event.widget);
			if ( !button.getSelection() )
				return;
			
			String value = null;
			if ( button.equals(SpecializationFolderSection.this.btnIconsYes) )
				value = "yes";
			else if ( button.equals(SpecializationFolderSection.this.btnIconsNo) )
				value = "no";
			
			SpecializationPropertyCommand command = new SpecializationPropertyCommand(SpecializationFolderSection.this.folder, "must replace icons", value);
			
            if ( command.canExecute() ) {
    			CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
            	compoundCommand.add(command);

    		    CommandStack stack = (CommandStack) SpecializationFolderSection.this.folder.getArchimateModel().getAdapter(CommandStack.class);
    		    stack.execute(compoundCommand);
    		    logger.trace("Setting property \"must replace icons\" to "+value);
    		    SpecializationPlugin.refreshIconsAndLabels(SpecializationFolderSection.this.folder);
            }
		}
	};
}
