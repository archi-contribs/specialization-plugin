/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.figures.AbstractTextControlContainerFigure;
import com.archimatetool.editor.diagram.figures.IFigureDelegate;
import com.archimatetool.editor.diagram.figures.RectangleFigureDelegate;
import com.archimatetool.editor.propertysections.AbstractArchimatePropertySection;
import com.archimatetool.model.*;
import com.archimatetool.model.impl.Bounds;


public class SpecializationIconSection extends AbstractArchimatePropertySection {
    private static final SpecializationLogger logger = new SpecializationLogger(SpecializationIconSection.class);

    private ArchimateElementEditPart elementEditPart = null;

    private Composite compoIcon;
    private Composite compoNoIcon;
    private Label lblNoIcon;
    private Text txtIconName;
    private Text txtIconSize;
    private Text txtIconLocation;
    private Tree fileTree;
    private Composite compoPreview;
    private Button btnNoResize;
    private Button btnAutoResize;
    private Button btnCustomResize;
    private Text txtWidth;
    private Text txtHeight;
    private Label imagePreview;
    
    private boolean mouseOverHelpButton = false;

    static final private String[] validImageSuffixes = {"jpg", "png", "gif", "bmp", "ico"};
    static final private Image    closedFolderImage  = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/16x16/closedFolder.png"));
    static final private Image    openedFolderImage  = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/16x16/openedFolder.png"));
    static final private Image    HELP_ICON          = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/28x28/help.png"));

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
     * Adapter to listen to changes made elsewhere (including Undo/Redo commands)
     */
    private Adapter eAdapter = new AdapterImpl() {
        @Override
        public void notifyChanged(Notification msg) {
            Object feature = msg.getFeature();
            // Model event (Undo/Redo and here!)
            if(feature == IArchimatePackage.Literals.DIAGRAM_MODEL_ARCHIMATE_OBJECT__TYPE 
                    || feature == IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH
                    || feature == IArchimatePackage.Literals.PROPERTIES__PROPERTIES) 
                refreshControls();
            else if ( feature == IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__BOUNDS) {
                Bounds bounds = (Bounds)msg.getNewValue();
                refreshControls();
                showPreviewImage(bounds.getWidth(), bounds.getHeight());
            }
        }
    };
    
