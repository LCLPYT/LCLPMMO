package work.lclpnet.mmo.gui;

import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

public abstract class GenericSelectionScreen<T extends MMOSelectionItem> extends MMOScreen implements GenericSelectionSetup<T> {

	protected final Screen prevScreen;
	private String tooltip;
	private Button selectButton;
	protected TextFieldWidget searchField;
	private GenericSelectionList<T, GenericSelectionScreen<T>> selectionList;

	protected GenericSelectionScreen(ITextComponent titleIn, Screen screenIn) {
		super(titleIn);
		this.prevScreen = null;
	}

	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
	}

	@Override
	public void tick() {
		this.searchField.tick();
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.searchField = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.searchField, I18n.format("mmo.menu.select_race.search"));
		this.searchField.setResponder((p_214329_1_) -> {
			this.selectionList.search(() -> {
				return p_214329_1_;
			}, false);
		});
		this.selectionList = new GenericSelectionList<T, GenericSelectionScreen<T>>(this, this.minecraft, this.width, this.height, 48, this.height - 32, 36, this::getEntries, () -> {
			return this.searchField.getText();
		}, this.selectionList);
		this.children.add(this.searchField);
		this.children.add(this.selectionList);
		this.selectButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 150, 20, I18n.format("mmo.menu.select_race.choose"), clicked -> {
			this.selectionList.getSelection().ifPresent(GenericSelectionList<T, GenericSelectionScreen<T>>.Entry::onSelect);
		}));
		this.addButton(new Button(this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_214326_1_) -> {
			this.minecraft.displayGuiScreen(this.prevScreen);
		}));
		this.setButtonsActive(false);
		this.setFocusedDefault(this.searchField);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : this.searchField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public void onClose() {
		this.minecraft.displayGuiScreen(this.prevScreen);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.searchField.charTyped(c, i);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		this.tooltip = null;
		this.selectionList.render(mouseX, mouseY, partialTicks);
		this.searchField.render(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
		super.render(mouseX, mouseY, partialTicks);
		if (this.tooltip != null) {
			this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.tooltip)), mouseX, mouseY);
		}
	}
	
	public abstract List<T> getEntries();
	
	@Override
	public void setButtonsActive(boolean active) {
		this.selectButton.active = active;
	}

	@Override
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

}
