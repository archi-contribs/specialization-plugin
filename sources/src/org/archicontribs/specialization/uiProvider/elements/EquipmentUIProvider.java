/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.elements;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;


/**
 * Equipment UI Provider
 * 
 * @author Herve Jouin
 */
public class EquipmentUIProvider extends com.archimatetool.editor.ui.factory.elements.EquipmentUIProvider {
    private static final SpecializationLogger logger = new SpecializationLogger(EquipmentUIProvider.class);
    
    @Override
    public EditPart createEditPart() {
            // we override the standard method because we want our EquipmentFigure class to be called
        return new ArchimateElementEditPart(org.archicontribs.specialization.uiProvider.elements.figures.EquipmentFigure.class);
    }
    
    /**
     * Gets the icon image from the component's properties. If not found, return the default one.
     */
    @Override
    public Image getImage() {
        logger.debug("Getting image");
        
        String iconName = null;
        
        if ( SpecializationPlugin.mustReplaceIcon(instance) )
            iconName = SpecializationPlugin.getIconName(instance, true);
        
        return iconName==null ? super.getImage() : getImageWithUserFillColor(iconName);
    }
}