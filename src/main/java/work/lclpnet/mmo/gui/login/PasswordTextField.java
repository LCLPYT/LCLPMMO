package work.lclpnet.mmo.gui.login;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordTextField extends TextFieldWidget {

	public PasswordTextField(FontRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, TranslationTextComponent msg) {
		super(fontIn, xIn, yIn, widthIn, heightIn, msg);
	}

	@Override
	public void renderButton(MatrixStack mStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
		final String before = this.getText();

		this.text = IntStream.range(0, before.length()).mapToObj(i -> "*").collect(Collectors.joining());
		super.renderButton(mStack, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
		this.text = before;
	}
}
