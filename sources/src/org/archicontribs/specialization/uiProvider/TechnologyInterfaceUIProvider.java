/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider;

import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.figure.InterfaceFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;


/**
 * TechnologyInterface UI Provider
 * 
 * @author Herve Jouin
 */
public class TechnologyInterfaceUIProvider extends com.archimatetool.editor.ui.factory.elements.TechnologyInterfaceUIProvider {
    @Override
    public EditPart createEditPart() {
            // we override the standard method because we want our TechnologyInterfaceFigure class to be called
        return new ArchimateElementEditPart(InterfaceFigure.class);
    }
    
    /**
     * Gets the icon name from the properties. If not found, return the default one.
     */
    @Override
    public Image getImage() {
        String iconName = SpecializationPlugin.getIconName(instance, true);
        
        if ( iconName == null )
            return super.getImage();

        return getImageWithUserFillColor(iconName);
    }
}