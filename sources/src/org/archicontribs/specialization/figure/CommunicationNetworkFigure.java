package org.archicontribs.specialization.figure;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.ui.factory.ObjectUIFactory;

/**
 * CommunicationNetworkFigure
 * 
 * @author HJOUIN
 */
public class CommunicationNetworkFigure extends com.archimatetool.editor.diagram.figures.elements.CommunicationNetworkFigure {
    private static SpecializationLogger logger = new SpecializationLogger(CommunicationNetworkFigure.class);
    
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
}