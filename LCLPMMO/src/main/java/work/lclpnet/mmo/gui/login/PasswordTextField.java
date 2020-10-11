package work.lclpnet.mmo.gui.login;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mojang.blaze3d.matrix.MatrixStack;

public class PasswordTextField extends TextFieldWidget {

    private static final Field f = ObfuscationReflectionHelper.findField(TextFieldWidget.class, "field_146216_j");

    static {
        f.setAccessible(true);
    }

    public PasswordTextField(FontRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, TranslationTextComponent msg) {
        super(fontIn, xIn, yIn, widthIn, heightIn, msg);
    }

    @Override
    public void renderButton(MatrixStack mStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        final String before = this.getText();
        final String to = IntStream.range(0, before.length()).mapToObj(i -> "*").collect(Collectors.joining());

        try {
            f.set(this, to);
            super.renderButton(mStack, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
            f.set(this, before);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
