package work.lclpnet.mmo.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageTutorial;
import work.lclpnet.mmo.network.msg.MessageTutorial.Type;

public class TutorialScreen extends MMOScreen {

	public TutorialScreen(boolean allowSkip) {
		super(new TranslationTextComponent("mmo.screen.tutorial.title"));
	}

	@Override
	protected void init() {
		int btnWidth = 150;
		this.addButton(new Button(this.width / 2 - btnWidth / 2, this.height / 2, btnWidth, 20, new TranslationTextComponent("mmo.screen.tutorial.start"), (p_213031_1_) -> {
			MMOPacketHandler.INSTANCE.sendToServer(new MessageTutorial(Type.START_TUTORIAL));
		}));
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		drawMultiLineCenteredString(matrixStack, this.font, this.title, 3F, this.width / 2, 50, Color.fromTextFormatting(TextFormatting.GOLD).getColor());

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

}
