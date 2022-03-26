package work.lclpnet.mmo.client.gui.select;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.client.gui.MMOScreen;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class GenericSelectionScreen<T extends IMMOSelectionItem> extends MMOScreen implements GenericSelectionSetup<T> {

    protected final Screen previousScreen;
    private List<OrderedText> tooltip;
    protected ButtonWidget selectButton;
    protected TextFieldWidget searchField;
    protected GenericSelectionList<T, GenericSelectionScreen<T>> selectionList;
    protected final Properties props;
    protected Identifier background = null;
    protected T preSelected = null;

    protected GenericSelectionScreen(Text titleIn, Screen previousScreen) {
        this(titleIn, previousScreen, new Properties());
    }

    protected GenericSelectionScreen(Text titleIn, Screen previousScreen, Properties props) {
        super(titleIn);
        this.previousScreen = previousScreen;
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
        Objects.requireNonNull(this.client);

        this.client.keyboard.setRepeatEvents(true);
        this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20,
                this.searchField, new TranslatableText("mmo.menu.generic.search"));

        this.searchField.setChangedListener(s -> this.selectionList.search(() -> s, false));

        updateSelectionList();
        this.addDrawableChild(this.searchField);
        this.addDrawableChild(this.selectionList);

        ButtonWidget selectBtn = new ButtonWidget(
                this.props.selBtnPosX.apply(this.width),
                this.props.selBtnPosY.apply(this.height),
                this.props.selBtnWidth.apply(this.width),
                this.props.selBtnHeight.apply(this.height),
                new TranslatableText("mmo.menu.generic.choose"),
                clicked -> this.selectionList.getSelection()
                        .ifPresent(GenericSelectionList.Entry::onSelect)
        );

        this.selectButton = this.addDrawableChild(selectBtn);

        ButtonWidget cancelBtn = new ButtonWidget(
                this.props.cancelBtnPosX.apply(this.width),
                this.props.cancelBtnPosY.apply(this.height),
                this.props.cancelBtnWidth.apply(this.width),
                this.props.cancelBtnHeight.apply(this.height),
                new TranslatableText("gui.cancel"),
                buttonWidget -> this.client.setScreen(this.previousScreen)
        );

        this.addDrawableChild(cancelBtn);

        this.setButtonsActive(false);
        this.setFocused(this.searchField);
    }

    public void updateSelectionList() {
        this.selectionList = new GenericSelectionList<>(this, this.client, this.width, this.height,
                48, props.selectionListHeight.apply(this.height), 36, this::getEntries,
                () -> this.searchField.getText(), this.selectionList, preSelected);

        if (background != null) this.selectionList.setBgTexture(background);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) || this.searchField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(this.client).setScreen(this.previousScreen);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.searchField.charTyped(c, i);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (background == null) this.renderBackground(matrices);
        else this.renderBackgroundTexture(background);

        this.tooltip = null;
        this.selectionList.render(matrices, mouseX, mouseY, delta);
        this.searchField.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);

        super.render(matrices, mouseX, mouseY, delta);

        if (this.tooltip != null)
            this.renderOrderedTooltip(matrices, tooltip, mouseX, mouseY);
    }

    public abstract List<T> getEntries();

    @Override
    public void setButtonsActive(boolean active) {
        this.selectButton.active = active;
    }

    @Override
    public void setTooltip(List<OrderedText> tooltip) {
        this.tooltip = tooltip;
    }

    public Screen getPreviousScreen() {
        return previousScreen;
    }

    protected static class Properties {

        Function<Integer, Integer> selectionListHeight = height -> height - 32;
        final Function<Integer, Integer> selBtnPosX = width -> width / 2 - 154;
        Function<Integer, Integer> selBtnPosY = height -> height - 28;
        final Function<Integer, Integer> selBtnWidth = width -> 150;
        final Function<Integer, Integer> selBtnHeight = height -> 20;
        Function<Integer, Integer> cancelBtnPosX = width -> width / 2 + 4;
        final Function<Integer, Integer> cancelBtnPosY = height -> height - 28;
        Function<Integer, Integer> cancelBtnWidth = width -> 150;
        final Function<Integer, Integer> cancelBtnHeight = height -> 20;

        protected Properties() {
        }
    }
}
