package org.archicontribs.specialization.uiProvider.relationships.connections;

import org.archicontribs.specialization.SpecializationPlugin;

public class TriggeringConnectionFigure extends com.archimatetool.editor.diagram.figures.connections.TriggeringConnectionFigure {
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