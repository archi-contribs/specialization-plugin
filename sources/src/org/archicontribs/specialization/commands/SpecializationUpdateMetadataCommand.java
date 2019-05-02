package org.archicontribs.specialization.commands;

import org.archicontribs.specialization.SpecializationLogger;
import org.archicontribs.specialization.SpecializationPlugin;
import org.archicontribs.specialization.types.ElementSpecializationMap;
import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IMetadata;
import com.archimatetool.model.IProperty;
import com.google.gson.Gson;

public class SpecializationUpdateMetadataCommand extends Command {
	static final SpecializationLogger logger = new SpecializationLogger(SpecializationUpdateMetadataCommand.class);
	
	IArchimateModel model = null;
	IMetadata oldMetadata = null;
	IMetadata metadata = null;
	IProperty metadataProperty = null;
	String oldMetadataValue = null;
	String metadataValue = null;
	Exception exception = null;
	
	boolean executed = false;
	boolean metadataChanged = false;

	public SpecializationUpdateMetadataCommand(IArchimateModel model, ElementSpecializationMap specializationsMap) {
		this.model = model;
		
		this.oldMetadata = this.model.getMetadata();
		if ( this.oldMetadata != null ) {
			this.metadata = this.oldMetadata;
			this.metadataProperty = this.metadata.getEntry(SpecializationPlugin.METADATA_KEY);
			if ( this.metadataProperty != null )
				this.oldMetadataValue = this.metadataProperty.getValue();
		} else {
			this.metadata = IArchimateFactory.eINSTANCE.createMetadata();
		}
		
		try {
			Gson gson = new Gson();
			this.metadataValue = gson.toJson(specializationsMap);
		} catch (Exception e) {
			this.exception = e;
		}
		
		this.metadataChanged = !this.metadataValue.equals(this.oldMetadataValue);
	}

	@Override public void execute() {
		logger.trace("Setting specialization metadata from ...");
		logger.trace(this.oldMetadataValue);
		logger.trace("to ...");
		logger.trace(this.metadataValue);
		
		if ( this.oldMetadata == null )
			this.model.setMetadata(this.metadata);
		
		if ( this.metadataProperty == null )
			this.model.getMetadata().addEntry(SpecializationPlugin.METADATA_KEY, this.metadataValue);
		else
			this.metadataProperty.setValue(this.metadataValue);
		
		this.executed = true;
	}

	@Override public void undo() {
		logger.trace("Restoring specialization metadata from ...");
		logger.trace(this.metadataValue);
		logger.trace("to ...");
		logger.trace(this.oldMetadataValue);
		
		if ( this.oldMetadata == null )
			this.model.setMetadata(null);
		else {
			if ( this.metadataProperty == null ) {
				for (int i = 0 ; i < this.model.getMetadata().getEntries().size(); ++i ) {
					if ( this.model.getMetadata().getEntries().get(i).getKey().equals(SpecializationPlugin.METADATA_KEY) ) {
						this.model.getMetadata().getEntries().remove(i);
						break;
					}
				}
			} else
				this.metadataProperty.setValue(this.oldMetadataValue);
		}
		
		this.executed = false;
	}
	
	public Exception getException() {
		return this.exception;
	}
	
	@Override public boolean canExecute() {
		return !this.executed && this.metadataChanged && (this.exception == null);
	}

	@Override public boolean canUndo() {
		return this.executed && this.metadataChanged && (this.exception == null);
	}

	@Override public boolean canRedo() {
		return canExecute();
	}
	
	@Override public void dispose() {
		this.model = null;
		this.oldMetadata = null;
		this.metadata = null;
		this.metadataProperty = null;
		this.oldMetadataValue = null;
		this.metadataValue = null;
		this.exception = null;
	}
}
