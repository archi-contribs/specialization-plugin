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
        logger.debug("Getting image");
        
        String iconName = null;
        
        if ( SpecializationPlugin.mustShowIcon(instance) )
            iconName = SpecializationPlugin.getIconName(instance, true);
        
        return iconName==null ? super.getImage() : getImageWithUserFillColor(iconName);
    }
}
