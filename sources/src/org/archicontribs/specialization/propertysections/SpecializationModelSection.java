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

public class SpecializationModelSection extends AbstractArchimatePropertySection {
	private static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);

	private IArchimateModel model;

	private Label lblIconInViewsInfo;
	private Label lblReplaceInViewsIcons;
	private Button btnIconsInViewsYes;
	private Button btnIconsInViewsNo;
	private Button btnIconsInViewsDefault;
	
    private Label lblIconInTreeInfo;
    private Label lblReplaceInTreeIcons;
    private Button btnIconsInTreeYes;
    private Button btnIconsInTreeNo;
    private Button btnIconsInTreeDefault;


	private Label lblLabelInfo;
	private Label lblReplaceLabels;
	private Button btnLabelsYes;
	private Button btnLabelsNo;
	private Button btnLabelsDefault;
	
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
			
			logger.trace(object.getClass().getSimpleName()+" -> filter : "+(object instanceof IArchimateModel));
			return object instanceof IArchimateModel;
		}

		@Override
		protected Class<?> getAdaptableType() {
			return IArchimateModel.class;
		}
	}

	/**
	 * Create the controls
	 */
	@Override
	protected void createControls(Composite parent) {
		parent.setLayout(new FormLayout());

		/* **************************************************** */
        boolean mustReplaceIconsInViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews").length() == 0;

        lblIconInViewsInfo = new Label(parent, SWT.NONE);
        if ( mustReplaceIconsInViews )
            lblIconInViewsInfo.setText("Icons in views: the preference states to use properties.");
        else 
            lblIconInViewsInfo.setText("Icons in views: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews")+" replace icons.");
        lblIconInViewsInfo.setForeground(parent.getForeground());
        lblIconInViewsInfo.setBackground(parent.getBackground());
        lblIconInViewsInfo.setFont(parent.getFont());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 5);
        fd.left = new FormAttachment(0, 10);
        lblIconInViewsInfo.setLayoutData(fd);
        
        lblReplaceInViewsIcons = new Label(parent, SWT.NONE);
		lblReplaceInViewsIcons.setText("Replace icons in all the views of this model:");
		lblReplaceInViewsIcons.setForeground(parent.getForeground());
		lblReplaceInViewsIcons.setBackground(parent.getBackground());
		lblReplaceInViewsIcons.setFont(parent.getFont());
		lblReplaceInViewsIcons.setEnabled(false);
		fd = new FormData();
        fd.top = new FormAttachment(lblIconInViewsInfo, 5);
        fd.left = new FormAttachment(0, 10);
		lblReplaceInViewsIcons.setLayoutData(fd);

		Composite compoReplaceIconsInViews = new Composite(parent, SWT.NONE);
		compoReplaceIconsInViews.setBackground(parent.getBackground());
		compoReplaceIconsInViews.setLayout(new RowLayout(SWT.VERTICAL));
		fd = new FormData();
        fd.top = new FormAttachment(lblReplaceInViewsIcons, 0, SWT.TOP);
        fd.left = new FormAttachment(lblReplaceInViewsIcons, 20);
		compoReplaceIconsInViews.setLayoutData(fd);

		btnIconsInViewsYes = new Button(compoReplaceIconsInViews, SWT.RADIO);
		btnIconsInViewsYes.setBackground(parent.getBackground());
		btnIconsInViewsYes.setForeground(parent.getForeground());
		btnIconsInViewsYes.setFont(parent.getFont());
		btnIconsInViewsYes.setText("yes");
		btnIconsInViewsYes.setSelection(false);
		btnIconsInViewsYes.setEnabled(false);
		btnIconsInViewsYes.addSelectionListener(replaceIconsInViewsListener);

		btnIconsInViewsNo = new Button(compoReplaceIconsInViews, SWT.RADIO);
		btnIconsInViewsNo.setBackground(parent.getBackground());
		btnIconsInViewsNo.setForeground(parent.getForeground());
		btnIconsInViewsNo.setFont(parent.getFont());
		btnIconsInViewsNo.setText("no");
		btnIconsInViewsNo.setSelection(false);
		btnIconsInViewsNo.setEnabled(false);
		btnIconsInViewsNo.addSelectionListener(replaceIconsInViewsListener);

		btnIconsInViewsDefault = new Button(compoReplaceIconsInViews, SWT.RADIO);
		btnIconsInViewsDefault.setBackground(parent.getBackground());
		btnIconsInViewsDefault.setForeground(parent.getForeground());
		btnIconsInViewsDefault.setFont(parent.getFont());
		btnIconsInViewsDefault.setText("use views properties");
		btnIconsInViewsDefault.setSelection(false);
		btnIconsInViewsDefault.setEnabled(false);
		btnIconsInViewsDefault.addSelectionListener(replaceIconsInViewsListener);
		
		/* **************************************************** */
        boolean mustReplaceIconsInTree = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").length() == 0;

        lblIconInTreeInfo = new Label(parent, SWT.NONE);
        if ( mustReplaceIconsInTree )
            lblIconInTreeInfo.setText("Icons in model tree: the preference states to use properties.");
        else 
            lblIconInTreeInfo.setText("Icons in model tree: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree")+" replace icons.");
        lblIconInTreeInfo.setForeground(parent.getForeground());
        lblIconInTreeInfo.setBackground(parent.getBackground());
        lblIconInTreeInfo.setFont(parent.getFont());
        fd = new FormData();
        fd.top = new FormAttachment(compoReplaceIconsInViews, 20);
        fd.left = new FormAttachment(0, 10);
        lblIconInTreeInfo.setLayoutData(fd);
        
        lblReplaceInTreeIcons = new Label(parent, SWT.NONE);
        lblReplaceInTreeIcons.setText("Replace icons in the model tree of this model:");
        lblReplaceInTreeIcons.setForeground(parent.getForeground());
        lblReplaceInTreeIcons.setBackground(parent.getBackground());
        lblReplaceInTreeIcons.setFont(parent.getFont());
        lblReplaceInTreeIcons.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconInTreeInfo, 5);
        fd.left = new FormAttachment(0, 10);
        lblReplaceInTreeIcons.setLayoutData(fd);

        Composite compoReplaceIconsInTree = new Composite(parent, SWT.NONE);
        compoReplaceIconsInTree.setBackground(parent.getBackground());
        compoReplaceIconsInTree.setLayout(new RowLayout(SWT.VERTICAL));
        fd = new FormData();
        fd.top = new FormAttachment(lblReplaceInTreeIcons, 0, SWT.TOP);
        fd.left = new FormAttachment(lblReplaceInTreeIcons, 20);
        compoReplaceIconsInTree.setLayoutData(fd);

        btnIconsInTreeYes = new Button(compoReplaceIconsInTree, SWT.RADIO);
        btnIconsInTreeYes.setBackground(parent.getBackground());
        btnIconsInTreeYes.setForeground(parent.getForeground());
        btnIconsInTreeYes.setFont(parent.getFont());
        btnIconsInTreeYes.setText("yes");
        btnIconsInTreeYes.setSelection(false);
        btnIconsInTreeYes.setEnabled(false);
        btnIconsInTreeYes.addSelectionListener(replaceIconsInTreeListener);

        btnIconsInTreeNo = new Button(compoReplaceIconsInTree, SWT.RADIO);
        btnIconsInTreeNo.setBackground(parent.getBackground());
        btnIconsInTreeNo.setForeground(parent.getForeground());
        btnIconsInTreeNo.setFont(parent.getFont());
        btnIconsInTreeNo.setText("no");
        btnIconsInTreeNo.setSelection(false);
        btnIconsInTreeNo.setEnabled(false);
        btnIconsInTreeNo.addSelectionListener(replaceIconsInTreeListener);

        btnIconsInTreeDefault = new Button(compoReplaceIconsInTree, SWT.RADIO);
        btnIconsInTreeDefault.setBackground(parent.getBackground());
        btnIconsInTreeDefault.setForeground(parent.getForeground());
        btnIconsInTreeDefault.setFont(parent.getFont());
        btnIconsInTreeDefault.setText("use views properties");
        btnIconsInTreeDefault.setSelection(false);
        btnIconsInTreeDefault.setEnabled(false);
        btnIconsInTreeDefault.addSelectionListener(replaceIconsInTreeListener);
		
		/* **************************************************** */
        boolean mustReplaceLabelsInViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews").length() == 0;
        
        lblLabelInfo = new Label(parent, SWT.NONE);
        if ( mustReplaceLabelsInViews )
            lblLabelInfo.setText("Labels in views: the preference states to use properties.");
        else 
            lblLabelInfo.setText("Labels in views: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews")+" replace labels.");
        lblLabelInfo.setForeground(parent.getForeground());
        lblLabelInfo.setBackground(parent.getBackground());
        lblLabelInfo.setFont(parent.getFont());
        fd = new FormData();
        fd.top = new FormAttachment(compoReplaceIconsInTree, 20);
        fd.left = new FormAttachment(0, 10);
        lblLabelInfo.setLayoutData(fd);

		lblReplaceLabels = new Label(parent, SWT.NONE);
		lblReplaceLabels.setText("Replace labels in all the views of this model:");
		lblReplaceLabels.setForeground(parent.getForeground());
		lblReplaceLabels.setBackground(parent.getBackground());
		lblReplaceLabels.setFont(parent.getFont());
		lblReplaceLabels.setEnabled(false);
		fd = new FormData();
        fd.top = new FormAttachment(lblLabelInfo, 5);
        fd.left = new FormAttachment(0, 10);
		lblReplaceLabels.setLayoutData(fd);

		Composite compoReplaceLabels = new Composite(parent, SWT.NONE);
		compoReplaceLabels.setBackground(parent.getBackground());
		compoReplaceLabels.setLayout(new RowLayout(SWT.VERTICAL));
		fd = new FormData();
        fd.top = new FormAttachment(lblReplaceLabels, 0, SWT.TOP);
        fd.left = new FormAttachment(compoReplaceIconsInTree, 0, SWT.LEFT);
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
		btnLabelsDefault.setText("use views properties");
		btnLabelsDefault.setSelection(false);
		btnLabelsDefault.setEnabled(false);
		btnLabelsDefault.addSelectionListener(replaceLabelsListener);
		
		Label btnHelp = new Label(parent, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(compoReplaceLabels, 25);
        fd.bottom = new FormAttachment(compoReplaceLabels, 55, SWT.BOTTOM);
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
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/configureModel.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/configureModel.html"); } });
        
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
		return model;
	}

	protected void setElement(Object element) {
		model = (IArchimateModel)new Filter().adaptObject(element);
		if(model == null) {
			logger.error("failed to get element for " + element); //$NON-NLS-1$
		}
		
		refreshControls();
	}
	
	private void refreshControls() {
        boolean yes;
        boolean no;
        boolean mustReplaceIconsInViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews").length() == 0;
        boolean mustReplaceIconsInTree = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").length() == 0;
        boolean mustReplaceLabelsInViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews").length() == 0;

        if ( mustReplaceIconsInViews ) {
            lblIconInViewsInfo.setText("Icons in views: the preference states to use properties.");
            String propValue = SpecializationPlugin.getPropertyValue(model, "must replace icons in views");
            if ( propValue != null )
                propValue = propValue.toLowerCase();
            yes = SpecializationPlugin.areEqual(propValue, "yes");
            no = SpecializationPlugin.areEqual(propValue, "no");

            btnIconsInViewsYes.setSelection(yes);
            btnIconsInViewsNo.setSelection(no);
            btnIconsInViewsDefault.setSelection(!yes && !no);
        } else { 
            lblIconInViewsInfo.setText("Icons in views: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews")+" replace icons.");
            yes = SpecializationPlugin.areEqual(SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews").toLowerCase(), "always");
            btnIconsInViewsYes.setSelection(yes);
            btnIconsInViewsNo.setSelection(!yes);
            btnIconsInViewsDefault.setSelection(false);
        }
        lblReplaceInViewsIcons.setEnabled(mustReplaceIconsInViews);
        btnIconsInViewsYes.setEnabled(mustReplaceIconsInViews);
        btnIconsInViewsNo.setEnabled(mustReplaceIconsInViews);
        btnIconsInViewsDefault.setEnabled(mustReplaceIconsInViews);
        
        if ( mustReplaceIconsInTree ) {
            lblIconInTreeInfo.setText("Icons in model tree: the preference states to use properties.");
            String propValue = SpecializationPlugin.getPropertyValue(model, "must replace icons in tree");
            if ( propValue != null )
                propValue = propValue.toLowerCase();
            yes = SpecializationPlugin.areEqual(propValue, "yes");
            no = SpecializationPlugin.areEqual(propValue, "no");

            btnIconsInTreeYes.setSelection(yes);
            btnIconsInTreeNo.setSelection(no);
            btnIconsInTreeDefault.setSelection(!yes && !no);
        } else { 
            lblIconInTreeInfo.setText("Icons in model tree: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree")+" replace icons.");
            yes = SpecializationPlugin.areEqual(SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").toLowerCase(), "always");
            btnIconsInTreeYes.setSelection(yes);
            btnIconsInTreeNo.setSelection(!yes);
            btnIconsInTreeDefault.setSelection(false);
        }
        lblReplaceInTreeIcons.setEnabled(mustReplaceIconsInTree);
        btnIconsInTreeYes.setEnabled(mustReplaceIconsInTree);
        btnIconsInTreeNo.setEnabled(mustReplaceIconsInTree);
        btnIconsInTreeDefault.setEnabled(mustReplaceIconsInTree);

        if ( mustReplaceLabelsInViews ) {
            lblLabelInfo.setText("Labels in views: the preference states to use properties.");
            String propValue = SpecializationPlugin.getPropertyValue(model, "must replace labels");
            if ( propValue != null )
                propValue = propValue.toLowerCase();
            yes = SpecializationPlugin.areEqual(propValue, "yes");
            no = SpecializationPlugin.areEqual(propValue, "no");

            btnLabelsYes.setSelection(yes);
            btnLabelsNo.setSelection(no);
            btnLabelsDefault.setSelection(!yes && !no);
        } else { 
            lblLabelInfo.setText("Labels in views: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews")+" replace labels.");
            yes = SpecializationPlugin.areEqual(SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews").toLowerCase(), "always");
            btnLabelsYes.setSelection(yes);
            btnLabelsNo.setSelection(!yes);
            btnLabelsDefault.setSelection(false);
        }
        lblReplaceLabels.setEnabled(mustReplaceLabelsInViews);
        btnLabelsYes.setEnabled(mustReplaceLabelsInViews);
        btnLabelsNo.setEnabled(mustReplaceLabelsInViews);
        btnLabelsDefault.setEnabled(mustReplaceLabelsInViews);
	}


	private SelectionListener replaceIconsInViewsListener = new SelectionAdapter () {
		public void widgetSelected(SelectionEvent event) {
			if ( model == null ) 
				return;

			Button button = ((Button) event.widget);
			if ( !button.getSelection() )
				return;
			
			String value = null;
			if ( button.equals(btnIconsInViewsYes) )
				value = "yes";
			else if ( button.equals(btnIconsInViewsNo) )
				value = "no";
			
			SpecializationPropertyCommand command = new SpecializationPropertyCommand(model, "must replace icons in views", value);
			
            if ( command.canExecute() ) {
    			CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
            	compoundCommand.add(command);

    		    CommandStack stack = (CommandStack) model.getArchimateModel().getAdapter(CommandStack.class);
    		    stack.execute(compoundCommand);
    		    logger.trace("Setting property \"must replace icons in views\" to "+value);
    		    SpecializationPlugin.refreshIconsAndLabels(model);
            }
		};
	};
	
	   private SelectionListener replaceIconsInTreeListener = new SelectionAdapter () {
	        public void widgetSelected(SelectionEvent event) {
	            if ( model == null ) 
	                return;

	            Button button = ((Button) event.widget);
	            if ( !button.getSelection() )
	                return;
	            
	            String value = null;
	            if ( button.equals(btnIconsInTreeYes) )
	                value = "yes";
	            else if ( button.equals(btnIconsInTreeNo) )
	                value = "no";
	            
	            SpecializationPropertyCommand command = new SpecializationPropertyCommand(model, "must replace icons in tree", value);
	            
	            if ( command.canExecute() ) {
	                CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
	                compoundCommand.add(command);

	                CommandStack stack = (CommandStack) model.getArchimateModel().getAdapter(CommandStack.class);
	                stack.execute(compoundCommand);
	                logger.trace("Setting property \"must replace icons in tree\" to "+value);
	                SpecializationPlugin.refreshIconsAndLabels(model);
	            }
	        };
	    };

	private SelectionListener replaceLabelsListener = new SelectionAdapter () {
		public void widgetSelected(SelectionEvent event) {
			if ( model == null ) 
				return;

			Button button = ((Button) event.widget);
			if ( !button.getSelection() )
				return;

			String value = null;
			if ( button.equals(btnLabelsYes) )
				value = "yes";
			else if ( button.equals(btnLabelsNo) )
				value = "no";
			
			SpecializationPropertyCommand command = new SpecializationPropertyCommand(model, "must replace labels", value);
			
            if ( command.canExecute() ) {
    			CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
            	compoundCommand.add(command);

    		    CommandStack stack = (CommandStack) model.getAdapter(CommandStack.class);
    		    stack.execute(compoundCommand);
    		    logger.trace("Setting property \"must replace labels\" to "+value);
    		    SpecializationPlugin.refreshIconsAndLabels(model);
            }
		};
	};
}
