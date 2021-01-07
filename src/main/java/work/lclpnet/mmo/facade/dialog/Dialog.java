package work.lclpnet.mmo.facade.dialog;

import net.minecraft.entity.Entity;

public class Dialog {

	private Entity partner;
	private DialogData data;
	private boolean dismissable = true;
	
	public Dialog(Entity partner, DialogData data) {
		this.partner = partner;
		this.data = data;
	}
	
	public Entity getPartner() {
		return partner;
	}
	
	public DialogData getData() {
		return data;
	}
	
	public boolean isDismissable() {
		return dismissable;
	}
	
	public Dialog setDismissable(boolean dismissable) {
		this.dismissable = dismissable;
		return this;
	}
	
}
