/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */

package org.archicontribs.specialization.preferences;

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

	Table tblFolders;
	
	private Label lblFolder;
	private Text  txtFolder;
	private Label lblLocation;
    private Text  txtLocation;
	private Button btnBrowse;
	
	private Button btnUp;
	private Button btnNew;
    private Button btnModify;
	private Button btnRemove;
	private Button btnDown;
	private Button btnDiscard;
	private Button btnSet;
	
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
	@Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
		if ( logger.isTraceEnabled() ) logger.trace("doFillIntoGrid()");

		// we create a composite with layout as FormLayout
		this.grpImageFolders = new Group(parent, SWT.NONE);
		this.grpImageFolders.setFont(parent.getFont());
		this.grpImageFolders.setLayout(new FormLayout());
		this.grpImageFolders.setBackground(PreferencePage.COMPO_BACKGROUND_COLOR);
		this.grpImageFolders.setText("Image folders : ");

		this.btnUp = new Button(this.grpImageFolders, SWT.NONE);
		this.btnUp.setText("^");
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(100, -70);
		fd.right = new FormAttachment(100, -40);
		this.btnUp.setLayoutData(fd);
		this.btnUp.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { swapConfigFileEntries(-1); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnUp.setEnabled(false);

		this.btnDown = new Button(this.grpImageFolders, SWT.NONE);
		this.btnDown.setText("v");
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(100, -35);
		fd.right = new FormAttachment(100, -5);
		this.btnDown.setLayoutData(fd);
		this.btnDown.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { swapConfigFileEntries(1); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnDown.setEnabled(false);

		this.btnNew = new Button(this.grpImageFolders, SWT.NONE);
		this.btnNew.setText("New");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnUp, 5);
		fd.left = new FormAttachment(100, -70);
		fd.right = new FormAttachment(100, -5);
		this.btnNew.setLayoutData(fd);
		this.btnNew.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { newCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		
        this.btnModify = new Button(this.grpImageFolders, SWT.NONE);
        this.btnModify.setText("Modify");
        fd = new FormData();
        fd.top = new FormAttachment(this.btnNew, 5);
        fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
        fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
        this.btnModify.setLayoutData(fd);
        this.btnModify.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) { modifyCallback(); }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
        });
        this.btnModify.setEnabled(false);

		this.btnRemove = new Button(this.grpImageFolders, SWT.NONE);
		this.btnRemove.setText("Remove");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnModify, 5);
		fd.left = new FormAttachment(this.btnModify, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnModify, 0, SWT.RIGHT);
		this.btnRemove.setLayoutData(fd);
		this.btnRemove.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { removeCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnRemove.setEnabled(false);


		this.tblFolders = new Table(this.grpImageFolders, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
		this.tblFolders.setLinesVisible(true);
		this.tblFolders.setHeaderVisible(true);
		fd = new FormData();
		fd.top = new FormAttachment(this.btnUp, 0, SWT.TOP);
		fd.left = new FormAttachment(0, 10);
		fd.right = new FormAttachment(this.btnNew, -10, SWT.LEFT);
		fd.bottom = new FormAttachment(this.btnRemove, 0, SWT.BOTTOM);
		this.tblFolders.setLayoutData(fd);
		this.tblFolders.addListener(SWT.Selection, new Listener() {
			@Override
            public void handleEvent(Event e) {
				discardCallback(false);
			}
		});
		TableColumn folderColumn = new TableColumn(this.tblFolders, SWT.NONE);
		folderColumn.setText("Folders");
		folderColumn.setWidth(150);
		TableColumn locationColumn = new TableColumn(this.tblFolders, SWT.NONE);
		locationColumn.setText("Locations");
        this.tblFolders.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                FolderTableEditor.this.tblFolders.getColumns()[1].setWidth(FolderTableEditor.this.tblFolders.getClientArea().width-folderColumn.getWidth());
            }
        });

		this.lblFolder = new Label(this.grpImageFolders, SWT.NONE);
		this.lblFolder.setText("Folder:");
		this.lblFolder.setBackground(PreferencePage.COMPO_BACKGROUND_COLOR);
		fd = new FormData();
		fd.top = new FormAttachment(this.tblFolders, 20);
		fd.left = new FormAttachment(this.tblFolders, 0 , SWT.LEFT);
		this.lblFolder.setLayoutData(fd);
		this.lblFolder.setVisible(false);
		
	    this.txtFolder = new Text(this.grpImageFolders, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblFolder, 0, SWT.TOP);
        fd.left = new FormAttachment(this.lblFolder, 30);
        fd.right = new FormAttachment(this.lblFolder, 250, SWT.RIGHT);
        this.txtFolder.setLayoutData(fd);
        this.txtFolder.setVisible(false);
        this.txtFolder.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(VerifyEvent e) {
                for (int i = 0; i < e.text.length(); i++) {
                    if ( !Character.isLetterOrDigit(e.text.charAt(i)) && (e.text.charAt(i) != '-') && (e.text.charAt(i) != '_') ) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });
		
	    this.lblLocation = new Label(this.grpImageFolders, SWT.NONE);
	    this.lblLocation.setText("Location:");
	    this.lblLocation.setBackground(PreferencePage.COMPO_BACKGROUND_COLOR);
	    fd = new FormData();
	    fd.top = new FormAttachment(this.lblFolder, 10);
	    fd.left = new FormAttachment(this.tblFolders, 0 , SWT.LEFT);
	    this.lblLocation.setLayoutData(fd);
	    this.lblLocation.setVisible(false);
	    
	    this.txtLocation = new Text(this.grpImageFolders, SWT.BORDER);
        this.txtLocation.setVisible(false);

		this.btnBrowse = new Button(this.grpImageFolders, SWT.NONE);
		this.btnBrowse.setText("Browse");
		fd = new FormData();
		fd.top = new FormAttachment(this.lblLocation, 0, SWT.CENTER);
		fd.right = new FormAttachment(this.tblFolders, -30, SWT.RIGHT);
		this.btnBrowse.setLayoutData(fd);
		this.btnBrowse.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { browseCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnBrowse.setVisible(false);

        fd = new FormData();
        fd.top = new FormAttachment(this.lblLocation, 0, SWT.TOP);
        fd.left = new FormAttachment(this.lblFolder, 30);
        fd.right = new FormAttachment(this.btnBrowse, -10);
        this.txtLocation.setLayoutData(fd);

		this.btnSet = new Button(this.grpImageFolders, SWT.NONE);
		this.btnSet.setText("Set");
		fd = new FormData();
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		fd.bottom = new FormAttachment(100, -7);
		this.btnSet.setLayoutData(fd);
		this.btnSet.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { setCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnSet.setVisible(false);

		this.btnDiscard = new Button(this.grpImageFolders, SWT.NONE);
		this.btnDiscard.setText("Discard");
		fd = new FormData();
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		fd.bottom = new FormAttachment(this.btnSet, -5, SWT.TOP);
		this.btnDiscard.setLayoutData(fd);
		this.btnDiscard.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { discardCallback(false); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnDiscard.setVisible(false);


		this.grpImageFolders.setTabList(new Control[] {this.txtFolder, this.txtLocation, this.btnBrowse, this.btnDiscard, this.btnSet});

		this.grpImageFolders.layout();

		GridData gd = new GridData();
		gd.heightHint = this.txtLocation.getLocation().y + 10;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		this.grpImageFolders.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void adjustForNumColumns(int numColumns) {
	    // nothing to do
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doLoadDefault() {
	    // nothing to do
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doLoad() {
		if ( logger.isTraceEnabled() ) logger.trace("doLoad()");

		this.tblFolders.removeAll();
		
		int lines = store.getInt(SpecializationPlugin.storeFolderPrefix+"_#");
		
		for (int line = 0; line <lines; line++) {
   			TableItem tableItem = new TableItem(this.tblFolders, SWT.NONE);
   			tableItem.setText(0, store.getString(SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line)));
   			tableItem.setText(1, store.getString(SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line)));
		}
			
		if ( this.tblFolders.getItemCount() != 0 ) {
			this.tblFolders.setSelection(0);
			this.tblFolders.notifyListeners(SWT.Selection, new Event());
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doStore() {
		if ( logger.isTraceEnabled() ) logger.trace("doStore()");
		
		int lines = this.tblFolders.getItemCount();
	    if ( logger.isTraceEnabled() ) logger.trace("   setting "+SpecializationPlugin.storeFolderPrefix+"_# = "+lines);
		store.setValue(SpecializationPlugin.storeFolderPrefix+"_#", lines);

		for (int line = 0; line < lines; line++) {
		    if ( logger.isTraceEnabled() ) logger.trace("   setting "+SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line)+" = "+this.tblFolders.getItem(line).getText(0));
			store.setValue(SpecializationPlugin.storeFolderPrefix+"_"+String.valueOf(line), this.tblFolders.getItem(line).getText(0));
			if ( logger.isTraceEnabled() ) logger.trace("   setting "+SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line)+" = "+this.tblFolders.getItem(line).getText(1));
			store.setValue(SpecializationPlugin.storeLocationPrefix+"_"+String.valueOf(line), this.tblFolders.getItem(line).getText(1));
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    public int getNumberOfControls() {
		return 1;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    public void setFocus() {
		if ( this.tblFolders != null )
			this.tblFolders.setFocus();
	}

	/**
	 * Called when the "new" button has been pressed
	 */
	void newCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("newCallback()");

		// we unselect all the lines of the tblDatabases table
		this.tblFolders.deselectAll();
		
		// we show up the edition widgets
		discardCallback(true);
	}
	
	/**
     * Called when the "modify" button has been pressed
     */
    void modifyCallback() {
        if ( logger.isTraceEnabled() ) logger.trace("updateCallback()");
        
        // we show up the edition widgets
        discardCallback(true);
        
        this.txtFolder.setText(this.tblFolders.getSelection()[0].getText(0));
        this.txtLocation.setText(this.tblFolders.getSelection()[0].getText(1));
    }
	
	/**
	 * Called when the "set" button has been pressed
	 */
	void setCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("saveCallback()");

		if ( this.txtFolder.getText().isEmpty() || this.txtLocation.getText().isEmpty() ) {
		    // TODO : disable the save button when the txtFile field is empty, and enable it when not empty ... may be activate only when valid file with tooltip with error message
		    return;		    
		}
		
	    TableItem tableItem = null;
	    
        // we check if a line is selected in tha table (i.e. modification mode)
        if ( this.tblFolders.getSelection().length != 0 && this.tblFolders.getSelection()[0] != null )
            tableItem = this.tblFolders.getSelection()[0];
	    
		// We check if the folder already exists in the table
		for (int line = 0; line < this.tblFolders.getItemCount(); ++line) {
            if ( !this.tblFolders.getItem(line).equals(tableItem) ) {
                if ( Paths.get(this.txtFolder.getText()).equals(Paths.get(this.tblFolders.getItem(line).getText(0))) ) {
                    if ( tableItem != null ) {
                        SpecializationPlugin.popup(Level.ERROR, "A folder \""+this.txtFolder.getText()+"\" already exists in the table.");
                        return;
                    }
                    tableItem = this.tblFolders.getItem(line);
                    break;
                }
		    }
		}

   		if ( tableItem == null ) {
            tableItem = new TableItem(this.tblFolders, SWT.NONE);
   		}
   		tableItem.setText(0, this.txtFolder.getText());
   		tableItem.setText(1, this.txtLocation.getText());
    
   		discardCallback(false);
  
 		this.tblFolders.setSelection(tableItem);
   		this.tblFolders.notifyListeners(SWT.Selection, new Event());
	}

	void discardCallback(boolean editMode) {
		String folder = "";
		String location = "";

		if ( this.tblFolders.getSelectionIndex() != -1 ) {
			folder = this.tblFolders.getItem(this.tblFolders.getSelectionIndex()).getText(0);
			location = this.tblFolders.getItem(this.tblFolders.getSelectionIndex()).getText(1);
		}

		this.lblFolder.setVisible(editMode);
		this.txtFolder.setVisible(editMode);					this.txtFolder.setText(folder);
	    this.lblLocation.setVisible(editMode);
	    this.txtLocation.setVisible(editMode);               this.txtLocation.setText(location);
		this.btnBrowse.setVisible(editMode);
		this.btnSet.setVisible(editMode);
		this.btnDiscard.setVisible(editMode);

		this.btnNew.setEnabled(!editMode);
		this.btnModify.setEnabled(!editMode && (this.tblFolders.getSelection()!=null) && (this.tblFolders.getSelection().length!=0));
		this.btnRemove.setEnabled(!editMode && (this.tblFolders.getSelection()!=null) && (this.tblFolders.getSelection().length!=0));
		this.btnUp.setEnabled(!editMode && (this.tblFolders.getSelectionIndex() > 0));
		this.btnDown.setEnabled(!editMode && (this.tblFolders.getSelectionIndex() < this.tblFolders.getItemCount()-1));
		this.tblFolders.setEnabled(!editMode);

		this.grpImageFolders.layout();
	}

	/**
	 * Called when the "remove" button has been pressed
	 */
	void removeCallback() {
        int index = this.tblFolders.getSelectionIndex();
        if ( index == -1 )
            return;
        
  		this.tblFolders.remove(index);

		if ( this.tblFolders.getItemCount() > 0 ) {
			if ( index < this.tblFolders.getItemCount() )
				this.tblFolders.setSelection(index);
			else {
				if ( index > 0 )
					this.tblFolders.setSelection(index-1);
			}
			discardCallback(false);
		} else {
			this.lblFolder.setVisible(false);
			this.txtFolder.setVisible(false);
			this.lblLocation.setVisible(false);
            this.txtLocation.setVisible(false);
			this.btnBrowse.setVisible(false);

			this.btnSet.setVisible(false);
			this.btnDiscard.setVisible(false);

			this.btnNew.setEnabled(true);
			this.btnModify.setEnabled(false);
			this.btnRemove.setEnabled(false);
			this.btnUp.setEnabled(false);
			this.btnDown.setEnabled(false);
			this.tblFolders.setEnabled(true);

			this.grpImageFolders.layout();
		}
	}

	/**
	 * Called when the "browse" button has been pressed
	 */
	void browseCallback() {
		DirectoryDialog dlg = new DirectoryDialog(Display.getDefault().getActiveShell(), SWT.SINGLE);
		String folder = dlg.open();
		if (folder != null)
			this.txtLocation.setText(folder);
	}

	/**
	 * Moves the currently selected item up or down.
	 *
	 * @param direction :
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	void swapConfigFileEntries(int direction) {
		if ( logger.isTraceEnabled() ) logger.trace("swap("+direction+")");

		int source = this.tblFolders.getSelectionIndex();
		int target = this.tblFolders.getSelectionIndex()+direction;

		if ( logger.isTraceEnabled() ) logger.trace("swapping entrie "+source+" and "+target+".");
		TableItem sourceItem = this.tblFolders.getItem(source);
		String sourceText = sourceItem.getText();

		TableItem targetItem = this.tblFolders.getItem(target);
		String targetText = targetItem.getText();

		sourceItem.setText(targetText);
		targetItem.setText(sourceText);

		this.tblFolders.setSelection(target);
		this.tblFolders.notifyListeners(SWT.Selection, new Event());
	}

	/**
	 * If we are in edit mode, then ask the user is if wants to save or discard
	 */
	public void close() {
		if ( this.txtFolder.isVisible() && this.txtFolder.isEnabled() ) {
			if ( SpecializationPlugin.question("Do you wish to save or discard your currents updates ?", new String[] {"save", "discard"}) == 0 ) {
				setCallback();
			}			
		}
	}
}
