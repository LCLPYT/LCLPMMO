package work.lclpnet.mmo.gui.dialog;

import net.minecraft.util.math.MathHelper;
import work.lclpnet.mmo.facade.dialog.DialogData;
import work.lclpnet.mmo.facade.dialog.DialogFragment;

import java.util.Objects;

public class DialogWrapper {
	
	private DialogData data;
	private int step = 0;
	
	public DialogWrapper(DialogData data) {
		this.data = Objects.requireNonNull(data);
	}
	
	public DialogData getData() {
		return data;
	}
	
	public DialogFragment getCurrent() {
		return data.getStructure().get(step);
	}
	
	public void setStep(int step) {
		this.step = MathHelper.clamp(step, 0, data.getStructure().size());
	}
	
	public boolean hasNext() {
		return step + 1 < data.getStructure().size();
	}
	
	public void next() {
		setStep(step + 1);
	}
	
}
