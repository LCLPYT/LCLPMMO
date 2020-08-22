package work.lclpnet.mmo.gui;

import java.util.List;
import java.util.function.Function;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

public abstract class GenericSelectionScreen<T extends MMOSelectionItem> extends MMOScreen implements GenericSelectionSetup<T> {

	protected final Screen prevScreen;
	protected String tooltip;
	protected Button selectButton;
	protected TextFieldWidget searchField;
	protected GenericSelectionList<T, GenericSelectionScreen<T>> selectionList;
	protected Properties props;

	protected GenericSelectionScreen(ITextComponent titleIn, Screen prevScreen) {
		this(titleIn, prevScreen, new Properties());
	}

	protected GenericSelectionScreen(ITextComponent titleIn, Screen prevScreen, Properties props) {
		super(titleIn);
		this.prevScreen = prevScreen;
		this.props = props;
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
		this.searchField = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.searchField, I18n.format("mmo.menu.generic.search"));
		this.searchField.setResponder((p_214329_1_) -> {
			this.selectionList.search(() -> {
				return p_214329_1_;
			}, false);
		});
		updateSelectionList();
		this.children.add(this.searchField);
		this.children.add(this.selectionList);

		Button selBtn = new Button(
				this.props.selBtnPosX.apply(this.width), 
				this.props.selBtnPosY.apply(this.height), 
				this.props.selBtnWidth.apply(this.width), 
				this.props.selBtnHeight.apply(this.height), 
				I18n.format("mmo.menu.generic.choose"), clicked -> {
					this.selectionList.getSelection().ifPresent(GenericSelectionList<T, GenericSelectionScreen<T>>.Entry::onSelect);
				});

		this.selectButton = this.addButton(selBtn);

		Button cancelBtn = new Button(
				this.props.cancelBtnPosX.apply(this.width), 
				this.props.cancelBtnPosY.apply(this.height), 
				this.props.cancelBtnWidth.apply(this.width), 
				this.props.cancelBtnHeight.apply(this.height), 
				I18n.format("gui.cancel"), (p_214326_1_) -> {
					this.minecraft.displayGuiScreen(this.prevScreen);
				});

		this.addButton(cancelBtn);

		this.setButtonsActive(false);
		this.setFocusedDefault(this.searchField);
	}

	public void updateSelectionList() {
		this.selectionList = new GenericSelectionList<T, GenericSelectionScreen<T>>(this, this.minecraft, this.width, this.height, 48, props.selectionListHeight.apply(this.height), 36, this::getEntries, () -> {
			return this.searchField.getText();
		}, this.selectionList);
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
	
	public Screen getPrevScreen() {
		return prevScreen;
	}

	protected static class Properties {

		Function<Integer, Integer> selectionListHeight = height -> height - 32;
		Function<Integer, Integer> selBtnPosX = width -> width / 2 - 154;
		Function<Integer, Integer> selBtnPosY = height -> height - 28;
		Function<Integer, Integer> selBtnWidth = width -> 150;
		Function<Integer, Integer> selBtnHeight = height -> 20;
		Function<Integer, Integer> cancelBtnPosX = width -> width / 2 + 4;
		Function<Integer, Integer> cancelBtnPosY = height -> height - 28;
		Function<Integer, Integer> cancelBtnWidth = width -> 150;
		Function<Integer, Integer> cancelBtnHeight = height -> 20;

		protected Properties() {}

	}

}
