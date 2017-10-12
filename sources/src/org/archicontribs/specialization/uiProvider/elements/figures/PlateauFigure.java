/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.elements.figures;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.ui.factory.ObjectUIFactory;
import com.archimatetool.model.IDiagramModelArchimateObject;

/**
 * Plateau Figure
 * 
 * @author Hervé Jouin
 */
public class PlateauFigure extends com.archimatetool.editor.diagram.figures.elements.PlateauFigure {
    private static SpecializationLogger logger = new SpecializationLogger(PlateauFigure.class);
    
    @Override
    protected void drawIcon(Graphics graphics) {
        Image image = ObjectUIFactory.INSTANCE.getProvider(getDiagramModelObject()).getImage();
        
        if ( image == null )
            logger.error("Image not found");
        else {
            int x = bounds.x + bounds.width - image.getBounds().width - SpecializationPlugin.getIconMargin();
            int y = bounds.y + SpecializationPlugin.getIconMargin();
            graphics.drawImage(image, new Point(x, y));
        }
    }
    
    @Override
    protected void setText() {
        String labelName = null;
        
        if ( SpecializationPlugin.mustShowLabel(getDiagramModelObject()) )
            labelName = SpecializationPlugin.getLabelName(((IDiagramModelArchimateObject)getDiagramModelObject()).getArchimateElement());
        
        if ( labelName==null )
            super.setText();
        else {
            if(getTextControl() instanceof TextFlow) {
                ((TextFlow)getTextControl()).setText(labelName);
            }
            else if(getTextControl() instanceof Label) {
                ((Label)getTextControl()).setText(labelName);
            }
        }
    }
}