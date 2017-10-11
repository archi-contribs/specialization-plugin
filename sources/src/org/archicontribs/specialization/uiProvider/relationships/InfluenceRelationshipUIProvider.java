/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.relationships;

import org.archicontribs.specialization.uiProvider.relationships.connections.InfluenceConnectionFigure;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.EditPart;

import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.model.IArchimatePackage;



/**
 * Influence Relationship UI Provider
 * 
 * @author Hervé Jouin
 */
public class InfluenceRelationshipUIProvider extends com.archimatetool.editor.ui.factory.relationships.InfluenceRelationshipUIProvider {

    public EClass providerFor() {
        return IArchimatePackage.eINSTANCE.getInfluenceRelationship();
    }
    
    @Override
    public EditPart createEditPart() {
        return new ArchimateRelationshipEditPart(InfluenceConnectionFigure.class);
    }
}
