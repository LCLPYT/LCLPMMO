package work.lclpnet.mmo.asm.type;

import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.mmo.data.dialog.Dialog;

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

    static IMMOPlayer of(PlayerEntity player) {
        return (IMMOPlayer) player;
    }
}
