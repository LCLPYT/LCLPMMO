package work.lclpnet.mmo.client.gui.login;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ResponsiveCheckboxButton extends CheckboxWidget {

    protected Consumer<Boolean> changeListener = null;

    public ResponsiveCheckboxButton(int xIn, int yIn, int widthIn, int heightIn, Text msg, boolean isChecked) {
        super(xIn, yIn, widthIn, heightIn, msg, isChecked);
    }

    @Override
    public void onPress() {
        super.onPress();
        if (changeListener != null) changeListener.accept(this.isChecked());
    }

    public void setChangeListener(Consumer<Boolean> changeListener) {
        this.changeListener = changeListener;
    }
}
