package org.archicontribs.specialization;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * The SpecializationLogger class is a proxy for the log4j Logger class.
 * 
 * @author Herve Jouin
 */
public class SpecializationLogger {
	static private boolean initialised = false;
	private Logger logger;

	public <T> SpecializationLogger(Class<T> clazz) {
		if ( ! initialised ) {
			try {
				configure();
			} catch (Exception e) {
				initialised = false;
				SpecializationPlugin.popup(Level.ERROR, "Failed to configure logger", e);
			}
		}
		logger = Logger.getLogger(clazz);
	}
	
	/**
	 * Gets the logger
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Configure the logger
	 */
	public void configure() throws Exception {
		LinkedProperties properties = getLoggerProperties();

		if ( properties != null ) {
			PropertyConfigurator.configure(properties);
			initialised = true;
		} else {
			LogManager.shutdown();
			initialised = false;
		}
		
		if ( initialised ) {
			Logger oldLogger = logger;
			logger = Logger.getLogger(SpecializationLogger.class);
			if ( isDebugEnabled() ) debug("Logger initialised.");
			if ( isTraceEnabled() ) {
				StringBuilder param = new StringBuilder();
				String eol = "";
				for ( Object oKey: properties.orderedKeys() ) {
					param.append((String)oKey+" = "+properties.getProperty((String)oKey)+eol);
					eol = "\n";
				}
				trace(param.toString());
			}
			logger = oldLogger;
		}
    }

	/**
	 * Logs a message
	 */
	public <T> void log(Class<T> clazz, Level level, String message, Throwable t) {
		String className = clazz.getName();
		
		if ( initialised ) {
			String[] lines = message.split("\n");
			if ( lines.length == 1 ) {
				logger.log(className, level, "- "+message.replace("\r",""), t);
			} else {
				logger.log(className, level, "┌ "+lines[0].replace("\r",""), null);
				for ( int i=1 ; i < lines.length-2 ; ++i) {
					logger.log(className, level, "│ "+lines[i].replace("\r",""), null);
				}
				logger.log(className, level, "└ "+lines[lines.length-1].replace("\r",""), t);
			}
		}
	}
	
	/**
	 * Logs a message
	 */
	public void log(Level level, String message)						{ log(this.getClass(), level, message, null); }
	/**
	 * Logs a message
	 */
	public <T> void log(Class<T> clazz, Level level, String message)	{ log(clazz, level, message, null); }
	
	/**
	 * Logs a fatal message
	 */
	public void fatal(String message)									{ log(this.getClass(), Level.FATAL, message, null); }
	/**
	 * Logs a fatal message
	 */
    public void fatal(String message, Throwable t)						{ log(this.getClass(), Level.FATAL, message, t); }
	/**
	 * Logs a fatal message
	 */
    public <T> void fatal(Class<T> clazz, String message)				{ log(clazz, Level.FATAL, message, null); }
	/**
	 * Logs a fatal message
	 */
    public <T> void fatal(Class<T> clazz, String message, Throwable t)	{ log(clazz, Level.FATAL, message, t); }
    
	/**
	 * Logs an error message
	 */
    public void error(String message)									{ log(this.getClass(), Level.ERROR, message, null); }
	/**
	 * Logs an error message
	 */
    public void error(String message, Throwable t)						{ log(this.getClass(), Level.ERROR, message, t); }
	/**
	 * Logs an error message
	 */
    public <T> void error(Class<T> clazz, String message)				{ log(clazz, Level.ERROR, message, null); }
	/**
	 * Logs an error message
	 */
    public <T> void error(Class<T> clazz, String message, Throwable t)	{ log(clazz, Level.ERROR, message, t); }
	
	/**
	 * Logs a warn message
	 */
    public void warn(String message)									{ log(this.getClass(), Level.WARN, message, null); }
	/**
	 * Logs a warn message
	 */
    public void warn(String message, Throwable t)						{ log(this.getClass(), Level.WARN, message, t); }
	/**
	 * Logs a warn message
	 */
    public <T> void warn(Class<T> clazz, String message)				{ log(clazz, Level.WARN, message, null); }
	/**
	 * Logs a warn message
	 */
    public <T> void warn(Class<T> clazz, String message, Throwable t)	{ log(clazz, Level.WARN, message, t); }
    
	/**
	 * Logs an info message
	 */
    public void info(String message)									{ log(this.getClass(), Level.INFO, message, null); }
	/**
	 * Logs an info message
	 */
    public void info(String message, Throwable t)						{ log(this.getClass(), Level.INFO, message, t); }
	/**
	 * Logs an info message
	 */
    public <T> void info(Class<T> clazz, String message)				{ log(clazz, Level.INFO, message, null); }
	/**
	 * Logs an info message
	 */
    public <T> void info(Class<T> clazz, String message, Throwable t)	{ log(clazz, Level.INFO, message, t); }
    
