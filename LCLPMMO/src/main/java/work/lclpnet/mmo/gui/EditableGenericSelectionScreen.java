package work.lclpnet.mmo.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

public abstract class EditableGenericSelectionScreen<T extends MMOSelectionItem> extends GenericSelectionScreen<T>{

	protected Button deleteButton;
	protected Button editButton;
	protected Button copyButton;

	protected EditableGenericSelectionScreen(ITextComponent titleIn, Screen prevScreen) {
		this(titleIn, prevScreen, new Properties());
		this.props.selectionListHeight = height -> height - 64;
		this.props.selBtnPosY = height -> height - 52;
		this.props.cancelBtnPosX = width -> width / 2 + 82;
		this.props.cancelBtnWidth = width -> 72;
	}
	
	protected EditableGenericSelectionScreen(ITextComponent titleIn, Screen prevScreen, Properties props) {
		super(titleIn, prevScreen, props);
	}

	@Override
	protected void init() {
		this.addButton(new Button(this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("mmo.menu.generic.new"), (p_214326_1_) -> {
			addEntry();
		}));
		this.editButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("mmo.menu.generic.edit"), (p_214323_1_) -> {
			this.selectionList.getSelection().ifPresent(entry -> editEntry(entry.getEntry()));
		}));
		this.deleteButton = this.addButton(new Button(this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("mmo.menu.generic.delete"), (p_214330_1_) -> {
			this.selectionList.getSelection().ifPresent(entry -> deleteEntry(entry.getEntry()));
		}));
		this.copyButton = this.addButton(new Button(this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("mmo.menu.generic.duplicate"), (p_214328_1_) -> {
			this.selectionList.getSelection().ifPresent(entry -> copyEntry(entry.getEntry()));
		}));
		
		super.init();
	}
	
	@Override
	public void setButtonsActive(boolean active) {
		super.setButtonsActive(active);
		this.deleteButton.active = true;
	}
	
	public abstract void addEntry();
	
	public abstract void editEntry(T elem);
	
	public abstract void deleteEntry(T elem);
	
	public abstract void copyEntry(T elem);

}
