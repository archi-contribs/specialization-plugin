package org.archicontribs.specialization.uiProvider.relationships.figures;

import org.archicontribs.specialization.SpecializationPlugin;

public class ServingConnectionFigure extends com.archimatetool.editor.diagram.figures.connections.ServingConnectionFigure {
    @Override
    protected void setConnectionText() {
        String labelName = null;
        
        if ( SpecializationPlugin.mustShowLabel(getModelConnection()) )
            labelName = SpecializationPlugin.getLabelName(getModelConnection().getArchimateRelationship());
        
        if ( labelName==null )
            getConnectionLabel().setText(getModelConnection().getArchimateRelationship().getName());
        else
            getConnectionLabel().setText(labelName);
    }
}