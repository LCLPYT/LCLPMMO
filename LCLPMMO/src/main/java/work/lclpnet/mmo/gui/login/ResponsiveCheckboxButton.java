package work.lclpnet.mmo.gui.login;

import net.minecraft.client.gui.widget.button.CheckboxButton;

import java.util.function.Consumer;

public class ResponsiveCheckboxButton extends CheckboxButton {

    protected Consumer<Boolean> responder = null;

    public ResponsiveCheckboxButton(int xIn, int yIn, int widthIn, int heightIn, String msg, boolean isChecked) {
        super(xIn, yIn, widthIn, heightIn, msg, isChecked);
    }

    @Override
    public void onPress() {
        super.onPress();
        if(responder != null) responder.accept(this.isChecked());
    }

    public void setResponder(Consumer<Boolean> responder) {
        this.responder = responder;
    }
}
