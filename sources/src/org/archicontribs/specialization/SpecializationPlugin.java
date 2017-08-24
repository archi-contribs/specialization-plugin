package org.archicontribs.specialization;

import java.io.File;

import org.apache.log4j.Level;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.archimatetool.editor.ui.IArchiImages;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

/**
 * Specialization plugin for Archi, the Archimate modeler
 * 
 * The FormPlugin class alows to change the elements' icons in the Archi views.
 * 
 * @author Herve Jouin
 *
 * v0.1 :		24/08/2017		beta version
 */
public class SpecializationPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.archicontribs.specialization";
	public static SpecializationPlugin INSTANCE;
	
	public static final String pluginVersion = "0.1";
	public static final String pluginName = "SpecializationPlugin";
	public static final String pluginTitle = "Specialization plugin v" + pluginVersion;
	
	/**
	 * PreferenceStore allowing to store the plugin configuration.
	 */
	private static IPreferenceStore preferenceStore = null;
	
	private static SpecializationLogger logger;

	public SpecializationPlugin() {
		INSTANCE = this;
		
		preferenceStore = this.getPreferenceStore();
		preferenceStore.setDefault("progressWindow",	        "showAndWait");
		preferenceStore.setDefault("checkForUpdateAtStartup",   false);
		preferenceStore.setDefault("loggerMode",		        "disabled");
		preferenceStore.setDefault("loggerLevel",		        "INFO");
		preferenceStore.setDefault("loggerFilename",	        System.getProperty("user.home")+File.separator+pluginName+".log");
		preferenceStore.setDefault("loggerExpert",		        "log4j.rootLogger                               = INFO, stdout, file\n"+
																"\n"+
																"log4j.appender.stdout                          = org.apache.log4j.ConsoleAppender\n"+
																"log4j.appender.stdout.Target                   = System.out\n"+
																"log4j.appender.stdout.layout                   = org.apache.log4j.PatternLayout\n"+
																"log4j.appender.stdout.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-30.30C{1} %m%n\n"+
																"\n"+
																"log4j.appender.file                            = org.apache.log4j.FileAppender\n"+
																"log4j.appender.file.ImmediateFlush             = true\n"+
																"log4j.appender.file.Append                     = false\n"+
																"log4j.appender.file.Encoding                   = UTF-8\n"+
																"log4j.appender.file.File                       = "+(System.getProperty("user.home")+File.separator+pluginName+".log").replace("\\", "\\\\")+"\n"+
																"log4j.appender.file.layout                     = org.apache.log4j.PatternLayout\n"+
																"log4j.appender.file.layout.ConversionPattern   = %d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-30.30C{1} %m%n");
		preferenceStore.setDefault("iconMargin",                2);
		preferenceStore.setDefault("showImagesInView",          true);
		preferenceStore.setDefault("showImagesInTree",          true);
		logger = new SpecializationLogger(SpecializationPlugin.class);
		logger.info(pluginTitle+" initialized.");
	}
	
	public static int getIconMargin() {
	    return preferenceStore.getInt("iconMargin");
	}
	
	public static boolean showImagesInView() {
	    return preferenceStore.getBoolean("showImagesInView");
	}
	
	public static boolean showImagesInTree() {
	    return preferenceStore.getBoolean("showImagesInTree");
	}
	
	public static boolean shouldShowImages() { return false; };
	
	/**
	 * Shows up an on screen popup, displaying the message (and the exception message if any) and wait for the user to click on the "OK" button
	 */
	public static void popup(Level level, String msg) {
		popup(level, msg, null);
	}
	
	/**
	 * Shows up an on screen popup, displaying the message (and the exception message if any) and wait for the user to click on the "OK" button<br>
	 * The exception stacktrace is also printed on the standard error stream
	 */
	public static void popup(Level level, String msg, Exception e) {
		String popupMessage = msg;
		logger.log(SpecializationPlugin.class, level, msg, e);

		if ( e != null ) {
			if ( SpecializationPlugin.areEqual(e.getMessage(), msg)) {
				popupMessage += "\n\n" + e.getMessage();
			} else {
				popupMessage += "\n\n" + e.getClass().getName();
			}
		}
		
		switch ( level.toInt() ) {
			case Level.FATAL_INT :
			case Level.ERROR_INT :
				MessageDialog.openError(null, SpecializationPlugin.pluginTitle, popupMessage);
				break;
			case Level.WARN_INT :
				MessageDialog.openWarning(null, SpecializationPlugin.pluginTitle, popupMessage);
				break;
			default :
				MessageDialog.openInformation(null, SpecializationPlugin.pluginTitle, popupMessage);
				break;
		}
	}
	
	/**
	 * Check if two strings are equals<br>
	 * Replaces string.equals() to avoid nullPointerException
	 */
	public static boolean areEqual(String str1, String str2) {
		if ( str1 == null )
			return str2 == null;

		if ( str2 == null )
			return false;			// as str1 cannot be null at this stage

		return str1.equals(str2);
	}
	
	/**
	 * Calculates the full name of an EObject 
	 * @return getclass().getSimpleName()+":\""+getName()+"\""
	 */
	public static String getFullName(EObject obj) {
		StringBuilder objName = new StringBuilder(obj.getClass().getSimpleName());
		objName.append(":\""+((INameable)obj).getName()+"\"");
		return obj.toString();
	}
	
	/**
	 * Calculates the debug name of an EObject
	 * @return getclass().getSimpleName()+":\""+getName()+"\"("+getId()+")"
	 */
	public static String getDebugName(EObject obj) {
		StringBuilder objName = new StringBuilder(getFullName(obj));
		objName.append("("+((IIdentifier)obj).getId()+")");
		return objName.toString();
	}
	
	/**
	 * Retrieves the icon name from the EObject properties
	 * @param obj
	 */
	public static String getIconName(EObject obj, boolean mustExist) {
	    if ( obj != null ) {
    	    if ( obj instanceof IDiagramModelArchimateObject )
                obj = ((IDiagramModelArchimateObject)obj).getArchimateConcept();
        
            if ( obj instanceof IProperties ) { 
                for ( IProperty prop: ((IProperties)obj).getProperties() ) {
                    if ( SpecializationPlugin.areEqual(prop.getKey(), "icon") ) {
                        String iconName = "/img/"+prop.getValue();
                        if ( IArchiImages.ImageFactory.getImage(iconName) != null ) {
                            logger.trace("found icon \""+iconName+"\"");
                            return iconName;
                        }
                        logger.trace("missing icon \""+iconName+"\"");
                        // we continue the loop in case there is another "icon" property
                    }
                }
            }
	    }
        return null;
	}
}
