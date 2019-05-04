/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import java.util.List;

import org.apache.log4j.Level;
import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.commands.SpecializationUpdateMetadataCommand;
import org.archicontribs.specialization.types.ComponentLabel;
import org.archicontribs.specialization.types.ElementSpecialization;
import org.archicontribs.specialization.types.ElementSpecializationMap;
import org.archicontribs.specialization.types.ExclusiveComponentLabels;
import org.archicontribs.specialization.types.SpecializationProperty;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.impl.ApplicationCollaboration;
import com.archimatetool.model.impl.ApplicationComponent;
import com.archimatetool.model.impl.ApplicationEvent;
import com.archimatetool.model.impl.ApplicationFunction;
import com.archimatetool.model.impl.ApplicationInteraction;
import com.archimatetool.model.impl.ApplicationInterface;
import com.archimatetool.model.impl.ApplicationProcess;
import com.archimatetool.model.impl.ApplicationService;
import com.archimatetool.model.impl.Artifact;
import com.archimatetool.model.impl.Assessment;
import com.archimatetool.model.impl.BusinessActor;
import com.archimatetool.model.impl.BusinessCollaboration;
import com.archimatetool.model.impl.BusinessEvent;
import com.archimatetool.model.impl.BusinessFunction;
import com.archimatetool.model.impl.BusinessInteraction;
import com.archimatetool.model.impl.BusinessInterface;
import com.archimatetool.model.impl.BusinessObject;
import com.archimatetool.model.impl.BusinessProcess;
import com.archimatetool.model.impl.BusinessRole;
import com.archimatetool.model.impl.BusinessService;
import com.archimatetool.model.impl.Capability;
import com.archimatetool.model.impl.CommunicationNetwork;
import com.archimatetool.model.impl.Constraint;
import com.archimatetool.model.impl.Contract;
import com.archimatetool.model.impl.CourseOfAction;
import com.archimatetool.model.impl.DataObject;
import com.archimatetool.model.impl.Deliverable;
import com.archimatetool.model.impl.Device;
import com.archimatetool.model.impl.DistributionNetwork;
import com.archimatetool.model.impl.Driver;
import com.archimatetool.model.impl.Equipment;
import com.archimatetool.model.impl.Facility;
import com.archimatetool.model.impl.Gap;
import com.archimatetool.model.impl.Goal;
import com.archimatetool.model.impl.Grouping;
import com.archimatetool.model.impl.ImplementationEvent;
import com.archimatetool.model.impl.Junction;
import com.archimatetool.model.impl.Location;
import com.archimatetool.model.impl.Material;
import com.archimatetool.model.impl.Meaning;
import com.archimatetool.model.impl.Node;
import com.archimatetool.model.impl.Outcome;
import com.archimatetool.model.impl.Path;
import com.archimatetool.model.impl.Plateau;
import com.archimatetool.model.impl.Principle;
import com.archimatetool.model.impl.Product;
import com.archimatetool.model.impl.Representation;
import com.archimatetool.model.impl.Requirement;
import com.archimatetool.model.impl.Resource;
import com.archimatetool.model.impl.Stakeholder;
import com.archimatetool.model.impl.SystemSoftware;
import com.archimatetool.model.impl.TechnologyCollaboration;
import com.archimatetool.model.impl.TechnologyEvent;
import com.archimatetool.model.impl.TechnologyFunction;
import com.archimatetool.model.impl.TechnologyInteraction;
import com.archimatetool.model.impl.TechnologyInterface;
import com.archimatetool.model.impl.TechnologyProcess;
import com.archimatetool.model.impl.TechnologyService;
import com.archimatetool.model.impl.Value;
import com.archimatetool.model.impl.WorkPackage;
import lombok.Getter;

//TODO: use commands everywhere to allow undo/redo

