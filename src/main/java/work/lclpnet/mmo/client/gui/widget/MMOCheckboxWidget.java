package work.lclpnet.mmo.client.gui.widget;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class MMOCheckboxWidget extends CheckboxWidget {

    protected Consumer<Boolean> changeListener = null;

    public MMOCheckboxWidget(int x, int y, int width, int height, Text msg, boolean checked) {
        super(x, y, width, height, msg, checked);
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