    @Override
    protected void setLayout(Composite parent) {
       parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, shouldUseExtraSpace()));
       
       parent.setLayout(new FormLayout());
    }
    
    
    @Override
    public boolean shouldUseExtraSpace() {
        return true;
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
        FormData fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        compoNoIcon.setLayoutData(fd);

        lblNoIcon = new Label(compoNoIcon, SWT.NONE);
        lblNoIcon.setText("Please change the element's figure to show up the icon.");
        lblNoIcon.setForeground(parent.getForeground());
        lblNoIcon.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblNoIcon.setLayoutData(fd);
        
        Label btnHelp = new Label(compoNoIcon, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblNoIcon, 25);
        fd.bottom = new FormAttachment(lblNoIcon, 55, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 20);
        fd.right = new FormAttachment(0, 50);
        btnHelp.setLayoutData(fd);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceIcon.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceIcon.html"); } });
        
        Label helpLbl = new Label(compoNoIcon, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp, 5);
        helpLbl.setLayoutData(fd);

        /* ********************************************************* */
        compoIcon = new Composite(parent, SWT.NONE);
        compoIcon.setForeground(parent.getForeground());
        compoIcon.setBackground(parent.getBackground());
        compoIcon.setLayout(new FormLayout());
        fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        compoIcon.setLayoutData(fd);

        Label lblIconName = new Label(compoIcon, SWT.NONE);
        lblIconName.setText("Icon :");
        lblIconName.setForeground(parent.getForeground());
        lblIconName.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 10);
        lblIconName.setLayoutData(fd);
        
        Label lblIconSize = new Label(compoIcon, SWT.NONE);
        lblIconSize.setText("Icon size:");
        lblIconSize.setForeground(parent.getForeground());
        lblIconSize.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblIconName, 10);
        fd.left = new FormAttachment(0, 10);
        lblIconSize.setLayoutData(fd);

        Label lblIconLocation = new Label(compoIcon, SWT.NONE);
        lblIconLocation.setText("Icon location:");
        lblIconLocation.setForeground(parent.getForeground());
        lblIconLocation.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblIconSize, 10);
        fd.left = new FormAttachment(0, 10);
        lblIconLocation.setLayoutData(fd);

        txtIconName = new Text(compoIcon, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconName, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIconLocation, 5);
        fd.right = new FormAttachment(0, 400);
        txtIconName.setLayoutData(fd);
        txtIconName.addModifyListener(iconModifyListener);

        txtIconSize = new Text(compoIcon, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconSize, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIconLocation, 5);
        fd.right = new FormAttachment(0, 150);
        txtIconSize.setLayoutData(fd);
        txtIconSize.addModifyListener(iconSizeModifyListener);

        txtIconLocation = new Text(compoIcon, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconLocation, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIconLocation, 5);
        fd.right = new FormAttachment(0, 150);
        txtIconLocation.setLayoutData(fd);
        txtIconLocation.addModifyListener(iconLocationModifyListener);

        fileTree = new Tree(compoIcon, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        fileTree.setBackground(parent.getBackground());
        fileTree.addListener(SWT.Selection, fileTreeSelectionListener);
        fileTree.addListener(SWT.Expand, fileTreeSelectionListener);
        fileTree.addListener(SWT.Collapse, fileTreeSelectionListener);

        fd = new FormData();
        fd.top = new FormAttachment(txtIconLocation, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(txtIconName, 0, SWT.RIGHT);
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
        
        compoPreview = new Composite(compoIcon, SWT.RESIZE);
        compoPreview.setForeground(parent.getForeground());
        compoPreview.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(fileTree, 0, SWT.TOP);
        fd.left = new FormAttachment(fileTree, 10, SWT.RIGHT);
        compoPreview.setLayoutData(fd);
        compoPreview.setVisible(false);
        compoPreview.setLayout(new FormLayout());
                
        Button btnSetIcon = new Button(compoPreview, SWT.NONE);
        btnSetIcon.setText("Set Icon");
        fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        btnSetIcon.setLayoutData(fd);
        btnSetIcon.addSelectionListener(setIconSelectionListener);
        
        Label lblResize = new Label(compoPreview, SWT.NONE);
        lblResize.setText("Resize to");
        lblResize.setForeground(parent.getForeground());
        lblResize.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnSetIcon, 10);
        fd.left = new FormAttachment(0);
        lblResize.setLayoutData(fd);

        btnNoResize = new Button(compoPreview, SWT.RADIO);
        btnNoResize.setForeground(parent.getForeground());
        btnNoResize.setBackground(parent.getBackground());
        btnNoResize.setText("Do not Resize");
        fd = new FormData();
        fd.top = new FormAttachment(lblResize, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblResize, 5);
        btnNoResize.setLayoutData(fd);
        btnNoResize.setSelection(true);
        btnNoResize.addSelectionListener(resizeSelectionListener);
        
        btnAutoResize = new Button(compoPreview, SWT.RADIO);
        btnAutoResize.setForeground(parent.getForeground());
        btnAutoResize.setBackground(parent.getBackground());
        btnAutoResize.setText("Figure's size");
        fd = new FormData();
        fd.top = new FormAttachment(btnNoResize, 5);
        fd.left = new FormAttachment(btnNoResize, 0, SWT.LEFT);
        btnAutoResize.setLayoutData(fd);
        btnAutoResize.addSelectionListener(resizeSelectionListener);
        
        btnCustomResize = new Button(compoPreview, SWT.RADIO);
        btnCustomResize.setForeground(parent.getForeground());
        btnCustomResize.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnAutoResize, 5);
        fd.left = new FormAttachment(btnAutoResize, 0, SWT.LEFT);
        btnCustomResize.setLayoutData(fd);
        btnCustomResize.addSelectionListener(resizeSelectionListener);
        
        txtWidth = new Text(compoPreview, SWT.BORDER);
        txtWidth.setTextLimit(4);
        fd = new FormData();
        fd.top = new FormAttachment(btnCustomResize, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnCustomResize, 5, SWT.RIGHT);
        fd.right = new FormAttachment(btnCustomResize, 35, SWT.RIGHT);
        txtWidth.setLayoutData(fd);
        txtWidth.addVerifyListener(numberOnlyVerifyListener);
        txtWidth.addModifyListener(resizeModifyListener);
        txtWidth.setEnabled(false);
        
        Label lblX = new Label(compoPreview, SWT.NONE);
        lblX.setText("x");
        lblX.setForeground(parent.getForeground());
        lblX.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(txtWidth, 0, SWT.CENTER);
        fd.left = new FormAttachment(txtWidth, 5, SWT.RIGHT);
        lblX.setLayoutData(fd);
        
        txtHeight = new Text(compoPreview, SWT.BORDER);
        txtHeight.setTextLimit(4);
        fd = new FormData();
        fd.top = new FormAttachment(lblX, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblX, 5, SWT.RIGHT);
        fd.right = new FormAttachment(lblX, 35, SWT.RIGHT);
        txtHeight.setLayoutData(fd);
        txtHeight.addVerifyListener(numberOnlyVerifyListener);
        txtHeight.addModifyListener(resizeModifyListener);
        txtHeight.setEnabled(false);

        imagePreview = new Label(compoPreview, SWT.NONE);
        imagePreview.setForeground(parent.getForeground());
        imagePreview.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(txtHeight, 10);
        fd.left = new FormAttachment(0);
        imagePreview.setLayoutData(fd);
        
        Label btnHelp2 = new Label(compoIcon, SWT.NONE);
        btnHelp2.setForeground(parent.getForeground());
        btnHelp2.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblReminder, 10);
        fd.bottom = new FormAttachment(lblReminder, 40, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(0, 40);
        btnHelp2.setLayoutData(fd);
        btnHelp2.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { mouseOverHelpButton = true; btnHelp2.redraw(); } });
        btnHelp2.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { mouseOverHelpButton = false; btnHelp2.redraw(); } });
        btnHelp2.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(HELP_ICON, 2, 2);
            }
        });
        btnHelp2.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceIcon.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceIcon.html"); } });
        
        helpLbl = new Label(compoIcon, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp2, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp2, 5);
        helpLbl.setLayoutData(fd);
        
        refreshControls();            
    }
    
    private void showPreviewImage() {
        showPreviewImage(0, 0);
    }
    
    private void showPreviewImage(int forceWidth, int forceHeight) {
        Image image = imagePreview.getImage();
        if ( image != null ) {
            imagePreview.setImage(null);
            imagePreview.setSize(imagePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            compoPreview.setSize(compoPreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            image.dispose();
        }
        
        if ( fileTree.getItemCount() == 0 || fileTree.getSelection().length == 0 )
            return;
        
        String location = (String)fileTree.getSelection()[0].getData("location");
        if ( location == null )
            return;
        
        logger.trace("Showing preview for image \""+location+"\"");
        
        ImageData imageData;
        try {
            imageData = new ImageData(location);
        } catch (SWTException e) {
            logger.error("Cannot get image from file \""+location+"\"", e);
            return;
        }
        
        ImageData imagePreviewData;
        
        if ( btnNoResize.getSelection() ) {
            imagePreviewData = imageData;
        } else if ( btnAutoResize.getSelection() ) {
            Rectangle rect = elementEditPart.getFigure().getBounds();
            int width = forceWidth != 0 ? forceWidth : rect.width;
            int height = forceHeight != 0 ? forceHeight : rect.height;
            imagePreviewData = imageData.scaledTo(width, height);
        } else {
            int width = txtWidth.getText().isEmpty() ? 0 : Integer.parseInt(txtWidth.getText());
            int height = txtHeight.getText().isEmpty() ? 0 : Integer.parseInt(txtHeight.getText());
            
            if ( width == 0 && height == 0 )
                imagePreviewData = imageData;
            else {
                if ( width > 0 & width < 10 ) width = 10;
                if ( height > 0 & height < 10 ) height = 10;
                
                if ( width == 0 ) {
                    float scale = (float)height/imageData.height;
                    width = (int)(imageData.width * scale);
                } if ( height == 0 ) {
                    float scale = (float)width/imageData.width;
                    height = (int)(imageData.height * scale);
                }
                imagePreviewData = imageData.scaledTo(width, height);
            }
        }
        
        image = new Image(Display.getCurrent(), imagePreviewData);
        imagePreview.setImage(image);
        imagePreview.setSize(imagePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        compoPreview.setSize(compoPreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        compoPreview.setVisible(true);
    }
    
    private Listener fileTreeSelectionListener = new Listener() {
        public void handleEvent(Event e) {
            Image image = imagePreview.getImage();
            if ( image != null ) {
                imagePreview.setImage(null);
                imagePreview.setSize(imagePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                compoPreview.setSize(compoPreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                image.dispose();
            }
            
            compoPreview.setVisible(false);
            
            TreeItem treeItem = (TreeItem) e.item;
            if (treeItem == null)
                return;

            if ( treeItem.getImage() == openedFolderImage ) {
                treeItem.setImage(closedFolderImage);
                treeItem.setExpanded(false);
                return;
            }

            String location = (String)treeItem.getData("location");
            if ( location == null )
                return;
            
            File file = new File(location);

            if ( file.isDirectory() ) {
                logger.trace("Getting folder content : \""+file.getPath()+"\"");
                for ( TreeItem item : treeItem.getItems() ) {
                    item.dispose();
                }

                treeItem.setImage(openedFolderImage);

                File[] folders = file.listFiles(foldersFilter);
                if (folders != null) {
                    Arrays.sort(folders, nameComparator);
                    for (int i = 0; i < folders.length; i++) {
                        File folder = folders[i];
                        logger.trace("found folder : "+Paths.get(folder.getName()));
                        TreeItem subItem = new TreeItem(treeItem, SWT.NONE);
                        subItem.setText(folder.getName());
                        subItem.setImage(closedFolderImage);
                        subItem.setData("location", folder.getPath());
                        new TreeItem(subItem, SWT.NONE);         // to show the arrow in front of the folder
                    }
                }

                File[] files = file.listFiles(imagesFilter);
                if (files != null) {
                    Arrays.sort(files, nameComparator);
                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        logger.trace("found image : "+Paths.get(f.getName()));
                        TreeItem subItem = new TreeItem(treeItem, SWT.NONE);
                        subItem.setText(f.getName());
                        subItem.setData("location", f.getPath());
                    }
                }
                treeItem.setExpanded(true);

            } else {
                showPreviewImage();
            }
        }
    };

    private FileFilter imagesFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if ( file.isFile() && (file.getName().lastIndexOf('.') != -1) )
                return SpecializationPlugin.inArray(validImageSuffixes, (file.getName().substring(file.getName().lastIndexOf('.')+1)).toLowerCase());
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
    
    /**
     * This listener validates that the data entered in a Text Widget is numerical
     */
    private VerifyListener numberOnlyVerifyListener = new VerifyListener() {
        @Override
        public void verifyText(VerifyEvent event) {
            event.doit = false;
            try {
                if ( (event.character == '\b') || (Integer.parseInt(event.text) != -1) )
                    event.doit = true;
            } catch (NumberFormatException ignore) {}
        }
    };
    
    private SelectionListener resizeSelectionListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent event) {
            txtWidth.setEnabled(event.widget == btnCustomResize);
            txtHeight.setEnabled(event.widget == btnCustomResize);
            
            showPreviewImage();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
            widgetSelected(event);
        }
    };
    
    private ModifyListener resizeModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            showPreviewImage();
        }
    };
    
    private SelectionListener setIconSelectionListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent event) {
        	TreeItem selectedTreeItem = fileTree.getSelection()[0];
        	TreeItem rootTreeItem = selectedTreeItem;
        	while ( rootTreeItem.getParentItem() != null )
        		rootTreeItem = rootTreeItem.getParentItem();
            String rootLocation = (String)rootTreeItem.getData("location");
            String location = (String)selectedTreeItem.getData("location");
            if ( !location.startsWith(rootLocation) ) {
                logger.error("The file location does not start with the root location");
                logger.error("   file location = " + location);
                logger.error("   root location = " + rootLocation);
            } else {
                txtIconName.setText("/"+(String)rootTreeItem.getData("folder")+"/"+location.substring(rootLocation.length()+1).replace("\\","/"));
                
                if ( btnAutoResize.getSelection() )
                    txtIconSize.setText("auto");
                else if ( btnCustomResize.getSelection() )
                    if ( txtWidth.getText().isEmpty() && txtHeight.getText().isEmpty() )
                        txtIconSize.setText("");
                    else
                        txtIconSize.setText( (txtWidth.getText().isEmpty() ? "0" : txtWidth.getText()) + "x" + (txtHeight.getText().isEmpty() ? "0" : txtHeight.getText()) );
                else
                    txtIconSize.setText("");
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
            widgetSelected(event);
        }
    };
    
    /**
     * Called when the icon name is changed in the txtIconName text widget
     */
    private ModifyListener iconModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            Text text = (Text)event.widget;
            String value = text.getText();
            IArchimateElement concept = elementEditPart.getModel().getArchimateConcept();
            if ( value.isEmpty() )
                SpecializationPlugin.deleteProperty(concept, "icon");
            else
                SpecializationPlugin.setProperty(concept, "icon", value);
            // we force the icon to refresh on the graphical object
            elementEditPart.getModel().getArchimateConcept().setName(elementEditPart.getModel().getArchimateConcept().getName());
        }
    };
    
    /**
     * Called when the icon size is changed in the txtIconSize text widget
     */
    private ModifyListener iconSizeModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            Text text = (Text)event.widget;
            String value = text.getText();
            IArchimateElement concept = elementEditPart.getModel().getArchimateConcept();
            if ( value.isEmpty() )
                SpecializationPlugin.deleteProperty(concept, "icon size");
            else
                SpecializationPlugin.setProperty(concept, "icon size", value);
            // we force the icon to refresh on the graphical object
            elementEditPart.getModel().getArchimateConcept().setName(elementEditPart.getModel().getArchimateConcept().getName());
        }
    };
    
    /**
     * Called when the icon location is changed in the txtIconLocation text widget
     */
    private ModifyListener iconLocationModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            Text text = (Text)event.widget;
            String value = text.getText();
            IArchimateElement concept = elementEditPart.getModel().getArchimateConcept();
            if ( value.isEmpty() )
                SpecializationPlugin.deleteProperty(concept, "icon location");
            else
                SpecializationPlugin.setProperty(concept, "icon location", value);
            // we force the icon to refresh on the graphical object
            elementEditPart.getModel().getArchimateConcept().setName(elementEditPart.getModel().getArchimateConcept().getName());
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
        //TODO: show up the image in the fileTree if it exists
    }

    private void refreshControls() {
        logger.trace("Refreshing controls");
        
        if ( elementEditPart == null )
            return;
        
        if ( !SpecializationPlugin.mustReplaceIcon(elementEditPart.getModel()) ) {
            compoNoIcon.setVisible(true);
            compoIcon.setVisible(false);
            logger.trace("You must configure the view or the model to allow icons replacement.");
            lblNoIcon.setText("You must configure the view or the model to allow icons replacement.");
            return;
        }
        
        IFigure figure = elementEditPart.getFigure();
        IFigureDelegate figureDelegate = ((AbstractTextControlContainerFigure)figure).getFigureDelegate();

        if ( figureDelegate instanceof RectangleFigureDelegate ) {
            compoNoIcon.setVisible(false);
            logger.trace("Please change the element's figure to show up the icon.");
            lblNoIcon.setText("Please change the element's figure to show up the icon.");
            compoIcon.setVisible(true);
        } else {
            compoNoIcon.setVisible(true);
            compoIcon.setVisible(false);
            return;
        }
        
        txtIconName.removeModifyListener(iconModifyListener);
        String iconName = SpecializationPlugin.getPropertyValue(elementEditPart.getModel().getArchimateConcept(), "icon");
        txtIconName.setText(iconName == null ? "" : iconName);
        txtIconName.addModifyListener(iconModifyListener);
        
        txtIconSize.removeModifyListener(iconSizeModifyListener);
        String iconSize = SpecializationPlugin.getPropertyValue(elementEditPart.getModel().getArchimateConcept(), "icon size");
        txtIconSize.setText(iconSize == null ? "" : iconSize);
        txtIconSize.addModifyListener(iconSizeModifyListener);
        
        txtIconLocation.removeModifyListener(iconLocationModifyListener);
        String iconLocation = SpecializationPlugin.getPropertyValue(elementEditPart.getModel().getArchimateConcept(), "icon location");
        txtIconLocation.setText(iconLocation == null ? "" : iconLocation);
        txtIconLocation.addModifyListener(iconLocationModifyListener);
        
        fileTree.removeAll();
        
        int lines = SpecializationPlugin.INSTANCE.getPreferenceStore().getInt(SpecializationPlugin.storeFolderPrefix+"_#");
        
        if( lines == 0 ) {
            SpecializationPlugin.popup(Level.INFO, "No image folder has been defined.\n\nPlease define image folders on the preference page.");
            return;
        }
        
        for (int line = 0; line <lines; line++) {
        	String folder = SpecializationPlugin.INSTANCE.getPreferenceStore().getString(SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line));
        	String location = SpecializationPlugin.INSTANCE.getPreferenceStore().getString(SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line));
            TreeItem rootItem = new TreeItem(fileTree, SWT.NONE);
            rootItem.setText(folder);
            rootItem.setImage(closedFolderImage);
            try {
				rootItem.setData("location", new File(location).getCanonicalPath());
				rootItem.setData("folder", folder);
			} catch (IOException e) {
				logger.error("Cannot access folder "+location);
				rootItem.dispose();
			}
        }
    }
}
