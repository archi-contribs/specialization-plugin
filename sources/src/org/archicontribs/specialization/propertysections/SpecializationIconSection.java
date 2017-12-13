/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.specialization.propertysections;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
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
	private Tree treeImages = null;
	
	static private String[] validImageSuffixes = {"jpg", "png", "gif", "bmp"};

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
		parent.setLayout(new FormLayout());

		compoNoIcon = new Composite(parent, SWT.NONE);
        FormData fd = new FormData();
        compoNoIcon.setForeground(parent.getForeground());
        compoNoIcon.setBackground(parent.getBackground());
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        fd.bottom = new FormAttachment(100, 0);
        compoNoIcon.setLayoutData(fd);
        compoNoIcon.setLayout(new FormLayout());
		
		Label lblNoIcon = new Label(compoNoIcon, SWT.NONE);
		lblNoIcon.setText("Please change the element's figure to show up the icon.");
		lblNoIcon.setForeground(parent.getForeground());
		lblNoIcon.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 20);
        fd.left = new FormAttachment(0, 20);
        lblNoIcon.setLayoutData(fd);
		
		compoNoIcon.pack();
		
		compoIcon = new Composite(parent, SWT.NONE);
        fd = new FormData();
        compoIcon.setForeground(parent.getForeground());
        compoIcon.setBackground(parent.getBackground());
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        fd.bottom = new FormAttachment(100, 0);
        compoIcon.setLayoutData(fd);
        compoIcon.setLayout(new FormLayout());
		
		Label lblIcon = new Label(compoIcon, SWT.NONE);
		lblIcon.setText("Icon :");
		lblIcon.setForeground(parent.getForeground());
		lblIcon.setBackground(parent.getBackground());
        fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 10);
        lblIcon.setLayoutData(fd);
        
        Text txtIcon = new Text(compoIcon, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(lblIcon, 20);
        fd.right = new FormAttachment(70);
        txtIcon.setLayoutData(fd);
        
		treeImages = new Tree(compoIcon, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		treeImages.setBackground(parent.getBackground());
		treeImages.setHeaderVisible(true);
		treeImages.setLinesVisible(true);
		fd = new FormData();
		fd.top = new FormAttachment(txtIcon, 10);
		fd.left = new FormAttachment(txtIcon,10);
		fd.right = new FormAttachment(70, 0);
		fd.bottom = new FormAttachment(70, 0);
		treeImages.setLayoutData(fd);
        
		TreeColumn filesColumn = new TreeColumn(treeImages, SWT.NONE);
		filesColumn.setText("Files");
		filesColumn.setWidth(300);
		
		TreeColumn sizeColumn = new TreeColumn(treeImages, SWT.NONE);
		sizeColumn.setText("Image size");
		sizeColumn.setWidth(80);
		
		compoIcon.pack();
		
		//TODO: make the tree static to fill it only once (but create a "rescan" button")
		//TODO: show an image preview at the right of the tree
		//TODO: find a way to resize the image and update Archi's cache
		
		/*
		Dimension dimension = getImageDimension(file);
        if ( dimension == null )
        	return new StyledString("");
        return new StyledString(String.valueOf(dimension.width+"x"+dimension.height));
		 */
	}
    
    private Dimension getImageDimension(File path) {
        Dimension result = null;
        String suffix = this.getFileSuffix(path.getName());
        if ( !SpecializationPlugin.inArray(validImageSuffixes, suffix.toLowerCase()) ) {
        	logger.error("Unknown file extension : "+suffix);
        	return null;
        }
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                ImageInputStream stream = new FileImageInputStream(path);
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                result = new Dimension(width, height);
            } catch (IOException e) {
                logger.error("Fail to read image \""+path+"\"", e);
                return null;
            } finally {
                reader.dispose();
            }
        } else {
            logger.error("File \""+path+"\" doesn't seem to be an image");
        }
        return result;
    }
    
    private String getFileSuffix(final String path) {
        String result = null;
        if (path != null) {
            result = "";
            if (path.lastIndexOf('.') != -1) {
                result = path.substring(path.lastIndexOf('.'));
                if (result.startsWith(".")) {
                    result = result.substring(1);
                }
            }
        }
        return result;
    }
	
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
		if ( !(figureDelegate instanceof RectangleFigureDelegate) ) {
			logger.trace("********** refreshcontrols(visible);");
			compoNoIcon.setVisible(true);
			compoIcon.setVisible(false);
		} else {
			logger.trace("********** refreshcontrols(hidden);");
			compoNoIcon.setVisible(false);
			compoIcon.setVisible(true);
		}


	}
}