	/**
	 * Logs a debug message
	 */
    public void debug(String message)									{ log(this.getClass(), Level.DEBUG, message, null); }
	/**
	 * Logs a debug message
	 */
	public void debug(String message, Throwable t)						{ log(this.getClass(), Level.DEBUG, message, t); }
	/**
	 * Logs a debug message
	 */
    public <T> void debug(Class<T> clazz, String message)				{ log(clazz, Level.DEBUG, message, null); }
	/**
	 * Logs a debug message
	 */
    public <T> void debug(Class<T> clazz, String message, Throwable t)	{ log(clazz, Level.DEBUG, message, t); }
    
	/**
	 * Logs a trace message
	 */
    public void trace(String message)									{ log(this.getClass(), Level.TRACE, message, null); }
	/**
	 * Logs a trace message
	 */
    public void trace(String message, Throwable t)						{ log(this.getClass(), Level.TRACE, message, t); }
	/**
	 * Logs a trace message
	 */
    public <T> void trace(Class<T> clazz, String message)				{ log(clazz, Level.TRACE, message, null); }
	/**
	 * Logs a trace message
	 */
    public <T> void trace(Class<T> clazz, String message, Throwable t)	{ log(clazz, Level.TRACE, message, t); }

	/**
	 * Get the initialised state of the logger
	 */
    public boolean isInitialised() {
    	return initialised;
    }
    
	/**
	 * Gets the logger properties
	 */
	private LinkedProperties getLoggerProperties() throws Exception {
		//LogManager.resetConfiguration();
		
		
		String loggerMode = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("loggerMode");
		if ( loggerMode == null )
			return null;

		LinkedProperties properties = new LinkedProperties() {
			private static final long serialVersionUID = 1L;
			@Override
			public Set<Object> keySet() { return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet())); }
		};
		
		debug("getting logger preferences from store");
		
		switch (loggerMode) {
		case "disabled" :
			return null;
			
		case "simple" :
    		properties.setProperty("log4j.rootLogger",									SpecializationPlugin.INSTANCE.getPreferenceStore().getString("loggerLevel").toUpperCase()+", stdout, file");
    		
    		properties.setProperty("log4j.appender.stdout",								"org.apache.log4j.ConsoleAppender");
    		properties.setProperty("log4j.appender.stdout.Target",						"System.out");
    		properties.setProperty("log4j.appender.stdout.layout",						"org.apache.log4j.PatternLayout");
    		properties.setProperty("log4j.appender.stdout.layout.ConversionPattern",	"%d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-30.30C{1} %m%n");
    		
    		properties.setProperty("log4j.appender.file",								"org.apache.log4j.FileAppender");
    		properties.setProperty("log4j.appender.file.ImmediateFlush",				"true");
    		properties.setProperty("log4j.appender.file.Append",						"false");
    		properties.setProperty("log4j.appender.file.Encoding",						"UTF-8");
    		properties.setProperty("log4j.appender.file.File",							SpecializationPlugin.INSTANCE.getPreferenceStore().getString("loggerFilename"));
    		properties.setProperty("log4j.appender.file.layout", 						"org.apache.log4j.PatternLayout");
    		properties.setProperty("log4j.appender.file.layout.ConversionPattern",		"%d{yyyy-MM-dd HH:mm:ss} %-5p %4L:%-30.30C{1} %m%n");
    		break;
    		
		case "expert" :
    		String loggerExpert = SpecializationPlugin.INSTANCE.getPreferenceStore().getString("loggerExpert");
    		if ( loggerExpert == null ) SpecializationPlugin.INSTANCE.getPreferenceStore().getDefaultString("loggerExpert");
    		
    		try {
				properties.load(new StringReader(loggerExpert));
			} catch (IOException err) {
				throw new Exception("Error while parsing \"loggerExpert\" properties from the preference store");
			}
		}
		
		return properties;
	}
	
	/**
	 * List that maintain elements order 
	 */
	private class LinkedProperties extends Properties {
		private static final long serialVersionUID = 1L;
		
		private final HashSet<Object> keys = new LinkedHashSet<Object>();

	    public LinkedProperties() {
	    }

	    public Iterable<Object> orderedKeys() {
	        return Collections.list(keys());
	    }

	    public Enumeration<Object> keys() {
	        return Collections.<Object>enumeration(keys);
	    }

	    public Object put(Object key, Object value) {
	        keys.add(key);
	        return super.put(key, value);
	    }
	}
	
	/**
	 * Returns true if the logger is configured to print trace messages
	 */
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}
	
	/**
	 * Returns true if the logger is configured to print debug messages
	 */
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
}
