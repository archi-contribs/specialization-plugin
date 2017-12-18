package org.archicontribs.specialization;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

public class SpecializationVariable {
    private static final SpecializationLogger logger = new SpecializationLogger(SpecializationVariable.class);
    
    private static String variableSeparator = ":";
    
    public static void setVariableSeparator(String separator) {
    	variableSeparator = separator;
    }
    
    /**
     * Expands an expression containing variables<br>
     * It may return an empty string, but never a null value
     */
    public static String expand(String expression, EObject eObject) {
        if ( expression == null )
            return "";

        StringBuffer sb = new StringBuffer(expression.length());

        Pattern pattern = Pattern.compile("(\\$\\{([^${}]|(?1))+\\})");
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String variable = matcher.group(1);
            //if ( logger.isTraceEnabled() ) logger.trace("   matching "+variable);
            String variableValue = getVariable(variable, eObject);
            if ( variableValue == null )
                variableValue = "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    /**
     * Gets the real EObject that the variable refers to (as the variable can change the EObject using its scope)
     */
    public static EObject getReferedEObject(String variable, EObject eObject) throws RuntimeException {
        if ( logger.isTraceEnabled() ) logger.trace("         getting refered EObject from variable \""+variable+"\" (source object = "+SpecializationPlugin.getDebugName(eObject)+")");

        if ( variable == null || eObject == null )
        	return null;
        
        // we check that the variable provided is a string enclosed between "${" and "}"
        if ( !variable.startsWith("${") ) {
        	if ( logger.isTraceEnabled() ) logger.trace("         --> does not start with \"${\"");
            return null;
        }
        if ( !variable.endsWith("}") ) {
            if ( logger.isTraceEnabled() ) logger.trace("         --> does not end with \"}\"");
            return null;
        }
        
        String variableName = expand(variable.substring(2, variable.length()-1), eObject);

        //TODO : add a preference to choose between silently ignore or raise an error
        switch ( variableName.toLowerCase() ) {
            case "class" :
            case "documentation" :
            case "id" :
            case "name" :
            	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                return eObject;

            case "void":
            case "username":
                return null;
            
            /* TODO: add view screenshot support
            case "screenshot":
            	// at the moment, screenshots are allowed on views only
            	if ( eObject instanceof IDiagramModel ) {
                	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                    return eObject;
                }
            	throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not a DiagramModel ("+eObject.getClass().getSimpleName()+").");
            */
            default :
                    // check for ${property:xxx}
                if ( variableName.toLowerCase().startsWith("property"+variableSeparator) ) {
                	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                    return eObject;
                }

                    // check for ${view:xxx}
                else if ( variableName.toLowerCase().startsWith("view"+variableSeparator) ) {
                    if ( eObject instanceof IDiagramModel ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                        return eObject;
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("            --> "+SpecializationPlugin.getDebugName(((IDiagramModelArchimateObject)eObject).getDiagramModel()));
                        return ((IDiagramModelArchimateObject)eObject).getDiagramModel();
                    }
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not part of a DiagramModel ("+eObject.getClass().getSimpleName()+").");
                }

                    // check for ${model:xxx}
                else if ( variableName.toLowerCase().startsWith("model"+variableSeparator) ) {
                    if ( eObject instanceof IArchimateModelObject ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> "+SpecializationPlugin.getDebugName(((IArchimateModelObject)eObject).getArchimateModel()));
                        return ((IArchimateModelObject)eObject).getArchimateModel();
                    }
                    else if ( eObject instanceof IDiagramModelComponent ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> "+SpecializationPlugin.getDebugName(((IDiagramModelComponent)eObject).getDiagramModel().getArchimateModel()));
                        return  ((IDiagramModelComponent)eObject).getDiagramModel().getArchimateModel();
                    }
                    else if ( eObject instanceof IArchimateModel ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                        return eObject;
                    }
                    
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as we failed to get the object's model ("+SpecializationPlugin.getDebugName(eObject)+").");
                }
                
                    // check for ${source:xxx}
                else if ( variableName.toLowerCase().startsWith("source"+variableSeparator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }

                    if ( obj instanceof IArchimateRelationship ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> "+SpecializationPlugin.getDebugName(((IArchimateRelationship)obj).getSource()));
                        return ((IArchimateRelationship)obj).getSource();
                    }
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not a relationship.");
                }
                    
                    // check for ${target:xxx}
                else if ( variableName.toLowerCase().startsWith("target"+variableSeparator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }
                    
                    if ( obj instanceof IArchimateRelationship ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> "+SpecializationPlugin.getDebugName(((IArchimateRelationship)obj).getTarget()));
                        return ((IArchimateRelationship)obj).getTarget();
                    }
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not a relationship.");
                }
        }
        throw new RuntimeException("Unknown variable \""+variableName+"\" ("+variable+")");
    }
    
    /**
     * Gets the variable without its scope
     */
    public static String getUnscoppedVariable(String variable, EObject eObject) {
    	if ( variable == null || eObject == null )
    		return null;
    	
        // we check that the variable provided is a string enclosed between "${" and "}"
        if ( !variable.startsWith("${") || !variable.endsWith("}") )
            return null;
        
        String variableName = expand(variable.substring(2, variable.length()-1), eObject);

	        // check for ${property:xxx}
	    if ( variableName.toLowerCase().startsWith("view"+variableSeparator) )
	    	return getUnscoppedVariable("${"+variableName.substring(5)+"}", eObject);
	    else if ( variableName.toLowerCase().startsWith("model"+variableSeparator) )
	    	return getUnscoppedVariable("${"+variableName.substring(6)+"}", eObject);
	    else if ( variableName.toLowerCase().startsWith("source"+variableSeparator) )
	    	return getUnscoppedVariable("${"+variableName.substring(7)+"}", eObject);
	    else if ( variableName.toLowerCase().startsWith("target"+variableSeparator) )
	    	return getUnscoppedVariable("${"+variableName.substring(7)+"}", eObject);
	    else return "${"+variableName+"}";
    }
    
    /**
     * Gets the value of the variable<br>
     * can return a null value in case the property does not exist. This way it is possible to distinguish between empty value and null value
     */
    public static String getVariable(String variable, EObject eObject) throws RuntimeException  {
        if ( logger.isTraceEnabled() ) logger.trace("         getting variable \""+variable+"\"");

        // we check that the variable provided is a string enclosed between "${" and "}"
        if ( !variable.startsWith("${") || !variable.endsWith("}") )
            throw new RuntimeException("The expression \""+variable+"\" is not a variable (it should be enclosed between \"${\" and \"}\")");
        
        String variableName = expand(variable.substring(2, variable.length()-1), eObject);

        //TODO : add a preference to choose between silently ignore or raise an error
        switch ( variableName.toLowerCase() ) {
            case "class" :
                if (eObject instanceof IDiagramModelArchimateObject) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDiagramModelArchimateObject)eObject).getArchimateElement().getClass().getSimpleName() +"\"");
                    return ((IDiagramModelArchimateObject)eObject).getArchimateElement().getClass().getSimpleName();
                }
                if (eObject instanceof IDiagramModelArchimateConnection) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getClass().getSimpleName() +"\"");
                    return ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getClass().getSimpleName();
                }
                if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ eObject.getClass().getSimpleName() +"\"");
                return eObject.getClass().getSimpleName();

            case "id" :
                if (eObject instanceof IIdentifier) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IIdentifier)eObject).getId() +"\"");
                    return ((IIdentifier)eObject).getId();
                }
                throw new RuntimeException("Cannot get variable \""+variable+"\" as the object does not an ID ("+eObject.getClass().getSimpleName()+").");

            case "documentation" :
                if (eObject instanceof IDiagramModelArchimateObject) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDiagramModelArchimateObject)eObject).getArchimateElement().getDocumentation() +"\"");
                    return ((IDiagramModelArchimateObject)eObject).getArchimateElement().getDocumentation();
                }
                if (eObject instanceof IDiagramModelArchimateConnection) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getDocumentation() +"\"");
                    return ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getDocumentation();
                }
                if (eObject instanceof IDocumentable) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDocumentable)eObject).getDocumentation() +"\"");
                    return ((IDocumentable)eObject).getDocumentation();
                }
                throw new RuntimeException("Cannot get variable \""+variable+"\" as the object does not have a documentation ("+eObject.getClass().getSimpleName()+").");

            case "void":
                if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \"\"");
                return "";
                
            case "name" :
                if (eObject instanceof INameable) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((INameable)eObject).getName() +"\"");
                    return ((INameable)eObject).getName();
                }
                throw new RuntimeException("cannot get variable \""+variable+"\" as the object does not have a name ("+eObject.getClass().getSimpleName()+").");

            case "username":
            	return System.getProperty("user.name");
            	
            /* TODO: add view screenshot support
            case "screenshot":
            	if ( eObject instanceof IDiagramModel ) {
            		return SpecializationPlugin.imageToString(DiagramUtils.createImage((IDiagramModel)eObject, 1.0, 2));
            	}
            	throw new RuntimeException("cannot get variable \""+variable+"\" as the object is not a view ("+eObject.getClass().getSimpleName()+").");
            */
            default :
            		// check for ${date:format}
            	if ( variableName.toLowerCase().startsWith("date"+variableSeparator)) {
            		String format = variableName.substring(5);
            		DateFormat df = new SimpleDateFormat(format);
            		Date now = Calendar.getInstance().getTime();
            		return df.format(now);
            	}
            	
                    // check for ${property:xxx}
            	else if ( variableName.toLowerCase().startsWith("property"+variableSeparator) ) {
                    if ( eObject instanceof IDiagramModelArchimateObject )
                        eObject = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    if ( eObject instanceof IDiagramModelArchimateConnection )
                        eObject = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    if ( eObject instanceof IProperties ) {
                        String propertyName = variableName.substring(9);
                        for ( IProperty property: ((IProperties)eObject).getProperties() ) {
                            if ( SpecializationPlugin.areEqual(property.getKey(),propertyName) ) {
                                if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ property.getValue() +"\"");
                                return property.getValue();
                            }
                        }
                        if ( logger.isTraceEnabled() ) logger.trace("         ---> value is null");
                        return null;
                    }
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as the object does not have properties ("+eObject.getClass().getSimpleName()+").");
                }

                    // check for ${view:xxx}
                else if ( variableName.toLowerCase().startsWith("view"+variableSeparator) ) {
                    if ( eObject instanceof IDiagramModel ) {
                        return getVariable("${"+variableName.substring(5)+"}", eObject);
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                        return getVariable("${"+variableName.substring(5)+"}", ((IDiagramModelArchimateObject)eObject).getDiagramModel());
                    }
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not part of a DiagramModel ("+eObject.getClass().getSimpleName()+").");
                }

                    // check for ${model:xxx}
                else if ( variableName.toLowerCase().startsWith("model"+variableSeparator) ) {
                    if ( eObject instanceof IArchimateModelObject ) {
                        return getVariable("${"+variableName.substring(6)+"}", ((IArchimateModelObject)eObject).getArchimateModel());
                    }
                    else if ( eObject instanceof IDiagramModelComponent ) {
                        return getVariable("${"+variableName.substring(6)+"}", ((IDiagramModelComponent)eObject).getDiagramModel().getArchimateModel());
                    }
                    else if ( eObject instanceof IArchimateModel ) {
                        return getVariable("${"+variableName.substring(6)+"}", eObject);
                    }
                    
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as we failed to get the object's model ("+eObject.getClass().getSimpleName()+").");
                }
                
                    // check for ${source:xxx}
                else if ( variableName.toLowerCase().startsWith("source"+variableSeparator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }

                    if ( obj instanceof IArchimateRelationship ) {
                        return getVariable("${"+variableName.substring(7)+"}", ((IArchimateRelationship)obj).getSource());
                    }
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not a relationship.");
                }
                    
                    // check for ${target:xxx}
                else if ( variableName.toLowerCase().startsWith("target"+variableSeparator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }
                    
                    if ( obj instanceof IArchimateRelationship ) {
                        return getVariable("${"+variableName.substring(7)+"}", ((IArchimateRelationship)obj).getTarget());
                    }
                    throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not a relationship.");
                }
        }
        throw new RuntimeException("Unknown variable \""+variableName+"\" ("+variable+")");
    }
}
