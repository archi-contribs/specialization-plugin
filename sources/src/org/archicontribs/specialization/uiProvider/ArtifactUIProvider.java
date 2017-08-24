/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider;

import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.figure.ArtifactFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;


/**
 * Artifact UI Provider
 * 
 * @author Herve Jouin
 */
public class ArtifactUIProvider extends com.archimatetool.editor.ui.factory.elements.ArtifactUIProvider {
    @Override
    public EditPart createEditPart() {
            // we override the standard method because we want our NodeFigure class to be called
        return new ArchimateElementEditPart(ArtifactFigure.class);
    }
    
    /**
     * Gets the icon image from the component's properties. If not found, return the default one.
     */
    @Override
    public Image getImage() {
    	StackTraceElement[] stack = new Exception().getStackTrace();
    	String iconName = null;

    	// if we are in the model tree, we show the image only if the showImagesInTree flag is set
        if ( stack[2].getClassName().startsWith("com.archimatetool.editor.views.tree") ) {
        	if ( SpecializationPlugin.showImagesInTree() )
        		iconName = SpecializationPlugin.getIconName(instance, true);
        }
        // if we are in a view, we show the image only if the showImagesInView flag is set
        else if ( stack[2].getClassName().startsWith("com.archimatetool.editor.diagram") ) {
        	if ( SpecializationPlugin.showImagesInView() )
        		iconName = SpecializationPlugin.getIconName(instance, true);
        }
        // in all the other parts of Arch, we stick to the default icon
        
        return iconName==null ? super.getImage() : getImageWithUserFillColor(iconName);
    }
}