public class SpecializationModelSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);
	
	public static SpecializationModelSection INSTANCE;

	ExclusiveComponentLabels exclusiveComponentLabels = null;

	Composite strategyCanvas;
	Composite businessCanvas;
	Composite applicationCanvas;
	Composite technologyCanvas;
	Composite physicalCanvas;
	Composite implementationCanvas;
	Composite motivationCanvas;
	Composite otherCanvas;

	SpecializationCombo comboSpecializationNames = null;
	TableViewer tblProperties = null;
	Button btnNewProperty = null;
	Button btnDeleteProperty = null;
	Text txtElementLabel = null;
	ElementFigure elementFigure = null;

	@Getter IArchimateModel currentModel = null;

	boolean mouseOverHelpButton = false;

	ElementSpecializationMap elementSpecializationMap;

	/**
	 * Filter to show or hide this section depending on the selected object in the model
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
		// /!\ at this stage, this.model is not set as the setElement() method has not yet been called

		parent.setLayout(new FormLayout());

		Label lblChooseClass = new Label(parent, SWT.NONE);
		lblChooseClass.setForeground(parent.getForeground());
		lblChooseClass.setBackground(parent.getBackground());
		lblChooseClass.setText("Please choose the class of elements to specialize:");
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 10);
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

		this.exclusiveComponentLabels = new ExclusiveComponentLabels();

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Strategy layer
		// Passive
		// Behavior
		this.exclusiveComponentLabels.add(strategyBehaviorCompo, Capability.class);
		this.exclusiveComponentLabels.add(strategyBehaviorCompo,  CourseOfAction.class);
		// Active
		this.exclusiveComponentLabels.add(strategyActiveCompo, Resource.class);

		// Business layer
		// Passive
		this.exclusiveComponentLabels.add(businessPassiveCompo, Product.class);
		// Behavior
		this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessProcess.class);
		this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessFunction.class);
		this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessInteraction.class);
		this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessEvent.class);
		this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessService.class);
		this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessObject.class);
		this.exclusiveComponentLabels.add(businessBehaviorCompo, Contract.class);
		this.exclusiveComponentLabels.add(businessBehaviorCompo, Representation.class);
		// Active
		this.exclusiveComponentLabels.add(businessActiveCompo, BusinessActor.class);
		this.exclusiveComponentLabels.add(businessActiveCompo, BusinessRole.class);
		this.exclusiveComponentLabels.add(businessActiveCompo, BusinessCollaboration.class);
		this.exclusiveComponentLabels.add(businessActiveCompo, BusinessInterface.class);

		// Application layer
		//Passive
		this.exclusiveComponentLabels.add(applicationPassiveCompo, DataObject.class);
		//Behavior
		this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationFunction.class);
		this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationInteraction.class);
		this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationEvent.class);
		this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationService.class);
		this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationProcess.class);
		//  Active      
		this.exclusiveComponentLabels.add(applicationActiveCompo, ApplicationComponent.class);
		this.exclusiveComponentLabels.add(applicationActiveCompo, ApplicationCollaboration.class);
		this.exclusiveComponentLabels.add(applicationActiveCompo, ApplicationInterface.class);

		// Technology layer
		// Passive
		this.exclusiveComponentLabels.add(technologyPassiveCompo, Artifact.class);
		// Behavior
		this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyFunction.class);
		this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyProcess.class);
		this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyInteraction.class);
		this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyEvent.class);
		this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyService.class);
		// Active
		this.exclusiveComponentLabels.add(technologyActiveCompo, Node.class);
		this.exclusiveComponentLabels.add(technologyActiveCompo, Device.class);
		this.exclusiveComponentLabels.add(technologyActiveCompo, SystemSoftware.class);
		this.exclusiveComponentLabels.add(technologyActiveCompo, TechnologyCollaboration.class);
		this.exclusiveComponentLabels.add(technologyActiveCompo, TechnologyInterface.class);
		this.exclusiveComponentLabels.add(technologyActiveCompo, Path.class);
		this.exclusiveComponentLabels.add(technologyActiveCompo, CommunicationNetwork.class);

		// Physical layer
		// Passive
		// Behavior
		this.exclusiveComponentLabels.add(physicalBehaviorCompo, Material.class);
		// Active
		this.exclusiveComponentLabels.add(physicalActiveCompo, Equipment.class);
		this.exclusiveComponentLabels.add(physicalActiveCompo, Facility.class);
		this.exclusiveComponentLabels.add(physicalActiveCompo, DistributionNetwork.class);

		// Implementation layer
		this.exclusiveComponentLabels.add(implementationCompo, WorkPackage.class);
		this.exclusiveComponentLabels.add(implementationCompo, Deliverable.class);
		this.exclusiveComponentLabels.add(implementationCompo, ImplementationEvent.class);
		this.exclusiveComponentLabels.add(implementationCompo, Plateau.class);
		this.exclusiveComponentLabels.add(implementationCompo, Gap.class);

		// Motivation layer
		this.exclusiveComponentLabels.add(motivationCompo, Stakeholder.class);
		this.exclusiveComponentLabels.add(motivationCompo, Driver.class);
		this.exclusiveComponentLabels.add(motivationCompo, Assessment.class);
		this.exclusiveComponentLabels.add(motivationCompo, Goal.class);
		this.exclusiveComponentLabels.add(motivationCompo, Outcome.class);
		this.exclusiveComponentLabels.add(motivationCompo, Principle.class);
		this.exclusiveComponentLabels.add(motivationCompo, Requirement.class);
		this.exclusiveComponentLabels.add(motivationCompo, Constraint.class);
		this.exclusiveComponentLabels.add(motivationCompo, Meaning.class);
		this.exclusiveComponentLabels.add(motivationCompo, Value.class);

		// Containers !!!
		//
		this.exclusiveComponentLabels.add(otherCompo, Grouping.class);
		this.exclusiveComponentLabels.add(otherCompo, Location.class);
		this.exclusiveComponentLabels.add(otherCompo, Junction.class);

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

		Label lblMotivation = new Label(compoElements, SWT.CENTER);
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
		lblMotivation.setLayoutData(fd);
		lblMotivation.setText("Motivation");
		lblMotivation.setBackground(SpecializationPlugin.MOTIVATION_COLOR);

		Label lblStrategy = new Label(compoElements, SWT.NONE);
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
		lblStrategy.setLayoutData(fd);
		lblStrategy.setBackground(SpecializationPlugin.STRATEGY_COLOR);
		lblStrategy.setText("Strategy");

		Label lblBusiness = new Label(compoElements, SWT.NONE);
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
		lblBusiness.setLayoutData(fd);
		lblBusiness.setBackground(SpecializationPlugin.BUSINESS_COLOR);
		lblBusiness.setText("Business");

		Label lblApplication = new Label(compoElements, SWT.NONE);
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
		lblApplication.setLayoutData(fd);
		lblApplication.setBackground(SpecializationPlugin.APPLICATION_COLOR);
		lblApplication.setText("Application");

		Label lblTechnology = new Label(compoElements, SWT.NONE);
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
		lblTechnology.setLayoutData(fd);
		lblTechnology.setBackground(SpecializationPlugin.TECHNOLOGY_COLOR);
		lblTechnology.setText("Technology");

		Label lblPhysical = new Label(compoElements, SWT.NONE);
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
		lblPhysical.setLayoutData(fd);
		lblPhysical.setBackground(SpecializationPlugin.PHYSICAL_COLOR);
		lblPhysical.setText("Physical");

		Label lblImplementation = new Label(compoElements, SWT.NONE);
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
		lblImplementation.setLayoutData(fd);
		lblImplementation.setBackground(SpecializationPlugin.IMPLEMENTATION_COLOR);
		lblImplementation.setText("Implementation");

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

		Label lblSpecializationName = new Label(parent, SWT.NONE);
		lblSpecializationName.setForeground(parent.getForeground());
		lblSpecializationName.setBackground(parent.getBackground());
		lblSpecializationName.setText("Specializations:");
		fd = new FormData();
		fd.top = new FormAttachment(compoElements, 10);
		fd.left = new FormAttachment(0, 30);
		lblSpecializationName.setLayoutData(fd);

		this.comboSpecializationNames = new SpecializationCombo(parent, SWT.NONE | SWT.READ_ONLY);
		this.comboSpecializationNames.setForeground(parent.getForeground());
		this.comboSpecializationNames.setBackground(parent.getBackground());
		this.comboSpecializationNames.setLabel("specialization");
		this.comboSpecializationNames.setEnabled(false);
		fd = new FormData();
		fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
		fd.left = new FormAttachment(lblSpecializationName, 10);
		fd.right = new FormAttachment(compoElements, 0, SWT.RIGHT);
		this.comboSpecializationNames.setLayoutData(fd);

		// This event is fired when a componentLabel is selected
		// it fills in the comboSpecializationNames combo
		this.exclusiveComponentLabels.addListener(new Listener() {
			@Override public void handleEvent(Event event) {
				// we reinitialize the combo, list and figure 
				SpecializationModelSection.this.comboSpecializationNames.removeAll();
				
				SpecializationModelSection.this.tblProperties.setInput(null);
				SpecializationModelSection.this.tblProperties.refresh();
				SpecializationModelSection.this.tblProperties.getTable().setEnabled(false);
				SpecializationModelSection.this.btnNewProperty.setEnabled(false);
				SpecializationModelSection.this.btnDeleteProperty.setEnabled(false);
				SpecializationModelSection.this.txtElementLabel.setText("");
				SpecializationModelSection.this.txtElementLabel.setEnabled(false);
				SpecializationModelSection.this.elementFigure.reset();
				SpecializationModelSection.this.elementFigure.setEnabled(false);

				if ( event.widget == null ) {
					logger.trace("No component label selected");
					SpecializationModelSection.this.comboSpecializationNames.setEnabled(false);
				} else {
					ComponentLabel componentlabel = (ComponentLabel)event.widget;
					String componentClass = componentlabel.getToolTipText();
					logger.trace("Component label selected: "+componentClass);
					
					List<ElementSpecialization> specialisationList = SpecializationModelSection.this.elementSpecializationMap.get(componentClass);
					if ( specialisationList != null ) {
						for (ElementSpecialization elementSpecialization: SpecializationModelSection.this.elementSpecializationMap.get(componentClass))
							SpecializationModelSection.this.comboSpecializationNames.add(elementSpecialization.getSpecializationName());
					}
					
					SpecializationModelSection.this.comboSpecializationNames.setEnabled(true);
					
					if ( SpecializationModelSection.this.comboSpecializationNames.getItemCount() != 0 )
						SpecializationModelSection.this.comboSpecializationNames.select(0);
				}
			}
		});

		// This event is fired when  a specialization is chosen in the combo list
		this.comboSpecializationNames.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent e) {
				if ( SpecializationModelSection.this.comboSpecializationNames.getEnabled() ) {
					String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelectedComponentLabel().getToolTipText();
					String specializationName = SpecializationModelSection.this.comboSpecializationNames.getText();

					if ( specializationName.isEmpty() ) {
						// we reinitialize list and figure
						if ( SpecializationModelSection.this.comboSpecializationNames.getPreviousValue().isEmpty() )
							logger.trace("No specialization selected");
						else {
							logger.trace("Specialization removed: "+SpecializationModelSection.this.comboSpecializationNames.getPreviousValue());
							SpecializationModelSection.this.elementSpecializationMap.removeElementSpecialization(selectedClass, SpecializationModelSection.this.comboSpecializationNames.getPreviousValue());
							setMetadata("Remove specialization "+selectedClass+"/"+SpecializationModelSection.this.comboSpecializationNames.getPreviousValue());
						}
						
						SpecializationModelSection.this.tblProperties.setInput(null);
						SpecializationModelSection.this.tblProperties.refresh();
						SpecializationModelSection.this.tblProperties.getTable().setEnabled(false);
						SpecializationModelSection.this.btnNewProperty.setEnabled(false);
						SpecializationModelSection.this.btnDeleteProperty.setEnabled(false);
						SpecializationModelSection.this.txtElementLabel.setEnabled(false);
						SpecializationModelSection.this.txtElementLabel.setText("");
						SpecializationModelSection.this.elementFigure.reset();
						SpecializationModelSection.this.elementFigure.setEnabled(false);
					} else {
						ElementSpecialization elementSpecialization = SpecializationModelSection.this.elementSpecializationMap.getElementSpecialization(selectedClass, specializationName);
						
						logger.trace("Specialization selected: "+specializationName);
						
						if ( elementSpecialization == null ) {
							if ( SpecializationModelSection.this.comboSpecializationNames.getPreviousValue().isEmpty() ) {
								// new specialization
								logger.trace("Creating new specialization");
								elementSpecialization = new ElementSpecialization(specializationName);
								SpecializationModelSection.this.elementSpecializationMap.addElementSpecialization(selectedClass, elementSpecialization);
								setMetadata("Create new specialization "+selectedClass+"/"+specializationName);
							} else {
								// renamed specialization
								logger.trace("Renaming specialization "+SpecializationModelSection.this.comboSpecializationNames.getPreviousValue()+" to "+specializationName);
								elementSpecialization = SpecializationModelSection.this.elementSpecializationMap.getElementSpecialization(selectedClass, SpecializationModelSection.this.comboSpecializationNames.getPreviousValue());
								elementSpecialization.setSpecializationName(specializationName);
								setMetadata("Rename specialization "+selectedClass+"/"+SpecializationModelSection.this.comboSpecializationNames.getPreviousValue());
							}
						}
						logger.trace("it has got "+elementSpecialization.getProperties().size()+" properties.");

						SpecializationModelSection.this.tblProperties.setInput(elementSpecialization.getProperties());
						SpecializationModelSection.this.tblProperties.refresh();
						SpecializationModelSection.this.tblProperties.getTable().setEnabled(true);
						SpecializationModelSection.this.btnNewProperty.setEnabled(true);
						SpecializationModelSection.this.btnDeleteProperty.setEnabled(!elementSpecialization.getProperties().isEmpty());
						SpecializationModelSection.this.txtElementLabel.setEnabled(true);
						SpecializationModelSection.this.txtElementLabel.setText(elementSpecialization.getLabel());
						SpecializationModelSection.this.elementFigure.setEClass(SpecializationModelSection.this.exclusiveComponentLabels.getSelectedComponentLabel().getEClass(), elementSpecialization);
						SpecializationModelSection.this.elementFigure.setEnabled(true);
					}
				}
			}
		});

		Label lblProperties = new Label(parent, SWT.NONE);
		lblProperties.setForeground(parent.getForeground());
		lblProperties.setBackground(parent.getBackground());
		lblProperties.setText("Properties:");
		fd = new FormData();
		fd.top = new FormAttachment(lblSpecializationName, 15);
		fd.left = new FormAttachment(lblSpecializationName, 0, SWT.LEFT);
		lblProperties.setLayoutData(fd);

		this.tblProperties = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		this.tblProperties.setContentProvider( new ArrayContentProvider());
		Table table = this.tblProperties.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setEnabled(false);

		TableViewerColumn col = new TableViewerColumn(this.tblProperties, SWT.NONE);
		col.getColumn().setText("Name");
		col.getColumn().setWidth(140);
		col.getColumn().setResizable(true);
		col.setLabelProvider(new ColumnLabelProvider() { @Override public String getText(Object element) { return ((SpecializationProperty)element).getName(); }});
		col.setEditingSupport(new EditingSupport(this.tblProperties) {
			private final CellEditor editor = new TextCellEditor(SpecializationModelSection.this.tblProperties.getTable());

			@Override protected void setValue(Object element, Object value) {
				((SpecializationProperty)element).setName((String)value);
				SpecializationModelSection.this.tblProperties.update(element, null);

				setMetadata("Update specialization property");
			}

			@Override protected Object getValue(Object element) {
				return ((SpecializationProperty)element).getName();
			}

			@Override protected CellEditor getCellEditor(Object element) {
				return this.editor;
			}

			@Override protected boolean canEdit(Object element) {
				return true;
			}
		});

		col = new TableViewerColumn(this.tblProperties, SWT.NONE);
		col.getColumn().setText("Default value");
		col.getColumn().setWidth(220);
		col.getColumn().setResizable(true);
		col.setLabelProvider(new ColumnLabelProvider() { @Override public String getText(Object element) { return ((SpecializationProperty)element).getValue(); }});
		col.setEditingSupport(new EditingSupport(this.tblProperties) {
			private final CellEditor editor = new TextCellEditor(SpecializationModelSection.this.tblProperties.getTable());

			@Override protected void setValue(Object element, Object value) {
				String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelectedComponentLabel().getToolTipText();
				String specializationName = SpecializationModelSection.this.comboSpecializationNames.getText();
				
				((SpecializationProperty)element).setValue((String)value);
				SpecializationModelSection.this.tblProperties.update(element, null);

				setMetadata("Update property for specialization "+selectedClass+"/"+specializationName);
			}

			@Override protected Object getValue(Object element) {
				return ((SpecializationProperty)element).getValue();
			}

			@Override protected CellEditor getCellEditor(Object element) {
				return this.editor;
			}

			@Override protected boolean canEdit(Object element) {
				return true;
			}
		});

		this.btnNewProperty = new Button(parent, SWT.PUSH);
		this.btnNewProperty.setImage(SpecializationPlugin.NEW_ICON);
		this.btnNewProperty.setToolTipText("New property");
		this.btnNewProperty.setEnabled(false);
		fd = new FormData();
		fd.top = new FormAttachment(table, 0, SWT.TOP);
		fd.right = new FormAttachment(compoElements, 0, SWT.RIGHT);
		fd.width = 20;
		fd.height = 20;
		this.btnNewProperty.setLayoutData(fd);
		this.btnNewProperty.addSelectionListener(new SelectionListener() {
			@Override public void widgetSelected(SelectionEvent e) {
				String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelectedComponentLabel().getToolTipText();
				String specializationName = SpecializationModelSection.this.comboSpecializationNames.getText();
				ElementSpecialization elementSpecialization = SpecializationModelSection.this.elementSpecializationMap.getElementSpecialization(selectedClass, SpecializationModelSection.this.comboSpecializationNames.getText());
				elementSpecialization.getProperties().add(new SpecializationProperty("new property",""));
				SpecializationModelSection.this.tblProperties.refresh();

				SpecializationModelSection.this.tblProperties.getTable().setSelection(SpecializationModelSection.this.tblProperties.getTable().getItemCount()-1);
				SpecializationModelSection.this.btnDeleteProperty.setEnabled(true);

				setMetadata("New property for specialization "+selectedClass+"/"+specializationName);
			}

			@Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});

		this.btnDeleteProperty = new Button(parent, SWT.PUSH);
		this.btnDeleteProperty.setImage(SpecializationPlugin.DELETE_ICON);
		this.btnDeleteProperty.setToolTipText("Delete property");
		this.btnDeleteProperty.setEnabled(false);
		fd = new FormData();
		fd.top = new FormAttachment(this.btnNewProperty, 5);
		fd.right = new FormAttachment(this.btnNewProperty, 0, SWT.RIGHT);
		fd.width = 20;
		fd.height = 20;
		this.btnDeleteProperty.setLayoutData(fd);
		this.btnDeleteProperty.addSelectionListener(new SelectionListener() {
			@Override public void widgetSelected(SelectionEvent e) {
				String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelectedComponentLabel().getToolTipText();
				String specializationName = SpecializationModelSection.this.comboSpecializationNames.getText();
				ElementSpecialization elementSpecialization = SpecializationModelSection.this.elementSpecializationMap.getElementSpecialization(selectedClass, SpecializationModelSection.this.comboSpecializationNames.getText());
				int selectionIndex = SpecializationModelSection.this.tblProperties.getTable().getSelectionIndex();
				elementSpecialization.getProperties().remove(selectionIndex);
				SpecializationModelSection.this.tblProperties.refresh();

				if ( SpecializationModelSection.this.tblProperties.getTable().getItemCount() == 0 )
					SpecializationModelSection.this.btnDeleteProperty.setEnabled(false);
				else {
					if ( selectionIndex == 0 )
						SpecializationModelSection.this.tblProperties.getTable().setSelection(0);
					else
						SpecializationModelSection.this.tblProperties.getTable().setSelection(selectionIndex-1);
				}

				setMetadata("Delete property for specialization "+selectedClass+"/"+specializationName);
			}

			@Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		
		fd = new FormData();
		fd.top = new FormAttachment(lblProperties, -5, SWT.TOP);
		fd.left = new FormAttachment(lblProperties, 5);
		fd.right = new FormAttachment(this.btnNewProperty, -5);
		fd.height = 100;
		table.setLayoutData(fd);
		
		Label lblLabel = new Label(parent, SWT.NONE);
		lblLabel.setForeground(parent.getForeground());
		lblLabel.setBackground(parent.getBackground());
		lblLabel.setText("Label:");
		fd = new FormData();
		fd.top = new FormAttachment(table, 10);
		fd.left = new FormAttachment(lblSpecializationName, 0, SWT.LEFT);
		lblLabel.setLayoutData(fd);
		
		this.txtElementLabel = new Text(parent, SWT.BORDER);
		this.txtElementLabel.setToolTipText("Specify the text that will be shown in the figure.\n\nYou may format your text using \\n (newline) and \\t (tab) and use variables(please refer to the online help for a complete list): \n   ${name} name of the Archimate element\n   ${property:xxx} value of the property xxx\n   ${view:name} name of the view in which the Archimate element is displayed\n   ${model:name} name of the model");
		this.txtElementLabel.setEnabled(false);
		fd = new FormData();
		fd.top = new FormAttachment(lblLabel, 0, SWT.CENTER);
		fd.left = new FormAttachment(table, 0, SWT.LEFT);
		fd.right = new FormAttachment(table, 0, SWT.RIGHT);
		this.txtElementLabel.setLayoutData(fd);
		this.txtElementLabel.addListener(SWT.DefaultSelection, new Listener() {
			@Override public void handleEvent(Event e) {
				String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelectedComponentLabel().getToolTipText();
				String specializationName = SpecializationModelSection.this.comboSpecializationNames.getText();
				ElementSpecialization elementSpecialization = SpecializationModelSection.this.elementSpecializationMap.getElementSpecialization(selectedClass, SpecializationModelSection.this.comboSpecializationNames.getText());
				
				elementSpecialization.setLabel(((Text)e.widget).getText());
				
				setMetadata("Change label for specialization "+selectedClass+"/"+specializationName);
			}
		});
		this.txtElementLabel.addFocusListener(new FocusListener() {
			String text;
			
			@Override public void focusGained(FocusEvent e) {
				this.text=((Text)e.widget).getText();
			}
			@Override public void focusLost(FocusEvent e) {
				if ( !((Text)e.widget).getText().equals(this.text) ) {
					((Text)e.widget).notifyListeners(SWT.DefaultSelection, new Event());		// simulates a return or enter key
				}
			}
		});

		Label lblFigure = new Label(parent, SWT.NONE);
		lblFigure.setForeground(parent.getForeground());
		lblFigure.setBackground(parent.getBackground());
		lblFigure.setText("Figure:");
		fd = new FormData();
		fd.top = new FormAttachment(lblLabel, 15);
		fd.left = new FormAttachment(lblSpecializationName, 0, SWT.LEFT);
		lblFigure.setLayoutData(fd);

		this.elementFigure = new ElementFigure(parent, SWT.NONE);
		this.elementFigure.setEnabled(false);
		fd = new FormData();
		fd.top = new FormAttachment(lblFigure, -5, SWT.TOP);
		fd.left = new FormAttachment(table, 0, SWT.LEFT);
		fd.right = new FormAttachment(table, 0, SWT.RIGHT);
		this.elementFigure.setLayoutData(fd);
		this.elementFigure.addListener(SWT.Selection, new Listener() {
			@Override public void handleEvent(Event e) {
				ComponentLabel selectedComponentLabel = SpecializationModelSection.this.exclusiveComponentLabels.getSelectedComponentLabel();
				if ( selectedComponentLabel != null ) {
					String selectedClass = selectedComponentLabel.getToolTipText();
					String specializationName = SpecializationModelSection.this.comboSpecializationNames.getText();
					ElementSpecialization elementSpecialization = SpecializationModelSection.this.elementSpecializationMap.getElementSpecialization(selectedClass, specializationName);
					elementSpecialization.setFigure(((ElementFigure)e.widget).getSelectedFigure());
					elementSpecialization.setIconName(((ElementFigure)e.widget).getIconName());
					elementSpecialization.setIconSize(((ElementFigure)e.widget).getIconSize());
					elementSpecialization.setIconLocation(((ElementFigure)e.widget).getIconLocation());
	
					setMetadata("Change figure for specialization "+selectedClass+"/"+specializationName);
			        
			        // we force the figures to be redrawn
					((ElementFigure)e.widget).resetPreviewImages();
				}
			}
		});

		/* **************************************************** */

		Label lblSeparator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		fd = new FormData();
		fd.top = new FormAttachment(this.elementFigure, 10);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		lblSeparator.setLayoutData(fd);

		/* **************************************************** */

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
		btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help: /"+SpecializationPlugin.PLUGIN_ID+"/help/html/specializeModel.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/specializeModel.html"); } });

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
				// nothing to do
			}
		}
	};

	@Override
	protected Adapter getECoreAdapter() {
		return this.eAdapter;
	}

	@Override
	protected EObject getEObject() {
		return this.currentModel;
	}

	@Override
	protected void setElement(Object element) {
		IArchimateModel selectedModel = (IArchimateModel)new Filter().adaptObject(element);
		
		INSTANCE = this;

		if(selectedModel == null) {
			logger.error("Failed to get model from " + element); //$NON-NLS-1$
			this.elementSpecializationMap = null;
		} else {
			logger.debug("Showing specialization tab for "+SpecializationPlugin.getDebugName(selectedModel));

			if ( this.exclusiveComponentLabels != null )
				this.exclusiveComponentLabels.setModel(selectedModel);
			
			if ( this.elementFigure != null )
				this.elementFigure.setModel(selectedModel);

			this.elementSpecializationMap = ElementSpecializationMap.getFromArchimateModel(selectedModel);
		}

		// TODO: the rest of the code assumes the variable is not null ! add check for null everywhere
		if ( this.elementSpecializationMap == null )
			this.elementSpecializationMap = new ElementSpecializationMap();

		if ( selectedModel != this.currentModel ) {
			if ( this.comboSpecializationNames != null && !this.comboSpecializationNames.isDisposed() )
				this.comboSpecializationNames.removeAll();

			if ( this.tblProperties != null && !this.tblProperties.getTable().isDisposed() ) {
				this.tblProperties.setInput(null);
				this.tblProperties.refresh();
			}

			if ( this.exclusiveComponentLabels != null ) {
				for ( ComponentLabel lbl: this.exclusiveComponentLabels.getAllComponentLabels() )
					lbl.setSelected(false);
			}

			this.currentModel = selectedModel;
		}
	}
	
	public static String getSelectedClass() {
		if ( SpecializationModelSection.INSTANCE.exclusiveComponentLabels == null )
			return null;
		Label label = SpecializationModelSection.INSTANCE.exclusiveComponentLabels.getSelectedComponentLabel();
		if ( (label == null) || label.isDisposed() )
			return null;
		return label.getToolTipText();
	}
	
	public static String getSelectedSpecializationName() {
		if ( SpecializationModelSection.INSTANCE.comboSpecializationNames == null )
			return null;
		return SpecializationModelSection.INSTANCE.comboSpecializationNames.getText();
	}
	
	public static int getSelectedfigure() {
		if ( SpecializationModelSection.INSTANCE.elementFigure == null )
			return 0;
		return SpecializationModelSection.INSTANCE.elementFigure.getSelectedFigure();
	}

	void setMetadata(String label) {
		SpecializationUpdateMetadataCommand command = new SpecializationUpdateMetadataCommand(this.currentModel, this.elementSpecializationMap, label);
		
		if ( command.canExecute() )
			((CommandStack)SpecializationModelSection.this.currentModel.getAdapter(CommandStack.class)).execute(command);

		if ( command.getException() != null )
			SpecializationPlugin.popup(Level.ERROR, "Failed to save specializations to model's metadata.", command.getException());
	}


}
