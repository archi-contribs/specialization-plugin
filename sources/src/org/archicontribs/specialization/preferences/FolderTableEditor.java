/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */

package org.archicontribs.specialization.preferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class FolderTableEditor extends FieldEditor {
	private static final SpecializationLogger logger = new SpecializationLogger(FolderTableEditor.class);

	private Group grpImageFolders;

	private Table tblFolders;
	
	private Label lblFolder;
	private Text  txtFolder;
	private Label lblLocation;
    private Text  txtLocation;
	private Button btnBrowse;
	
	private Button btnUp;
	private Button btnNew;
	private Button btnRemove;
	private Button btnDown;
	private Button btnDiscard;
	private Button btnCreate;
	
	private static final IPreferenceStore store = SpecializationPlugin.INSTANCE.getPreferenceStore();
	

	/**
	 * Creates a table field editor.
	 */
	public FolderTableEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		if ( logger.isTraceEnabled() ) logger.trace("new SpecializationConfigFileTableEditor(\""+name+"\",\""+labelText+"\")");
		createControl(parent);		// calls doFillIntoGrid
	}

	/*
	 * (non-Javadoc) Method declared in FieldEditor.
	 * 
	 * called by createControl(parent)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		if ( logger.isTraceEnabled() ) logger.trace("doFillIntoGrid()");

		// we create a composite with layout as FormLayout
		grpImageFolders = new Group(parent, SWT.NONE);
		grpImageFolders.setFont(parent.getFont());
		grpImageFolders.setLayout(new FormLayout());
		grpImageFolders.setBackground(PreferencePage.COMPO_BACKGROUND_COLOR);
		grpImageFolders.setText("Image folders : ");

		btnUp = new Button(grpImageFolders, SWT.NONE);
		btnUp.setText("^");
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(100, -70);
		fd.right = new FormAttachment(100, -40);
		btnUp.setLayoutData(fd);
		btnUp.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { swapConfigFileEntries(-1); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnUp.setEnabled(false);

		btnDown = new Button(grpImageFolders, SWT.NONE);
		btnDown.setText("v");
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(100, -35);
		fd.right = new FormAttachment(100, -5);
		btnDown.setLayoutData(fd);
		btnDown.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { swapConfigFileEntries(1); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnDown.setEnabled(false);

		btnNew = new Button(grpImageFolders, SWT.NONE);
		btnNew.setText("New");
		fd = new FormData();
		fd.top = new FormAttachment(btnUp, 5);
		fd.left = new FormAttachment(100, -70);
		fd.right = new FormAttachment(100, -5);
		btnNew.setLayoutData(fd);
		btnNew.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { newCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});

		btnRemove = new Button(grpImageFolders, SWT.NONE);
		btnRemove.setText("Remove");
		fd = new FormData();
		fd.top = new FormAttachment(btnNew, 5);
		fd.left = new FormAttachment(btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(btnNew, 0, SWT.RIGHT);
		btnRemove.setLayoutData(fd);
		btnRemove.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { removeCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnRemove.setEnabled(false);


		tblFolders = new Table(grpImageFolders, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
		tblFolders.setLinesVisible(true);
		tblFolders.setHeaderVisible(true);
		fd = new FormData();
		fd.top = new FormAttachment(btnUp, 0, SWT.TOP);
		fd.left = new FormAttachment(0, 10);
		fd.right = new FormAttachment(btnNew, -10, SWT.LEFT);
		fd.bottom = new FormAttachment(btnRemove, 0, SWT.BOTTOM);
		tblFolders.setLayoutData(fd);
		tblFolders.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				discardCallback(false);
			}
		});
		TableColumn folderColumn = new TableColumn(tblFolders, SWT.NONE);
		folderColumn.setText("Folders");
		folderColumn.setWidth(150);
		TableColumn locationColumn = new TableColumn(tblFolders, SWT.NONE);
		locationColumn.setText("Locations");
        tblFolders.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                tblFolders.getColumns()[1].setWidth(tblFolders.getClientArea().width-folderColumn.getWidth());
            }
        });

		lblFolder = new Label(grpImageFolders, SWT.NONE);
		lblFolder.setText("Folder:");
		lblFolder.setBackground(PreferencePage.COMPO_BACKGROUND_COLOR);
		fd = new FormData();
		fd.top = new FormAttachment(tblFolders, 20);
		fd.left = new FormAttachment(tblFolders, 0 , SWT.LEFT);
		lblFolder.setLayoutData(fd);
		lblFolder.setVisible(false);
		
	    txtFolder = new Text(grpImageFolders, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblFolder, 0, SWT.TOP);
        fd.left = new FormAttachment(lblFolder, 30);
        fd.right = new FormAttachment(lblFolder, 250, SWT.RIGHT);
        txtFolder.setLayoutData(fd);
        txtFolder.setVisible(false);
        txtFolder.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(VerifyEvent e) {
                for (int i = 0; i < e.text.length(); i++) {
                    if ( !Character.isLetterOrDigit(e.text.charAt(i)) ) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });
		
	    lblLocation = new Label(grpImageFolders, SWT.NONE);
	    lblLocation.setText("Location:");
	    lblLocation.setBackground(PreferencePage.COMPO_BACKGROUND_COLOR);
	    fd = new FormData();
	    fd.top = new FormAttachment(lblFolder, 10);
	    fd.left = new FormAttachment(tblFolders, 0 , SWT.LEFT);
	    lblLocation.setLayoutData(fd);
	    lblLocation.setVisible(false);
	    
	    txtLocation = new Text(grpImageFolders, SWT.BORDER);
        txtLocation.setVisible(false);

		btnBrowse = new Button(grpImageFolders, SWT.NONE);
		btnBrowse.setText("Browse");
		fd = new FormData();
		fd.top = new FormAttachment(lblLocation, 0, SWT.CENTER);
		fd.right = new FormAttachment(tblFolders, -30, SWT.RIGHT);
		btnBrowse.setLayoutData(fd);
		btnBrowse.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { browseCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnBrowse.setVisible(false);

        fd = new FormData();
        fd.top = new FormAttachment(lblLocation, 0, SWT.TOP);
        fd.left = new FormAttachment(lblFolder, 30);
        fd.right = new FormAttachment(btnBrowse, -10);
        txtLocation.setLayoutData(fd);

		btnCreate = new Button(grpImageFolders, SWT.NONE);
		btnCreate.setText("Create");
		fd = new FormData();
		fd.left = new FormAttachment(btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(btnNew, 0, SWT.RIGHT);
		fd.bottom = new FormAttachment(100, -7);
		btnCreate.setLayoutData(fd);
		btnCreate.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { createCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnCreate.setVisible(false);

		btnDiscard = new Button(grpImageFolders, SWT.NONE);
		btnDiscard.setText("Discard");
		fd = new FormData();
		fd.left = new FormAttachment(btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(btnNew, 0, SWT.RIGHT);
		fd.bottom = new FormAttachment(btnCreate, -5, SWT.TOP);
		btnDiscard.setLayoutData(fd);
		btnDiscard.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { discardCallback(false); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnDiscard.setVisible(false);


		grpImageFolders.setTabList(new Control[] {txtFolder, txtLocation, btnBrowse, btnDiscard, btnCreate});

		grpImageFolders.layout();

		GridData gd = new GridData();
		gd.heightHint = txtLocation.getLocation().y + 10;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		grpImageFolders.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns) {
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault() {
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad() {
		if ( logger.isTraceEnabled() ) logger.trace("doLoad()");

		tblFolders.removeAll();
		
		int lines = store.getInt(SpecializationPlugin.storeFolderPrefix+"_#");
		
		for (int line = 0; line <lines; line++) {
		    if ( createSymlink(store.getString(SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line)), store.getString(SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line))) ) {
    			TableItem tableItem = new TableItem(tblFolders, SWT.NONE);
    			tableItem.setText(0, store.getString(SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line)));
    			tableItem.setText(1, store.getString(SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line)));
		    }
		}
			
		if ( tblFolders.getItemCount() != 0 ) {
			tblFolders.setSelection(0);
			tblFolders.notifyListeners(SWT.Selection, new Event());
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore() {
		if ( logger.isTraceEnabled() ) logger.trace("doStore()");
		
		int lines = tblFolders.getItemCount();
	    if ( logger.isTraceEnabled() ) logger.trace("   setting "+SpecializationPlugin.storeFolderPrefix+"_# = "+lines);
		store.setValue(SpecializationPlugin.storeFolderPrefix+"_#", lines);

		for (int line = 0; line < lines; line++) {
		    if ( logger.isTraceEnabled() ) logger.trace("   setting "+SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line)+" = "+tblFolders.getItem(line).getText(0));
			store.setValue(SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line), tblFolders.getItem(line).getText(0));
			if ( logger.isTraceEnabled() ) logger.trace("   setting "+SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line)+" = "+tblFolders.getItem(line).getText(1));
			store.setValue(SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line), tblFolders.getItem(line).getText(1));
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls() {
		return 1;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus() {
		if ( tblFolders != null )
			tblFolders.setFocus();
	}

	/**
	 * Called when the "new" button has been pressed
	 */
	private void newCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("newCallback()");

		// we unselect all the lines of the tblDatabases table
		tblFolders.deselectAll();
		
		// we show up the edition widgets
		discardCallback(true);
	}
	
	private boolean createSymlink(String folder, String location) {
    	// we create the symbolic link
        String pluginsFilename;
        String imgFolder;
        try {
            pluginsFilename = new File(com.archimatetool.editor.ArchiPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalPath();
            imgFolder = (new File(pluginsFilename+File.separator+".."+File.separator+"img")).getCanonicalPath();
        } catch (IOException e) {
            SpecializationPlugin.popup(Level.ERROR, "Cannot get plugin's folder !", e);
            return false;
        }
        
        // if the symbolic link already exists
        if ( Files.isSymbolicLink(Paths.get(imgFolder+File.separator+folder).toAbsolutePath()) )
            return true;
        //TODO: check that it points to the right location
        
        try {
            if( logger.isTraceEnabled() ) logger.trace("Creating symbolic link \""+Paths.get(imgFolder+File.separator+folder).toAbsolutePath()+"\" -> \""+Paths.get(location).toAbsolutePath()+"\"");
            Files.createSymbolicLink(Paths.get(imgFolder+File.separator+folder).toAbsolutePath(), Paths.get(location).toAbsolutePath());
        } catch (IOException e) {
            SpecializationPlugin.popup(Level.ERROR, "Cannot create the symbolic link !", e);
            return false;
        }
        return true;
	}

	/**
	 * Called when the "save" button has been pressed
	 */
	private void createCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("saveCallback()");

		if ( txtFolder.getText().isEmpty() || txtLocation.getText().isEmpty() ) {
		    // TODO : disable the save button when the txtFile field is empty, and enable it when not empty ... may be activate only when valid file with tooltip with error message
		    return;		    
		}
		
		// We check if the folder already exists in the table
		for (int line = 0; line < tblFolders.getItemCount(); ++line) {
            if ( Paths.get(txtFolder.getText()).equals(Paths.get(tblFolders.getItem(line).getText(0))) ) {
                 SpecializationPlugin.popup(Level.ERROR, "The folder is already defined, please choose another one.");
                 return;
		    }
		}
		


		if ( createSymlink(txtFolder.getText(), txtLocation.getText()) ) {
		    TableItem tableItem;
    		try {
    		    tableItem = new TableItem(tblFolders, SWT.NONE);
    		} catch (Exception e) {
    		    SpecializationPlugin.popup(Level.ERROR, "Cannot create new tableItem !", e);
                return;
            }
    		tableItem.setText(0, txtFolder.getText());
    		tableItem.setText(1, txtLocation.getText());
    
    		discardCallback(false);
    
    		tblFolders.setSelection(tableItem);
    		tblFolders.notifyListeners(SWT.Selection, new Event());
		}
	}

	private void discardCallback(boolean editMode) {
		String folder = "";
		String location = "";

		if ( tblFolders.getSelectionIndex() != -1 ) {
			folder = tblFolders.getItem(tblFolders.getSelectionIndex()).getText(0);
			location = tblFolders.getItem(tblFolders.getSelectionIndex()).getText(1);
		}

		lblFolder.setVisible(editMode);
		txtFolder.setVisible(editMode);					txtFolder.setText(folder);
	    lblLocation.setVisible(editMode);
	    txtLocation.setVisible(editMode);               txtLocation.setText(location);
		btnBrowse.setVisible(editMode);
		btnCreate.setVisible(editMode);
		btnDiscard.setVisible(editMode);

		btnNew.setEnabled(!editMode);
		btnRemove.setEnabled(!editMode && (tblFolders.getSelection()!=null) && (tblFolders.getSelection().length!=0));
		btnUp.setEnabled(!editMode && (tblFolders.getSelectionIndex() > 0));
		btnDown.setEnabled(!editMode && (tblFolders.getSelectionIndex() < tblFolders.getItemCount()-1));
		tblFolders.setEnabled(!editMode);

		grpImageFolders.layout();
	}

	/**
	 * Called when the "remove" button has been pressed
	 */
	private void removeCallback() {
        int index = tblFolders.getSelectionIndex();
        if ( index == -1 )
            return;
        
        
        String location = tblFolders.getItem(index).getText(1);
        
        // we check if the location is a symbolic link
        if ( !Files.isSymbolicLink(Paths.get(location).toAbsolutePath()) ) {
            SpecializationPlugin.popup(Level.ERROR, "\""+location+"\" is not a symbolic link.");
            return;
        }
        
        // we delete the symbolic link
        try {
            Files.delete(Paths.get(location).toAbsolutePath());
        } catch (IOException e) {
            SpecializationPlugin.popup(Level.ERROR, "Cannot delete \""+location+"\"", e);
            return;
        }

		tblFolders.remove(index);

		if ( tblFolders.getItemCount() > 0 ) {
			if ( index < tblFolders.getItemCount() )
				tblFolders.setSelection(index);
			else {
				if ( index > 0 )
					tblFolders.setSelection(index-1);
			}
			discardCallback(false);
		} else {
			lblFolder.setVisible(false);
			txtFolder.setVisible(false);
			lblLocation.setVisible(false);
            txtLocation.setVisible(false);
			btnBrowse.setVisible(false);

			btnCreate.setVisible(false);
			btnDiscard.setVisible(false);

			btnNew.setEnabled(true);
			btnRemove.setEnabled(false);
			btnUp.setEnabled(false);
			btnDown.setEnabled(false);
			tblFolders.setEnabled(true);

			grpImageFolders.layout();
		}
	}

	/**
	 * Called when the "browse" button has been pressed
	 */
	private void browseCallback() {
		DirectoryDialog dlg = new DirectoryDialog(Display.getDefault().getActiveShell(), SWT.SINGLE);
		String folder = dlg.open();
		if (folder != null)
			txtLocation.setText(folder);
	}

	/**
	 * Moves the currently selected item up or down.
	 *
	 * @param direction :
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	private void swapConfigFileEntries(int direction) {
		if ( logger.isTraceEnabled() ) logger.trace("swap("+direction+")");

		int source = tblFolders.getSelectionIndex();
		int target = tblFolders.getSelectionIndex()+direction;

		if ( logger.isTraceEnabled() ) logger.trace("swapping entrie "+source+" and "+target+".");
		TableItem sourceItem = tblFolders.getItem(source);
		String sourceText = sourceItem.getText();

		TableItem targetItem = tblFolders.getItem(target);
		String targetText = targetItem.getText();

		sourceItem.setText(targetText);
		targetItem.setText(sourceText);

		tblFolders.setSelection(target);
		tblFolders.notifyListeners(SWT.Selection, new Event());
	}

	/**
	 * If we are in edit mode, then ask the user is if wants to save or discard
	 */
	public void close() {
		if ( txtFolder.isVisible() && txtFolder.isEnabled() ) {
			if ( SpecializationPlugin.question("Do you wish to save or discard your currents updates ?", new String[] {"save", "discard"}) == 0 ) {
				createCallback();
			}			
		}
	}
}
