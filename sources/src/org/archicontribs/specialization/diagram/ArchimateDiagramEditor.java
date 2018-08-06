package org.archicontribs.specialization.diagram;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

public class ArchimateDiagramEditor extends com.archimatetool.editor.diagram.ArchimateDiagramEditor {
    @Override
    protected void createRootEditPart(GraphicalViewer viewer) {
    	super.createRootEditPart(viewer);
    	viewer.getControl().addListener(SWT.MouseDoubleClick, new Listener() {
			@Override public void handleEvent(Event event) {
				EditPart editPart = viewer.findObjectAt(new Point(event.x, event.y));
				Object component = editPart.getModel();
				if ( component instanceof IDiagramModelArchimateObject ) {
					IArchimateElement element = ((IDiagramModelArchimateObject) component).getArchimateConcept();
					for ( IProperty prop: element.getProperties() ) {
						if ( prop.getKey() != null && prop.getKey().equals("drill down to") ) {
							String value = prop.getValue();
							System.out.println("drill down to "+value);
						}
					}
				}
				System.out.println("double click on "+event.widget);				
			}
    		
    	});
    }
}
