/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.elements;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.swt.graphics.Image;


/**
 * Junction UI Provider
 * 
 * @author Herve Jouin
 */
public class JunctionUIProvider extends com.archimatetool.editor.ui.factory.elements.JunctionUIProvider {
    private static final SpecializationLogger logger = new SpecializationLogger(JunctionUIProvider.class);
    
    // we do not override the createEditPart of the junction.
    
    /**
     * Gets the icon image from the component's properties. If not found, return the default one.
     */
    @Override
    public Image getImage() {
    	if ( SpecializationPlugin.mustReplaceIcon(instance) ) {
    	    Image image = SpecializationPlugin.getImage(instance);
            if ( image != null ) {
                if ( logger.isTraceEnabled() ) logger.trace(SpecializationPlugin.getFullName(instance)+": Displaying custom icon");
                return image;
            }
    	}
    	
        logger.trace(SpecializationPlugin.getFullName(instance)+": Displaying default icon");        	
    	return super.getImage();
    }
}
