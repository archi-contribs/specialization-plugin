package org.archicontribs.specialization.menu;

import org.archicontribs.specialization.SpecializationLogger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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

import com.archimatetool.canvas.model.ICanvasModel;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.ISketchModel;

/**
 * This class is used when the user right-click on a graphical object
 */
public class SpecializationTreeModelContextMenu extends ExtensionContributionFactory {
	private static final SpecializationLogger logger = new SpecializationLogger(SpecializationTreeModelContextMenu.class);

    @Override
    public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {

    	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null)
        {
            IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
            if ( selection.size() == 1 ) {
                Object obj = selection.getFirstElement();

                if ( logger.isDebugEnabled() ) logger.debug("Showing menu for class "+obj.getClass().getSimpleName());

                if ( obj instanceof IArchimateModel
                		|| obj instanceof IArchimateDiagramModel
                		|| obj instanceof ICanvasModel
                		|| obj instanceof ISketchModel
                		|| obj instanceof IFolder
                		|| obj instanceof IArchimateElement
                		|| obj instanceof IArchimateRelationship ) {
                	ImageDescriptor menuIcon = ImageDescriptor.createFromURL(FileLocator.find(Platform.getBundle("com.archimatetool.editor"), new Path("img/formatpainter.png"), null));
                    String menuLabel;
                    
                    // we add a menu to refresh the view labels
                    menuLabel = "Refresh model tree";
                    
                    if ( logger.isDebugEnabled() ) logger.debug("adding menu label : "+menuLabel);

                    additions.addContributionItem(new Separator(), null);
                    additions.addContributionItem(new CommandContributionItem(new CommandContributionItemParameter(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow(),                             // serviceLocator
                            "org.archicontribs.specialization.menu.SpecializationTreeModelContextMenu",       // id
                            "org.archicontribs.specialization.menu.SpecializationRefreshModelTreeHandler",    // commandId
                            null,                                                                             // parameters
                            menuIcon,                                                                         // icon
                            null,                                                                             // disabledIcon
                            null,                                                                             // hoverIcon
                            menuLabel,                                                                        // label
                            null,                                                                             // mnemonic
                            null,                                                                             // tooltip 
                            CommandContributionItem.STYLE_PUSH,                                               // style
                            null,                                                                             // helpContextId
                            true)), null);
                }
            }
        }
    }
}
