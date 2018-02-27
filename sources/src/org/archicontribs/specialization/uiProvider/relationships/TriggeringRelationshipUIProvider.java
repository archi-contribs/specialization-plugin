/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.relationships;

import org.archicontribs.specialization.uiProvider.relationships.figures.TriggeringConnectionFigure;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.EditPart;

import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.model.IArchimatePackage;



/**
 * Triggering Relationship UI Provider
 * 
 * @author Herve Jouin
 */
public class TriggeringRelationshipUIProvider extends com.archimatetool.editor.ui.factory.relationships.TriggeringRelationshipUIProvider {

    @Override
    public EClass providerFor() {
        return IArchimatePackage.eINSTANCE.getTriggeringRelationship();
    }
    
    @Override
    public EditPart createEditPart() {
        return new ArchimateRelationshipEditPart(TriggeringConnectionFigure.class);
    }
}
