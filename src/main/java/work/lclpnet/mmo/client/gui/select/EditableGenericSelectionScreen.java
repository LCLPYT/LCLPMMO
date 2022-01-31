package work.lclpnet.mmo.client.gui.select;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class EditableGenericSelectionScreen<T extends IMMOSelectionItem> extends GenericSelectionScreen<T> {

    protected ButtonWidget deleteButton;
    protected ButtonWidget editButton;
    protected ButtonWidget copyButton;

    protected EditableGenericSelectionScreen(Text titleIn, Screen previousScreen) {
        this(titleIn, previousScreen, new Properties());
        this.props.selectionListHeight = height -> height - 64;
        this.props.selBtnPosY = height -> height - 52;
        this.props.cancelBtnPosX = width -> width / 2 + 82;
        this.props.cancelBtnWidth = width -> 72;
    }

    protected EditableGenericSelectionScreen(Text titleIn, Screen previousScreen, Properties props) {
        super(titleIn, previousScreen, props);
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20,
                new TranslatableText("mmo.menu.generic.new"), buttonWidget -> addEntry()));

        this.editButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 72, 20,
                new TranslatableText("mmo.menu.generic.edit"),
                buttonWidget -> this.selectionList.getSelection()
                        .ifPresent(entry -> editEntry(entry.getEntry())))
        );

        this.deleteButton = this.addButton(new ButtonWidget(this.width / 2 - 76, this.height - 28, 72, 20,
                new TranslatableText("mmo.menu.generic.delete"),
                buttonWidget -> this.selectionList.getSelection()
                        .ifPresent(entry -> deleteEntry(entry.getEntry())))
        );

        this.copyButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 72, 20,
                new TranslatableText("mmo.menu.generic.duplicate"),
                buttonWidget -> this.selectionList.getSelection()
                        .ifPresent(entry -> copyEntry(entry.getEntry())))
        );

        editButton.active = false;
        deleteButton.active = false;
        copyButton.active = false;

        super.init();
    }

    @Override
    public void setButtonsActive(boolean active) {
        super.setButtonsActive(active);
        this.deleteButton.active = active;
        this.editButton.active = active;
    }

    public abstract void addEntry();

    public abstract void editEntry(T elem);

    public abstract void deleteEntry(T elem);

    public abstract void copyEntry(T elem);
}
