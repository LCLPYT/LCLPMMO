package work.lclpnet.mmo.facade.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DialogData {

	private List<DialogFragment> structure = new ArrayList<>();
	
	public DialogData(List<DialogFragment> structure) {
		this.structure = Objects.requireNonNull(structure);
	}
	
	public List<DialogFragment> getStructure() {
		return structure;
	}
	
}
