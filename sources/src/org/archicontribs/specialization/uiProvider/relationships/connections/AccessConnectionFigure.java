package org.archicontribs.specialization.uiProvider.relationships.connections;

import org.archicontribs.specialization.SpecializationPlugin;

public class AccessConnectionFigure extends com.archimatetool.editor.diagram.figures.connections.AccessConnectionFigure {
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
