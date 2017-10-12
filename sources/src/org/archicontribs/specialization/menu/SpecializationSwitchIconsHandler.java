/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */

package org.archicontribs.specialization.menu;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.archimatetool.editor.diagram.ArchimateDiagramEditor;
import com.archimatetool.editor.diagram.editparts.AbstractConnectedEditPart;
import com.archimatetool.editor.diagram.editparts.ArchimateDiagramPart;
import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.editor.diagram.editparts.DiagramConnectionEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.GroupEditPart;
import com.archimatetool.editor.diagram.util.DiagramUtils;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.Property;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

@SuppressWarnings("unused")
public class SpecializationSwitchIconsHandler extends AbstractHandler {
    private static final SpecializationLogger logger = new SpecializationLogger(SpecializationSwitchIconsHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Object firstElement = ((IStructuredSelection)HandlerUtil.getCurrentSelection(event)).getFirstElement();
        EObject selectedObject = null;
        
        switch ( firstElement.getClass().getSimpleName() ) {
            case "ArchimateDiagramPart" :           selectedObject = ((ArchimateDiagramPart)firstElement).getModel();           break;      // when the user right clicks in a diagram background
            case "ArchimateElementEditPart" :       selectedObject = ((ArchimateElementEditPart)firstElement).getModel();       break;      // when the user right clicks in a diagram and an element is selected
            case "ArchimateRelationshipEditPart" :  selectedObject = ((ArchimateRelationshipEditPart)firstElement).getModel();  break;      // when the user right clicks in a diagram and a relationship is selected
            case "DiagramConnectionEditPart" :      selectedObject = ((DiagramConnectionEditPart)firstElement).getModel();      break;      // when the user right clicks on a connection
            case "GroupEditPart" :                  selectedObject = ((GroupEditPart)firstElement).getModel();                  break;      // when the user right clicks on a group
            case "ArchimateDiagramModel" :          selectedObject = (IArchimateDiagramModel)firstElement;                      break;      // when the user right clicks on a diagram in the model tree
        }
        
        if ( selectedObject != null ) {
            // we get the view that contains the graphical object that the user clicked on
            while ( selectedObject!=null && !(selectedObject instanceof IDiagramModel) ) {
                selectedObject = selectedObject.eContainer();
            }
            
             // if the selected object is in a view, we refresh the relationships names to force the connections to redraw their label 
            if ( selectedObject != null ) {
                IDiagramModel selectedView  = (IDiagramModel)selectedObject;
                
                for ( IProperty prop: selectedView.getProperties() ) {
                    if ( SpecializationPlugin.areEqual(prop.getKey(), "replace icons") ) {
                        if ( SpecializationPlugin.areEqual(prop.getValue(), "true") ) {
                            logger.trace("Switching off customized icons");
                            prop.setValue("false");
                            refresh(event);
                            return null;
                        }
                        logger.trace("Switching on customized icons");
                        prop.setValue("true");
                        refresh(event);
                        return null;
                    }
                }
                
                // if we're here, this means that there is no "replace icons" property, so we should create one
                IProperty prop = IArchimateFactory.eINSTANCE.createProperty();
                prop.setKey("replace icons");
                prop.setValue("true");
                selectedView.getProperties().add(prop);
                refresh(event);
            }
        }
        return null;
    }
    
    private void refresh(ExecutionEvent event) throws ExecutionException {
        SpecializationRefreshViewHandler refreshHandler = new SpecializationRefreshViewHandler();
        refreshHandler.execute(event);
        refreshHandler = null;
    }
}