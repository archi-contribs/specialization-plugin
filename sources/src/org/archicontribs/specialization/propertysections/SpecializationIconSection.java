/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.figures.AbstractTextControlContainerFigure;
import com.archimatetool.editor.diagram.figures.IFigureDelegate;
import com.archimatetool.editor.diagram.figures.RectangleFigureDelegate;
import com.archimatetool.editor.propertysections.AbstractArchimatePropertySection;
import com.archimatetool.model.*;


public class SpecializationIconSection extends AbstractArchimatePropertySection {
    private static final SpecializationLogger logger = new SpecializationLogger(SpecializationIconSection.class);

    private ArchimateElementEditPart elementEditPart;

    private Composite compoIcon;
    private Composite compoNoIcon;
    private Text txtIcon;
    private Tree fileTree;
    private Label imagePreview;
    private Text txtWidth;
    private Text txtHeight;

    static final private String[] validImageSuffixes = {"jpg", "png", "gif", "bmp"};
    static final private Image    closedFolderImage  = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/16x16/closedFolder.png"));
    static final private Image    openedFolderImage  = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/16x16/openedFolder.png"));

    /**
     * Filter to show or reject this section depending on input value
     */
    public static class Filter extends ObjectFilter {
        @Override
        protected boolean isRequiredType(Object object) {
            if ( object == null )
                return false;

            logger.trace(object.getClass().getSimpleName()+" -> filter : "+(object instanceof ArchimateElementEditPart));
            if ( !(object instanceof ArchimateElementEditPart) )
                return false;

            // we show up the icon tab if the figure has got an icon
            // i.e. if the Figure class has got a drawIcon method
            // the loggerLevel is a private property, so we use reflection to access it
            try {
                ((ArchimateElementEditPart)object).getFigure().getClass().getDeclaredMethod("drawIcon", Graphics.class);
            } catch (NoSuchMethodException e) {
                logger.trace("hiding icon tab as the element has not got any icon");
                return false;
            } catch (SecurityException e) {
                logger.error("failed to check for \"drawIcon\" method", e);
                return false;
            }

            logger.trace("showing icon tab as the element has got a icon");
            return true;
        }

        @Override
        protected Class<?> getAdaptableType() {
            return ArchimateElementEditPart.class;
        }
    }

    /**
     * Create the controls
     */
    @Override
    protected void createControls(Composite parent) {
        compoNoIcon = new Composite(parent, SWT.NONE);
        compoNoIcon.setForeground(parent.getForeground());
        compoNoIcon.setBackground(parent.getBackground());
        compoNoIcon.setLayout(new FormLayout());
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        compoNoIcon.setLayoutData(gd);

        Label lblNoIcon = new Label(compoNoIcon, SWT.NONE);
        lblNoIcon.setText("Please change the element's figure to show up the icon.");
        lblNoIcon.setForeground(parent.getForeground());
        lblNoIcon.setBackground(parent.getBackground());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblNoIcon.setLayoutData(fd);

        /* ********************************************************* */
        compoIcon = new Composite(parent, SWT.NONE);
        compoIcon.setForeground(parent.getForeground());
        compoIcon.setBackground(parent.getBackground());
        compoIcon.setLayout(new FormLayout());
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        compoIcon.setLayoutData(gd);

        Label lblIcon = new Label(compoIcon, SWT.NONE);
        lblIcon.setText("Icon :");
        lblIcon.setForeground(parent.getForeground());
        lblIcon.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 10);
        lblIcon.setLayoutData(fd);

        txtIcon = new Text(compoIcon, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblIcon, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIcon, 20);
        fd.right = new FormAttachment(0, 500);
        txtIcon.setLayoutData(fd);

        fileTree = new Tree(compoIcon, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        fileTree.setBackground(parent.getBackground());
        //fileTree.setHeaderVisible(true);
        //fileTree.setLinesVisible(false);

        //TreeColumn filesColumn = new TreeColumn(fileTree, SWT.NONE);
        //filesColumn.setText("Files");

        //TreeColumn sizeColumn = new TreeColumn(fileTree, SWT.NONE);
        //sizeColumn.setText("Image size");
        //sizeColumn.setWidth(80);

        fd = new FormData();
        fd.top = new FormAttachment(txtIcon, 10);
        fd.left = new FormAttachment(txtIcon, 0, SWT.LEFT);
        fd.right = new FormAttachment(txtIcon, 0, SWT.RIGHT);
        fd.bottom = new FormAttachment(0, 220);
        fileTree.setLayoutData(fd);

        Label lblReminder = new Label(compoIcon, SWT.NONE);
        lblReminder.setText("(you may define new image folders on the preference page)");
        lblReminder.setForeground(parent.getForeground());
        lblReminder.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(fileTree, 3);
        fd.left = new FormAttachment(fileTree, 0, SWT.CENTER);
        lblReminder.setLayoutData(fd);
        

        Button btnSetIcon = new Button(compoIcon, SWT.NONE);
        btnSetIcon.setText("Set Icon");
        fd = new FormData();
        fd.top = new FormAttachment(fileTree, 0, SWT.TOP);
        fd.left = new FormAttachment(fileTree, 10, SWT.RIGHT);
        btnSetIcon.setLayoutData(fd);
        btnSetIcon.setVisible(false);
        
        Label lblResize = new Label(compoIcon, SWT.NONE);
        lblResize.setText("Resize to");
        lblResize.setForeground(parent.getForeground());
        lblResize.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnSetIcon, 10);
        fd.left = new FormAttachment(fileTree, 10, SWT.RIGHT);
        lblResize.setLayoutData(fd);
        lblResize.setVisible(false);
        
