package org.archicontribs.specialization.propertysections;

import java.lang.reflect.Field;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.archimatetool.editor.ArchiPlugin;
import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.figures.IDiagramModelObjectFigure;
import com.archimatetool.editor.diagram.figures.RectangleFigureDelegate;
import com.archimatetool.editor.ui.FigureImagePreviewFactory;
import com.archimatetool.editor.ui.factory.ObjectUIFactory;

public class ElementFigure extends Composite {
    static final SpecializationLogger logger = new SpecializationLogger(ElementFigure.class);
    
    static final int figureMargin = 2;
    static final String canChangeIconString = "canChangeIcon";
    
    Label lblIconSize = null;
    Label lblIconLocation = null;
    Button btnNewIcon = null;
    Button btnDeleteIcon = null;
    Text txtIconSize= null;
    Text txtIconLocation = null;
    
    Composite outerCompo1 = null;
    Composite innerCompo1 = null;
    Composite outerCompo2 = null;
    Composite innerCompo2 = null;
    Label figure1 = null;
    Label figure2 = null;
    
    Composite selectedFigure = null;

    public ElementFigure(Composite parent, int type) {
        super(parent, type);
        setBackgroundMode(SWT.INHERIT_DEFAULT);
        setLayout(new FormLayout());
        
        // figure 1
        this.outerCompo1 = new Composite(this, SWT.NONE);
        this.setBackground(parent.getBackground());
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(0, 0);
        fd.width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH) + 2*figureMargin;
        fd.height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT) + 2*figureMargin;
        this.outerCompo1.setLayoutData(fd);
        this.outerCompo1.setLayout(new FormLayout());
        //this.outerCompo1.addPaintListener(this.redrawListener);

        this.innerCompo1 = new Composite(this.outerCompo1, SWT.NONE);
        this.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, figureMargin);
        fd.left = new FormAttachment(0, figureMargin);
        fd.width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH);
        fd.height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT);
        this.innerCompo1.setLayoutData(fd);
        this.innerCompo1.setLayout(new FormLayout());
        
        this.figure1 = new Label(this.innerCompo1, SWT.NULL);
        this.figure1.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.figure1.setData("imageFigure", this);
        this.figure1.addMouseListener(this.selectListener);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(0, 0);
        fd.width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH);
        fd.height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT);
        this.figure1.setLayoutData(fd);
        
        // figure 2
        this.outerCompo2 = new Composite(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(this.outerCompo1, 5);
        fd.width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH) + 2*figureMargin;
        fd.height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT) + 2*figureMargin;
        this.outerCompo2.setLayoutData(fd);
        this.outerCompo2.setLayout(new FormLayout());
        //this.outerCompo2.addPaintListener(this.redrawListener);
        
        this.innerCompo2 = new Composite(this.outerCompo2, SWT.NONE);
        this.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, figureMargin);
        fd.left = new FormAttachment(0, figureMargin);
        fd.width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH);
        fd.height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT);
        this.innerCompo2.setLayoutData(fd);
        this.innerCompo2.setLayout(new FormLayout());

        this.figure2 = new Label(this.innerCompo2, SWT.NULL);
        this.figure2.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.figure2.setData("imageFigure", this);
        this.figure2.addMouseListener(this.selectListener);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(0, 0);
        fd.width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH);
        fd.height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT);
        this.figure2.setLayoutData(fd);

        
        // buttons icon
        this.btnNewIcon = new Button(this, SWT.PUSH);
        this.btnNewIcon.setImage(SpecializationPlugin.NEW_ICON);
        this.btnNewIcon.setToolTipText("set Icon");
        this.btnNewIcon.setEnabled(false);
        this.btnNewIcon.setVisible(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.outerCompo2, 0, SWT.TOP);
        fd.left = new FormAttachment(this.outerCompo2, 5);
        fd.right = new FormAttachment(this.outerCompo2, 40, SWT.RIGHT);
        this.btnNewIcon.setLayoutData(fd);

        this.btnDeleteIcon = new Button(this, SWT.PUSH);
        this.btnDeleteIcon.setImage(SpecializationPlugin.DELETE_ICON);
        this.btnDeleteIcon.setToolTipText("delete Icon");
        this.btnDeleteIcon.setEnabled(false);
        this.btnDeleteIcon.setVisible(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.btnNewIcon, 5);
        fd.left = new FormAttachment(this.outerCompo2, 5);
        fd.right = new FormAttachment(this.outerCompo2, 40, SWT.RIGHT);
        this.btnDeleteIcon.setLayoutData(fd);

        this.lblIconSize = new Label(this, SWT.NONE);
        this.lblIconSize.setForeground(this.getForeground());
        this.lblIconSize.setBackground(this.getBackground());
        this.lblIconSize.setText("Size:");
        this.lblIconSize.setEnabled(false);
        this.lblIconSize.setVisible(false);
        fd = new FormData();
        fd.top = new FormAttachment(0, 8);
        fd.left = new FormAttachment(this.btnNewIcon, 5);
        this.lblIconSize.setLayoutData(fd);

        this.txtIconSize = new Text(this, SWT.BORDER);
        this.txtIconSize.setToolTipText("Size of the icon");
        this.txtIconSize.setEnabled(false);
        this.txtIconSize.setVisible(false);

        this.lblIconLocation = new Label(this, SWT.NONE);
        this.lblIconLocation.setForeground(this.getForeground());
        this.lblIconLocation.setBackground(this.getBackground());
        this.lblIconLocation.setText("Location:");
        this.lblIconLocation.setEnabled(false);
        this.lblIconLocation.setVisible(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblIconSize, 5);
        fd.left = new FormAttachment(this.lblIconSize, 0, SWT.LEFT);
        this.lblIconLocation.setLayoutData(fd);

        this.txtIconLocation = new Text(this, SWT.BORDER);
        this.txtIconLocation.setToolTipText("Location of the icon");
        this.txtIconLocation.setEnabled(false);
        this.txtIconLocation.setVisible(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblIconLocation, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.lblIconLocation, 5);
        fd.right = new FormAttachment(this.lblIconLocation, 80, SWT.RIGHT);
        this.txtIconLocation.setLayoutData(fd);

        fd = new FormData();
        fd.top = new FormAttachment(this.lblIconSize, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.txtIconLocation, 0, SWT.LEFT);
        fd.right = new FormAttachment(this.txtIconLocation, 0, SWT.RIGHT);
        this.txtIconSize.setLayoutData(fd);
    }

    void reset() {
        this.figure1.setBackgroundImage(null);
        this.figure2.setBackgroundImage(null);
        this.selectedFigure = null;
    }

    void setEClass(EClass eClass) {
        if ( eClass == null ) {
            reset();
        } else {
            this.figure1.setBackgroundImage(FigureImagePreviewFactory.getPreviewImage(eClass, 0));
            this.figure2.setBackgroundImage(FigureImagePreviewFactory.getPreviewImage(eClass, 1));

            IDiagramModelObjectFigure figure = ((ArchimateElementEditPart)ObjectUIFactory.INSTANCE.getProviderForClass(eClass).createEditPart()).getFigure();

            try {
                // either the IDiagramModelObjectFigure delegates the drawing to a figure delegate,
            	// and in that case, we need to check if the figure delegate allows to change the icon
                Field field = figure.getClass().getSuperclass().getDeclaredField("fFigureDelegate1");
                field.setAccessible(true);
                this.outerCompo1.setData(canChangeIconString, (field.get(figure) instanceof RectangleFigureDelegate));
                field.setAccessible(false);
                
                field = figure.getClass().getSuperclass().getDeclaredField("fFigureDelegate2");
                field.setAccessible(true);
                this.outerCompo2.setData(canChangeIconString, (field.get(figure) instanceof RectangleFigureDelegate));
                field.setAccessible(false);
            } catch (@SuppressWarnings("unused") Exception err) {
            	// either there is no figure delegate, and then the icon can be changed.
            	this.outerCompo1.setData(canChangeIconString, true);
            	this.outerCompo2.setData(canChangeIconString, true);
            }
            
        }
    }
    
    void select(Composite figure) {
        this.selectedFigure = figure;
        
        this.outerCompo1.setBackground(figure == this.outerCompo1 ? ColorConstants.blue : this.outerCompo1.getParent().getBackground());
        this.outerCompo2.setBackground(figure == this.outerCompo2 ? ColorConstants.blue : this.outerCompo2.getParent().getBackground());
        
        if ( (this.selectedFigure != null) && (this.selectedFigure.getData(canChangeIconString) != null) ) {
            boolean canChangeIcon = (boolean)this.selectedFigure.getData(canChangeIconString);
            this.btnNewIcon.setVisible(canChangeIcon);
            this.btnDeleteIcon.setVisible(canChangeIcon);
            this.lblIconSize.setVisible(canChangeIcon);
            this.txtIconSize.setVisible(canChangeIcon);
            this.lblIconLocation.setVisible(canChangeIcon);
            this.txtIconLocation.setVisible(canChangeIcon);
            
            logger.trace("canchangeIcon = " + canChangeIcon);
        }
    }

    void select(int type) {
        if ( type == 0 )
            select(this.outerCompo1);
        else
            select(this.outerCompo2);
    }
    
    void setIconSize(String iconSize) {
        this.txtIconSize.setText(iconSize);
    }
    
    void setIconLocation(String iconLocation) {
        this.txtIconLocation.setText(iconLocation);
    }
    
    private MouseAdapter selectListener = new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent e) {
            if ( ((Label)e.widget).getBackgroundImage() != null )
            	select(((Label)e.widget).getParent().getParent());		// we select the outer composite
        }
    };
}