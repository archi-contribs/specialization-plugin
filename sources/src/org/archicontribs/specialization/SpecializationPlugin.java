package org.archicontribs.specialization;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Level;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.archimatetool.editor.ui.IArchiImages;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

/**
 * Specialization plugin for Archi, the Archimate modeler
 * 
 * It allows to change the icons and labels in Archi views.
 * 
 * @author Herve Jouin
 *
 * v0.1:        24/08/2017		beta version
 *                              Icons can be changed on few technical elements using the "icon" property
 * 
 * v0.2:        12/10/2017      Icon change;
 *                                  Increase the number of supported elements
 *                                  Add an option on the preference page to use customized icons on all the views of those that have a "change icons" property set to "true"
 *                                  Add a context menu to switch icons from Archi's standard icons to customized ones and back
 *                              Label change:
 *                                  Add new functionality to change label on relationships and elements
 *                                  Add an option on the preference page to use customized icons on all the views of those that have a "change labels" property set to "true"
 *                                  Add a context menu to switch icons from Archi's standard labels to customized ones and back
 *                                  Add context menu "refresh view" to refresh the labels as they do not refresh automatically
 *                                  
 * v0.3:        06/12/2017      Add debug and trace messages
 *                              Add the ability to change the name of the property that contains the icon filename
 *                              Add the ability to change the name of the property that contains the label text
 *                              Add a context menu to refresh the model tree
 *                              
 * v0.4:        12/12/2017      Add more debug and trace messages
 *                              Add the ability to change the name of the property that contains the icon filename at the model, view and concept levels
 *                              Add the ability to change the name of the property that contains the label text at the model, view and concept levels
 *                              Fix elements shape (quare corners instead of round ones)
 *                              Fix junction shape
 *                              Do not replace the icon in the properties window anymore
 *                              Replace the context menus by a new tab in the properties window
 *                              Add the creation of symbolic links in the preferene page
 *                                  
 * TODO list:                   Add a file explorer window on the preference page that allow to manage the icons
 *                              and allow drag&drop to this file exporer window
 *                              Allow to change the icon size in the icon's property
 * 
 */
