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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ArchiPlugin;
import com.archimatetool.editor.ui.IArchiImages;
import com.archimatetool.editor.ui.ImageFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.impl.Capability;
import com.archimatetool.model.impl.CourseOfAction;
import com.archimatetool.model.impl.Resource;

public class SpecializationModelSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);
	
	ComponentLabel resourceLabel;
    ComponentLabel capabilityLabel;
    ComponentLabel courseOfActionLabel;
    ComponentLabel applicationComponentLabel;
    ComponentLabel applicationCollaborationLabel;
    ComponentLabel applicationInterfaceLabel;
    ComponentLabel applicationFunctionLabel;
    ComponentLabel applicationInteractionLabel;
    ComponentLabel applicationEventLabel;
    ComponentLabel applicationServiceLabel;
    ComponentLabel dataObjectLabel;
    ComponentLabel applicationProcessLabel;
    ComponentLabel businessActorLabel;
    ComponentLabel businessRoleLabel;
    ComponentLabel businessCollaborationLabel;
    ComponentLabel businessInterfaceLabel;
    ComponentLabel businessProcessLabel;
    ComponentLabel businessFunctionLabel;
    ComponentLabel businessInteractionLabel;
    ComponentLabel businessEventLabel;
    ComponentLabel businessServiceLabel;
    ComponentLabel businessObjectLabel;
    ComponentLabel contractLabel;
    ComponentLabel representationLabel;
    ComponentLabel nodeLabel;
    ComponentLabel deviceLabel;
    ComponentLabel systemSoftwareLabel;
    ComponentLabel technologyCollaborationLabel;
    ComponentLabel technologyInterfaceLabel;
    ComponentLabel pathLabel;
    ComponentLabel communicationNetworkLabel;
    ComponentLabel technologyFunctionLabel;
    ComponentLabel technologyProcessLabel;
    ComponentLabel technologyInteractionLabel;
    ComponentLabel technologyEventLabel;
    ComponentLabel technologyServiceLabel;
    ComponentLabel artifactLabel;
    ComponentLabel equipmentLabel;
    ComponentLabel facilityLabel;
    ComponentLabel distributionNetworkLabel;
    ComponentLabel materialLabel;
    ComponentLabel workpackageLabel;
    ComponentLabel deliverableLabel;
    ComponentLabel implementationEventLabel;
    ComponentLabel plateauLabel;
    ComponentLabel gapLabel;
    ComponentLabel stakeholderLabel;
    ComponentLabel driverLabel;
    ComponentLabel assessmentLabel;
    ComponentLabel goalLabel;
    ComponentLabel outcomeLabel;
    ComponentLabel principleLabel;
    ComponentLabel requirementLabel;
    ComponentLabel constaintLabel;
    ComponentLabel smeaningLabel;
    ComponentLabel valueLabel;
    ComponentLabel productLabel;
    ComponentLabel groupingLabel;
    ComponentLabel locationLabel;
    ComponentLabel junctionLabel;
    
    Label lblStrategy;
    Label lblBusiness;
    Label lblApplication;
    Label lblTechnology;
    Label lblPhysical;
    Label lblImplementation;
    Label lblMotivation;
    
    Composite strategyCanvas;
    Composite businessCanvas;
    Composite applicationCanvas;
    Composite technologyCanvas;
    Composite physicalCanvas;
    Composite implementationCanvas;
    Composite motivationCanvas;
    Composite otherCanvas;

	IArchimateModel model;

	boolean mouseOverHelpButton = false;
	
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
		
		Label lblSpecializationName = new Label(parent, SWT.NONE);
		lblSpecializationName.setForeground(parent.getForeground());
		lblSpecializationName.setBackground(parent.getBackground());
		lblSpecializationName.setText("Specialization name :");
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 5);
        lblSpecializationName.setLayoutData(fd);
        
        Combo comboSpecializationNames = new Combo(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblSpecializationName, 10);
        fd.right = new FormAttachment(lblSpecializationName, 350);
        comboSpecializationNames.setLayoutData(fd);
        //TODO : add existing names
        
        Button btnNewSpecialization = new Button(parent, SWT.PUSH);
        btnNewSpecialization.setImage(SpecializationPlugin.NEW_ICON);
        btnNewSpecialization.setToolTipText("New");
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
        fd.left = new FormAttachment(comboSpecializationNames, 10);
        fd.right = new FormAttachment(comboSpecializationNames, 40, SWT.RIGHT);
        btnNewSpecialization.setLayoutData(fd);
        
        Button btnEditSpecialization = new Button(parent, SWT.PUSH);
        btnEditSpecialization.setImage(SpecializationPlugin.EDIT_ICON);
        btnEditSpecialization.setToolTipText("Edit");
        btnEditSpecialization.setEnabled(comboSpecializationNames.getItemCount()!=0);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnNewSpecialization, 10);
        fd.right = new FormAttachment(btnNewSpecialization, 40, SWT.RIGHT);
        btnEditSpecialization.setLayoutData(fd);
        
        Button btnDeleteSpecialization = new Button(parent, SWT.PUSH);
        btnDeleteSpecialization.setImage(SpecializationPlugin.DELETE_ICON);
        btnDeleteSpecialization.setToolTipText("Delete");
        btnDeleteSpecialization.setEnabled(comboSpecializationNames.getItemCount()!=0);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnEditSpecialization, 10);
        fd.right = new FormAttachment(btnEditSpecialization, 40, SWT.RIGHT);
        btnDeleteSpecialization.setLayoutData(fd);
        
        Label lblIcon = new Label(parent, SWT.NONE);
        lblIcon.setForeground(parent.getForeground());
        lblIcon.setBackground(parent.getBackground());
        lblIcon.setText("Icon:");
        lblIcon.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 15);
        fd.left = new FormAttachment(0, 30);
        lblIcon.setLayoutData(fd);
        
        Button btnIcon = new Button(parent, SWT.PUSH);
        btnIcon.setText("...");
        btnIcon.setToolTipText("Assign icon");
        btnIcon.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(lblIcon, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIcon, 5);
        fd.right = new FormAttachment(lblIcon, 40, SWT.RIGHT);
        btnIcon.setLayoutData(fd);
        
        Label lblIconSize = new Label(parent, SWT.NONE);
        lblIconSize.setForeground(parent.getForeground());
        lblIconSize.setBackground(parent.getBackground());
        lblIconSize.setEnabled(false);
        lblIconSize.setText("Size:");
        fd = new FormData();
        fd.top = new FormAttachment(lblIcon, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnIcon, 20);
        lblIconSize.setLayoutData(fd);
        
        Text txtIconSize = new Text(parent, SWT.BORDER);
        txtIconSize.setToolTipText("Size of the icon");
        txtIconSize.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconSize, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIconSize, 5);
        fd.right = new FormAttachment(lblIconSize, 90, SWT.RIGHT);
        txtIconSize.setLayoutData(fd);
        
        Label lblIconLocation = new Label(parent, SWT.NONE);
        lblIconLocation.setForeground(parent.getForeground());
        lblIconLocation.setBackground(parent.getBackground());
        lblIconLocation.setEnabled(false);
        lblIconLocation.setText("Location:");
        fd = new FormData();
        fd.top = new FormAttachment(lblIcon, 0, SWT.CENTER);
        fd.left = new FormAttachment(txtIconSize, 20);
        lblIconLocation.setLayoutData(fd);
        
        Text txtIconLocation = new Text(parent, SWT.BORDER);
        txtIconLocation.setToolTipText("Location of the icon");
        txtIconLocation.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconLocation, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIconLocation, 5);
        fd.right = new FormAttachment(lblIconLocation, 100, SWT.RIGHT);
        txtIconLocation.setLayoutData(fd);
        
        Label lblProperties = new Label(parent, SWT.NONE);
        lblProperties.setForeground(parent.getForeground());
        lblProperties.setBackground(parent.getBackground());
        lblProperties.setEnabled(false);
        lblProperties.setText("Properties:");
        fd = new FormData();
        fd.top = new FormAttachment(lblIcon, 15);
        fd.left = new FormAttachment(lblIcon, 0, SWT.LEFT);
        lblProperties.setLayoutData(fd);
        
        Table tblProperties = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        tblProperties.setHeaderVisible(true);
        tblProperties.setLinesVisible(true);
        tblProperties.setEnabled(false);
        TableColumn tblColumn = new TableColumn(tblProperties, SWT.NULL);
        tblColumn.setText("Name");
        tblColumn.setWidth(180);
        tblColumn = new TableColumn(tblProperties, SWT.NULL);
        tblColumn.setText("Default value");
        tblColumn.setWidth(120);
        fd = new FormData();
        fd.top = new FormAttachment(lblProperties, -5, SWT.TOP);
        fd.left = new FormAttachment(lblProperties, 10);
        fd.right = new FormAttachment(lblProperties, 370);
        fd.bottom = new FormAttachment(lblProperties, 100, SWT.BOTTOM);
        tblProperties.setLayoutData(fd);
        
        Label lblSeparator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        fd = new FormData();
        fd.top = new FormAttachment(tblProperties, 10);
        fd.left = new FormAttachment(0, 5);
        fd.right = new FormAttachment(100, -5);
        lblSeparator.setLayoutData(fd);
        
		/* **************************************************** */
        Label lblChooseClass = new Label(parent, SWT.NONE);
        lblChooseClass.setForeground(parent.getForeground());
        lblChooseClass.setBackground(parent.getBackground());
        lblChooseClass.setText("Please choose the class of elements to specialize:");
        fd = new FormData();
        fd.top = new FormAttachment(lblSeparator, 5);
        fd.left = new FormAttachment(0, 5);
        lblChooseClass.setLayoutData(fd);
        
        Composite compoElements = new Composite(parent, SWT.NONE);
        compoElements.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblChooseClass, 5);
        fd.left = new FormAttachment(0, 30);
        fd.right = new FormAttachment(0, 500);
        fd.bottom = new FormAttachment(lblChooseClass, 200, SWT.BOTTOM);
        compoElements.setLayoutData(fd);
        compoElements.setLayout(new FormLayout());
        
        Composite strategyActiveCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite strategyBehaviorCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite strategyPassiveCompo = new Composite(compoElements, SWT.TRANSPARENT );

        Composite businessActiveCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite businessBehaviorCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite businessPassiveCompo = new Composite(compoElements, SWT.TRANSPARENT );

        Composite applicationActiveCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite applicationBehaviorCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite applicationPassiveCompo = new Composite(compoElements, SWT.TRANSPARENT);

        Composite technologyActiveCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite technologyBehaviorCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite technologyPassiveCompo = new Composite(compoElements, SWT.TRANSPARENT);

        Composite physicalActiveCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite physicalBehaviorCompo = new Composite(compoElements, SWT.TRANSPARENT);
        Composite physicalPassive = new Composite(compoElements, SWT.TRANSPARENT);

        Composite implementationCompo = new Composite(compoElements, SWT.TRANSPARENT);


        Composite motivationCompo = new Composite(compoElements, SWT.TRANSPARENT);

        Composite otherCompo = new Composite(compoElements, SWT.TRANSPARENT);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Strategy layer
        // Passive
        // Behavior
        this.capabilityLabel = new ComponentLabel(strategyBehaviorCompo, Capability.class);
        this.courseOfActionLabel = new ComponentLabel(strategyBehaviorCompo,  CourseOfAction.class);
        // Active
        this.resourceLabel = new ComponentLabel(strategyActiveCompo, Resource.class);

        // Business layer
        // Passive
        this.productLabel = new ComponentLabel(businessPassiveCompo, "Product");
        // Behavior
        this.businessProcessLabel = new ComponentLabel(businessBehaviorCompo, "Business Process");
        this.businessFunctionLabel = new ComponentLabel(businessBehaviorCompo, "Business Function");
        this.businessInteractionLabel = new ComponentLabel(businessBehaviorCompo, "Business Interaction");
        this.businessEventLabel = new ComponentLabel(businessBehaviorCompo, "Business Event");
        this.businessServiceLabel = new ComponentLabel(businessBehaviorCompo, "Business Service");
        this.businessObjectLabel = new ComponentLabel(businessBehaviorCompo, "Business Object");
        this.contractLabel = new ComponentLabel(businessBehaviorCompo, "Contract");
        this.representationLabel = new ComponentLabel(businessBehaviorCompo, "Representation");
        // Active
        this.businessActorLabel = new ComponentLabel(businessActiveCompo, "Business Actor");
        this.businessRoleLabel = new ComponentLabel(businessActiveCompo, "Business Role");
        this.businessCollaborationLabel = new ComponentLabel(businessActiveCompo, "Business Collaboration");
        this.businessInterfaceLabel = new ComponentLabel(businessActiveCompo, "Business Interface");

        // Application layer
        //Passive
        this.dataObjectLabel = new ComponentLabel(applicationPassiveCompo, "Data Object");
        //Behavior
        this.applicationFunctionLabel = new ComponentLabel(applicationBehaviorCompo, "Application Function");
        this.applicationInteractionLabel = new ComponentLabel(applicationBehaviorCompo, "Application Interaction");
        this.applicationEventLabel = new ComponentLabel(applicationBehaviorCompo, "Application Event");
        this.applicationServiceLabel = new ComponentLabel(applicationBehaviorCompo, "Application Service");
        this.applicationProcessLabel = new ComponentLabel(applicationBehaviorCompo, "Application Process");
        //  Active      
        this.applicationComponentLabel = new ComponentLabel(applicationActiveCompo, "Application Component");
        this.applicationCollaborationLabel = new ComponentLabel(applicationActiveCompo, "Application Collaboration");
        this.applicationInterfaceLabel = new ComponentLabel(applicationActiveCompo, "Application Interface");

        // Technology layer
        // Passive
        this.artifactLabel = new ComponentLabel(technologyPassiveCompo, "Artifact");
        // Behavior
        this.technologyFunctionLabel = new ComponentLabel(technologyBehaviorCompo, "Technology Function");
        this.technologyProcessLabel = new ComponentLabel(technologyBehaviorCompo, "Technology Process");
        this.technologyInteractionLabel = new ComponentLabel(technologyBehaviorCompo, "Technology Interaction");
        this.technologyEventLabel = new ComponentLabel(technologyBehaviorCompo, "Technology Event");
        this.technologyServiceLabel = new ComponentLabel(technologyBehaviorCompo, "Technology Service");
        // Active
        this.nodeLabel = new ComponentLabel(technologyActiveCompo, "Node");
        this.deviceLabel = new ComponentLabel(technologyActiveCompo, "Device");
        this.systemSoftwareLabel = new ComponentLabel(technologyActiveCompo, "System Software");
        this.technologyCollaborationLabel = new ComponentLabel(technologyActiveCompo, "Technology Collaboration");
        this.technologyInterfaceLabel = new ComponentLabel(technologyActiveCompo, "Technology Interface");
        this.pathLabel = new ComponentLabel(technologyActiveCompo, "Path");
        this.communicationNetworkLabel = new ComponentLabel(technologyActiveCompo, "Communication Network");

        // Physical layer
        // Passive
        // Behavior
        this.materialLabel = new ComponentLabel(physicalBehaviorCompo, "Material");
        // Active
        this.equipmentLabel = new ComponentLabel(physicalActiveCompo, "Equipment");
        this.facilityLabel = new ComponentLabel(physicalActiveCompo, "Facility");
        this.distributionNetworkLabel = new ComponentLabel(physicalActiveCompo, "Distribution Network");

        // Implementation layer
        this.workpackageLabel = new ComponentLabel(implementationCompo, "Work Package");
        this.deliverableLabel = new ComponentLabel(implementationCompo, "Deliverable");
        this.implementationEventLabel = new ComponentLabel(implementationCompo, "Implementation Event");
        this.plateauLabel = new ComponentLabel(implementationCompo, "Plateau");
        this.gapLabel = new ComponentLabel(implementationCompo, "Gap");

        // Motivation layer
        this.stakeholderLabel = new ComponentLabel(motivationCompo, "Stakeholder");
        this.driverLabel = new ComponentLabel(motivationCompo, "Driver");
        this.assessmentLabel = new ComponentLabel(motivationCompo, "Assessment");
        this.goalLabel = new ComponentLabel(motivationCompo, "Goal");
        this.outcomeLabel = new ComponentLabel(motivationCompo, "Outcome");
        this.principleLabel = new ComponentLabel(motivationCompo, "Principle");
        this.requirementLabel = new ComponentLabel(motivationCompo, "Requirement");
        this.constaintLabel = new ComponentLabel(motivationCompo, "Constraint");
        this.smeaningLabel = new ComponentLabel(motivationCompo, "Meaning");
        this.valueLabel = new ComponentLabel(motivationCompo, "Value");

        // Containers !!!
        //
        this.groupingLabel = new ComponentLabel(otherCompo, "Grouping");
        this.locationLabel = new ComponentLabel(otherCompo, "Location");
        this.junctionLabel = new ComponentLabel(otherCompo, "Junction");
        
        Label passiveLabel = new Label(compoElements, SWT.TRANSPARENT | SWT.CENTER);
        Composite passiveCanvas = new Composite(compoElements, SWT.TRANSPARENT | SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblChooseClass, 2);
        fd.bottom = new FormAttachment(implementationCompo, -2, SWT.TOP);
        fd.left = new FormAttachment(0, 70);
        fd.right = new FormAttachment(0, 110);
        passiveCanvas.setLayoutData(fd);
        passiveCanvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent event) {
                event.gc.setAlpha(100);
                event.gc.setBackground(SpecializationPlugin.PASSIVE_COLOR);
                event.gc.fillRectangle(event.x, event.y, event.width, event.height);
            }
        });
        fd = new FormData();
        fd.top = new FormAttachment(passiveCanvas, 1, SWT.TOP);
        fd.left = new FormAttachment(0, 71);
        fd.right = new FormAttachment(0, 109);
        passiveLabel.setLayoutData(fd);
        passiveLabel.setText("Passive");
        passiveLabel.setBackground(SpecializationPlugin.PASSIVE_COLOR);

        Label behaviorLabel = new Label(compoElements, SWT.CENTER);
        Composite behaviorCanvas = new Composite(compoElements, SWT.TRANSPARENT | SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblChooseClass, 2);
        fd.bottom = new FormAttachment(implementationCompo, -2, SWT.TOP);
        fd.left = new FormAttachment(0, 115);
        fd.right = new FormAttachment(55);
        behaviorCanvas.setLayoutData(fd);
        behaviorCanvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent event) {
                event.gc.setAlpha(100);
                event.gc.setBackground(SpecializationPlugin.PASSIVE_COLOR);
                event.gc.fillRectangle(event.x, event.y, event.width, event.height);
            }
        });
        fd = new FormData();
        fd.top = new FormAttachment(behaviorCanvas, 1, SWT.TOP);
        fd.left = new FormAttachment(0, 116);
        fd.right = new FormAttachment(55, -1);
        behaviorLabel.setLayoutData(fd);
        behaviorLabel.setText("Behavior");
        behaviorLabel.setBackground(SpecializationPlugin.PASSIVE_COLOR);

        Label activeLabel = new Label(compoElements, SWT.CENTER);
        Composite activeCanvas = new Composite(compoElements, SWT.TRANSPARENT | SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblChooseClass, 2);
        fd.bottom = new FormAttachment(implementationCompo, -2, SWT.TOP);
        fd.left = new FormAttachment(55, 5);
        fd.right = new FormAttachment(100, -65);
        activeCanvas.setLayoutData(fd);
        activeCanvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent event) {
                event.gc.setAlpha(100);
                event.gc.setBackground(SpecializationPlugin.PASSIVE_COLOR);
                event.gc.fillRectangle(event.x, event.y, event.width, event.height);
            }
        });
        fd = new FormData();
        fd.top = new FormAttachment(activeCanvas, 1, SWT.TOP);
        fd.left = new FormAttachment(55, 6);
        fd.right = new FormAttachment(100, -66);
        activeLabel.setLayoutData(fd);
        activeLabel.setText("Active");
        activeLabel.setBackground(SpecializationPlugin.PASSIVE_COLOR);

        this.lblMotivation = new Label(compoElements, SWT.CENTER);
        this.motivationCanvas = new Composite(compoElements, SWT.TRANSPARENT | SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblChooseClass, 2);
        fd.bottom = new FormAttachment(implementationCompo, -2, SWT.TOP);
        fd.left = new FormAttachment(100, -60);
        fd.right = new FormAttachment(100);
        this.motivationCanvas.setLayoutData(fd);
        this.motivationCanvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent event) {
                event.gc.setAlpha(100);
                event.gc.setBackground(SpecializationPlugin.MOTIVATION_COLOR);
                event.gc.fillRectangle(event.x, event.y, event.width, event.height);
            }
        });
        fd = new FormData();
        fd.top = new FormAttachment(this.motivationCanvas, 1, SWT.TOP);
        fd.left = new FormAttachment(100, -59);
        fd.right = new FormAttachment(100, -1);
        this.lblMotivation.setLayoutData(fd);
        this.lblMotivation.setText("Motivation");
        this.lblMotivation.setBackground(SpecializationPlugin.MOTIVATION_COLOR);
		
        this.lblStrategy = new Label(compoElements, SWT.NONE);
        this.strategyCanvas = new Composite(compoElements, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(10, 2);
        fd.bottom = new FormAttachment(24, -2);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100, -60);
        this.strategyCanvas.setLayoutData(fd);
        this.strategyCanvas.setBackground(SpecializationPlugin.STRATEGY_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(this.strategyCanvas, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.strategyCanvas, 2, SWT.LEFT);
        this.lblStrategy.setLayoutData(fd);
        this.lblStrategy.setBackground(SpecializationPlugin.STRATEGY_COLOR);
        this.lblStrategy.setText("Strategy");

        this.lblBusiness = new Label(compoElements, SWT.NONE);
        this.businessCanvas = new Composite(compoElements, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(25, 2);
        fd.bottom = new FormAttachment(39, -2);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100, -60);
        this.businessCanvas.setLayoutData(fd);
        this.businessCanvas.setBackground(SpecializationPlugin.BUSINESS_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(this.businessCanvas, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.businessCanvas, 2, SWT.LEFT);
        this.lblBusiness.setLayoutData(fd);
        this.lblBusiness.setBackground(SpecializationPlugin.BUSINESS_COLOR);
        this.lblBusiness.setText("Business");

        this.lblApplication = new Label(compoElements, SWT.NONE);
        this.applicationCanvas = new Composite(compoElements, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(40, 2);
        fd.bottom = new FormAttachment(54, -2);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100, -60);
        this.applicationCanvas.setLayoutData(fd);
        this.applicationCanvas.setBackground(SpecializationPlugin.APPLICATION_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(this.applicationCanvas, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.applicationCanvas, 2, SWT.LEFT);
        this.lblApplication.setLayoutData(fd);
        this.lblApplication.setBackground(SpecializationPlugin.APPLICATION_COLOR);
        this.lblApplication.setText("Application");

        this.lblTechnology = new Label(compoElements, SWT.NONE);
        this.technologyCanvas = new Composite(compoElements, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(55, 2);
        fd.bottom = new FormAttachment(69, -2);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100, -60);
        this.technologyCanvas.setLayoutData(fd);
        this.technologyCanvas.setBackground(SpecializationPlugin.TECHNOLOGY_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(this.technologyCanvas, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.technologyCanvas, 2, SWT.LEFT);
        this.lblTechnology.setLayoutData(fd);
        this.lblTechnology.setBackground(SpecializationPlugin.TECHNOLOGY_COLOR);
        this.lblTechnology.setText("Technology");

        this.lblPhysical = new Label(compoElements, SWT.NONE);
        this.physicalCanvas = new Composite(compoElements, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(70, 2);
        fd.bottom = new FormAttachment(84, -2);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100, -60);
        this.physicalCanvas.setLayoutData(fd);
        this.physicalCanvas.setBackground(SpecializationPlugin.PHYSICAL_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(this.physicalCanvas, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.physicalCanvas, 2, SWT.LEFT);
        this.lblPhysical.setLayoutData(fd);
        this.lblPhysical.setBackground(SpecializationPlugin.PHYSICAL_COLOR);
        this.lblPhysical.setText("Physical");

        this.lblImplementation = new Label(compoElements, SWT.NONE);
        this.implementationCanvas = new Composite(compoElements, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(85, 2);
        fd.bottom = new FormAttachment(99);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100, -65);
        this.implementationCanvas.setLayoutData(fd);
        this.implementationCanvas.setBackground(SpecializationPlugin.IMPLEMENTATION_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(this.implementationCanvas, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.implementationCanvas, 2, SWT.LEFT);
        this.lblImplementation.setLayoutData(fd);
        this.lblImplementation.setBackground(SpecializationPlugin.IMPLEMENTATION_COLOR);
        this.lblImplementation.setText("Implementation");

        this.otherCanvas = new Composite(compoElements, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(85, 2);
        fd.bottom = new FormAttachment(99);
        fd.left = new FormAttachment(100, -60);
        fd.right = new FormAttachment(100);
        this.otherCanvas.setLayoutData(fd);
        this.otherCanvas.setBackground(SpecializationPlugin.OTHER_COLOR);

        // strategy + active
        fd = new FormData();
        fd.top = new FormAttachment(this.strategyCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.strategyCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(activeCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(activeCanvas, 0, SWT.RIGHT);
        strategyActiveCompo.setLayoutData(fd);
        RowLayout rd = new RowLayout(SWT.HORIZONTAL);
        rd.center = true;
        rd.fill = true;
        rd.justify = true;
        rd.wrap = true;
        rd.marginBottom = 5;
        rd.marginTop = 5;
        rd.marginLeft = 5;
        rd.marginRight = 5;
        rd.spacing = 0;
        strategyActiveCompo.setLayout(rd);

        // strategy + behavior
        fd = new FormData();
        fd.top = new FormAttachment(this.strategyCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.strategyCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(behaviorCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(behaviorCanvas, 0, SWT.RIGHT);
        strategyBehaviorCompo.setLayoutData(fd);
        strategyBehaviorCompo.setLayout(rd);

        // strategy + passive
        fd = new FormData();
        fd.top = new FormAttachment(this.strategyCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.strategyCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(passiveCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(passiveCanvas, 0, SWT.RIGHT);
        strategyPassiveCompo.setLayoutData(fd);
        strategyPassiveCompo.setLayout(rd); 

        // business + active
        fd = new FormData();
        fd.top = new FormAttachment(this.businessCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.businessCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(activeCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(activeCanvas, 0, SWT.RIGHT);
        businessActiveCompo.setLayoutData(fd);
        businessActiveCompo.setLayout(rd);

        // business + behavior
        fd = new FormData();
        fd.top = new FormAttachment(this.businessCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.businessCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(behaviorCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(behaviorCanvas, 0, SWT.RIGHT);
        businessBehaviorCompo.setLayoutData(fd);
        businessBehaviorCompo.setLayout(rd);

        // Business + passive
        fd = new FormData();
        fd.top = new FormAttachment(this.businessCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.businessCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(passiveCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(passiveCanvas, 0, SWT.RIGHT);
        businessPassiveCompo.setLayoutData(fd);
        businessPassiveCompo.setLayout(rd);



        // application + active
        fd = new FormData();
        fd.top = new FormAttachment(this.applicationCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.applicationCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(activeCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(activeCanvas, 0, SWT.RIGHT);
        applicationActiveCompo.setLayoutData(fd);
        applicationActiveCompo.setLayout(rd);


        // application + behavior
        fd = new FormData();
        fd.top = new FormAttachment(this.applicationCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.applicationCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(behaviorCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(behaviorCanvas, 0, SWT.RIGHT);
        applicationBehaviorCompo.setLayoutData(fd);
        applicationBehaviorCompo.setLayout(rd);

        // application + passive
        fd = new FormData();
        fd.top = new FormAttachment(this.applicationCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.applicationCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(passiveCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(passiveCanvas, 0, SWT.RIGHT);
        applicationPassiveCompo.setLayoutData(fd);
        applicationPassiveCompo.setLayout(rd);


        // technology + active
        fd = new FormData();
        fd.top = new FormAttachment(this.technologyCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.technologyCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(activeCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(activeCanvas, 0, SWT.RIGHT);
        technologyActiveCompo.setLayoutData(fd);
        technologyActiveCompo.setLayout(rd);

        // technology + behavior
        fd = new FormData();
        fd.top = new FormAttachment(this.technologyCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.technologyCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(behaviorCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(behaviorCanvas, 0, SWT.RIGHT);
        technologyBehaviorCompo.setLayoutData(fd);
        technologyBehaviorCompo.setLayout(rd);

        // technology + passive
        fd = new FormData();
        fd.top = new FormAttachment(this.technologyCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.technologyCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(passiveCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(passiveCanvas, 0, SWT.RIGHT);
        technologyPassiveCompo.setLayoutData(fd);
        technologyPassiveCompo.setLayout(rd);

        // physical + active
        fd = new FormData();
        fd.top = new FormAttachment(this.physicalCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.physicalCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(activeCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(activeCanvas, 0, SWT.RIGHT);
        physicalActiveCompo.setLayoutData(fd);
        physicalActiveCompo.setLayout(rd);

        // physical + behavior
        fd = new FormData();
        fd.top = new FormAttachment(this.physicalCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.physicalCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(behaviorCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(behaviorCanvas, 0, SWT.RIGHT);
        physicalBehaviorCompo.setLayoutData(fd);
        physicalBehaviorCompo.setLayout(rd);

        // physical + passive
        fd = new FormData();
        fd.top = new FormAttachment(this.physicalCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.physicalCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(passiveCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(passiveCanvas, 0, SWT.RIGHT);
        physicalPassive.setLayoutData(fd);
        physicalPassive.setLayout(rd);

        // implementation
        fd = new FormData();
        fd.top = new FormAttachment(this.implementationCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.implementationCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(passiveCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(activeCanvas, 0, SWT.RIGHT);
        implementationCompo.setLayoutData(fd);
        rd = new RowLayout(SWT.HORIZONTAL);
        rd.center = true;
        rd.fill = true;
        rd.justify = true;
        rd.wrap = true;
        rd.marginBottom = 5;
        rd.marginTop = 7;
        rd.marginLeft = 5;
        rd.marginRight = 5;
        rd.spacing = 0;
        implementationCompo.setLayout(rd);

        // motivation
        fd = new FormData();
        fd.top = new FormAttachment(this.motivationCanvas, 20, SWT.TOP);
        fd.bottom = new FormAttachment(this.motivationCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(this.motivationCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(this.motivationCanvas, 0, SWT.RIGHT);
        motivationCompo.setLayoutData(fd);
        rd = new RowLayout(SWT.VERTICAL);
        rd.center = true;
        rd.fill = true;
        rd.justify = true;
        rd.wrap = true;
        rd.marginBottom = 5;
        rd.marginTop = 5;
        rd.marginLeft = 20;
        rd.marginRight = 5;
        rd.spacing = 0;
        motivationCompo.setLayout(rd);

        // other
        fd = new FormData();
        fd.top = new FormAttachment(this.otherCanvas, 0, SWT.TOP);
        fd.bottom = new FormAttachment(this.otherCanvas, 0, SWT.BOTTOM);
        fd.left = new FormAttachment(this.otherCanvas, 0, SWT.LEFT);
        fd.right = new FormAttachment(this.otherCanvas, 0, SWT.RIGHT);
        otherCompo.setLayoutData(fd);
        rd = new RowLayout(SWT.HORIZONTAL);
        rd.center = true;
        rd.fill = true;
        rd.justify = true;
        rd.wrap = true;
        rd.marginBottom = 5;
        rd.marginTop = 5;
        rd.marginLeft = 5;
        rd.marginRight = 5;
        rd.spacing = 0;
        otherCompo.setLayout(rd);
		
		compoElements.layout();
		
		
		
		
		
		lblSeparator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        fd = new FormData();
        fd.top = new FormAttachment(compoElements, 25);
        fd.left = new FormAttachment(0, 5);
        fd.right = new FormAttachment(100, -5);
        lblSeparator.setLayoutData(fd);
		
		Label btnHelp = new Label(parent, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblSeparator, 10);
        fd.bottom = new FormAttachment(lblSeparator, 28+10, SWT.BOTTOM);
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
                 e.gc.drawImage(SpecializationPlugin.HELP_ICON, 2, 2);
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
        // TOTO
	}


	private class ComponentLabel {
        Label label;

        ComponentLabel(Composite parent, @SuppressWarnings("rawtypes") Class clazz) {
            this.label = new Label(parent, SWT.NONE);
            this.label.setSize(100,  100);
            this.label.setToolTipText(clazz.getName().replaceAll(" ", ""));
            this.label.setImage(getImage(getElementClassname()));
            this.label.addPaintListener(this.redraw);
            this.label.addListener(SWT.MouseUp, new Listener() {
                @Override public void handleEvent(Event event) {
                    setSelected(!isSelected());
                    redraw();
                }
            });
            setSelected(false);
        }

        private PaintListener redraw = new PaintListener() {
            @Override
            public void paintControl(PaintEvent event)
            {
                if ( ComponentLabel.this.isSelected() )
                    ComponentLabel.this.label.setBackground(SpecializationPlugin.GREY_COLOR);
                //event.gc.drawRoundRectangle(0, 0, 16, 16, 2, 2);
                else
                    ComponentLabel.this.label.setBackground(getColor(getElementClassname()));
            }
        };

        public String getElementClassname() {
            return this.label.getToolTipText().replaceAll(" ",  "");
        }

        public void setSelected(boolean selected) {
            this.label.setData("isSelected", selected);
        }

        public boolean isSelected() {
            Boolean selected = (Boolean)this.label.getData("isSelected");
            if ( selected == null )
                return false;
            return selected.booleanValue();
        }

        public void redraw() {
            this.label.redraw();
        }
        
        public Image getImage(String clazz) {
            ImageFactory ImageFactory = new ImageFactory(ArchiPlugin.INSTANCE);
            switch (clazz.toUpperCase()) {
                case "FOLDER": return ImageFactory.getImage(IArchiImages.ECLIPSE_IMAGE_FOLDER);
                case "JUNCTION": return ImageFactory.getImage(IArchiImages.ICON_AND_JUNCTION);
                case "APPLICATIONCOLLABORATION": return ImageFactory.getImage(IArchiImages.ICON_APPLICATION_COLLABORATION);
                case "APPLICATIONCOMPONENT": return ImageFactory.getImage(IArchiImages.ICON_APPLICATION_COMPONENT);
                case "APPLICATIONEVENT": return ImageFactory.getImage(IArchiImages.ICON_APPLICATION_EVENT);
                case "APPLICATIONFUNCTION": return ImageFactory.getImage(IArchiImages.ICON_APPLICATION_FUNCTION);
                case "APPLICATIONINTERACTION": return ImageFactory.getImage(IArchiImages.ICON_APPLICATION_INTERACTION);
                case "APPLICATIONINTERFACE": return ImageFactory.getImage(IArchiImages.ICON_APPLICATION_INTERFACE);
                case "APPLICATIONPROCESS": return ImageFactory.getImage(IArchiImages.ICON_APPLICATION_PROCESS);
                case "APPLICATIONSERVICE": return ImageFactory.getImage(IArchiImages.ICON_APPLICATION_SERVICE);
                case "ARTIFACT": return ImageFactory.getImage(IArchiImages.ICON_ARTIFACT);
                case "ASSESSMENT": return ImageFactory.getImage(IArchiImages.ICON_ASSESSMENT);
                case "BUSINESSACTOR": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_ACTOR);
                case "BUSINESSCOLLABORATION": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_COLLABORATION);
                case "BUSINESSEVENT": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_EVENT);
                case "BUSINESSFUNCTION": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_FUNCTION);
                case "BUSINESSINTERACTION": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_INTERACTION);
                case "BUSINESSINTERFACE": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_INTERFACE);
                case "BUSINESSOBJECT": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_OBJECT);
                case "BUSINESSPROCESS": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_PROCESS);
                case "BUSINESSROLE": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_ROLE);
                case "BUSINESSSERVICE": return ImageFactory.getImage(IArchiImages.ICON_BUSINESS_SERVICE);
                case "CAPABILITY": return ImageFactory.getImage(IArchiImages.ICON_CAPABILITY);
                case "COMMUNICATIONNETWORK": return ImageFactory.getImage(IArchiImages.ICON_COMMUNICATION_NETWORK);
                case "CONTRACT": return ImageFactory.getImage(IArchiImages.ICON_CONTRACT);
                case "CONSTRAINT": return ImageFactory.getImage(IArchiImages.ICON_CONSTRAINT);
                case "COURSEOFACTION": return ImageFactory.getImage(IArchiImages.ICON_COURSE_OF_ACTION);
                case "DATAOBJECT": return ImageFactory.getImage(IArchiImages.ICON_DATA_OBJECT);
                case "DELIVERABLE": return ImageFactory.getImage(IArchiImages.ICON_DELIVERABLE);
                case "DEVICE": return ImageFactory.getImage(IArchiImages.ICON_DEVICE);
                case "DISTRIBUTIONNETWORK": return ImageFactory.getImage(IArchiImages.ICON_DISTRIBUTION_NETWORK);
                case "DRIVER": return ImageFactory.getImage(IArchiImages.ICON_DRIVER);
                case "EQUIPMENT": return ImageFactory.getImage(IArchiImages.ICON_EQUIPMENT);
                case "FACILITY": return ImageFactory.getImage(IArchiImages.ICON_FACILITY);
                case "GAP": return ImageFactory.getImage(IArchiImages.ICON_GAP);
                case "GOAL": return ImageFactory.getImage(IArchiImages.ICON_GOAL);
                case "GROUPING": return ImageFactory.getImage(IArchiImages.ICON_GROUPING);
                case "IMPLEMENTATIONEVENT": return ImageFactory.getImage(IArchiImages.ICON_IMPLEMENTATION_EVENT);
                case "LOCATION": return ImageFactory.getImage(IArchiImages.ICON_LOCATION);
                case "MATERIAL": return ImageFactory.getImage(IArchiImages.ICON_MATERIAL);
                case "MEANING": return ImageFactory.getImage(IArchiImages.ICON_MEANING);
                case "NODE": return ImageFactory.getImage(IArchiImages.ICON_NODE);
                case "OUTCOME": return ImageFactory.getImage(IArchiImages.ICON_OUTCOME);
                case "PATH": return ImageFactory.getImage(IArchiImages.ICON_PATH);
                case "PLATEAU": return ImageFactory.getImage(IArchiImages.ICON_PLATEAU);
                case "PRINCIPLE": return ImageFactory.getImage(IArchiImages.ICON_PRINCIPLE);
                case "PRODUCT": return ImageFactory.getImage(IArchiImages.ICON_PRODUCT);
                case "REPRESENTATION": return ImageFactory.getImage(IArchiImages.ICON_REPRESENTATION);
                case "RESOURCE": return ImageFactory.getImage(IArchiImages.ICON_RESOURCE);
                case "REQUIREMENT": return ImageFactory.getImage(IArchiImages.ICON_REQUIREMENT);
                case "STAKEHOLDER": return ImageFactory.getImage(IArchiImages.ICON_STAKEHOLDER);
                case "SYSTEMSOFTWARE": return ImageFactory.getImage(IArchiImages.ICON_SYSTEM_SOFTWARE);
                case "TECHNOLOGYCOLLABORATION": return ImageFactory.getImage(IArchiImages.ICON_TECHNOLOGY_COLLABORATION);
                case "TECHNOLOGYEVENT": return ImageFactory.getImage(IArchiImages.ICON_TECHNOLOGY_EVENT);
                case "TECHNOLOGYFUNCTION": return ImageFactory.getImage(IArchiImages.ICON_TECHNOLOGY_FUNCTION);
                case "TECHNOLOGYINTERFACE": return ImageFactory.getImage(IArchiImages.ICON_TECHNOLOGY_INTERFACE);
                case "TECHNOLOGYINTERACTION": return ImageFactory.getImage(IArchiImages.ICON_TECHNOLOGY_INTERACTION);
                case "TECHNOLOGYPROCESS": return ImageFactory.getImage(IArchiImages.ICON_TECHNOLOGY_PROCESS);
                case "TECHNOLOGYSERVICE": return ImageFactory.getImage(IArchiImages.ICON_TECHNOLOGY_SERVICE);
                case "VALUE": return ImageFactory.getImage(IArchiImages.ICON_VALUE);
                case "WORKPACKAGE": return ImageFactory.getImage(IArchiImages.ICON_WORKPACKAGE);
                case "ACCESSRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_ACESS_RELATION);
                case "AGGREGATIONRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_AGGREGATION_RELATION);
                case "ASSIGNMENTRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_ASSIGNMENT_RELATION);
                case "ASSOCIATIONRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_ASSOCIATION_RELATION);
                case "COMPOSITIONRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_COMPOSITION_RELATION);
                case "FLOWRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_FLOW_RELATION);
                case "INFLUENCERELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_INFLUENCE_RELATION);
                case "REALIZATIONRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_REALIZATION_RELATION);
                case "SERVINGRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_SERVING_RELATION);
                case "SPECIALIZATIONRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_SPECIALIZATION_RELATION);
                case "TRIGGERINGRELATIONSHIP": return ImageFactory.getImage(IArchiImages.ICON_TRIGGERING_RELATION);
                case "DIAGRAMMODELGROUP": return ImageFactory.getImage(IArchiImages.ICON_GROUP);
                case "DIAGRAMMODELNOTE": return ImageFactory.getImage(IArchiImages.ICON_NOTE);
                case "ARCHIMATEDIAGRAMMODEL": return ImageFactory.getImage(IArchiImages.ICON_DIAGRAM);
                case "SKETCHMODEL": return ImageFactory.getImage(IArchiImages.ICON_SKETCH);
                case "SKETCHMODELSTICKY": return ImageFactory.getImage(IArchiImages.ICON_STICKY);
                case "SKETCHMODELACTOR": return ImageFactory.getImage(IArchiImages.ICON_ACTOR);
                case "MODEL": return ImageFactory.getImage(IArchiImages.ICON_APP); 
                default:
                    throw new IllegalArgumentException("The class '" + clazz + "' is not a valid class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        
        public Color getColor(String clazz) {
            switch (clazz.toUpperCase()) {
                case "GROUPING": 
                case "LOCATION": 
                case "JUNCTION": 
                    return SpecializationPlugin.OTHER_COLOR;
                
                case "APPLICATIONCOLLABORATION":
                case "APPLICATIONCOMPONENT":
                case "APPLICATIONEVENT":
                case "APPLICATIONFUNCTION":
                case "APPLICATIONINTERACTION":
                case "APPLICATIONINTERFACE":
                case "APPLICATIONPROCESS":
                case "APPLICATIONSERVICE":
                case "DATAOBJECT": 
                    return SpecializationPlugin.APPLICATION_COLOR;
                
                case "BUSINESSACTOR":
                case "BUSINESSCOLLABORATION":
                case "BUSINESSEVENT":
                case "BUSINESSFUNCTION":
                case "BUSINESSINTERACTION":
                case "BUSINESSINTERFACE":
                case "BUSINESSOBJECT":
                case "BUSINESSPROCESS":
                case "BUSINESSROLE":
                case "BUSINESSSERVICE": 
                case "CONTRACT": 
                case "PRODUCT": 
                case "REPRESENTATION": 
                    return SpecializationPlugin.BUSINESS_COLOR;
                
                case "CAPABILITY":
                case "COURSEOFACTION":
                case "RESOURCE": 
                    return SpecializationPlugin.STRATEGY_COLOR;
                
                case "DISTRIBUTIONNETWORK": 
                case "EQUIPMENT": 
                case "FACILITY": 
                case "MATERIAL": 
                    return SpecializationPlugin.PHYSICAL_COLOR;
                                
                case "ARTIFACT":
                case "COMMUNICATIONNETWORK":
                case "DEVICE":
                case "NODE":
                case "PATH":
                case "SYSTEMSOFTWARE":
                case "TECHNOLOGYCOLLABORATION":
                case "TECHNOLOGYEVENT":
                case "TECHNOLOGYFUNCTION":
                case "TECHNOLOGYINTERFACE":
                case "TECHNOLOGYINTERACTION":
                case "TECHNOLOGYPROCESS":
                case "TECHNOLOGYSERVICE": 
                    return SpecializationPlugin.TECHNOLOGY_COLOR;
                
                case "DELIVERABLE": 
                case "IMPLEMENTATIONEVENT":
                case "GAP": 
                case "PLATEAU": 
                case "WORKPACKAGE": 
                    return SpecializationPlugin.IMPLEMENTATION_COLOR; 
                    
                case "ASSESSMENT": 
                case "CONSTRAINT": 
                case "DRIVER": 
                case "GOAL": 
                case "MEANING": 
                case "OUTCOME": 
                case "PRINCIPLE": 
                case "REQUIREMENT": 
                case "STAKEHOLDER": 
                case "VALUE": 
                    return SpecializationPlugin.MOTIVATION_COLOR;
                    
                case "FOLDER": 
                case "ACCESSRELATIONSHIP": 
                case "AGGREGATIONRELATIONSHIP": 
                case "ASSIGNMENTRELATIONSHIP": 
                case "ASSOCIATIONRELATIONSHIP": 
                case "COMPOSITIONRELATIONSHIP": 
                case "FLOWRELATIONSHIP": 
                case "INFLUENCERELATIONSHIP": 
                case "REALIZATIONRELATIONSHIP": 
                case "SERVINGRELATIONSHIP": 
                case "SPECIALIZATIONRELATIONSHIP": 
                case "TRIGGERINGRELATIONSHIP": 
                case "DIAGRAMMODELGROUP": 
                case "DIAGRAMMODELNOTE": 
                case "ARCHIMATEDIAGRAMMODEL": 
                case "SKETCHMODEL": 
                case "SKETCHMODELSTICKY": 
                case "SKETCHMODELACTOR": 
                case "MODEL":
                    return null;
                
                default:
                    throw new IllegalArgumentException("The class '" + clazz + "' is not a valid class"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
}
