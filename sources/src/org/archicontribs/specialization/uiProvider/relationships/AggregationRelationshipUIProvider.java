/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.uiProvider.relationships;

import org.archicontribs.specialization.uiProvider.relationships.connections.AggregationConnectionFigure;
import org.eclipse.gef.EditPart;

import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;

/**
 * Aggregation Relationship UI Provider
 * 
 * @author Hervé Jouin
 */
public class AggregationRelationshipUIProvider extends com.archimatetool.editor.ui.factory.relationships.AggregationRelationshipUIProvider {

    @Override
    public EditPart createEditPart() {
        return new ArchimateRelationshipEditPart(AggregationConnectionFigure.class);
    }
}
