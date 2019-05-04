package org.archicontribs.specialization.uiProvider.relationships.figures;

import org.archicontribs.specialization.SpecializationPlugin;

public class TriggeringConnectionFigure extends com.archimatetool.editor.diagram.figures.connections.TriggeringConnectionFigure {
    @Override
    protected void setConnectionText() {
        String labelName = SpecializationPlugin.getLabelName(getModelConnection());
        
        if ( labelName==null )
            getConnectionLabel().setText(getModelConnection().getArchimateRelationship().getName());
        else
            getConnectionLabel().setText(labelName);
    }
}