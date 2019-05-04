package org.archicontribs.specialization.uiProvider.relationships.figures;

import org.archicontribs.specialization.SpecializationPlugin;

public class FlowConnectionFigure extends com.archimatetool.editor.diagram.figures.connections.FlowConnectionFigure {
    @Override
    protected void setConnectionText() {
        String labelName = SpecializationPlugin.getLabelName(getModelConnection());
        
        if ( labelName==null )
            getConnectionLabel().setText(getModelConnection().getArchimateRelationship().getName());
        else
            getConnectionLabel().setText(labelName);
    }
}