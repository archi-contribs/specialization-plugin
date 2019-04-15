package org.archicontribs.specialization.types;

import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.ArchiPlugin;
import com.archimatetool.editor.ui.IArchiImages;
import com.archimatetool.editor.ui.ImageFactory;

public class ArchimateIcons {
    public static Image getImage(String clazz) {
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
            case "CONSTRAINT": return ImageFactory.getImage(IArchiImages.ICON_CONSTRAINT);
            case "CONTRACT": return ImageFactory.getImage(IArchiImages.ICON_CONTRACT);
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

    public static Color getColor(String clazz) {
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
    
    public static boolean canChangeIcon(String clazz) {
        ImageFactory ImageFactory = new ImageFactory(ArchiPlugin.INSTANCE);
        switch (clazz.toUpperCase()) {
            case "FOLDER":
            case "MODEL":
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
            	return false;
            
            case "APPLICATIONCOLLABORATION":
            case "APPLICATIONCOMPONENT": 
            case "APPLICATIONEVENT": 
            case "APPLICATIONFUNCTION": 
            case "APPLICATIONINTERACTION": 
            case "APPLICATIONINTERFACE": 
            case "APPLICATIONPROCESS": 
            case "APPLICATIONSERVICE": 
            case "ARTIFACT":
            case "ASSESSMENT":
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
            case "CAPABILITY":
            case "COMMUNICATIONNETWORK":
            case "CONSTRAINT":
            case "CONTRACT":
            case "COURSEOFACTION":
            case "DATAOBJECT":
            case "DELIVERABLE":
            case "DEVICE":
            case "DISTRIBUTIONNETWORK":
            case "DRIVER":
            case "EQUIPMENT":
            case "FACILITY":
            case "GAP":
            case "GOAL":
            case "GROUPING":
            case "IMPLEMENTATIONEVENT":
            case "JUNCTION":
            case "LOCATION":
            case "MATERIAL":
            case "MEANING":
            case "NODE":
            case "OUTCOME":
            case "PATH":
            case "PLATEAU":
            case "PRINCIPLE":
            case "PRODUCT":
            case "REPRESENTATION":
            case "REQUIREMENT":
            case "RESOURCE":
            case "STAKEHOLDER":
            case "SYSTEMSOFTWARE":
            case "TECHNOLOGYCOLLABORATION":
            case "TECHNOLOGYEVENT":
            case "TECHNOLOGYFUNCTION":
            case "TECHNOLOGYINTERFACE":
            case "TECHNOLOGYINTERACTION":
            case "TECHNOLOGYPROCESS":
            case "TECHNOLOGYSERVICE":
            case "VALUE":
            case "WORKPACKAGE":
            	return true;

            default:
                throw new IllegalArgumentException("The class '" + clazz + "' is not a valid class"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
