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
    
    Label lblIconSize = null;
    Label lblIconLocation = null;
    Button btnNewIcon = null;
    Button btnDeleteIcon = null;
    Text txtIconSize= null;
    Text txtIconLocation = null;
    
    Composite compo1 = null;
    Composite compo2 = null;
    Label figure1 = null;
    Label figure2 = null;
    
    Label selectedFigure = null;

    public ElementFigure(Composite parent, int type) {
        super(parent, type);
        setBackgroundMode(SWT.INHERIT_DEFAULT);
        setLayout(new FormLayout());
        
        // figure 1
        this.compo1 = new Composite(this, SWT.BORDER);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = figureMargin;
        gridLayout.marginHeight = figureMargin;
        this.compo1.setLayout(gridLayout);
        this.compo1.addPaintListener(this.redrawListener);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH) + 2*figureMargin;
        fd.left = new FormAttachment(0, 0);
        fd.height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT) + 2*figureMargin;
        this.compo1.setLayoutData(fd);
        
        this.figure1 = new Label(compo1, SWT.NULL);
        this.figure1.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.figure1.setData("imageFigure", this);
        this.figure1.addMouseListener(this.selectListener);
        
        // figure 2
        this.compo2 = new Composite(this, SWT.BORDER);
        gridLayout = new GridLayout();
        gridLayout.marginWidth = figureMargin;
        gridLayout.marginHeight = figureMargin;
        this.compo2.setLayout(gridLayout);
        this.compo2.addPaintListener(this.redrawListener);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH) + 2*figureMargin;
        fd.left = new FormAttachment(this.compo1, 5);
        fd.height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(com.archimatetool.editor.preferences.IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT) + 2*figureMargin;
        this.compo2.setLayoutData(fd);

        this.figure2 = new Label(compo2, SWT.NULL);
        this.figure2.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.figure2.setData("imageFigure", this);
        this.figure2.addMouseListener(this.selectListener);

        
        // buttons icon
        this.btnNewIcon = new Button(this, SWT.PUSH);
        this.btnNewIcon.setImage(SpecializationPlugin.NEW_ICON);
        this.btnNewIcon.setToolTipText("set Icon");
        this.btnNewIcon.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.compo2, 0, SWT.TOP);
        fd.left = new FormAttachment(this.compo2, 5);
        fd.right = new FormAttachment(this.compo2, 40, SWT.RIGHT);
        this.btnNewIcon.setLayoutData(fd);
        this.btnNewIcon.setVisible(false);

        this.btnDeleteIcon = new Button(this, SWT.PUSH);
        this.btnDeleteIcon.setImage(SpecializationPlugin.DELETE_ICON);
        this.btnDeleteIcon.setToolTipText("delete Icon");
        this.btnDeleteIcon.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.btnNewIcon, 5);
        fd.left = new FormAttachment(this.compo2, 5);
        fd.right = new FormAttachment(this.compo2, 40, SWT.RIGHT);
        this.btnDeleteIcon.setLayoutData(fd);
        this.btnDeleteIcon.setVisible(false);

        this.lblIconSize = new Label(this, SWT.NONE);
        this.lblIconSize.setForeground(this.getForeground());
        this.lblIconSize.setBackground(this.getBackground());
        this.lblIconSize.setEnabled(false);
        this.lblIconSize.setText("Size:");
        fd = new FormData();
        fd.top = new FormAttachment(0, 8, SWT.TOP);
        fd.left = new FormAttachment(this.btnNewIcon, 5);
        this.lblIconSize.setLayoutData(fd);
        this.lblIconSize.setVisible(false);

        this.txtIconSize = new Text(this, SWT.BORDER);
        this.txtIconSize.setToolTipText("Size of the icon");
        this.txtIconSize.setEnabled(false);
        this.txtIconSize.setVisible(false);

        this.lblIconLocation = new Label(this, SWT.NONE);
        this.lblIconLocation.setForeground(this.getForeground());
        this.lblIconLocation.setBackground(this.getBackground());
        this.lblIconLocation.setEnabled(false);
        this.lblIconLocation.setText("Location:");
        fd = new FormData();
        fd.top = new FormAttachment(this.lblIconSize, 5);
        fd.left = new FormAttachment(this.lblIconSize, 0, SWT.LEFT);
        this.lblIconLocation.setLayoutData(fd);
        this.lblIconLocation.setVisible(false);

        this.txtIconLocation = new Text(this, SWT.BORDER);
        this.txtIconLocation.setToolTipText("Location of the icon");
        this.txtIconLocation.setEnabled(false);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblIconLocation, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.lblIconLocation, 5);
        fd.right = new FormAttachment(this.lblIconLocation, 80, SWT.RIGHT);
        this.txtIconLocation.setLayoutData(fd);
        this.txtIconLocation.setVisible(false);

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
                Field field = figure.getClass().getSuperclass().getDeclaredField("fFigureDelegate1");
                field.setAccessible(true);
                this.figure1.setData("canchangeIcon", (field.get(figure) instanceof RectangleFigureDelegate));
                field.setAccessible(false);
                
                field = figure.getClass().getSuperclass().getDeclaredField("fFigureDelegate2");
                field.setAccessible(true);
                this.figure2.setData("canchangeIcon", (field.get(figure) instanceof RectangleFigureDelegate));
                field.setAccessible(false);
            } catch (@SuppressWarnings("unused") Exception err) {
                logger.error("Failed to get the fFigureDelegate fields for "+figure);
            }
            
        }
    }
    
    void select(Label figure) {
        this.selectedFigure = figure;
        
        if ( (this.selectedFigure != null) && (this.selectedFigure.getData("canChangeIcon") != null) ) {
            boolean canChangeIcon = (boolean)this.selectedFigure.getData("canChangeIcon");
            this.btnNewIcon.setVisible(canChangeIcon);
            this.btnDeleteIcon.setVisible(canChangeIcon);
            this.lblIconSize.setVisible(canChangeIcon);
            this.txtIconSize.setVisible(canChangeIcon);
            this.lblIconLocation.setVisible(canChangeIcon);
            this.txtIconLocation.setVisible(canChangeIcon);
        }

        this.compo1.redraw();
        this.compo2.redraw();
    }

    void select(int type) {
        if ( type == 0 )
            select(this.figure1);
        else
            select(this.figure2);
    }
    
    void setIconSize(String iconSize) {
        this.txtIconSize.setText(iconSize);
    }
    
    void setIconLocation(String iconLocation) {
        this.txtIconLocation.setText(iconLocation);
    }
    
    private PaintListener redrawListener = new PaintListener() {
        @Override
        public void paintControl(PaintEvent e) {
            if ( e.widget == ElementFigure.this.selectedFigure ) {
                GC graphics = e.gc;
                graphics.setForeground(ColorConstants.blue);
                graphics.setLineWidth(figureMargin);
                Rectangle bounds = getBounds();
                graphics.drawRectangle(1, 1, bounds.width - figureMargin, bounds.height - figureMargin);
            }
        }
    };
    
    private MouseAdapter selectListener = new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent e) {
            select((Label)e.widget);
        }
    };
}