public class SpecializationPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.archicontribs.specialization";
	public static SpecializationPlugin INSTANCE;
	
	public static final String pluginVersion = "0.4";
	public static final String pluginName = "SpecializationPlugin";
	public static final String pluginTitle = "Specialization plugin v" + pluginVersion;
	
	public static String pluginsFolder;
	public static String pluginsPackage;
	public static String pluginsFilename;
	
	protected static final Display  display           = Display.getCurrent();
	public    static final Cursor   CURSOR_WAIT       = new Cursor(null, SWT.CURSOR_WAIT);
	public    static final Cursor   CURSOR_ARROW      = new Cursor(null, SWT.CURSOR_ARROW);
	public    static final Color    BLACK_COLOR       = new Color(display, 0, 0, 0);
	public    static final Color    COMPO_LEFT_COLOR  = new Color(display, 240, 248, 255);			// light blue
	public    static final FontData SYSTEM_FONT       = display.getSystemFont().getFontData()[0];
    public    static final Font     TITLE_FONT        = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight()+2, SWT.BOLD);
    
    public static String storeFolderPrefix            = "folder";
    public static String storeLocationPrefix          = "location";
    
	/**
	 * PreferenceStore allowing to store the plugin configuration.
	 */
	private static IPreferenceStore preferenceStore = null;
	
	private static SpecializationLogger logger;

	public SpecializationPlugin() {
		INSTANCE = this;
		
		preferenceStore = this.getPreferenceStore();
		preferenceStore.setDefault("progressWindow",	        "showAndWait");
		preferenceStore.setDefault("checkForUpdateAtStartup",   false);
		preferenceStore.setDefault("loggerMode",		        "disabled");
		preferenceStore.setDefault("loggerLevel",		        "INFO");
		preferenceStore.setDefault("loggerFilename",	        System.getProperty("user.home")+File.separator+pluginName+".log");
		preferenceStore.setDefault("loggerExpert",		        "log4j.rootLogger                               = INFO, stdout, file\n"+
																"\n"+
																"log4j.appender.stdout                          = org.apache.log4j.ConsoleAppender\n"+
																"log4j.appender.stdout.Target                   = System.out\n"+
																"log4j.appender.stdout.layout                   = org.apache.log4j.PatternLayout\n"+
																"log4j.appender.stdout.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-40.40C{1} %m%n\n"+
																"\n"+
																"log4j.appender.file                            = org.apache.log4j.FileAppender\n"+
																"log4j.appender.file.ImmediateFlush             = true\n"+
																"log4j.appender.file.Append                     = false\n"+
																"log4j.appender.file.Encoding                   = UTF-8\n"+
																"log4j.appender.file.File                       = "+(System.getProperty("user.home")+File.separator+pluginName+".log").replace("\\", "\\\\")+"\n"+
																"log4j.appender.file.layout                     = org.apache.log4j.PatternLayout\n"+
																"log4j.appender.file.layout.ConversionPattern   = %d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-40.40C{1} %m%n");
		preferenceStore.setDefault("iconMargin",                2);
		preferenceStore.setDefault("mustReplaceIconsInViews",   "");
	    preferenceStore.setDefault("mustReplaceIconsInTree",    "");
		preferenceStore.setDefault("mustReplaceLabelsInviews",  "");
		logger = new SpecializationLogger(SpecializationPlugin.class);
		
		logger.info("Initialising "+pluginName+" plugin ...");
		
		logger.info("===============================================");
		// we force the class initialization by the SWT thread
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				SpecializationPlugin.closePopup();
			}
		});
		
		// we check if the plugin has been upgraded using the automatic procedure
		try {
			pluginsPackage = SpecializationPlugin.class.getPackage().getName();
			pluginsFilename = new File(SpecializationPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalPath();
			pluginsFolder = (new File(pluginsFilename+File.separator+"..")).getCanonicalPath();

			if ( logger.isDebugEnabled() ) {
				logger.debug("plugin's package  = "+pluginsPackage);
				logger.debug("plugin's folder   = "+pluginsFolder);
				logger.debug("plugin's filename = "+pluginsFilename);
				if ( !pluginsFilename.endsWith(".jar") )
					logger.debug("(the plugin's filename is not a jar file, so Archi is running inside Eclipse)");
			}

			if ( Files.exists(FileSystems.getDefault().getPath(pluginsFolder+File.separator+"specializationPlugin.new"), LinkOption.NOFOLLOW_LINKS) ) {
				if ( logger.isDebugEnabled() ) logger.debug("found file \""+pluginsFolder+File.separator+"specializationPlugin.new\"");
				
				try {
					String installedPluginsFilename = Files.readAllBytes(Paths.get(pluginsFolder+File.separator+"specializationPlugin.new")).toString();
					
					if ( areEqual(pluginsFilename, installedPluginsFilename) ) 
						popup(Level.INFO, "The specialization plugin has been correctly updated to version "+pluginVersion);
					else
						popup(Level.ERROR, "The specialization plugin has been correctly downloaded to \""+installedPluginsFilename+"\" but you are still using the specialization plugin version "+pluginVersion+".\n\nPlease check the plugin files located in the \""+pluginsFolder+"\" folder.");
				} catch (IOException e1) {
					popup(Level.WARN, "A new version of the specialization plugin has been downloaded but we failed to check if you are using the latest version.\n\nPlease check the plugin files located in the \""+pluginsFolder+"\" folder.");
				}
				
				try {
					if ( logger.isDebugEnabled() ) logger.debug("deleting file "+pluginsFolder+File.separator+"specializationPlugin.new");
					Files.delete(FileSystems.getDefault().getPath(pluginsFolder+File.separator+"specializationPlugin.new"));
				} catch ( IOException e ) {
					popup(Level.ERROR, "Failed to delete file \""+pluginsFolder+File.separator+"specializationPlugin.new\"\n\nYou need to delete it manually.");
				}
			} else if ( preferenceStore.getBoolean("checkForUpdateAtStartup") ) {
				checkForUpdate(false);
			}
		} catch ( IOException e ) {
			popup(Level.ERROR, "Failed to get specialization plugin's folder.", e);
		}
		
		logger.info(pluginTitle+" initialized.");
	}
	
	public static int getIconMargin() {
	    return preferenceStore.getInt("iconMargin");
	}
	
	public static String mustReplaceIconsInViews() {
	    return preferenceStore.getString("mustReplaceIconsInViews");
	}
	
	public static String mustReplaceIconsInTree() {
	    return preferenceStore.getString("mustReplaceIconsInTree");
	}
	
	public static String mustReplaceLabelsInViews() {
	    return preferenceStore.getString("mustReplaceLabelsInViews");
	}
	
	public static boolean shouldShowImages() { return false; };
	
	/**
	 * Shows up an on screen popup, displaying the message (and the exception message if any) and wait for the user to click on the "OK" button
	 */
	public static void popup(Level level, String msg) {
		popup(level, msg, null);
	}
	
	/**
	 * Shows up an on screen popup, displaying the message (and the exception message if any) and wait for the user to click on the "OK" button<br>
	 * The exception stacktrace is also printed on the standard error stream
	 */
	public static void popup(Level level, String msg, Exception e) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				String popupMessage = msg;
				logger.log(SpecializationPlugin.class, level, msg, e);
		
				if ( e != null ) {
					if ( !SpecializationPlugin.areEqual(e.getMessage(), msg)) {
						popupMessage += "\n\n" + e.getMessage();
					} else {
						popupMessage += "\n\n" + e.getClass().getName();
					}
				}
				
				switch ( level.toInt() ) {
					case Level.FATAL_INT :
					case Level.ERROR_INT :
						MessageDialog.openError(null, SpecializationPlugin.pluginTitle, popupMessage);
						break;
					case Level.WARN_INT :
						MessageDialog.openWarning(null, SpecializationPlugin.pluginTitle, popupMessage);
						break;
					default :
						MessageDialog.openInformation(null, SpecializationPlugin.pluginTitle, popupMessage);
						break;
				}
			}
		});
	}
	
	/**
	 * Check if two strings are equals<br>
	 * Replaces string.equals() to avoid nullPointerException
	 */
	public static boolean areEqual(String str1, String str2) {
		if ( str1 == null )
			return str2 == null;

		if ( str2 == null )
			return false;			// as str1 cannot be null at this stage

		return str1.equals(str2);
	}
	
	/**
	 * Calculates the full name of an EObject 
	 * @return getclass().getSimpleName()+":\""+getName()+"\""
	 */
	public static String getFullName(EObject obj) {
		StringBuilder objName = new StringBuilder(obj.getClass().getSimpleName());
		objName.append(":\""+((INameable)obj).getName()+"\"");
		return objName.toString();
	}
	
	/**
	 * Calculates the debug name of an EObject
	 * @return getclass().getSimpleName()+":\""+getName()+"\"("+getId()+")"
	 */
	public static String getDebugName(EObject obj) {
		StringBuilder objName = new StringBuilder(getFullName(obj));
		objName.append("("+((IIdentifier)obj).getId()+")");
		return objName.toString();
	}
	
	private static Shell dialogShell = null;
	private static Label dialogLabel = null;
	
	/**
	 * shows up an on screen popup displaying the message but does not wait for any user input<br>
	 * it is the responsibility of the caller to dismiss the popup 
	 */
	public static Shell popup(String msg) {
	    logger.info(msg);

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if ( dialogShell == null ) {
					dialogShell = new Shell(display, SWT.BORDER | SWT.APPLICATION_MODAL);
					dialogShell.setSize(500, 70);
					dialogShell.setBackground(COMPO_LEFT_COLOR);
					dialogShell.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - dialogShell.getSize().x) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - dialogShell.getSize().y) / 4);
					dialogShell.setLayout(new GridLayout( 1, false ) );
					
					dialogLabel = new Label(dialogShell, SWT.CENTER | SWT.WRAP);
					dialogLabel.setBackground(COMPO_LEFT_COLOR);
					dialogLabel.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, true ) );
					dialogLabel.setFont(TITLE_FONT);
				} else {
					restoreCursors();
				}
				
				dialogLabel.setText(msg);
				dialogShell.layout(true);
				dialogShell.open();
				
				setArrowCursor();
			}
		});
		
		return dialogShell;
	}
	/**
	 * dismiss the popup if it is displayed (else, does nothing) 
	 */
	public static void closePopup() {
		if ( dialogShell != null ) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					dialogShell.close();
					dialogShell = null;
					
					restoreCursors();
				}
			});
		}
	}
	
	private static Stack<Map<Shell, Cursor>> cursorsStack = new Stack<Map<Shell, Cursor>>();
	public static void setArrowCursor() {
		if ( logger.isDebugEnabled() ) logger.debug("Setting arrow cursor");
		Map<Shell, Cursor> cursors = new HashMap<Shell, Cursor>();
		for ( Shell shell: display.getShells() ) {
			cursors.put(shell,  shell.getCursor());
			shell.setCursor(CURSOR_WAIT);
		}
		cursorsStack.push(cursors);
		refreshDisplay();
	}
	
	public static void restoreCursors() {
		if ( logger.isDebugEnabled() ) logger.debug("Restoring cursors");
		Map<Shell, Cursor> cursors = cursorsStack.pop();
		for ( Shell shell: display.getShells() ) {
			Cursor cursor = (cursors==null) ? null : cursors.get(shell);
			shell.setCursor(cursor==null ? CURSOR_ARROW : cursor);
		}
		refreshDisplay();
	}
	
	private static int questionResult;
	
	/**
	 * Shows up an on screen popup displaying the question (and the exception message if any)  and wait for the user to click on the "YES" or "NO" button<br>
	 * The exception stacktrace is also printed on the standard error stream
	 */
	public static boolean question(String msg) {
		return question(msg, new String[] {"Yes", "No"}) == 0;
	}

	/**
	 * Shows up an on screen popup displaying the question (and the exception message if any)  and wait for the user to click on the "YES" or "NO" button<br>
	 * The exception stacktrace is also printed on the standard error stream
	 */
	public static int question(String msg, String[] buttonLabels) {
		if ( logger.isDebugEnabled() ) logger.debug("question : "+msg);
		
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				//questionResult = MessageDialog.openQuestion(display.getActiveShell(), SpecializationPlugin.pluginTitle, msg);
				MessageDialog dialog = new MessageDialog(display.getActiveShell(), SpecializationPlugin.pluginTitle, null, msg, MessageDialog.QUESTION, buttonLabels, 0);
				questionResult = dialog.open();
			}
		});

		if ( logger.isDebugEnabled() ) logger.debug("answer : "+buttonLabels[questionResult]);
		return questionResult;
	}
	
	/**
	 * shows up an on screen popup with a progressbar<br>
	 * it is the responsibility of the caller to dismiss the popup 
	 */
	public static ProgressBar progressbarPopup(String msg) {
		if ( logger.isDebugEnabled() ) logger.debug("new progressbarPopup(\""+msg+"\")");
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(600, 100);
		shell.setBackground(BLACK_COLOR);
		shell.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - shell.getSize().x) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - shell.getSize().y) / 4);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBackground(COMPO_LEFT_COLOR);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		label.setBackground(COMPO_LEFT_COLOR);
		label.setFont(TITLE_FONT);
		label.setText(msg);
		
		ProgressBar progressBar = new ProgressBar(composite, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));
    	progressBar.setMinimum(0);
    	progressBar.setMaximum(100);
    	
    	shell.layout();
    	shell.open();
    	
    	refreshDisplay();
    	
    	return progressBar;
	}
	
	/**
	 * Refreshes the display
	 */
	public static void refreshDisplay() {
		while ( Display.getCurrent().readAndDispatch() ) 
			;
	}
	
	private static ProgressBar updateProgressbar = null;
	private static int updateDownloaded = 0;
	public static void checkForUpdate(boolean verbose) {
		new Thread("checkForUpdate") {
			@Override
			public void run() {
				if ( verbose )
					popup("Please wait while checking for new specialization plugin ...");
				else
					logger.debug("Checking for a new plugin version on GitHub");
				
				// We connect to GitHub and get the latest plugin file version
				// Do not forget the "-Djdk.http.auth.tunneling.disabledSchemes=" in the ini file if you connect through a proxy
				String PLUGIN_API_URL = "https://api.github.com/repos/archi-contribs/specialization-plugin/contents";
				String RELEASENOTE_URL = "https://github.com/archi-contribs/specialization-plugin/blob/master/release_note.md";

				Map<String, String> versions = new TreeMap<String, String>(Collections.reverseOrder());

				try {
					JSONParser parser = new JSONParser();
					Authenticator.setDefault(new Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
						    logger.debug("requestor type = "+getRequestorType());
							if (getRequestorType() == RequestorType.PROXY) {
								String prot = getRequestingProtocol().toLowerCase();
								String host = System.getProperty(prot + ".proxyHost", "");
								String port = System.getProperty(prot + ".proxyPort", "80");
								String user = System.getProperty(prot + ".proxyUser", "");
								String pass = System.getProperty(prot + ".proxyPassword", "");
								
								if ( logger.isDebugEnabled() ) {
								    logger.debug("proxy request from "+getRequestingHost()+":"+getRequestingPort());
								    logger.debug("proxy configuration:");
								    logger.debug("   prot : "+prot);
								    logger.debug("   host : "+host);
								    logger.debug("   port : "+port);
								    logger.debug("   user : "+user);
								    logger.debug("   pass : xxxxx");
								}

								// we check if the request comes from the proxy, else we do not send the password (for security reason)
								// TODO: check IP address in addition of the FQDN
								if ( getRequestingHost().equalsIgnoreCase(host) && (Integer.parseInt(port) == getRequestingPort()) ) {
									// Seems to be OK.
									logger.debug("Setting PasswordAuthenticator");
									return new PasswordAuthentication(user, pass.toCharArray());
								} else {
								    logger.debug("Not setting PasswordAuthenticator as the request does not come from the proxy (host + port)");
								}
							}
							return null;
						}  
					});
					
					
                    if ( logger.isDebugEnabled() ) logger.debug("connecting to "+PLUGIN_API_URL);
                    HttpsURLConnection conn = (HttpsURLConnection)new URL(PLUGIN_API_URL).openConnection();

					if ( logger.isDebugEnabled() ) logger.debug("getting file list");
					JSONArray result = (JSONArray)parser.parse(new InputStreamReader(conn.getInputStream()));

					if ( result == null ) {
						if ( verbose ) {
							closePopup();
							popup(Level.ERROR, "Failed to check for new specialization plugin version.\n\nParsing error.");
						} else
							logger.error("Failed to check for new specialization plugin version.\n\nParsing error.");
						return;
					}

					if ( logger.isDebugEnabled() ) logger.debug("searching for plugins jar files");
					Pattern p = Pattern.compile(pluginsPackage+"_v(.*).jar") ;

					@SuppressWarnings("unchecked")
					Iterator<JSONObject> iterator = result.iterator();
					while (iterator.hasNext()) {
						JSONObject file = iterator.next();
						Matcher m = p.matcher((String)file.get("name")) ;
						if ( m.matches() ) {
							if ( logger.isDebugEnabled() ) logger.debug("found version "+m.group(1)+" ("+(String)file.get("download_url")+")");
							versions.put(m.group(1), (String)file.get("download_url"));
						}
					}

					if ( verbose ) closePopup();

					if ( versions.isEmpty() ) {
						if ( verbose )
							popup(Level.ERROR, "Failed to check for new specialization plugin version.\n\nDid not find any "+pluginsPackage+" JAR file.");
						else
							logger.error("Failed to check for new specialization plugin version.\n\nDid not find any "+pluginsPackage+" JAR file.");
						return;
					}
				} catch (Exception e) {
					if ( verbose ) {
						closePopup();
						popup(Level.ERROR, "Failed to check for new version on GitHub.", e);
					} else {
						logger.error("Failed to check for new version on GitHub.", e);
					}
					return;
				}

				String newPluginFilename = null;
				String tmpFilename = null;
				try {
					// treemap is sorted in descending order, so first entry should have the "bigger" key value, i.e. the latest version
					Entry<String, String> entry = versions.entrySet().iterator().next();

					if ( pluginVersion.compareTo((String)entry.getKey()) >= 0 ) {
						if ( verbose )
							popup(Level.INFO, "You already have got the latest version : "+pluginVersion);
						else
							logger.info("You already have got the latest version : "+pluginVersion);
						return;
					}
					
					if ( !pluginsFilename.endsWith(".jar") ) {
						if ( verbose )
							popup(Level.ERROR,"A new version of the specialization plugin is available:\n     actual version: "+pluginVersion+"\n     new version: "+(String)entry.getKey()+"\n\nUnfortunately, it cannot be downloaded while Archi is running inside Eclipse.");
						else
							logger.error("A new version of the specialization plugin is available:\n     actual version: "+pluginVersion+"\n     new version: "+(String)entry.getKey()+"\n\nUnfortunately, it cannot be downloaded while Archi is running inside Eclipse.");
						return;
					}

					boolean ask = true;
					while ( ask ) {
					    switch ( question("A new version of the specialization plugin is available:\n     actual version: "+pluginVersion+"\n     new version: "+(String)entry.getKey()+"\n\nDo you wish to download and install it ?", new String[] {"Yes", "No", "Check release note"}) ) {
					        case 0 : ask = false ; break;  // Yes
					        case 1 : return ;              // No
					        case 2 : ask = true ;          // release note
        					         Program.launch(RELEASENOTE_URL);
        					         break;
					    }
					}

					Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar = progressbarPopup("Downloading new version of specialization plugin ..."); }});

					URLConnection conn = new URL(entry.getValue()).openConnection();
					String FileType = conn.getContentType();
					int fileLength = conn.getContentLength();

					newPluginFilename = pluginsFolder+File.separator+entry.getValue().substring(entry.getValue().lastIndexOf('/')+1, entry.getValue().length());
					tmpFilename = newPluginFilename+".tmp";

					if ( logger.isTraceEnabled() ) {
						logger.trace("   File URL : " + entry.getValue());
						logger.trace("   File type : " + FileType);
						logger.trace("   File length : "+fileLength);
						logger.trace("   Tmp download file path : " + tmpFilename);
						logger.trace("   New Plugin file path : " + newPluginFilename);
					}

					if (fileLength == -1)
						throw new IOException("Failed to get file size.");
					else
						Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar.setMaximum(fileLength); }});

					InputStream in = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(new File(tmpFilename));	                
					byte[] buff = new byte[1024];
					int n;
					updateDownloaded = 0;

					if ( logger.isDebugEnabled() ) logger.debug("downloading file ...");
					while ((n=in.read(buff)) !=-1) {
						fos.write(buff, 0, n);
						updateDownloaded +=n;
						Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar.setSelection(updateDownloaded); }});
						//if ( logger.isTraceEnabled() ) logger.trace(updateDownloaded+"/"+fileLength);
					}
					fos.flush();
					fos.close();

					if ( logger.isDebugEnabled() ) logger.debug("download finished");

				} catch (Exception e) {
					logger.info("here");
					if( updateProgressbar != null ) Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar.getShell().dispose(); updateProgressbar = null; }});
					try {
						if ( tmpFilename != null ) Files.deleteIfExists(FileSystems.getDefault().getPath(tmpFilename));
					} catch (IOException e1) {
						logger.error("cannot delete file \""+tmpFilename+"\"", e1);
					}
					if ( verbose )
						popup(Level.ERROR, "Failed to download new version of specialization plugin.", e);
					else
						logger.error("Failed to download new version of specialization plugin.",e);
					return;
				}

				if( updateProgressbar != null ) Display.getDefault().syncExec(new Runnable() { @Override public void run() { updateProgressbar.getShell().dispose(); updateProgressbar = null;}});

				//install new plugin

				// we rename the tmpFilename to its definitive filename
				if ( logger.isDebugEnabled() ) logger.debug("renaming \""+tmpFilename+"\" to \""+newPluginFilename+"\"");
				try {
					Files.move(FileSystems.getDefault().getPath(tmpFilename), FileSystems.getDefault().getPath(newPluginFilename), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					if ( verbose )
						popup(Level.ERROR, "Failed to rename \""+tmpFilename+"\" to \""+newPluginFilename+"\"");
					else
						logger.error("Failed to rename \""+tmpFilename+"\" to \""+newPluginFilename+"\"");
					return;
				}

				try {
					Files.write(Paths.get(pluginsFolder+File.separator+"specializationPlugin.new"), newPluginFilename.getBytes());
				} catch(IOException ign) {
					// not a big deal, just that there will be no message after Archi is restarted
					logger.error("Cannot create file \""+pluginsFolder+File.separator+"specializationPlugin.new\"", ign);
				}

				// we delete the actual plugin file on Archi exit (can't do it here because the plugin is in use).
				(new File(pluginsFilename)).deleteOnExit();

				if( question("A new version on the specialization plugin has been downloaded. Archi needs to be restarted to install it.\n\nDo you wish to restart Archi now ?") ) {
					Display.getDefault().syncExec(new Runnable() { @Override public void run() { PlatformUI.getWorkbench().restart(); }});
				}
			};
		}.start();
	}
	
    public static String getPropertyValue(EObject obj, String propertyName) {
        if ( obj != null && obj instanceof IProperties) {
            for ( IProperty property:((IProperties)obj).getProperties() ) {
                if ( areEqual(property.getKey(), propertyName) ) {
                	// we return the value of the first required key
                    return property.getValue();
                }
            }
        }
        
        return null;
    }
    
    public static void setProperty(EObject obj, String propertyName, String propertyValue) {
    	boolean mustCreateProperty = true;
    	
        if ( obj != null && obj instanceof IProperties) {
            for ( IProperty property:((IProperties)obj).getProperties() ) {
                if ( areEqual(property.getKey(), propertyName) ) {
                    property.setValue(propertyValue);
                    // we change all properties that have the required key
                    mustCreateProperty = false;
                }
            }
        }
        
        if ( mustCreateProperty ) {
	        IProperty property = IArchimateFactory.eINSTANCE.createProperty();
	        property.setKey(propertyName);
	        property.setValue(propertyValue);
	        ((IProperties)obj).getProperties().add(property);
        }
    }
    
    public static void deleteProperty(EObject obj, String propertyName) {
    	boolean propertyRemoved = true;
    	
    	while ( propertyRemoved ) {
    		propertyRemoved = false;
	        if ( obj != null && obj instanceof IProperties) {
	            for ( IProperty property:((IProperties)obj).getProperties() ) {
	                if ( areEqual(property.getKey(), propertyName) ) {
	                	((IProperties)obj).getProperties().remove(property);
	                	propertyRemoved = true;
	                	break;
	                }
	            }
	        }
    	}
    }
	
    public static boolean mustReplaceIcon(EObject obj) {
        if ( obj != null ) {
        	// we do not change the icon of the element in the properties window
            if ( SpecializationPlugin.areEqual(new Exception().getStackTrace()[3].getClassName(), "com.archimatetool.editor.propertysections.PropertiesLabelProvider") )
            	return false;
            
            // we determine if the object is in a view or in a folder
            EObject container = obj;
            while ( container!=null && !(container instanceof IDiagramModel || container instanceof IFolder) )
                container = container.eContainer();
            if ( container == null ) {
                // Should not happen, but just in case
                logger.error(getFullName(obj) + " is not in a container");
                return false; 
            }
            
            String containerType = null;
            
            // then we check the preference in case we should ALWAYS or NEVER replace the icons
            String mustReplaceIcons;
            if ( container instanceof IFolder ) {
                mustReplaceIcons = mustReplaceIconsInTree();
                containerType = "folder";
            } else {
                mustReplaceIcons =  mustReplaceIconsInViews();
                containerType = "view";
            }
            
            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": must replace icons in "+containerType+" : "+mustReplaceIcons);
            if ( mustReplaceIcons != null ) {
                switch ( mustReplaceIcons ) {
                    case "always": return true;
                    case "never":  return false;
                }
            }
            
            // the we check if the container has got a "must replace icons"
            String propertyValue = getPropertyValue(container, "must replace icons");
            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": "+containerType+" says must replace icons = "+propertyValue);
            boolean mustReplace = false;
            if ( propertyValue != null ) {
                switch ( propertyValue.toLowerCase() ) {
                    case "yes": mustReplace = true;
                    case "no":  return false;
                    default:
                }
            }
                        
            // then we check if the model has got a "must replace icons"
            IArchimateModel model;
            if ( obj instanceof IDiagramModelArchimateObject )
                model = ((IDiagramModelArchimateObject)obj).getArchimateConcept().getArchimateModel();
            else
                return false;
            propertyValue = getPropertyValue(model, "must replace icons");
            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": model says must replace icons = "+propertyValue);
            if ( propertyValue != null ) {
                switch ( propertyValue.toLowerCase() ) {
                    case "yes": return true;
                    case "no":  return false;
                    default:
                }
            }
            
            return mustReplace;
        } else
            logger.error("got null object parameter");
        
        // if we're here, it means that we haven't seen any "must replace icons" property. 
        // so by default, we do not change the icon
        return false;
    }
    
    /**
     * Retrieves the icon name from the EObject properties
     * @param obj
     * @param mustExist (not used at the moment, created for future use)
     */
    public static String getIconName(EObject obj, boolean mustExist) {
        if ( obj != null ) {
            EObject concept = (obj instanceof IDiagramModelArchimateObject) ? ((IDiagramModelArchimateObject)obj).getArchimateConcept() : obj;
            if ( !(concept instanceof IProperties) ) {
                // Should not happen, but just in case
                logger.error(getFullName(obj) + " does not have properties");
                return null;
            }
            
            // first, we get the name of the property that will contains the name of the icon
            String iconPropertyName = getIconPropertyName(obj);
            
            if ( iconPropertyName == null ) 
                return null;
            
            // Now we get the icon filename from the property
            String iconName = getPropertyValue(obj, iconPropertyName);
            logger.trace(getFullName(obj)+" : property "+iconPropertyName+" = "+iconName);
            if ( iconName == null )
                return null;
            
            iconName = "/img/"+iconName;
            if ( IArchiImages.ImageFactory.getImage(iconName) != null ) {
                if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": icon file \"" + iconName + "\" has been found");
                return iconName;
            }
            logger.error(getFullName(obj) + ": icon file \"" + iconName + "\" has not been found");
        } else
        	logger.error("got null object parameter");
        return null;
    }
    
    
    /**
     * Sets the icon name into the EObject properties
     * @param obj
     * @param iconName
     */
    public static void setIconName(EObject obj, String iconName) {
        if ( obj != null ) {
            EObject concept = (obj instanceof IDiagramModelArchimateObject) ? ((IDiagramModelArchimateObject)obj).getArchimateConcept() : obj;
            if ( !(concept instanceof IProperties) ) {
                // Should not happen, but just in case
                logger.error(getFullName(obj) + " does not have properties");
                return;
            }
            
            // first, we get the name of the property that will contains the name of the icon
            
            // we check the element, the view and the model (in that order)
            String iconPropertyName = getIconPropertyName(obj);
            if ( iconPropertyName == null )
                return;
            
            // Now we set the icon filename into the property
            logger.trace(getFullName(obj)+" : setting property "+iconPropertyName+" = "+iconName);
            setProperty(obj, iconPropertyName, iconName);
        }
    }
    
    /**
     * Gets the name of the property that should contain the icon name
     * @param obj
     * @return
     */
    public static String getIconPropertyName(EObject obj) {
        EObject concept = (obj instanceof IDiagramModelArchimateObject) ? ((IDiagramModelArchimateObject)obj).getArchimateConcept() : obj;
        if ( !(concept instanceof IProperties) ) {
            // Should not happen, but just in case
            logger.error(getFullName(obj) + " does not have properties");
            return null;
        }

        // we check the element, the view and the model (in that order)
        String iconPropertyName = getPropertyValue(concept, "replace icons property");
        if ( iconPropertyName != null ) {
            if ( iconPropertyName.length() == 0 ) {
                if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": replace icons property is empty, we ignore it");
                iconPropertyName = null;
            } else
                if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": replace icons property = "+iconPropertyName);
        }
        if ( iconPropertyName == null ) {
            EObject container = obj;
            while ( container!=null && !(container instanceof IDiagramModel || container instanceof IFolder) )
                container = container.eContainer();
            if ( container == null ) {
                // Should not happen, but just in case
                logger.error(getFullName(obj) + " is not in a container");
                return null; 
            }
            
            String containerType = (container instanceof IFolder) ? "folder" : "view";
            iconPropertyName = getPropertyValue(container, "replace icons property");
            if ( iconPropertyName != null ) {
                if ( iconPropertyName.length() == 0 ) {
                    if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": "+containerType+"'s replace icons property is empty, we ignore it");
                    iconPropertyName = null;
                } else {
                    if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": "+containerType+"'s replace icons property = "+iconPropertyName);
                    iconPropertyName = getPropertyValue(((IArchimateModelObject)obj).getArchimateModel(), "replace icons property");
                    if ( iconPropertyName != null )
                        if ( iconPropertyName.length() == 0 ) {
                            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": model's replace icons property is empty, we ignore it");
                            iconPropertyName = null;
                        } else
                            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": model's replace icons property = "+iconPropertyName);
                }
            }
        }
        if ( iconPropertyName == null ) {
            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": replace icons property defaults to \"icon\"");
            iconPropertyName = "icon";
        }
        
        return iconPropertyName;
    }
    
    public static boolean mustReplaceLabel(EObject obj) {
        if ( obj != null ) {
            // we determine if the object is in a view or in a folder
            EObject container = obj;
            while ( container!=null && !(container instanceof IDiagramModel || container instanceof IFolder) )
                container = container.eContainer();
            if ( container == null ) {
                // Should not happen, but just in case
                logger.error(getFullName(obj) + " is not in a container");
                return false; 
            }
            
            // then we check the preference in case we should ALWAYS or NEVER replace the labels
            String mustReplaceLabels;
            if ( container instanceof IFolder ) {
                if ( logger.isDebugEnabled() ) logger.debug(getFullName(obj) + ": we never change the label in the model tree");
                return false;
            }
            mustReplaceLabels =  mustReplaceLabelsInViews();
            
            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": must replace labels : "+mustReplaceLabels);
            if ( mustReplaceLabels != null ) {
                switch ( mustReplaceLabels ) {
                    case "always": return true;
                    case "never":  return false;
                }
            }
            
            // the we check if the container has got a "must replace labels"
            String propertyValue = getPropertyValue(container, "must replace labels");
            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": view says must replace labels = "+propertyValue);
            if ( propertyValue != null ) {
                switch ( propertyValue.toLowerCase() ) {
                    case "yes": return true;
                    case "no":  return false;
                    default:
                }
            }
                        
            // then we check if the model has got a "must replace labels"
            IArchimateModel model;
            if ( obj instanceof IDiagramModelArchimateObject )
                model = ((IDiagramModelArchimateObject)obj).getArchimateConcept().getArchimateModel();
            else
                return false;
            propertyValue = getPropertyValue(model, "must replace labels");
            if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": model says must replace labels = "+propertyValue);
            if ( propertyValue != null ) {
                switch ( propertyValue.toLowerCase() ) {
                    case "yes": return true;
                    case "no":  return false;
                    default:
                }
            }
        } else
            logger.error("got null object parameter");
        
        // if we're here, it means that we haven't seen any "must replace labels" property. 
        // so by default, we do not change the icon
        return false;
    }
    
    /**
     * Retrieves the label name from the EObject properties
     * @param obj
     */
    public static String getLabelName(EObject obj) {
        if ( obj != null ) {
            EObject concept = (obj instanceof IDiagramModelArchimateObject) ? ((IDiagramModelArchimateObject)obj).getArchimateConcept() : obj;
            if ( !(concept instanceof IProperties) ) {
                // Should not happen, but just in case
                logger.error(getFullName(obj) + " does not have properties");
                return null;
            }
            
            // first, we get the name of the property that will contains the name of the label
            
            // we check the element, the view and the model (in that order)
            String iconPropertyName = getPropertyValue(concept, "replace labels property");
            if ( iconPropertyName != null ) {
                if ( iconPropertyName.length() == 0 ) {
                    if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": replace labels property is empty, we ignore it");
                    iconPropertyName = null;
                } else
                    if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": replace labels property = "+iconPropertyName);
            }
            if ( iconPropertyName == null ) {
                EObject container = obj;
                while ( container!=null && !(container instanceof IDiagramModel || container instanceof IFolder) )
                    container = container.eContainer();
                if ( container == null ) {
                    // Should not happen, but just in case
                    logger.error(getFullName(obj) + " is not in a container");
                    return null; 
                }
                
                String containerType = (container instanceof IFolder) ? "folder" : "view";
                iconPropertyName = getPropertyValue(container, "replace labels property");
                if ( iconPropertyName != null ) {
                    if ( iconPropertyName.length() == 0 ) {
                        if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": view's replace labels property is empty, we ignore it");
                        iconPropertyName = null;
                    } else {
                        if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": "+containerType+"'s replace labels property = "+iconPropertyName);
                        iconPropertyName = getPropertyValue(((IArchimateModelObject)obj).getArchimateModel(), "replace labels property");
                        if ( iconPropertyName != null )
                            if ( iconPropertyName.length() == 0 ) {
                                if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": model's replace labels property is empty, we ignore it");
                                iconPropertyName = null;
                            } else
                                if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": model's replace labels property = "+iconPropertyName);
                    }
                }
            }
            if ( iconPropertyName == null ) {
                if ( logger.isTraceEnabled() ) logger.trace(getFullName(obj) + ": replace labels property defaults to \"icon\"");
                iconPropertyName = "icon";
            }
            
            // Now we get the icon filename from the property
            for ( IProperty prop: ((IProperties)concept).getProperties() ) {
                if ( SpecializationPlugin.areEqual(prop.getKey(), iconPropertyName) ) {
                    if ( logger.isDebugEnabled() ) logger.debug(getFullName(obj) + ": Replacing label by "+ prop.getValue());
                    return prop.getValue();
                }
            }
        } else
            logger.error("got null object parameter");
        return null;
    }
    
    public static void refreshIconsAndLabels(INameable obj) {
        // we set the object name to force the framework to redraw the corresponding object in the current view 
    	obj.setName(obj.getName());
        if ( obj instanceof IDiagramModelContainer ) {
            for ( IDiagramModelObject child: ((IDiagramModelContainer)obj).getChildren() ) {
            	refreshIconsAndLabels(child);
                for ( IDiagramModelConnection relation: child.getSourceConnections() ) {
                	refreshIconsAndLabels(relation);
                }
            }
        }
        if ( obj instanceof IDiagramModelObject ) {
            for ( IDiagramModelConnection relation: ((IDiagramModelObject)obj).getSourceConnections() ) {
            	refreshIconsAndLabels(relation);
            }
        }
    }
    
    public static boolean inArray(String[] stringArray, String string) {
    	if ( string == null )
    		return true;
    	
        for (String s : stringArray) {
            if (areEqual(s, string))
                return true;
        }
        return false;
    }
}