        txtWidth = new Text(compoIcon, SWT.BORDER);
        txtWidth.setTextLimit(4);
        fd = new FormData();
        fd.top = new FormAttachment(lblResize, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblResize, 5, SWT.RIGHT);
        fd.right = new FormAttachment(lblResize, 35, SWT.RIGHT);
        txtWidth.setLayoutData(fd);
        txtWidth.setVisible(false);
        txtWidth.addVerifyListener(numberOnlyVerifyListener);
        
        Label lblX = new Label(compoIcon, SWT.NONE);
        lblX.setText("x");
        lblX.setForeground(parent.getForeground());
        lblX.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblResize, 0, SWT.TOP);
        fd.left = new FormAttachment(txtWidth, 5, SWT.RIGHT);
        lblX.setLayoutData(fd);
        lblX.setVisible(false);
        
        txtHeight = new Text(compoIcon, SWT.BORDER);
        txtHeight.setTextLimit(4);
        fd = new FormData();
        fd.top = new FormAttachment(lblX, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblX, 5, SWT.RIGHT);
        fd.right = new FormAttachment(lblX, 35, SWT.RIGHT);
        txtHeight.setLayoutData(fd);
        txtHeight.setVisible(false);
        txtHeight.addVerifyListener(numberOnlyVerifyListener);

        imagePreview = new Label(compoIcon, SWT.NONE);
        imagePreview.setForeground(parent.getForeground());
        imagePreview.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(txtHeight, 10);
        fd.left = new FormAttachment(fileTree, 10, SWT.RIGHT);
        imagePreview.setLayoutData(fd);
        imagePreview.setVisible(false);
        
        compoIcon.layout();
        
        parent.layout();

        /* ********************************************************* */


        File root = null;
        try {
            String pluginsFilename = new File(com.archimatetool.editor.ArchiPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalPath();
            root = new File(pluginsFilename+File.separator+".."+File.separator+"img");
            root = new File(root.getCanonicalPath());
        } catch (IOException e) {
            logger.error("Cannot get plugin's folder !", e);
            return;
        }

        logger.trace("Getting images folder content \""+root.getPath()+"\"");
        File[] files;
        try {
            files = root.listFiles();
        } catch (SecurityException e) {
            SpecializationPlugin.popup(Level.ERROR, "Cannot read folder \""+root.getPath()+"\"", e);
            return;
        }

        if( files == null ) {
            SpecializationPlugin.popup(Level.INFO, "No image folder has been defined.\n\nPlease define image folders on the preference page.");
            return;
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            logger.trace("found folder entry : "+Paths.get(file.getName()));
            if ( Files.isSymbolicLink(Paths.get(file.getPath())) ) {
                logger.trace("   is a symbolic link. Adding to the file tree.");
                TreeItem newTreeItem = new TreeItem(fileTree, SWT.NONE);
                newTreeItem.setText(file.getName());
                newTreeItem.setImage(closedFolderImage);
                newTreeItem.setData(file);
                new TreeItem(newTreeItem, SWT.NONE);        // to show the arrow in front of the folder
            }
        }

        fileTree.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                Image image = imagePreview.getImage();
                if ( image != null ) {
                    imagePreview.setImage(null);
                    imagePreview.setSize(imagePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    image.dispose();
                }
                
                btnSetIcon.setVisible(false);
                lblResize.setVisible(false);
                txtWidth.setVisible(false);
                lblX.setVisible(false);
                txtHeight.setVisible(false);
                imagePreview.setVisible(false);
                
                TreeItem treeItem = (TreeItem) e.item;
                if (treeItem == null)
                    return;

                if ( treeItem.getImage() == openedFolderImage ) {
                    treeItem.setImage(closedFolderImage);
                    treeItem.setExpanded(false);
                    return;
                }

                File root = (File)treeItem.getData();
                if ( root == null )
                    return;

                if ( root.isDirectory() ) {
                    logger.trace("Getting folder content : \""+root.getPath()+"\"");
                    for ( TreeItem item : treeItem.getItems() ) {
                        item.dispose();
                    }

                    treeItem.setImage(openedFolderImage);

                    File[] folders = root.listFiles(foldersFilter);
                    if (folders != null) {
                        Arrays.sort(files, nameComparator);
                        for (int i = 0; i < folders.length; i++) {
                            File folder = folders[i];
                            logger.trace("found folder : "+Paths.get(folder.getName()));
                            TreeItem subItem = new TreeItem(treeItem, SWT.NONE);
                            subItem.setText(folder.getName());
                            subItem.setImage(closedFolderImage);
                            subItem.setData(folder);
                            new TreeItem(subItem, SWT.NONE);         // to show the arrow in front of the folder
                        }
                    }

                    File[] files = root.listFiles(imagesFilter);
                    if (files != null) {
                        Arrays.sort(files, nameComparator);
                        for (int i = 0; i < files.length; i++) {
                            File file = files[i];
                            logger.trace("found image : "+Paths.get(file.getName()));
                            TreeItem subItem = new TreeItem(treeItem, SWT.NONE);
                            subItem.setText(file.getName());
                            subItem.setData(file);
                        }
                    }
                    treeItem.setExpanded(true);

                } else {
                    logger.trace("Showing preview for image \""+root.getPath()+"\"");
                    ImageData imageData = new ImageData(root.getPath());
                    ImageData imagePreviewData;
                    
                    int width = txtWidth.getText().isEmpty() ? 0 : Integer.parseInt(txtWidth.getText());
                    int height = txtHeight.getText().isEmpty() ? 0 : Integer.parseInt(txtHeight.getText());
                    
                    if ( width+height == 0 )
                    	imagePreviewData = imageData;
                    else {
                    	float scale = 0f;
                    	if ( width == 0 ) { scale = (float)height/imageData.height ; width = (int)(imageData.width * scale); }
                    	if ( height == 0 ) { scale = (float)width/imageData.width ; height = (int)(imageData.height * scale); }
                    	logger.trace("resizing image to "+width+"x"+height+" (scale factor = "+scale+")");
                    	imagePreviewData = imageData.scaledTo(width, height);
                    }
                    
                    image = new Image(Display.getCurrent(), imagePreviewData);
                    imagePreview.setImage(image);
                    imagePreview.setSize(imagePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    
                    btnSetIcon.setVisible(true);
                    lblResize.setVisible(true);
                    txtWidth.setVisible(true);
                    lblX.setVisible(true);
                    txtHeight.setVisible(true);
                    imagePreview.setVisible(true);
                }
            }
        });
    }

    private FileFilter imagesFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if ( file.isFile() && (file.getName().lastIndexOf('.') != -1) )
                return SpecializationPlugin.inArray(validImageSuffixes, (file.getName().substring(file.getName().lastIndexOf('.')+1)).toLowerCase());
            logger.trace("fichier refus� : "+file.getName());
            return false;
        }
    };

    private FileFilter foldersFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    private Comparator<File> nameComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    };
    
    private VerifyListener numberOnlyVerifyListener = new VerifyListener() {
        @Override
        public void verifyText(VerifyEvent event) {
            try {
                Integer.parseInt(event.text);
                event.doit = true;
            } catch (NumberFormatException e) {
                event.doit = false;
            }
        }
    };

    /*
     * Adapter to listen to changes made elsewhere (including Undo/Redo commands)
     */
    private Adapter eAdapter = new AdapterImpl() {
        @Override
        public void notifyChanged(Notification msg) {
            Object feature = msg.getFeature();
            // Model event (Undo/Redo and here!)
            if(feature == IArchimatePackage.Literals.DIAGRAM_MODEL_ARCHIMATE_OBJECT__TYPE) {
                refreshControls();
            }
        }
    };

    @Override
    protected Adapter getECoreAdapter() {
        return eAdapter;
    }

    @Override
    protected EObject getEObject() {
        if ( elementEditPart == null ) {
            logger.error("elementEditPart is null"); //$NON-NLS-1$
            return null;
        }

        return elementEditPart.getModel();
    }

    protected void setElement(Object element) {
        elementEditPart = (ArchimateElementEditPart)new Filter().adaptObject(element);
        if(elementEditPart == null) {
            logger.error("failed to get elementEditPart for " + element); //$NON-NLS-1$
        }

        refreshControls();
    }

    private void refreshControls() {
        IFigure figure = elementEditPart.getFigure();
        IFigureDelegate figureDelegate = ((AbstractTextControlContainerFigure)figure).getFigureDelegate();

        compoNoIcon.setVisible(!(figureDelegate instanceof RectangleFigureDelegate));
        compoIcon.setVisible(figureDelegate instanceof RectangleFigureDelegate);
    }
    
    @Override
    public boolean shouldUseExtraSpace() {
        return true;
    }
    
    @Override
    protected void setLayout(Composite parent) {
        GridLayout layout = new GridLayout(1, false);
        layout.marginTop = 10;
        layout.marginHeight = 0;
        layout.marginLeft = 3;
        layout.marginBottom = 2; 
        layout.verticalSpacing = 10;
        parent.setLayout(layout);
        
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, shouldUseExtraSpace()));
    }
}
