package org.archicontribs.specialization.diagram;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.archimatetool.editor.ui.services.EditorManager;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IProperty;

public class ArchimateDiagramEditor extends com.archimatetool.editor.diagram.ArchimateDiagramEditor {
    static final SpecializationLogger logger = new SpecializationLogger(ArchimateDiagramEditor.class);
    
    
    
    @Override
    protected void createRootEditPart(GraphicalViewer viewer) {
    	super.createRootEditPart(viewer);
    	viewer.getControl().addListener(SWT.MouseDoubleClick, new Listener() {
			@Override public void handleEvent(Event event) {
			    logger.trace("Double-click on "+event.widget);
				EditPart editPart = viewer.findObjectAt(new Point(event.x, event.y));
				if ( editPart != null ) {
    				Object component = editPart.getModel();
    				if ( component instanceof IDiagramModelArchimateObject ) {
    					IArchimateElement element = ((IDiagramModelArchimateObject) component).getArchimateConcept();
    					boolean propFound = false;
    					for ( IProperty prop: element.getProperties() ) {
    						if ( SpecializationPlugin.DRILLDOWN_PROPERTY_KEY.equals(prop.getKey()) ) {
    						    propFound = true;
    				            IArchimateModel model = element.getArchimateModel();
    							String viewId = prop.getValue();
    	                        if ( viewId == null ) {
    	                            logger.debug("Property \""+SpecializationPlugin.DRILLDOWN_PROPERTY_KEY+"\" = null");
    	                        } else {
    	                            logger.debug("Property \""+SpecializationPlugin.DRILLDOWN_PROPERTY_KEY+"\" = \""+viewId+"\"");
        							for ( IDiagramModel view: model.getDiagramModels() ) {
        							    if ( view.getId().equals(viewId) ) {
        							        logger.debug("Opening view \""+view.getName()+"\"");
        							        EditorManager.openDiagramEditor(view);
        							        return;
        							    }
        							}
        							logger.error("Cannot find view with Id \""+viewId+"\"");
    	                        }
    						}
    					}
    					if ( !propFound ) logger.debug("The element does not have a property \"drill down\"");
    				}
				}
			}
    		
    	});
    }
}
