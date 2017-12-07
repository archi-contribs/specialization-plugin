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

import com.archimatetool.canvas.model.ICanvasModel;
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
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.ISketchModel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

@SuppressWarnings("unused")
public class SpecializationRefreshModelTreeHandler extends AbstractHandler {
    private static final SpecializationLogger logger = new SpecializationLogger(SpecializationRefreshModelTreeHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Object firstElement = ((IStructuredSelection)HandlerUtil.getCurrentSelection(event)).getFirstElement();
        IArchimateModel model = null;
        
        switch ( firstElement.getClass().getSimpleName() ) {
            case "ArchimateModel" :         model = ((IArchimateModel)firstElement);                                  break;              // when the user right clicks on the model in the model tree
            case "ArchimateDiagramModel" :  model = ((IArchimateDiagramModel)firstElement).getArchimateModel();       break;              // when the user right clicks on a view in the model tree
            case "CanvasModel" :            model = ((ICanvasModel)firstElement).getArchimateModel();                 break;              // when the user right clicks on a canvas in the model tree
            case "SketchModel" :            model = ((ISketchModel)firstElement).getArchimateModel();                 break;              // when the user right clicks on a sketch in the model tree
            case "Folder" :                 model = ((IFolder)firstElement).getArchimateModel();                      break;              // when the user right clicks on a folder in the model tree
            default :
            	if ( firstElement instanceof IArchimateElement )     model = ((IArchimateElement)firstElement).getArchimateModel();       // when the user right clicks on an element in the model tree
            	if ( firstElement instanceof IArchimateRelationship ) model = ((IArchimateRelationship)firstElement).getArchimateModel(); // when the user right clicks on a relationship in the model tree
        }
        
        if ( model != null )
        	refresh(model);
        return null;
    }
    
    private void refresh(INameable obj) {
        // we set the object name to force the framework to redraw the corresponding object in the current view 
    	obj.setName(obj.getName());
        if ( obj instanceof IArchimateModel ) {
            for ( IFolder folder: ((IArchimateModel)obj).getFolders() ) {
                refresh(folder);
            }
        }
        if ( obj instanceof IFolder ) {
            for ( IFolder subfolder: ((IFolder)obj).getFolders() ) {
                refresh(subfolder);
            }
            for ( EObject element: ((IFolder)obj).getElements() ) {
                refresh((INameable)element);
            }
        }
    }
}
