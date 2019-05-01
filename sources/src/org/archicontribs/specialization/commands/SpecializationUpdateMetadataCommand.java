package org.archicontribs.specialization.commands;

import org.archicontribs.specialization.types.ElementSpecializationMap;
import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IProperty;
import com.google.gson.Gson;

public class SpecializationUpdateMetadataCommand extends Command {
	IArchimateModel model;
	ElementSpecializationMap specializationsMap;
	Exception exception;

	public SpecializationUpdateMetadataCommand(IArchimateModel model, ElementSpecializationMap specializationsMap) {
		this.model = model;
		this.specializationsMap = specializationsMap;
		this.exception = null;
	}

	@Override public void execute() {
		try {
			Gson gson = new Gson();
			String jsonValue = gson.toJson(this.specializationsMap);

			IProperty metadata = this.model.getMetadata().getEntry("Specializations");
			if ( metadata == null )
				this.model.getMetadata().addEntry("Specializations", jsonValue);
			else
				metadata.setValue(jsonValue);
		} catch (Exception e) {
			this.exception = e;
		}
	}
	
	public Exception getException() {
		return this.exception;
	}

	@Override public boolean canUndo() {
		return false;
	}

	@Override public boolean canRedo() {
		return false;
	}
	@Override public void dispose() {
		this.model = null;
		this.specializationsMap = null;
		this.exception = null;
	}
}
