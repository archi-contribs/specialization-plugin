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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.editor.propertysections.AbstractArchimatePropertySection;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IFolder;

public class SpecializationFolderSection extends AbstractArchimatePropertySection {
	private static final SpecializationLogger logger = new SpecializationLogger(SpecializationFolderSection.class);

	private IFolder folder;

	private Label lblIconInfo;
	private Label lblReplaceIcons;
	private Button btnIconsYes;
	private Button btnIconsNo;
	private Button btnIconsDefault;
	
    private boolean mouseOverHelpButton = false;
	
    static final private Image    HELP_ICON          = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/28x28/help.png"));
	
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

        lblIconInfo = new Label(parent, SWT.NONE);
        if ( mustUseIconProperty )
            lblIconInfo.setText("Icons in model tree: the preference states to use properties.");
        else 
            lblIconInfo.setText("Icons in model tree: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree")+" replace icons.");
        lblIconInfo.setForeground(parent.getForeground());
        lblIconInfo.setBackground(parent.getBackground());
        lblIconInfo.setFont(parent.getFont());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 5);
        fd.left = new FormAttachment(0, 10);
        lblIconInfo.setLayoutData(fd);
        
        lblReplaceIcons = new Label(parent, SWT.NONE);
		lblReplaceIcons.setText("Replace icons in this folder:");
		lblReplaceIcons.setForeground(parent.getForeground());
		lblReplaceIcons.setBackground(parent.getBackground());
		lblReplaceIcons.setFont(parent.getFont());
		lblReplaceIcons.setEnabled(false);
		fd = new FormData();
        fd.top = new FormAttachment(lblIconInfo, 5);
        fd.left = new FormAttachment(0, 10);
		lblReplaceIcons.setLayoutData(fd);

		Composite compoReplaceIcons = new Composite(parent, SWT.NONE);
		compoReplaceIcons.setBackground(parent.getBackground());
		compoReplaceIcons.setLayout(new RowLayout(SWT.VERTICAL));
		fd = new FormData();
        fd.top = new FormAttachment(lblReplaceIcons, 0, SWT.TOP);
        fd.left = new FormAttachment(lblReplaceIcons, 20);
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
		btnIconsDefault.setText("use model properties");
		btnIconsDefault.setSelection(false);
		btnIconsDefault.setEnabled(false);
		btnIconsDefault.addSelectionListener(replaceIconsListener);

		Label btnHelp = new Label(parent, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(compoReplaceIcons, 25);
        fd.bottom = new FormAttachment(compoReplaceIcons, 55, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(0, 50);
        btnHelp.setLayoutData(fd);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(HELP_ICON, 2, 2);
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
		return eAdapter;
	}

	@Override
	protected EObject getEObject() {
		return folder;
	}

	protected void setElement(Object element) {
		folder = (IFolder)new Filter().adaptObject(element);
		if(folder == null) {
			logger.error("failed to get element for " + element); //$NON-NLS-1$
		}
		
		refreshControls();
	}
	
	private void refreshControls() {
        boolean yes;
        boolean no;
        boolean mustUseIconProperty = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").length() == 0;

        if ( mustUseIconProperty ) {
            lblIconInfo.setText("Icons in model tree: the preference states to use properties.");
            String propValue = SpecializationPlugin.getPropertyValue(folder, "must replace icons");
            if ( propValue != null )
                propValue = propValue.toLowerCase();
            yes = SpecializationPlugin.areEqual(propValue, "yes");
            no = SpecializationPlugin.areEqual(propValue, "no");

            btnIconsYes.setSelection(yes);
            btnIconsNo.setSelection(no);
            btnIconsDefault.setSelection(!yes && !no);
        } else { 
            lblIconInfo.setText("Icons in model tree: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree")+" replace icons.");
            yes = SpecializationPlugin.areEqual(SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").toLowerCase(), "always");
            btnIconsYes.setSelection(yes);
            btnIconsNo.setSelection(!yes);
            btnIconsDefault.setSelection(false);
        }
        lblReplaceIcons.setEnabled(mustUseIconProperty);
        btnIconsYes.setEnabled(mustUseIconProperty);
        btnIconsNo.setEnabled(mustUseIconProperty);
        btnIconsDefault.setEnabled(mustUseIconProperty);
	}


	private SelectionListener replaceIconsListener = new SelectionAdapter () {
		public void widgetSelected(SelectionEvent event) {
			if ( folder == null ) 
				return;

			Button button = ((Button) event.widget);
			if ( !button.getSelection() )
				return;
			
			String value = null;
			if ( button.equals(btnIconsYes) )
				value = "yes";
			else if ( button.equals(btnIconsNo) )
				value = "no";
			
			SpecializationPropertyCommand command = new SpecializationPropertyCommand(folder, "must replace icons", value);
			
            if ( command.canExecute() ) {
    			CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
            	compoundCommand.add(command);

    		    CommandStack stack = (CommandStack) folder.getArchimateModel().getAdapter(CommandStack.class);
    		    stack.execute(compoundCommand);
    		    logger.trace("Setting property \"must replace icons\" to "+value);
    		    SpecializationPlugin.refreshIconsAndLabels(folder);
            }
		};
	};
}
