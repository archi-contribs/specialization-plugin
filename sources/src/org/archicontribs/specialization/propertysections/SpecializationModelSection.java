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
import org.archicontribs.specialization.types.SpecializationProperty;
import org.archicontribs.specialization.types.SpecializationType;
import org.archicontribs.specialization.types.ComponentLabel;
import org.archicontribs.specialization.types.ExclusiveComponentLabels;
import org.archicontribs.specialization.types.SpecializationMap;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IProperty;
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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

//TODO: use commands everywhere to allow undo/redo

public class SpecializationModelSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
    static final SpecializationLogger logger = new SpecializationLogger(SpecializationModelSection.class);

    ExclusiveComponentLabels exclusiveComponentLabels = null;

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

    Button btnNewSpecialization = null;
    Button btnEditSpecialization = null;
    Button btnDeleteSpecialization = null;
    Combo comboSpecializationNames = null;
    TableViewer tblProperties = null;
    Button btnNewProperty = null;
    Button btnDeleteProperty = null;
    ElementFigure elementFigure = null;

    IArchimateModel model = null;

    boolean mouseOverHelpButton = false;

    SpecializationMap specializationMap;

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
        // at this stage, this.model is not set as the setElement() method has not yet been called

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
        this.capabilityLabel = this.exclusiveComponentLabels.add(strategyBehaviorCompo, Capability.class);
        this.courseOfActionLabel = this.exclusiveComponentLabels.add(strategyBehaviorCompo,  CourseOfAction.class);
        // Active
        this.resourceLabel = this.exclusiveComponentLabels.add(strategyActiveCompo, Resource.class);

        // Business layer
        // Passive
        this.productLabel = this.exclusiveComponentLabels.add(businessPassiveCompo, Product.class);
        // Behavior
        this.businessProcessLabel = this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessProcess.class);
        this.businessFunctionLabel = this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessFunction.class);
        this.businessInteractionLabel = this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessInteraction.class);
        this.businessEventLabel = this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessEvent.class);
        this.businessServiceLabel = this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessService.class);
        this.businessObjectLabel = this.exclusiveComponentLabels.add(businessBehaviorCompo, BusinessObject.class);
        this.contractLabel = this.exclusiveComponentLabels.add(businessBehaviorCompo, Contract.class);
        this.representationLabel = this.exclusiveComponentLabels.add(businessBehaviorCompo, Representation.class);
        // Active
        this.businessActorLabel = this.exclusiveComponentLabels.add(businessActiveCompo, BusinessActor.class);
        this.businessRoleLabel = this.exclusiveComponentLabels.add(businessActiveCompo, BusinessRole.class);
        this.businessCollaborationLabel = this.exclusiveComponentLabels.add(businessActiveCompo, BusinessCollaboration.class);
        this.businessInterfaceLabel = this.exclusiveComponentLabels.add(businessActiveCompo, BusinessInterface.class);

        // Application layer
        //Passive
        this.dataObjectLabel = this.exclusiveComponentLabels.add(applicationPassiveCompo, DataObject.class);
        //Behavior
        this.applicationFunctionLabel = this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationFunction.class);
        this.applicationInteractionLabel = this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationInteraction.class);
        this.applicationEventLabel = this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationEvent.class);
        this.applicationServiceLabel = this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationService.class);
        this.applicationProcessLabel = this.exclusiveComponentLabels.add(applicationBehaviorCompo, ApplicationProcess.class);
        //  Active      
        this.applicationComponentLabel = this.exclusiveComponentLabels.add(applicationActiveCompo, ApplicationComponent.class);
        this.applicationCollaborationLabel = this.exclusiveComponentLabels.add(applicationActiveCompo, ApplicationCollaboration.class);
        this.applicationInterfaceLabel = this.exclusiveComponentLabels.add(applicationActiveCompo, ApplicationInterface.class);

        // Technology layer
        // Passive
        this.artifactLabel = this.exclusiveComponentLabels.add(technologyPassiveCompo, Artifact.class);
        // Behavior
        this.technologyFunctionLabel = this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyFunction.class);
        this.technologyProcessLabel = this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyProcess.class);
        this.technologyInteractionLabel = this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyInteraction.class);
        this.technologyEventLabel = this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyEvent.class);
        this.technologyServiceLabel = this.exclusiveComponentLabels.add(technologyBehaviorCompo, TechnologyService.class);
        // Active
        this.nodeLabel = this.exclusiveComponentLabels.add(technologyActiveCompo, Node.class);
        this.deviceLabel = this.exclusiveComponentLabels.add(technologyActiveCompo, Device.class);
        this.systemSoftwareLabel = this.exclusiveComponentLabels.add(technologyActiveCompo, SystemSoftware.class);
        this.technologyCollaborationLabel = this.exclusiveComponentLabels.add(technologyActiveCompo, TechnologyCollaboration.class);
        this.technologyInterfaceLabel = this.exclusiveComponentLabels.add(technologyActiveCompo, TechnologyInterface.class);
        this.pathLabel = this.exclusiveComponentLabels.add(technologyActiveCompo, Path.class);
        this.communicationNetworkLabel = this.exclusiveComponentLabels.add(technologyActiveCompo, CommunicationNetwork.class);

        // Physical layer
        // Passive
        // Behavior
        this.materialLabel = this.exclusiveComponentLabels.add(physicalBehaviorCompo, Material.class);
        // Active
        this.equipmentLabel = this.exclusiveComponentLabels.add(physicalActiveCompo, Equipment.class);
        this.facilityLabel = this.exclusiveComponentLabels.add(physicalActiveCompo, Facility.class);
        this.distributionNetworkLabel = this.exclusiveComponentLabels.add(physicalActiveCompo, DistributionNetwork.class);

        // Implementation layer
        this.workpackageLabel = this.exclusiveComponentLabels.add(implementationCompo, WorkPackage.class);
        this.deliverableLabel = this.exclusiveComponentLabels.add(implementationCompo, Deliverable.class);
        this.implementationEventLabel = this.exclusiveComponentLabels.add(implementationCompo, ImplementationEvent.class);
        this.plateauLabel = this.exclusiveComponentLabels.add(implementationCompo, Plateau.class);
        this.gapLabel = this.exclusiveComponentLabels.add(implementationCompo, Gap.class);

        // Motivation layer
        this.stakeholderLabel = this.exclusiveComponentLabels.add(motivationCompo, Stakeholder.class);
        this.driverLabel = this.exclusiveComponentLabels.add(motivationCompo, Driver.class);
        this.assessmentLabel = this.exclusiveComponentLabels.add(motivationCompo, Assessment.class);
        this.goalLabel = this.exclusiveComponentLabels.add(motivationCompo, Goal.class);
        this.outcomeLabel = this.exclusiveComponentLabels.add(motivationCompo, Outcome.class);
        this.principleLabel = this.exclusiveComponentLabels.add(motivationCompo, Principle.class);
        this.requirementLabel = this.exclusiveComponentLabels.add(motivationCompo, Requirement.class);
        this.constaintLabel = this.exclusiveComponentLabels.add(motivationCompo, Constraint.class);
        this.smeaningLabel = this.exclusiveComponentLabels.add(motivationCompo, Meaning.class);
        this.valueLabel = this.exclusiveComponentLabels.add(motivationCompo, Value.class);

        // Containers !!!
        //
        this.groupingLabel = this.exclusiveComponentLabels.add(otherCompo, Grouping.class);
        this.locationLabel = this.exclusiveComponentLabels.add(otherCompo, Location.class);
        this.junctionLabel = this.exclusiveComponentLabels.add(otherCompo, Junction.class);

        for ( ComponentLabel lbl: this.exclusiveComponentLabels.getAllComponentLabels() ) {
            lbl.getLabel().addListener(SWT.MouseUp, new Listener() {
                @Override public void handleEvent(Event event) {
                    //
                    // This event is fired when an element class is selected in the componentLabels diagram
                    //

                    SpecializationModelSection.this.comboSpecializationNames.removeAll();

                    // we search the ComponentLabel that fired the event
                    for ( ComponentLabel componentLabel: SpecializationModelSection.this.exclusiveComponentLabels.getAllComponentLabels() ) {
                        if ( componentLabel.getLabel().equals(event.widget) ) {
                            if ( SpecializationModelSection.this.specializationMap != null ) {
                                List<SpecializationType> specializationTypes = SpecializationModelSection.this.specializationMap.get(componentLabel.getLabel().getToolTipText());
                                if ( specializationTypes != null ) {
                                    boolean isFirst = true;
                                    for ( SpecializationType specializationType: specializationTypes ) {
                                        SpecializationModelSection.this.comboSpecializationNames.add(specializationType.getName());
                                        SpecializationModelSection.this.btnEditSpecialization.setEnabled(true);
                                        SpecializationModelSection.this.btnDeleteSpecialization.setEnabled(true);
                                        if ( isFirst ) {
                                            isFirst = false;
                                            SpecializationModelSection.this.comboSpecializationNames.select(0);
                                            SpecializationModelSection.this.elementFigure.setIconSize(specializationType.getIconSize());
                                            SpecializationModelSection.this.elementFigure.setIconLocation(specializationType.getIconLocation());
                                            SpecializationModelSection.this.tblProperties.setInput(specializationType.getProperties());
                                            SpecializationModelSection.this.tblProperties.refresh();

                                            SpecializationModelSection.this.tblProperties.getTable().setEnabled(true);
                                        }
                                    }
                                }
                            }

                            if ( SpecializationModelSection.this.btnNewSpecialization != null )
                                SpecializationModelSection.this.btnNewSpecialization.setEnabled(true);

                            // we set the element figures
                            SpecializationModelSection.this.elementFigure.setEClass(componentLabel.getEClass());
                        } else
                            componentLabel.setSelected(false);
                    }
                }
            });
        }

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

        Label lblSpecializationName = new Label(parent, SWT.NONE);
        lblSpecializationName.setForeground(parent.getForeground());
        lblSpecializationName.setBackground(parent.getBackground());
        lblSpecializationName.setText("Specializations:");
        fd = new FormData();
        fd.top = new FormAttachment(compoElements, 30);
        fd.left = new FormAttachment(0, 30);
        lblSpecializationName.setLayoutData(fd);

        this.comboSpecializationNames = new Combo(parent, SWT.NONE | SWT.READ_ONLY);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblSpecializationName, 10);
        fd.right = new FormAttachment(lblSpecializationName, 350);
        this.comboSpecializationNames.setLayoutData(fd);
        this.comboSpecializationNames.addModifyListener(new ModifyListener() {
            @Override public void modifyText(ModifyEvent e) {
                //
                // This event is fired when a specialization is chosen in the combo list
                //
                
                String specializationName = SpecializationModelSection.this.comboSpecializationNames.getText();
                boolean enableIcons = !specializationName.isEmpty();
                
                SpecializationModelSection.this.btnEditSpecialization.setEnabled(enableIcons);
                SpecializationModelSection.this.btnDeleteSpecialization.setEnabled(enableIcons);
                SpecializationModelSection.this.elementFigure.setEnabled(enableIcons);
                SpecializationModelSection.this.tblProperties.getTable().setEnabled(enableIcons);
                SpecializationModelSection.this.btnNewProperty.setEnabled(enableIcons);

                if ( enableIcons ) {
                    String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelected().getLabel().getToolTipText();
                    SpecializationType specializationType = SpecializationModelSection.this.specializationMap.getSpecializationType(selectedClass, specializationName);
                    logger.debug("Specialization name = "+selectedClass+"/"+specializationName);
                    logger.trace("it has got "+specializationType.getProperties().size()+" properties.");

                    SpecializationModelSection.this.elementFigure.setIconSize(specializationType.getIconSize());
                    SpecializationModelSection.this.elementFigure.setIconLocation(specializationType.getIconLocation());
                    SpecializationModelSection.this.tblProperties.setInput(specializationType.getProperties());
                    SpecializationModelSection.this.tblProperties.refresh();
                    SpecializationModelSection.this.btnDeleteProperty.setEnabled(!specializationType.getProperties().isEmpty());
                    
                    SpecializationModelSection.this.elementFigure.select(specializationType.getFigure());
                } else {
                    logger.debug("Specialization name is empty");
                    SpecializationModelSection.this.elementFigure.setIconSize("");
                    SpecializationModelSection.this.elementFigure.setIconLocation("");
                    SpecializationModelSection.this.tblProperties.setInput(null);
                    SpecializationModelSection.this.tblProperties.refresh();
                    SpecializationModelSection.this.btnDeleteProperty.setEnabled(false);
                    
                    SpecializationModelSection.this.elementFigure.select(null);
                }
            }
        });


        this.btnNewSpecialization = new Button(parent, SWT.PUSH);
        this.btnNewSpecialization.setImage(SpecializationPlugin.NEW_ICON);
        this.btnNewSpecialization.setToolTipText("New specialization");
        this.btnNewSpecialization.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.comboSpecializationNames, 10);
        fd.right = new FormAttachment(this.comboSpecializationNames, 40, SWT.RIGHT);
        this.btnNewSpecialization.setLayoutData(fd);
        this.btnNewSpecialization.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent e) {
                InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), SpecializationPlugin.pluginTitle, "Specialization name:", "", new LengthValidator());
                if (dlg.open() == Window.OK) {

                    // User clicked OK, we verify that the name does not already exists
                    String newSpecializationName = dlg.getValue();
                    boolean alreadyExists = false;
                    for ( int index = 0; index < SpecializationModelSection.this.comboSpecializationNames.getItemCount(); ++index ) {
                        if ( newSpecializationName.equals(SpecializationModelSection.this.comboSpecializationNames.getItem(index)) ) {
                            SpecializationPlugin.popup(Level.WARN, "The specialization \""+newSpecializationName+"\" already exists.");
                            SpecializationModelSection.this.comboSpecializationNames.select(index);
                            alreadyExists = true;
                            break;
                        }
                    }

                    if ( !alreadyExists ) {
                        String clazz = SpecializationModelSection.this.exclusiveComponentLabels.getSelected().getLabel().getToolTipText();
                        SpecializationType specializationType = new SpecializationType(newSpecializationName);
                        SpecializationModelSection.this.specializationMap.addSpecializationType(clazz, specializationType);
                    }

                    SpecializationModelSection.this.comboSpecializationNames.add(newSpecializationName);    // the line is added at the end
                    SpecializationModelSection.this.comboSpecializationNames.select(SpecializationModelSection.this.comboSpecializationNames.getItemCount()-1);

                    setMetadata();
                }
            }

            @Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
        });

        this.btnEditSpecialization = new Button(parent, SWT.PUSH);
        this.btnEditSpecialization.setImage(SpecializationPlugin.EDIT_ICON);
        this.btnEditSpecialization.setToolTipText("Edit specialization");
        this.btnEditSpecialization.setEnabled(this.comboSpecializationNames.getItemCount()!=0);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.btnNewSpecialization, 10);
        fd.right = new FormAttachment(this.btnNewSpecialization, 40, SWT.RIGHT);
        this.btnEditSpecialization.setLayoutData(fd);
        this.btnEditSpecialization.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent e) {
                String oldSpecializationName = SpecializationModelSection.this.comboSpecializationNames.getText();
                InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), SpecializationPlugin.pluginTitle, "Specialization name:", oldSpecializationName, new LengthValidator());
                if (dlg.open() == Window.OK) {
                    // User clicked OK, we verify that the name does not already exists
                    String newSpecializationName = dlg.getValue();
                    for ( int index = 0; index < SpecializationModelSection.this.comboSpecializationNames.getItemCount(); ++index ) {
                        if ( newSpecializationName.equals(SpecializationModelSection.this.comboSpecializationNames.getItem(index)) ) {
                            SpecializationPlugin.popup(Level.WARN, "The specialization \""+newSpecializationName+"\" already exists.");
                            SpecializationModelSection.this.comboSpecializationNames.select(index);
                            return;
                        }
                    }

                    int oldNameIndex = 0;
                    for ( int index = 0; index < SpecializationModelSection.this.comboSpecializationNames.getItemCount(); ++index ) {
                        if ( oldSpecializationName.equals(SpecializationModelSection.this.comboSpecializationNames.getItem(index)) ) {
                            oldNameIndex = index;
                            break;
                        }
                    }

                    String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelected().getLabel().getToolTipText();
                    logger.debug("Renaming "+selectedClass+"/"+oldSpecializationName+ " to "+selectedClass+"/"+newSpecializationName);
                    SpecializationType specializationType = SpecializationModelSection.this.specializationMap.getSpecializationType(selectedClass, oldSpecializationName);
                    specializationType.setName(newSpecializationName);

                    SpecializationModelSection.this.comboSpecializationNames.remove(oldSpecializationName);
                    SpecializationModelSection.this.comboSpecializationNames.add(newSpecializationName, oldNameIndex);
                    SpecializationModelSection.this.comboSpecializationNames.select(oldNameIndex);

                    setMetadata();
                }
            }

            @Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
        });

        this.btnDeleteSpecialization = new Button(parent, SWT.PUSH);
        this.btnDeleteSpecialization.setImage(SpecializationPlugin.DELETE_ICON);
        this.btnDeleteSpecialization.setToolTipText("Delete specialization");
        this.btnDeleteSpecialization.setEnabled(this.comboSpecializationNames.getItemCount()!=0);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpecializationName, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.btnEditSpecialization, 10);
        fd.right = new FormAttachment(this.btnEditSpecialization, 40, SWT.RIGHT);
        this.btnDeleteSpecialization.setLayoutData(fd);
        this.btnDeleteSpecialization.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent e) {
                String selectedSpecialization = SpecializationModelSection.this.comboSpecializationNames.getText();
                int selectionIndex = SpecializationModelSection.this.comboSpecializationNames.getSelectionIndex();

                SpecializationModelSection.this.specializationMap.remove(selectedSpecialization);
                SpecializationModelSection.this.comboSpecializationNames.remove(selectedSpecialization);

                if ( SpecializationModelSection.this.tblProperties.getTable().getItemCount() == 0 )
                    SpecializationModelSection.this.btnDeleteSpecialization.setEnabled(false);
                else {
                    if ( selectionIndex == 0 )
                        SpecializationModelSection.this.comboSpecializationNames.select(0);
                    else
                        SpecializationModelSection.this.comboSpecializationNames.select(selectionIndex-1);
                }

                setMetadata();
            }

            @Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }

        });

        Label lblProperties = new Label(parent, SWT.NONE);
        lblProperties.setForeground(parent.getForeground());
        lblProperties.setBackground(parent.getBackground());
        lblProperties.setEnabled(false);
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
        fd = new FormData();
        fd.top = new FormAttachment(lblProperties, -5, SWT.TOP);
        fd.left = new FormAttachment(lblProperties, 5);
        fd.right = new FormAttachment(lblProperties, 430);
        fd.bottom = new FormAttachment(lblProperties, 100, SWT.BOTTOM);
        table.setLayoutData(fd);

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

                setMetadata();
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
                ((SpecializationProperty)element).setValue((String)value);
                SpecializationModelSection.this.tblProperties.update(element, null);

                setMetadata();
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
        fd.left = new FormAttachment(table, 5);
        fd.right = new FormAttachment(table, 40, SWT.RIGHT);
        this.btnNewProperty.setLayoutData(fd);
        this.btnNewProperty.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent e) {
                String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelected().getLabel().getToolTipText();
                SpecializationType type = SpecializationModelSection.this.specializationMap.getSpecializationType(selectedClass, SpecializationModelSection.this.comboSpecializationNames.getText());
                type.getProperties().add(new SpecializationProperty("new property",""));
                SpecializationModelSection.this.tblProperties.refresh();

                SpecializationModelSection.this.tblProperties.getTable().setSelection(SpecializationModelSection.this.tblProperties.getTable().getItemCount()-1);
                SpecializationModelSection.this.btnDeleteProperty.setEnabled(true);

                setMetadata();
            }

            @Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
        });

        this.btnDeleteProperty = new Button(parent, SWT.PUSH);
        this.btnDeleteProperty.setImage(SpecializationPlugin.DELETE_ICON);
        this.btnDeleteProperty.setToolTipText("Delete property");
        this.btnDeleteProperty.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.btnNewProperty, 5);
        fd.left = new FormAttachment(table, 5);
        fd.right = new FormAttachment(table, 40, SWT.RIGHT);
        this.btnDeleteProperty.setLayoutData(fd);
        this.btnDeleteProperty.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent e) {
                String selectedClass = SpecializationModelSection.this.exclusiveComponentLabels.getSelected().getLabel().getToolTipText();
                SpecializationType type = SpecializationModelSection.this.specializationMap.getSpecializationType(selectedClass, SpecializationModelSection.this.comboSpecializationNames.getText());
                int selectionIndex = SpecializationModelSection.this.tblProperties.getTable().getSelectionIndex();
                type.getProperties().remove(selectionIndex);
                SpecializationModelSection.this.tblProperties.refresh();

                if ( SpecializationModelSection.this.tblProperties.getTable().getItemCount() == 0 )
                    SpecializationModelSection.this.btnDeleteProperty.setEnabled(false);
                else {
                    if ( selectionIndex == 0 )
                        SpecializationModelSection.this.tblProperties.getTable().setSelection(0);
                    else
                        SpecializationModelSection.this.tblProperties.getTable().setSelection(selectionIndex-1);
                }

                setMetadata();
            }

            @Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
        });

        Label lblIcon = new Label(parent, SWT.NONE);
        lblIcon.setForeground(parent.getForeground());
        lblIcon.setBackground(parent.getBackground());
        lblIcon.setText("Icon:");
        lblIcon.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(table, 15);
        fd.left = new FormAttachment(lblSpecializationName, 0, SWT.LEFT);
        lblIcon.setLayoutData(fd);

        this.elementFigure = new ElementFigure(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblIcon, -5, SWT.TOP);
        fd.left = new FormAttachment(lblIcon, 10);
        this.elementFigure.setLayoutData(fd);
        
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
        return this.model;
    }

    @Override
    protected void setElement(Object element) {
        IArchimateModel selectedModel = (IArchimateModel)new Filter().adaptObject(element);
        logger.debug("Setting element to "+SpecializationPlugin.getDebugName(selectedModel));

        if(selectedModel == null) {
            logger.error("failed to get element for " + element); //$NON-NLS-1$
            this.specializationMap = null;
        } else {
            if ( selectedModel.getMetadata() == null ) {
                selectedModel.setMetadata(IArchimateFactory.eINSTANCE.createMetadata());
                this.specializationMap = null;
            }
            else {
                IProperty specializationsMetadata = selectedModel.getMetadata().getEntry("Specializations");
                if ( specializationsMetadata != null ) {
                    try {
                        Gson gson = new Gson();
                        this.specializationMap = gson.fromJson(specializationsMetadata.getValue(), SpecializationMap.class);
                    } catch (JsonSyntaxException e) {
                        SpecializationPlugin.popup(Level.FATAL, "An exception occured while retrieving the specialization metadata from the model.\n\nThe specialization plugin has been deactivated for this model.",e);
                        //TODO: store the exception in the model and deactivate the plugin for that model
                        //TODO: add an option to erase the specializations metadata and startup again from an empty configuration
                    }
                }
            }
        }

        // TODO: the rest of the code assumes the variable is not null ! add check for null everywhere
        if ( this.specializationMap == null )
            this.specializationMap = new SpecializationMap();

        if ( selectedModel != this.model ) {
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

            this.model = selectedModel;
        }
    }

    void setMetadata() {
        SpecializationUpdateMetadataCommand command = new SpecializationUpdateMetadataCommand(this.model, this.specializationMap);
        ((CommandStack)SpecializationModelSection.this.model.getAdapter(CommandStack.class)).execute(command);

        //TODO : sort the specialization names

        if ( command.getException() != null )
            SpecializationPlugin.popup(Level.ERROR, "Failed to save specializations to model's metadata.", command.getException());
    }

    class LengthValidator implements IInputValidator {
        @Override public String isValid(String newText) {
            // we check the string length
            int len = newText.length();
            if (len < 1) return " ";
            if (len > 50) return "Too long (50 chars max)";

            // we check that the string does not contain special chars
            if ( newText.contains("\n") ) return "Must not contain newline";
            if ( newText.contains("\t") ) return "Must not contain tab";

            return null;
        }
    }
}
