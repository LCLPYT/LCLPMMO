package work.lclpnet.mmo.facade.dialog;

import net.minecraft.entity.Entity;

public class Dialog {

    private final int id;
    private final Entity partner;
    private final DialogData data;
    private boolean dismissable = true;

    public Dialog(int id, Entity partner, DialogData data) {
        this.id = id;
        this.partner = partner;
        this.data = data;
    }

    public int getId() {
        return id;
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
