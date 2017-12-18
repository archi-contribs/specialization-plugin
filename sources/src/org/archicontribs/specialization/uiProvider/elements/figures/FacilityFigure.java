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
 * Facility Figure
 * 
 * @author Hervé Jouin
 */
public class FacilityFigure extends com.archimatetool.editor.diagram.figures.elements.FacilityFigure {
    private static SpecializationLogger logger = new SpecializationLogger(FacilityFigure.class);
    
    @Override
    protected void drawIcon(Graphics graphics) {
        Image image = ObjectUIFactory.INSTANCE.getProvider(getDiagramModelObject()).getImage();
        
        if ( image == null )
            logger.error("Image not found");
        else {
        	String iconLocation = SpecializationPlugin.getPropertyValue(getDiagramModelObject(), "icon location");
            
        	int defaultX = bounds.x + bounds.width - image.getBounds().width - SpecializationPlugin.getIconMargin();
            int defaultY = bounds.y + SpecializationPlugin.getIconMargin();
			int x;
			int y;
			
        	if ( iconLocation != null && !iconLocation.isEmpty() ) {
        		if ( logger.isTraceEnabled() ) logger.trace(SpecializationPlugin.getFullName(getDiagramModelObject())+": found icon location = "+iconLocation);
        		String[] parts = iconLocation.split(",");
        		try {
        			if ( parts[0].equals("center") )
        				x = bounds.x+(bounds.width-image.getBounds().width)/2;
        			else if ( Integer.parseInt(parts[0]) >= 0 )
        				x = bounds.x + Integer.parseInt(parts[0]);
        			else
        				x = bounds.x + bounds.width - image.getBounds().width + Integer.parseInt(parts[0]);
        			
        			if ( parts[1].equals("center") )
        				y = bounds.y+(bounds.height-image.getBounds().height)/2;
        			else if ( Integer.parseInt(parts[1]) >= 0 )
        				y = bounds.y + Integer.parseInt(parts[1]);
        			else
        				y = bounds.y + bounds.height - image.getBounds().height + Integer.parseInt(parts[1]);
        		} catch ( Exception e) {
        			logger.error("Malformed location. Shoule be under the form \"x,y\"");
        			x=defaultX;
        			y=defaultY;
        		}
        	} else {
    			x=defaultX;
    			y=defaultY;
        	}
			if ( logger.isTraceEnabled() ) logger.trace(SpecializationPlugin.getFullName(getDiagramModelObject())+": setting image location to "+(x-bounds.x)+","+(y-bounds.y));
            graphics.drawImage(image, new Point(x, y));
        }
    }
    
    @Override
    protected void setText() {
        String labelName = null;
        
        if ( SpecializationPlugin.mustReplaceLabel(getDiagramModelObject()) )
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