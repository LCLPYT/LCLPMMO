package work.lclpnet.mmo.gui.characterchooser;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.gui.MMOScreen;

public class CharacterChooserScreen extends MMOScreen {

	protected final Screen prevScreen;
	private String worldVersTooltip;
	private Button deleteButton;
	private Button selectButton;
	private Button renameButton;
	private Button copyButton;
	protected TextFieldWidget field_212352_g;
	private RaceSelectionList selectionList;

	public CharacterChooserScreen(Screen screenIn) {
		super(new TranslationTextComponent("mmo.menu.create_character.title"));
		this.prevScreen = null;
	}

	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
	}

	public void tick() {
		this.field_212352_g.tick();
	}

	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : this.field_212352_g.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	public void onClose() {
		this.minecraft.displayGuiScreen(this.prevScreen);
	}

	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		return this.field_212352_g.charTyped(p_charTyped_1_, p_charTyped_2_);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		this.worldVersTooltip = null;
		this.selectionList.render(mouseX, mouseY, partialTicks);
		this.field_212352_g.render(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);
		super.render(mouseX, mouseY, partialTicks);
		if (this.worldVersTooltip != null) {
			this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.worldVersTooltip)), mouseX, mouseY);
		}
	}

	public void setVersionTooltip(String p_184861_1_) {
		this.worldVersTooltip = p_184861_1_;
	}

	public void func_214324_a(boolean p_214324_1_) {
		this.selectButton.active = p_214324_1_;
		this.deleteButton.active = p_214324_1_;
		this.renameButton.active = p_214324_1_;
		this.copyButton.active = p_214324_1_;
	}

	public void removed() {
		if (this.selectionList != null) {
			this.selectionList.children().forEach(RaceSelectionList.Entry::close);
		}

	}

}
