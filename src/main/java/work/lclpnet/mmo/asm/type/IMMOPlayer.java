package work.lclpnet.mmo.asm.type;

import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.mmo.facade.dialog.Dialog;

public interface IMMOPlayer {

	void openMMODialog(Dialog dialog);

	/**
	 * Helper method. <b>ONLY FOR INTERNAL USAGE!</b>
	 */
	void setCurrentMMODialog(Dialog dialog);
	
	Dialog getCurrentMMODialog();
	
	void closeMMODialog();
	
	default boolean isMMODialogOpen() {
		return getCurrentMMODialog() != null;
	}
	
	public static IMMOPlayer get(PlayerEntity player) {
		return (IMMOPlayer) player;
	}
	
}
