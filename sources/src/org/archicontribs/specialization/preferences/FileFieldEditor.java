package org.archicontribs.specialization.preferences;

import org.eclipse.swt.widgets.Composite;

/**
 * This class extends the FileFieldEditor. It allows invalid filenames.
 * 
 * @author Herve Jouin
 *
 */
public class FileFieldEditor extends org.eclipse.jface.preference.FileFieldEditor {
    public FileFieldEditor() {
    	super();
    }
    
    public FileFieldEditor(String name, String labelText, Composite parent) {
        super(name, labelText, false, parent);
    }
    
    public FileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent) {
        super(name, labelText, enforceAbsolute, VALIDATE_ON_FOCUS_LOST, parent);
    }
    
    public FileFieldEditor(String name, String labelText, boolean enforceAbsolute, int validationStrategy, Composite parent) {
    	super(name, labelText, enforceAbsolute, validationStrategy, parent);
    }
    
    @Override
	protected String changePressed() {
        return getTextControl().getText();
    }
    
    @Override
	protected boolean checkState() {
    	return true;
    }
}
