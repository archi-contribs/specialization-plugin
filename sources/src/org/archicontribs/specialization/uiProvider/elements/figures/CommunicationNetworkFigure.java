/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.elements.figures;

import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.text.TextFlow;


/**
 * Communication Network Figure
 * 
 * @author Herve Jouin
 */
public class CommunicationNetworkFigure extends com.archimatetool.editor.diagram.figures.elements.CommunicationNetworkFigure {
    
    @Override
    protected void drawIcon(Graphics graphics) {
    	if ( !SpecializationPlugin.drawIcon(getDiagramModelObject(), graphics, this.bounds) )
			super.drawIcon(graphics);
    }
    
    @Override
    protected void setText() {
        String labelName = SpecializationPlugin.getLabelName(getDiagramModelObject());
        
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