package org.archicontribs.specialization.propertysections;

import org.apache.log4j.Level;
import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import lombok.Getter;

public class SpecializationCombo extends Composite {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationCombo.class);

	String label = "value";

	Combo combo = null;
	Button btnNew = null;
	Button btnEdit = null;
	Button btnDelete = null;
	
	@Getter String previousValue = "";


	public SpecializationCombo(Composite parent, int type) {
		super(parent, type);

		setLayout(new FormLayout());
		FormData fd;

		this.combo = new Combo(this, SWT.NONE | SWT.READ_ONLY);
		
		this.btnNew = new Button(this, SWT.PUSH);
		this.btnNew.setImage(SpecializationPlugin.NEW_ICON);
		this.btnNew.setToolTipText("New "+this.label);

		this.btnEdit = new Button(this, SWT.PUSH);
		this.btnEdit.setImage(SpecializationPlugin.EDIT_ICON);
		this.btnEdit.setToolTipText("Edit "+this.label);

		this.btnDelete = new Button(this, SWT.PUSH);
		this.btnDelete.setImage(SpecializationPlugin.DELETE_ICON);
		this.btnDelete.setToolTipText("Delete "+this.label);

		this.btnNew.addSelectionListener(this.newListener);
		this.btnEdit.addSelectionListener(this.editListener);
		this.btnDelete.addSelectionListener(this.deleteListener);
		
		fd = new FormData();
		fd.top = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.height = 20;
		fd.width = 20;
		this.btnDelete.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0);
		fd.right = new FormAttachment(this.btnDelete, -5);
		fd.height = 20;
		fd.width = 20;
		this.btnEdit.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0);
		fd.right = new FormAttachment(this.btnEdit, -5);
		fd.height = 20;
		fd.width = 20;
		this.btnNew.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0);
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(this.btnNew, -5);
		this.combo.setLayoutData(fd);
	}

	public void setLabel(String label) {
		this.label = label;
		
		this.btnNew.setToolTipText("New "+this.label);
		this.btnEdit.setToolTipText("Edit "+this.label);
		this.btnDelete.setToolTipText("Delete "+this.label);
	}

	public void addModifyListener(ModifyListener listener) {
		this.combo.addModifyListener(listener);
	}

	public String getText() {
		return this.combo.getText();
	}
	
	public int getItemCount() {
		return this.combo.getItemCount();
	}

	SelectionListener newListener = new SelectionListener() {
		@Override public void widgetSelected(SelectionEvent e) {
			InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), SpecializationPlugin.pluginTitle, "New "+SpecializationCombo.this.label+":", "", new LengthValidator());
			if (dlg.open() == Window.OK) {
				// User clicked OK, we verify that the name does not already exists
				String newValue = dlg.getValue();
				boolean alreadyExists = false;
				for ( int index = 0; index < SpecializationCombo.this.combo.getItemCount(); ++index ) {
					if ( newValue.equals(SpecializationCombo.this.combo.getItem(index)) ) {
						SpecializationPlugin.popup(Level.WARN, "The "+SpecializationCombo.this.label+" \""+newValue+"\" already exists.");
						SpecializationCombo.this.combo.select(index);
						alreadyExists = true;
						break;
					}
				}

				if ( !alreadyExists ) {
					SpecializationCombo.this.combo.add(newValue);    // the line is added at the end
					SpecializationCombo.this.combo.select(SpecializationCombo.this.combo.getItemCount()-1);
					
					// we activate the edit and new buttons
					SpecializationCombo.this.btnEdit.setEnabled(true);
					SpecializationCombo.this.btnDelete.setEnabled(true);
				}
				
				SpecializationCombo.this.previousValue = "";
			}
		}

		@Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
	};

	SelectionListener editListener = new SelectionListener() {
		@Override public void widgetSelected(SelectionEvent e) {
			String oldValue = SpecializationCombo.this.combo.getText();
			InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), SpecializationPlugin.pluginTitle, "Edit "+SpecializationCombo.this.label+":", oldValue, new LengthValidator());
			if (dlg.open() == Window.OK) {
				// User clicked OK, we verify that the name does not already exists
				String newValue = dlg.getValue();
				for ( int index = 0; index < SpecializationCombo.this.combo.getItemCount(); ++index ) {
					if ( newValue.equals(SpecializationCombo.this.combo.getItem(index)) ) {
						SpecializationPlugin.popup(Level.WARN, "The "+SpecializationCombo.this.label+" \""+newValue+"\" already exists.");
						return;
					}
				}

				logger.debug("Renaming "+oldValue+ " to "+newValue);
				
				SpecializationCombo.this.previousValue = oldValue;

				// we get the index of the old value in order to delete it
				int oldValueIndex = 0;
				for ( int index = 0; index < SpecializationCombo.this.combo.getItemCount(); ++index ) {
					if ( oldValue.equals(SpecializationCombo.this.combo.getItem(index)) ) {
						oldValueIndex = index;
						break;
					}
				}

				// we remove the old value, and add the new value at the same index
				/*
				SpecializationCombo.this.combo.remove(oldValue);
				SpecializationCombo.this.combo.add(newValue, oldValueIndex);
				SpecializationCombo.this.combo.select(oldValueIndex);
				*/
				SpecializationCombo.this.combo.setItem(oldValueIndex, newValue);
				
				//TODO: sort the names
			}
		}

		@Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
	};

	SelectionListener deleteListener = new SelectionListener() {
		@Override public void widgetSelected(SelectionEvent e) {
			String oldValue = SpecializationCombo.this.combo.getText();
			int oldIndex = SpecializationCombo.this.combo.getSelectionIndex();

			SpecializationCombo.this.combo.remove(oldValue);
			
			SpecializationCombo.this.previousValue = oldValue;

			// we select the previous item in the combo if it exists
			if ( SpecializationCombo.this.combo.getItemCount() == 0 ) {
				SpecializationCombo.this.btnEdit.setEnabled(false);
				SpecializationCombo.this.btnDelete.setEnabled(false);
			} else {
				if ( oldIndex == 0 )
					SpecializationCombo.this.combo.select(0);
				else
					SpecializationCombo.this.combo.select(oldIndex-1);
			}
		}

		@Override public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
	};

	class LengthValidator implements IInputValidator {
		@Override public String isValid(String newText) {
			// we check the string length
			int len = newText.length();
			if (len < 1) return " ";
			if (len > 50) return "Too long (50 chars max)";

			// we check that the string does not contain special chars
			if ( newText.contains("\n") ) return "Must not contain newline";
			if ( newText.contains("\t") ) return "Must not contain tab";

			return null;
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.combo.setEnabled(enabled);
		this.btnNew.setEnabled(enabled);
		this.btnEdit.setEnabled(enabled && (SpecializationCombo.this.combo.getItemCount()!=0));
		this.btnDelete.setEnabled(enabled && (SpecializationCombo.this.combo.getItemCount()!=0));
	}
	
	@Override
	public boolean getEnabled() {
		return this.combo.getEnabled();
	}
	
	public void removeAll() {
		this.combo.removeAll();
	}
	
	public int getSelectionIndex() {
		return this.combo.getSelectionIndex();
	}
	
	public void add(String string) {
		this.combo.add(string);
	}
	
	public void select(int index) {
		this.combo.select(index);
	}
}