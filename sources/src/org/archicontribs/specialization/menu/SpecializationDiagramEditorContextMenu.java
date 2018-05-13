package org.archicontribs.specialization.menu;

import org.archicontribs.specialization.SpecializationLogger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

import com.archimatetool.editor.diagram.editparts.ArchimateDiagramPart;
import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.editor.diagram.editparts.DiagramConnectionEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.GroupEditPart;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IDiagramModel;

/**
 * This class is used when the user right-click on a graphical object
 */
public class SpecializationDiagramEditorContextMenu extends ExtensionContributionFactory {
    private static final SpecializationLogger logger = new SpecializationLogger(SpecializationDiagramEditorContextMenu.class);

    @Override
    public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {


        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null)
        {
            IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
            if ( logger.isTraceEnabled() ) logger.trace("Showing menu items ("+selection.size()+" items selected)");
            if ( selection.size() != 0 ) {
                Object firstElement = selection.getFirstElement();
                EObject selectedObject = null;

                if ( logger.isDebugEnabled() ) logger.debug("Showing menu for class "+firstElement.getClass().getSimpleName());

                switch ( firstElement.getClass().getSimpleName() ) {
                    case "ArchimateDiagramPart" :           selectedObject = ((ArchimateDiagramPart)firstElement).getModel();           break;      // when the user right clicks in a diagram background
                    case "ArchimateElementEditPart" :       selectedObject = ((ArchimateElementEditPart)firstElement).getModel();       break;      // when the user right clicks in a diagram and an element is selected
                    case "ArchimateRelationshipEditPart" :  selectedObject = ((ArchimateRelationshipEditPart)firstElement).getModel();  break;      // when the user right clicks in a diagram and a relationship is selected
                    case "DiagramConnectionEditPart" :      selectedObject = ((DiagramConnectionEditPart)firstElement).getModel();      break;      // when the user right clicks on a connection
                    case "GroupEditPart" :                  selectedObject = ((GroupEditPart)firstElement).getModel();                  break;      // when the user right clicks on a group
                    case "ArchimateDiagramModel" :          selectedObject = (IArchimateDiagramModel)firstElement;                      break;      // when the user right clicks on a diagram in the model tree
					default:
                }
                
                if ( selectedObject != null ) {
                    // we get the view that contains the graphical object that the user clicked on
                    while ( selectedObject!=null && !(selectedObject instanceof IDiagramModel) ) {
                        selectedObject = selectedObject.eContainer();
                    }
                    
                    // if the selected object is in a view
                    if ( selectedObject != null ) {
                        IDiagramModel selectedView  = (IDiagramModel)selectedObject;
                        ImageDescriptor menuIcon = ImageDescriptor.createFromURL(FileLocator.find(Platform.getBundle("com.archimatetool.editor"), new Path("img/formatpainter.png"), null));
                        String menuLabel;
                        
                        // we add a menu to refresh the view labels
                        menuLabel = "Refresh view \"" + selectedView.getName() + "\"";
                        
                        if ( logger.isDebugEnabled() ) logger.debug("adding menu label : "+menuLabel);

                        additions.addContributionItem(new Separator(), null);
                        additions.addContributionItem(new CommandContributionItem(new CommandContributionItemParameter(
                                PlatformUI.getWorkbench().getActiveWorkbenchWindow(),                       	      // serviceLocator
                                "org.archicontribs.specialization.menu.SpecializationdiagramEditorContextMenu",       // id
                                "org.archicontribs.specialization.menu.SpecializationRefreshViewHandler",  		      // commandId
                                null,                                                                       	      // parameters
                                menuIcon,                                                                    	      // icon
                                null,                                                                        	      // disabledIcon
                                null,                                                                        	      // hoverIcon
                                menuLabel,                                                                   	      // label
                                null,                                                                        	      // mnemonic
                                null,                                                                        	      // tooltip 
                                CommandContributionItem.STYLE_PUSH,                                          	      // style
                                null,                                                                        	      // helpContextId
                                true)), null);
                    }
                } else {
                    logger.trace("Unknown selected object : "+firstElement.getClass().getSimpleName());
                }
            }
        } else {
            logger.trace("Window is null");
        }
    }
}