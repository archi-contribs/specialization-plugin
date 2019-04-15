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
import org.archicontribs.specialization.commands.SpecializationPropertyCommand;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
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
import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.model.*;
import com.archimatetool.model.impl.Bounds;


public class SpecializationIconSection extends org.archicontribs.specialization.propertysections.AbstractArchimatePropertySection {
    static final SpecializationLogger logger = new SpecializationLogger(SpecializationIconSection.class);

    ArchimateElementEditPart elementEditPart = null;

    private Composite compoIcon;
    private Composite compoNoIcon;
    private Label lblNoIcon;
    Text txtIconName;
    Text txtIconSize;
    Text txtIconLocation;
    Tree fileTree;
    Composite compoPreview;
    private Button btnNoResize;
    Button btnAutoResize;
    Button btnCustomResize;
    Text txtWidth;
    Text txtHeight;
    Label imagePreview;
    
    boolean mouseOverHelpButton = false;

    static final String[] validImageSuffixes = {"jpg", "png", "gif", "bmp", "ico"};
    static final Image    closedFolderImage  = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/16x16/closedFolder.png"));
    static final Image    openedFolderImage  = new Image(Display.getDefault(), SpecializationPlugin.class.getResourceAsStream("/img/16x16/openedFolder.png"));

    /**
     * Filter to show or reject this section depending on input value
     */
    public static class Filter extends ObjectFilter {
        @Override
        protected boolean isRequiredType(Object object) {
            if ( object == null )
                return false;

            if ( !(object instanceof ArchimateElementEditPart) )
                return false;

            // we show up the icon tab if the figure has got an icon
            // i.e. if the Figure class has got a drawIcon method
            // the loggerLevel is a private property, so we use reflection to access it
            try {
                ((ArchimateElementEditPart)object).getFigure().getClass().getDeclaredMethod("drawIcon", Graphics.class);
            } catch (@SuppressWarnings("unused") NoSuchMethodException ign) {
                logger.trace("Hiding icon tab as the element as the element is not able to draw an icon.");
                return false;
            } catch (SecurityException e) {
                logger.error("Failed to check for \"drawIcon()\" method", e);
                return false;
            }

            logger.trace("Showing icon tab.");
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
        this.compoNoIcon = new Composite(parent, SWT.NONE);
        this.compoNoIcon.setForeground(parent.getForeground());
        this.compoNoIcon.setBackground(parent.getBackground());
        this.compoNoIcon.setLayout(new FormLayout());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.compoNoIcon.setLayoutData(fd);

        this.lblNoIcon = new Label(this.compoNoIcon, SWT.NONE);
        this.lblNoIcon.setText("Please change the element's figure to show up the icon.");
        this.lblNoIcon.setForeground(parent.getForeground());
        this.lblNoIcon.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        this.lblNoIcon.setLayoutData(fd);
        
        Label btnHelp = new Label(this.compoNoIcon, SWT.NONE);
        btnHelp.setForeground(parent.getForeground());
        btnHelp.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(this.lblNoIcon, 25);
        fd.bottom = new FormAttachment(this.lblNoIcon, 55, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 20);
        fd.right = new FormAttachment(0, 50);
        btnHelp.setLayoutData(fd);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { SpecializationIconSection.this.mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { SpecializationIconSection.this.mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( SpecializationIconSection.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(SpecializationPlugin.HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceIcon.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceIcon.html"); } });
        
        Label helpLbl = new Label(this.compoNoIcon, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp, 5);
        helpLbl.setLayoutData(fd);

        /* ********************************************************* */
        this.compoIcon = new Composite(parent, SWT.NONE);
        this.compoIcon.setForeground(parent.getForeground());
        this.compoIcon.setBackground(parent.getBackground());
        this.compoIcon.setLayout(new FormLayout());
        fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        this.compoIcon.setLayoutData(fd);

        Label lblIconName = new Label(this.compoIcon, SWT.NONE);
        lblIconName.setText("Icon :");
        lblIconName.setForeground(parent.getForeground());
        lblIconName.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 10);
        lblIconName.setLayoutData(fd);
        
        Label lblIconSize = new Label(this.compoIcon, SWT.NONE);
        lblIconSize.setText("Icon size:");
        lblIconSize.setForeground(parent.getForeground());
        lblIconSize.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblIconName, 10);
        fd.left = new FormAttachment(0, 10);
        lblIconSize.setLayoutData(fd);

        Label lblIconLocation = new Label(this.compoIcon, SWT.NONE);
        lblIconLocation.setText("Icon location:");
        lblIconLocation.setForeground(parent.getForeground());
        lblIconLocation.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblIconSize, 10);
        fd.left = new FormAttachment(0, 10);
        lblIconLocation.setLayoutData(fd);

        this.txtIconName = new Text(this.compoIcon, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconName, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIconLocation, 5);
        fd.right = new FormAttachment(0, 400);
        this.txtIconName.setLayoutData(fd);
        this.txtIconName.addModifyListener(this.iconModifyListener);

        this.txtIconSize = new Text(this.compoIcon, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconSize, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIconLocation, 5);
        fd.right = new FormAttachment(0, 150);
        this.txtIconSize.setLayoutData(fd);
        this.txtIconSize.addModifyListener(this.iconSizeModifyListener);

        this.txtIconLocation = new Text(this.compoIcon, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblIconLocation, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblIconLocation, 5);
        fd.right = new FormAttachment(0, 150);
        this.txtIconLocation.setLayoutData(fd);
        this.txtIconLocation.addModifyListener(this.iconLocationModifyListener);

        this.fileTree = new Tree(this.compoIcon, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        this.fileTree.setBackground(parent.getBackground());
        this.fileTree.addListener(SWT.Selection, this.fileTreeSelectionListener);
        this.fileTree.addListener(SWT.Expand, this.fileTreeSelectionListener);
        this.fileTree.addListener(SWT.Collapse, this.fileTreeSelectionListener);

        fd = new FormData();
        fd.top = new FormAttachment(this.txtIconLocation, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(this.txtIconName, 0, SWT.RIGHT);
        fd.bottom = new FormAttachment(0, 220);
        this.fileTree.setLayoutData(fd);

        Label lblReminder = new Label(this.compoIcon, SWT.NONE);
        lblReminder.setText("(you may define new image folders on the preference page)");
        lblReminder.setForeground(parent.getForeground());
        lblReminder.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(this.fileTree, 3);
        fd.left = new FormAttachment(this.fileTree, 0, SWT.CENTER);
        lblReminder.setLayoutData(fd);
        
        this.compoPreview = new Composite(this.compoIcon, SWT.RESIZE);
        this.compoPreview.setForeground(parent.getForeground());
        this.compoPreview.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(this.fileTree, 0, SWT.TOP);
        fd.left = new FormAttachment(this.fileTree, 10, SWT.RIGHT);
        this.compoPreview.setLayoutData(fd);
        this.compoPreview.setVisible(false);
        this.compoPreview.setLayout(new FormLayout());
                
        Button btnSetIcon = new Button(this.compoPreview, SWT.NONE);
        btnSetIcon.setText("Set Icon");
        fd = new FormData();
        fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        btnSetIcon.setLayoutData(fd);
        btnSetIcon.addSelectionListener(this.setIconSelectionListener);
        
        Label lblResize = new Label(this.compoPreview, SWT.NONE);
        lblResize.setText("Resize to");
        lblResize.setForeground(parent.getForeground());
        lblResize.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnSetIcon, 10);
        fd.left = new FormAttachment(0);
        lblResize.setLayoutData(fd);

        this.btnNoResize = new Button(this.compoPreview, SWT.RADIO);
        this.btnNoResize.setForeground(parent.getForeground());
        this.btnNoResize.setBackground(parent.getBackground());
        this.btnNoResize.setText("Do not Resize");
        fd = new FormData();
        fd.top = new FormAttachment(lblResize, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblResize, 5);
        this.btnNoResize.setLayoutData(fd);
        this.btnNoResize.setSelection(true);
        this.btnNoResize.addSelectionListener(this.resizeSelectionListener);
        
        this.btnAutoResize = new Button(this.compoPreview, SWT.RADIO);
        this.btnAutoResize.setForeground(parent.getForeground());
        this.btnAutoResize.setBackground(parent.getBackground());
        this.btnAutoResize.setText("Figure's size");
        fd = new FormData();
        fd.top = new FormAttachment(this.btnNoResize, 5);
        fd.left = new FormAttachment(this.btnNoResize, 0, SWT.LEFT);
        this.btnAutoResize.setLayoutData(fd);
        this.btnAutoResize.addSelectionListener(this.resizeSelectionListener);
        
        this.btnCustomResize = new Button(this.compoPreview, SWT.RADIO);
        this.btnCustomResize.setForeground(parent.getForeground());
        this.btnCustomResize.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(this.btnAutoResize, 5);
        fd.left = new FormAttachment(this.btnAutoResize, 0, SWT.LEFT);
        this.btnCustomResize.setLayoutData(fd);
        this.btnCustomResize.addSelectionListener(this.resizeSelectionListener);
        
        this.txtWidth = new Text(this.compoPreview, SWT.BORDER);
        this.txtWidth.setTextLimit(4);
        fd = new FormData();
        fd.top = new FormAttachment(this.btnCustomResize, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.btnCustomResize, 5, SWT.RIGHT);
        fd.right = new FormAttachment(this.btnCustomResize, 35, SWT.RIGHT);
        this.txtWidth.setLayoutData(fd);
        this.txtWidth.addVerifyListener(this.numberOnlyVerifyListener);
        this.txtWidth.addModifyListener(this.resizeModifyListener);
        this.txtWidth.setEnabled(false);
        
        Label lblX = new Label(this.compoPreview, SWT.NONE);
        lblX.setText("x");
        lblX.setForeground(parent.getForeground());
        lblX.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(this.txtWidth, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.txtWidth, 5, SWT.RIGHT);
        lblX.setLayoutData(fd);
        
        this.txtHeight = new Text(this.compoPreview, SWT.BORDER);
        this.txtHeight.setTextLimit(4);
        fd = new FormData();
        fd.top = new FormAttachment(lblX, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblX, 5, SWT.RIGHT);
        fd.right = new FormAttachment(lblX, 35, SWT.RIGHT);
        this.txtHeight.setLayoutData(fd);
        this.txtHeight.addVerifyListener(this.numberOnlyVerifyListener);
        this.txtHeight.addModifyListener(this.resizeModifyListener);
        this.txtHeight.setEnabled(false);

        this.imagePreview = new Label(this.compoPreview, SWT.NONE);
        this.imagePreview.setForeground(parent.getForeground());
        this.imagePreview.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(this.txtHeight, 10);
        fd.left = new FormAttachment(0);
        this.imagePreview.setLayoutData(fd);
        
        Label btnHelp2 = new Label(this.compoIcon, SWT.NONE);
        btnHelp2.setForeground(parent.getForeground());
        btnHelp2.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(lblReminder, 10);
        fd.bottom = new FormAttachment(lblReminder, 40, SWT.BOTTOM);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(0, 40);
        btnHelp2.setLayoutData(fd);
        btnHelp2.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { SpecializationIconSection.this.mouseOverHelpButton = true; btnHelp2.redraw(); } });
        btnHelp2.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { SpecializationIconSection.this.mouseOverHelpButton = false; btnHelp2.redraw(); } });
        btnHelp2.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( SpecializationIconSection.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(SpecializationPlugin.HELP_ICON, 2, 2);
            }
        });
        btnHelp2.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceIcon.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+SpecializationPlugin.PLUGIN_ID+"/help/html/replaceIcon.html"); } });
        
        helpLbl = new Label(this.compoIcon, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setForeground(parent.getForeground());
        helpLbl.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp2, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp2, 5);
        helpLbl.setLayoutData(fd);
        
        refreshControls();            
    }
    
    void showPreviewImage() {
        showPreviewImage(0, 0);
    }
    
    void showPreviewImage(int forceWidth, int forceHeight) {
        Image image = this.imagePreview.getImage();
        if ( image != null ) {
            this.imagePreview.setImage(null);
            this.imagePreview.setSize(this.imagePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            this.compoPreview.setSize(this.compoPreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            image.dispose();
        }
        
        if ( this.fileTree.getItemCount() == 0 || this.fileTree.getSelection().length == 0 )
            return;
        
        String location = (String)this.fileTree.getSelection()[0].getData("location");
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
        
        if ( this.btnNoResize.getSelection() ) {
            imagePreviewData = imageData;
        } else if ( this.btnAutoResize.getSelection() ) {
            Rectangle rect = this.elementEditPart.getFigure().getBounds();
            int width = forceWidth != 0 ? forceWidth : rect.width;
            int height = forceHeight != 0 ? forceHeight : rect.height;
            imagePreviewData = imageData.scaledTo(width, height);
        } else {
            int width = this.txtWidth.getText().isEmpty() ? 0 : Integer.parseInt(this.txtWidth.getText());
            int height = this.txtHeight.getText().isEmpty() ? 0 : Integer.parseInt(this.txtHeight.getText());
            
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
        this.imagePreview.setImage(image);
        this.imagePreview.setSize(this.imagePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        this.compoPreview.setSize(this.compoPreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        this.compoPreview.setVisible(true);
    }
    
    private Listener fileTreeSelectionListener = new Listener() {
        @Override
        public void handleEvent(Event e) {
            Image image = SpecializationIconSection.this.imagePreview.getImage();
            if ( image != null ) {
                SpecializationIconSection.this.imagePreview.setImage(null);
                SpecializationIconSection.this.imagePreview.setSize(SpecializationIconSection.this.imagePreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                SpecializationIconSection.this.compoPreview.setSize(SpecializationIconSection.this.compoPreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                image.dispose();
            }
            
            SpecializationIconSection.this.compoPreview.setVisible(false);
            
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

                File[] folders = file.listFiles(SpecializationIconSection.this.foldersFilter);
                if (folders != null) {
                    Arrays.sort(folders, SpecializationIconSection.this.nameComparator);
                    for (int i = 0; i < folders.length; i++) {
                        File folder = folders[i];
                        logger.trace("found folder : "+Paths.get(folder.getName()));
                        TreeItem subItem = new TreeItem(treeItem, SWT.NONE);
                        subItem.setText(folder.getName());
                        subItem.setImage(closedFolderImage);
                        subItem.setData("location", folder.getPath());
                        @SuppressWarnings("unused")
                        TreeItem newTreeItem = new TreeItem(subItem, SWT.NONE);         // to show the arrow in front of the folder
                    }
                }

                File[] files = file.listFiles(SpecializationIconSection.this.imagesFilter);
                if (files != null) {
                    Arrays.sort(files, SpecializationIconSection.this.nameComparator);
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

    FileFilter imagesFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if ( file.isFile() && (file.getName().lastIndexOf('.') != -1) )
                return SpecializationPlugin.inArray(validImageSuffixes, (file.getName().substring(file.getName().lastIndexOf('.')+1)).toLowerCase());
            return false;
        }
    };

    FileFilter foldersFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    Comparator<File> nameComparator = new Comparator<File>() {
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
            } catch (@SuppressWarnings("unused") NumberFormatException ignore) {
                // nothing to do
            }
        }
    };
    
    private SelectionListener resizeSelectionListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent event) {
            SpecializationIconSection.this.txtWidth.setEnabled(event.widget == SpecializationIconSection.this.btnCustomResize);
            SpecializationIconSection.this.txtHeight.setEnabled(event.widget == SpecializationIconSection.this.btnCustomResize);
            
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
        	TreeItem selectedTreeItem = SpecializationIconSection.this.fileTree.getSelection()[0];
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
            	String locationName = location.substring(rootLocation.length()).replace("\\","/");
                SpecializationIconSection.this.txtIconName.setText("/"+(String)rootTreeItem.getData("folder")+(locationName.startsWith("/")?"":"/")+locationName);
                
                if ( SpecializationIconSection.this.btnAutoResize.getSelection() )
                    SpecializationIconSection.this.txtIconSize.setText("auto");
                else if ( SpecializationIconSection.this.btnCustomResize.getSelection() )
                    if ( SpecializationIconSection.this.txtWidth.getText().isEmpty() && SpecializationIconSection.this.txtHeight.getText().isEmpty() )
                        SpecializationIconSection.this.txtIconSize.setText("");
                    else
                        SpecializationIconSection.this.txtIconSize.setText( (SpecializationIconSection.this.txtWidth.getText().isEmpty() ? "0" : SpecializationIconSection.this.txtWidth.getText()) + "x" + (SpecializationIconSection.this.txtHeight.getText().isEmpty() ? "0" : SpecializationIconSection.this.txtHeight.getText()) );
                else
                    SpecializationIconSection.this.txtIconSize.setText("");
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
            IArchimateElement concept = SpecializationIconSection.this.elementEditPart.getModel().getArchimateConcept();
            String value = ((Text)event.widget).getText();
            if ( value.isEmpty() ) value = null;        // null value allows to delete the property
            
            SpecializationPropertyCommand command = new SpecializationPropertyCommand(concept, "icon", value);

            if ( command.canExecute() ) {
                CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
                compoundCommand.add(command);

                CommandStack stack = (CommandStack) concept.getArchimateModel().getAdapter(CommandStack.class);
                stack.execute(compoundCommand);
                
                // we force the label to refresh on the graphical object
                SpecializationIconSection.this.elementEditPart.getModel().getArchimateConcept().setName(SpecializationIconSection.this.elementEditPart.getModel().getArchimateConcept().getName());
            }
        }
    };
    
    /**
     * Called when the icon size is changed in the txtIconSize text widget
     */
    private ModifyListener iconSizeModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            IArchimateElement concept = SpecializationIconSection.this.elementEditPart.getModel().getArchimateConcept();
            String value = ((Text)event.widget).getText();
            if ( value.isEmpty() ) value = null;        // null value allows to delete the property
            
            SpecializationPropertyCommand command = new SpecializationPropertyCommand(concept, "icon size", value);

            if ( command.canExecute() ) {
                CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
                compoundCommand.add(command);

                CommandStack stack = (CommandStack) concept.getArchimateModel().getAdapter(CommandStack.class);
                stack.execute(compoundCommand);
                
                // we force the label to refresh on the graphical object
                SpecializationIconSection.this.elementEditPart.getModel().getArchimateConcept().setName(SpecializationIconSection.this.elementEditPart.getModel().getArchimateConcept().getName());
            }
        }
    };
    
    /**
     * Called when the icon location is changed in the txtIconLocation text widget
     */
    private ModifyListener iconLocationModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent event) {
            IArchimateElement concept = SpecializationIconSection.this.elementEditPart.getModel().getArchimateConcept();
            String value = ((Text)event.widget).getText();
            if ( value.isEmpty() ) value = null;        // null value allows to delete the property
            
            SpecializationPropertyCommand command = new SpecializationPropertyCommand(concept, "icon location", value);

            if ( command.canExecute() ) {
                CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
                compoundCommand.add(command);

                CommandStack stack = (CommandStack) concept.getArchimateModel().getAdapter(CommandStack.class);
                stack.execute(compoundCommand);
            }
        }
    };

    @Override
    protected Adapter getECoreAdapter() {
        return this.eAdapter;
    }

    @Override
    protected EObject getEObject() {
        if ( this.elementEditPart == null )
            return null;

        return this.elementEditPart.getModel();
    }

    @Override
    protected void setElement(Object element) {
        this.elementEditPart = (ArchimateElementEditPart)new Filter().adaptObject(element);
        if(this.elementEditPart == null) {
            logger.error("failed to get elementEditPart for " + element); //$NON-NLS-1$
        }

        refreshControls();
        //TODO: show up the image in the fileTree if it exists
    }

    void refreshControls() {
        logger.trace("Refreshing controls");
        
        if ( this.elementEditPart == null )
            return;
        
        if ( !SpecializationPlugin.mustReplaceIcon(this.elementEditPart.getModel()) ) {
            this.compoNoIcon.setVisible(true);
            this.compoIcon.setVisible(false);
            logger.trace("You must configure the view or the model to allow icons replacement.");
            this.lblNoIcon.setText("You must configure the view or the model to allow icons replacement.");
            return;
        }
        
        //TODO: find a better (and working) way to calculate if the icon is shown
        //IFigure figure = elementEditPart.getFigure();
        //IFigureDelegate figureDelegate = ((AbstractTextControlContainerFigure)figure).getFigureDelegate();
        //if ( figureDelegate instanceof RectangleFigureDelegate ) {
        this.compoNoIcon.setVisible(false);
        this.compoIcon.setVisible(true);
        //} else {
        //    logger.trace("Please change the element's figure to show up the icon.");
        //    lblNoIcon.setText("Please change the element's figure to show up the icon.");
        //    compoNoIcon.setVisible(true);
        //    compoIcon.setVisible(false);
        //    return;
        //}
        
        this.txtIconName.removeModifyListener(this.iconModifyListener);
        String iconName = SpecializationPlugin.getPropertyValue(this.elementEditPart.getModel().getArchimateConcept(), "icon");
        this.txtIconName.setText(iconName == null ? "" : iconName);
        this.txtIconName.addModifyListener(this.iconModifyListener);
        
        this.txtIconSize.removeModifyListener(this.iconSizeModifyListener);
        String iconSize = SpecializationPlugin.getPropertyValue(this.elementEditPart.getModel().getArchimateConcept(), "icon size");
        this.txtIconSize.setText(iconSize == null ? "" : iconSize);
        this.txtIconSize.addModifyListener(this.iconSizeModifyListener);
        
        this.txtIconLocation.removeModifyListener(this.iconLocationModifyListener);
        String iconLocation = SpecializationPlugin.getPropertyValue(this.elementEditPart.getModel().getArchimateConcept(), "icon location");
        this.txtIconLocation.setText(iconLocation == null ? "" : iconLocation);
        this.txtIconLocation.addModifyListener(this.iconLocationModifyListener);
        
        this.fileTree.removeAll();
        
        int lines = SpecializationPlugin.INSTANCE.getPreferenceStore().getInt(SpecializationPlugin.storeFolderPrefix+"_#");
        
        if( lines == 0 ) {
            SpecializationPlugin.popup(Level.INFO, "No image folder has been defined.\n\nPlease define image folders on the preference page.");
            return;
        }
        
        for (int line = 0; line <lines; line++) {
        	String folder = SpecializationPlugin.INSTANCE.getPreferenceStore().getString(SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line));
        	String location = SpecializationPlugin.INSTANCE.getPreferenceStore().getString(SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line));
            TreeItem rootItem = new TreeItem(this.fileTree, SWT.NONE);
            rootItem.setText(folder);
            rootItem.setImage(closedFolderImage);
            try {
				rootItem.setData("location", new File(location).getCanonicalPath());
				rootItem.setData("folder", folder);
			} catch (@SuppressWarnings("unused") IOException ign) {
				logger.error("Cannot access folder "+location);
				rootItem.dispose();
			}
        }
    }
}
