/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.relationships;

import org.archicontribs.specialization.uiProvider.relationships.figures.AggregationConnectionFigure;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.EditPart;

import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.model.IArchimatePackage;

/**
 * Aggregation Relationship UI Provider
 * 
 * @author Herve Jouin
 */
public class AggregationRelationshipUIProvider extends com.archimatetool.editor.ui.factory.relationships.AggregationRelationshipUIProvider {

    @Override
    public EClass providerFor() {
        return IArchimatePackage.eINSTANCE.getAggregationRelationship();
    }
    
    @Override
    public EditPart createEditPart() {
        return new ArchimateRelationshipEditPart(AggregationConnectionFigure.class);
    }
}
