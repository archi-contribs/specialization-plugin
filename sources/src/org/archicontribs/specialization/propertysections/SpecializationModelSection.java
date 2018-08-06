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
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;

public class SpecializationModelSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);

	IArchimateModel model;

	private Label lblIconInViewsInfo;
	private Label lblReplaceInViewsIcons;
	Button btnIconsInViewsYes;
	Button btnIconsInViewsNo;
	private Button btnIconsInViewsDefault;
	
    private Label lblIconInTreeInfo;
    private Label lblReplaceInTreeIcons;
    Button btnIconsInTreeYes;
    Button btnIconsInTreeNo;
    private Button btnIconsInTreeDefault;


	private Label lblLabelInfo;
	private Label lblReplaceLabels;
	Button btnLabelsYes;
	Button btnLabelsNo;
	private Button btnLabelsDefault;
	
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

        this.lblIconInViewsInfo = new Label(parent, SWT.NONE);
        if ( mustReplaceIconsInViews )
            this.lblIconInViewsInfo.setText("Icons in views: the preference states to use properties.");
        else 
            this.lblIconInViewsInfo.setText("Icons in views: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews")+" replace icons.");
        this.lblIconInViewsInfo.setForeground(parent.getForeground());
        this.lblIconInViewsInfo.setBackground(parent.getBackground());
        this.lblIconInViewsInfo.setFont(parent.getFont());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 5);
        fd.left = new FormAttachment(0, 10);
        this.lblIconInViewsInfo.setLayoutData(fd);
        
        this.lblReplaceInViewsIcons = new Label(parent, SWT.NONE);
		this.lblReplaceInViewsIcons.setText("Replace icons in all the views of this model:");
		this.lblReplaceInViewsIcons.setForeground(parent.getForeground());
		this.lblReplaceInViewsIcons.setBackground(parent.getBackground());
		this.lblReplaceInViewsIcons.setFont(parent.getFont());
		this.lblReplaceInViewsIcons.setEnabled(false);
		fd = new FormData();
        fd.top = new FormAttachment(this.lblIconInViewsInfo, 5);
        fd.left = new FormAttachment(0, 10);
		this.lblReplaceInViewsIcons.setLayoutData(fd);

		Composite compoReplaceIconsInViews = new Composite(parent, SWT.NONE);
		compoReplaceIconsInViews.setBackground(parent.getBackground());
		compoReplaceIconsInViews.setLayout(new RowLayout(SWT.VERTICAL));
		fd = new FormData();
        fd.top = new FormAttachment(this.lblReplaceInViewsIcons, 0, SWT.TOP);
        fd.left = new FormAttachment(this.lblReplaceInViewsIcons, 20);
		compoReplaceIconsInViews.setLayoutData(fd);

		this.btnIconsInViewsYes = new Button(compoReplaceIconsInViews, SWT.RADIO);
		this.btnIconsInViewsYes.setBackground(parent.getBackground());
		this.btnIconsInViewsYes.setForeground(parent.getForeground());
		this.btnIconsInViewsYes.setFont(parent.getFont());
		this.btnIconsInViewsYes.setText("yes");
		this.btnIconsInViewsYes.setSelection(false);
		this.btnIconsInViewsYes.setEnabled(false);
		this.btnIconsInViewsYes.addSelectionListener(this.replaceIconsInViewsListener);

		this.btnIconsInViewsNo = new Button(compoReplaceIconsInViews, SWT.RADIO);
		this.btnIconsInViewsNo.setBackground(parent.getBackground());
		this.btnIconsInViewsNo.setForeground(parent.getForeground());
		this.btnIconsInViewsNo.setFont(parent.getFont());
		this.btnIconsInViewsNo.setText("no");
		this.btnIconsInViewsNo.setSelection(false);
		this.btnIconsInViewsNo.setEnabled(false);
		this.btnIconsInViewsNo.addSelectionListener(this.replaceIconsInViewsListener);

		this.btnIconsInViewsDefault = new Button(compoReplaceIconsInViews, SWT.RADIO);
		this.btnIconsInViewsDefault.setBackground(parent.getBackground());
		this.btnIconsInViewsDefault.setForeground(parent.getForeground());
		this.btnIconsInViewsDefault.setFont(parent.getFont());
		this.btnIconsInViewsDefault.setText("use views properties");
		this.btnIconsInViewsDefault.setSelection(false);
		this.btnIconsInViewsDefault.setEnabled(false);
		this.btnIconsInViewsDefault.addSelectionListener(this.replaceIconsInViewsListener);
		
		/* **************************************************** */
        boolean mustReplaceIconsInTree = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").length() == 0;

        this.lblIconInTreeInfo = new Label(parent, SWT.NONE);
        if ( mustReplaceIconsInTree )
            this.lblIconInTreeInfo.setText("Icons in model tree: the preference states to use properties.");
        else 
            this.lblIconInTreeInfo.setText("Icons in model tree: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree")+" replace icons.");
        this.lblIconInTreeInfo.setForeground(parent.getForeground());
        this.lblIconInTreeInfo.setBackground(parent.getBackground());
        this.lblIconInTreeInfo.setFont(parent.getFont());
        fd = new FormData();
        fd.top = new FormAttachment(compoReplaceIconsInViews, 20);
        fd.left = new FormAttachment(0, 10);
        this.lblIconInTreeInfo.setLayoutData(fd);
        
        this.lblReplaceInTreeIcons = new Label(parent, SWT.NONE);
        this.lblReplaceInTreeIcons.setText("Replace icons in the model tree of this model:");
        this.lblReplaceInTreeIcons.setForeground(parent.getForeground());
        this.lblReplaceInTreeIcons.setBackground(parent.getBackground());
        this.lblReplaceInTreeIcons.setFont(parent.getFont());
        this.lblReplaceInTreeIcons.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblIconInTreeInfo, 5);
        fd.left = new FormAttachment(0, 10);
        this.lblReplaceInTreeIcons.setLayoutData(fd);

        Composite compoReplaceIconsInTree = new Composite(parent, SWT.NONE);
        compoReplaceIconsInTree.setBackground(parent.getBackground());
        compoReplaceIconsInTree.setLayout(new RowLayout(SWT.VERTICAL));
        fd = new FormData();
        fd.top = new FormAttachment(this.lblReplaceInTreeIcons, 0, SWT.TOP);
        fd.left = new FormAttachment(this.lblReplaceInTreeIcons, 20);
        compoReplaceIconsInTree.setLayoutData(fd);

        this.btnIconsInTreeYes = new Button(compoReplaceIconsInTree, SWT.RADIO);
        this.btnIconsInTreeYes.setBackground(parent.getBackground());
        this.btnIconsInTreeYes.setForeground(parent.getForeground());
        this.btnIconsInTreeYes.setFont(parent.getFont());
        this.btnIconsInTreeYes.setText("yes");
        this.btnIconsInTreeYes.setSelection(false);
        this.btnIconsInTreeYes.setEnabled(false);
        this.btnIconsInTreeYes.addSelectionListener(this.replaceIconsInTreeListener);

        this.btnIconsInTreeNo = new Button(compoReplaceIconsInTree, SWT.RADIO);
        this.btnIconsInTreeNo.setBackground(parent.getBackground());
        this.btnIconsInTreeNo.setForeground(parent.getForeground());
        this.btnIconsInTreeNo.setFont(parent.getFont());
        this.btnIconsInTreeNo.setText("no");
        this.btnIconsInTreeNo.setSelection(false);
        this.btnIconsInTreeNo.setEnabled(false);
        this.btnIconsInTreeNo.addSelectionListener(this.replaceIconsInTreeListener);

        this.btnIconsInTreeDefault = new Button(compoReplaceIconsInTree, SWT.RADIO);
        this.btnIconsInTreeDefault.setBackground(parent.getBackground());
        this.btnIconsInTreeDefault.setForeground(parent.getForeground());
        this.btnIconsInTreeDefault.setFont(parent.getFont());
        this.btnIconsInTreeDefault.setText("use views properties");
        this.btnIconsInTreeDefault.setSelection(false);
        this.btnIconsInTreeDefault.setEnabled(false);
        this.btnIconsInTreeDefault.addSelectionListener(this.replaceIconsInTreeListener);
		
		/* **************************************************** */
        boolean mustReplaceLabelsInViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews").length() == 0;
        
        this.lblLabelInfo = new Label(parent, SWT.NONE);
        if ( mustReplaceLabelsInViews )
            this.lblLabelInfo.setText("Labels in views: the preference states to use properties.");
        else 
            this.lblLabelInfo.setText("Labels in views: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews")+" replace labels.");
        this.lblLabelInfo.setForeground(parent.getForeground());
        this.lblLabelInfo.setBackground(parent.getBackground());
        this.lblLabelInfo.setFont(parent.getFont());
        fd = new FormData();
        fd.top = new FormAttachment(compoReplaceIconsInTree, 20);
        fd.left = new FormAttachment(0, 10);
        this.lblLabelInfo.setLayoutData(fd);

		this.lblReplaceLabels = new Label(parent, SWT.NONE);
		this.lblReplaceLabels.setText("Replace labels in all the views of this model:");
		this.lblReplaceLabels.setForeground(parent.getForeground());
		this.lblReplaceLabels.setBackground(parent.getBackground());
		this.lblReplaceLabels.setFont(parent.getFont());
		this.lblReplaceLabels.setEnabled(false);
		fd = new FormData();
        fd.top = new FormAttachment(this.lblLabelInfo, 5);
        fd.left = new FormAttachment(0, 10);
		this.lblReplaceLabels.setLayoutData(fd);

		Composite compoReplaceLabels = new Composite(parent, SWT.NONE);
		compoReplaceLabels.setBackground(parent.getBackground());
		compoReplaceLabels.setLayout(new RowLayout(SWT.VERTICAL));
		fd = new FormData();
        fd.top = new FormAttachment(this.lblReplaceLabels, 0, SWT.TOP);
        fd.left = new FormAttachment(compoReplaceIconsInTree, 0, SWT.LEFT);
		compoReplaceLabels.setLayoutData(fd);

		this.btnLabelsYes = new Button(compoReplaceLabels, SWT.RADIO);
		this.btnLabelsYes.setBackground(parent.getBackground());
		this.btnLabelsYes.setForeground(parent.getForeground());
		this.btnLabelsYes.setFont(parent.getFont());
		this.btnLabelsYes.setText("yes");
		this.btnLabelsYes.setSelection(false);
		this.btnLabelsYes.setEnabled(false);
		this.btnLabelsYes.addSelectionListener(this.replaceLabelsListener);

		this.btnLabelsNo = new Button(compoReplaceLabels, SWT.RADIO);
		this.btnLabelsNo.setBackground(parent.getBackground());
		this.btnLabelsNo.setForeground(parent.getForeground());
		this.btnLabelsNo.setFont(parent.getFont());
		this.btnLabelsNo.setText("no");
		this.btnLabelsNo.setSelection(false);
		this.btnLabelsNo.setEnabled(false);
		this.btnLabelsNo.addSelectionListener(this.replaceLabelsListener);

		this.btnLabelsDefault = new Button(compoReplaceLabels, SWT.RADIO);
		this.btnLabelsDefault.setBackground(parent.getBackground());
		this.btnLabelsDefault.setForeground(parent.getForeground());
		this.btnLabelsDefault.setFont(parent.getFont());
		this.btnLabelsDefault.setText("use views properties");
		this.btnLabelsDefault.setSelection(false);
		this.btnLabelsDefault.setEnabled(false);
		this.btnLabelsDefault.addSelectionListener(this.replaceLabelsListener);
		
		Label btnHelp = new Label(parent, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(compoReplaceLabels, 25);
        fd.bottom = new FormAttachment(compoReplaceLabels, 55, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(0, 50);
        btnHelp.setLayoutData(fd);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { SpecializationModelSection.this.mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { SpecializationModelSection.this.mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( SpecializationModelSection.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/specializeModel.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/specializeModel.html"); } });
        
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
		return this.model;
	}

	@Override
    protected void setElement(Object element) {
		this.model = (IArchimateModel)new Filter().adaptObject(element);
		if(this.model == null) {
			logger.error("failed to get element for " + element); //$NON-NLS-1$
		}
		
		refreshControls();
	}
	
	void refreshControls() {
        boolean yes;
        boolean no;
        boolean mustReplaceIconsInViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews").length() == 0;
        boolean mustReplaceIconsInTree = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").length() == 0;
        boolean mustReplaceLabelsInViews = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews").length() == 0;

        if ( mustReplaceIconsInViews ) {
            this.lblIconInViewsInfo.setText("Icons in views: the preference states to use properties.");
            String propValue = SpecializationPlugin.getPropertyValue(this.model, "must replace icons in views");
            if ( propValue != null )
                propValue = propValue.toLowerCase();
            yes = SpecializationPlugin.areEqual(propValue, "yes");
            no = SpecializationPlugin.areEqual(propValue, "no");

            this.btnIconsInViewsYes.setSelection(yes);
            this.btnIconsInViewsNo.setSelection(no);
            this.btnIconsInViewsDefault.setSelection(!yes && !no);
        } else { 
            this.lblIconInViewsInfo.setText("Icons in views: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews")+" replace icons.");
            yes = SpecializationPlugin.areEqual(SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInViews").toLowerCase(), "always");
            this.btnIconsInViewsYes.setSelection(yes);
            this.btnIconsInViewsNo.setSelection(!yes);
            this.btnIconsInViewsDefault.setSelection(false);
        }
        this.lblReplaceInViewsIcons.setEnabled(mustReplaceIconsInViews);
        this.btnIconsInViewsYes.setEnabled(mustReplaceIconsInViews);
        this.btnIconsInViewsNo.setEnabled(mustReplaceIconsInViews);
        this.btnIconsInViewsDefault.setEnabled(mustReplaceIconsInViews);
        
        if ( mustReplaceIconsInTree ) {
            this.lblIconInTreeInfo.setText("Icons in model tree: the preference states to use properties.");
            String propValue = SpecializationPlugin.getPropertyValue(this.model, "must replace icons in tree");
            if ( propValue != null )
                propValue = propValue.toLowerCase();
            yes = SpecializationPlugin.areEqual(propValue, "yes");
            no = SpecializationPlugin.areEqual(propValue, "no");

            this.btnIconsInTreeYes.setSelection(yes);
            this.btnIconsInTreeNo.setSelection(no);
            this.btnIconsInTreeDefault.setSelection(!yes && !no);
        } else { 
            this.lblIconInTreeInfo.setText("Icons in model tree: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree")+" replace icons.");
            yes = SpecializationPlugin.areEqual(SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceIconsInTree").toLowerCase(), "always");
            this.btnIconsInTreeYes.setSelection(yes);
            this.btnIconsInTreeNo.setSelection(!yes);
            this.btnIconsInTreeDefault.setSelection(false);
        }
        this.lblReplaceInTreeIcons.setEnabled(mustReplaceIconsInTree);
        this.btnIconsInTreeYes.setEnabled(mustReplaceIconsInTree);
        this.btnIconsInTreeNo.setEnabled(mustReplaceIconsInTree);
        this.btnIconsInTreeDefault.setEnabled(mustReplaceIconsInTree);

        if ( mustReplaceLabelsInViews ) {
            this.lblLabelInfo.setText("Labels in views: the preference states to use properties.");
            String propValue = SpecializationPlugin.getPropertyValue(this.model, "must replace labels");
            if ( propValue != null )
                propValue = propValue.toLowerCase();
            yes = SpecializationPlugin.areEqual(propValue, "yes");
            no = SpecializationPlugin.areEqual(propValue, "no");

            this.btnLabelsYes.setSelection(yes);
            this.btnLabelsNo.setSelection(no);
            this.btnLabelsDefault.setSelection(!yes && !no);
        } else { 
            this.lblLabelInfo.setText("Labels in views: the preference states to "+SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews")+" replace labels.");
            yes = SpecializationPlugin.areEqual(SpecializationPlugin.INSTANCE.getPreferenceStore().getString("mustReplaceLabelsInViews").toLowerCase(), "always");
            this.btnLabelsYes.setSelection(yes);
            this.btnLabelsNo.setSelection(!yes);
            this.btnLabelsDefault.setSelection(false);
        }
        this.lblReplaceLabels.setEnabled(mustReplaceLabelsInViews);
        this.btnLabelsYes.setEnabled(mustReplaceLabelsInViews);
        this.btnLabelsNo.setEnabled(mustReplaceLabelsInViews);
        this.btnLabelsDefault.setEnabled(mustReplaceLabelsInViews);
	}


	private SelectionListener replaceIconsInViewsListener = new SelectionAdapter () {
		@Override
        public void widgetSelected(SelectionEvent event) {
			if ( SpecializationModelSection.this.model == null ) 
				return;

			Button button = ((Button) event.widget);
			if ( !button.getSelection() )
				return;
			
			String value = null;
			if ( button.equals(SpecializationModelSection.this.btnIconsInViewsYes) )
				value = "yes";
			else if ( button.equals(SpecializationModelSection.this.btnIconsInViewsNo) )
				value = "no";
			
			SpecializationPropertyCommand command = new SpecializationPropertyCommand(SpecializationModelSection.this.model, "must replace icons in views", value);
			
            if ( command.canExecute() ) {
    			CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
            	compoundCommand.add(command);

    		    CommandStack stack = (CommandStack) SpecializationModelSection.this.model.getArchimateModel().getAdapter(CommandStack.class);
    		    stack.execute(compoundCommand);
    		    logger.trace("Setting property \"must replace icons in views\" to "+value);
    		    SpecializationPlugin.refreshIconsAndLabels(SpecializationModelSection.this.model);
            }
		}
	};
	
	   private SelectionListener replaceIconsInTreeListener = new SelectionAdapter () {
	        @Override
            public void widgetSelected(SelectionEvent event) {
	            if ( SpecializationModelSection.this.model == null ) 
	                return;

	            Button button = ((Button) event.widget);
	            if ( !button.getSelection() )
	                return;
	            
	            String value = null;
	            if ( button.equals(SpecializationModelSection.this.btnIconsInTreeYes) )
	                value = "yes";
	            else if ( button.equals(SpecializationModelSection.this.btnIconsInTreeNo) )
	                value = "no";
	            
	            SpecializationPropertyCommand command = new SpecializationPropertyCommand(SpecializationModelSection.this.model, "must replace icons in tree", value);
	            
	            if ( command.canExecute() ) {
	                CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
	                compoundCommand.add(command);

	                CommandStack stack = (CommandStack) SpecializationModelSection.this.model.getArchimateModel().getAdapter(CommandStack.class);
	                stack.execute(compoundCommand);
	                logger.trace("Setting property \"must replace icons in tree\" to "+value);
	                SpecializationPlugin.refreshIconsAndLabels(SpecializationModelSection.this.model);
	            }
	        }
	    };

	private SelectionListener replaceLabelsListener = new SelectionAdapter () {
		@Override
        public void widgetSelected(SelectionEvent event) {
			if ( SpecializationModelSection.this.model == null ) 
				return;

			Button button = ((Button) event.widget);
			if ( !button.getSelection() )
				return;

			String value = null;
			if ( button.equals(SpecializationModelSection.this.btnLabelsYes) )
				value = "yes";
			else if ( button.equals(SpecializationModelSection.this.btnLabelsNo) )
				value = "no";
			
			SpecializationPropertyCommand command = new SpecializationPropertyCommand(SpecializationModelSection.this.model, "must replace labels", value);
			
            if ( command.canExecute() ) {
    			CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
            	compoundCommand.add(command);

    		    CommandStack stack = (CommandStack) SpecializationModelSection.this.model.getAdapter(CommandStack.class);
    		    stack.execute(compoundCommand);
    		    logger.trace("Setting property \"must replace labels\" to "+value);
    		    SpecializationPlugin.refreshIconsAndLabels(SpecializationModelSection.this.model);
            }
		}
	};
}
