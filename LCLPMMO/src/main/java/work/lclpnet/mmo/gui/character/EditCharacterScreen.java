package work.lclpnet.mmo.gui.character;

import java.io.IOException;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.gui.MMOScreen;
import work.lclpnet.mmo.util.LCLPNetwork;

public class EditCharacterScreen extends MMOScreen{

	private Button saveButton;
	private TextFieldWidget nameEdit;
	private MMOCharacter character;
	private Screen prevScreen;

	public EditCharacterScreen(MMOCharacter character, Screen prevScreen) {
		super(new TranslationTextComponent("mmo.menu.edit_character.title"));
		this.character = character;
		this.prevScreen = prevScreen;
	}

	public void tick() {
		this.nameEdit.tick();
	}

	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.saveButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.format("mmo.menu.edit_character.save"), (p_214308_1_) -> {
			this.saveChanges();
		}));
		this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.format("gui.cancel"), (p_214306_1_) -> {
			this.minecraft.displayGuiScreen(prevScreen);
		}));
		this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 53, 200, 20, I18n.format("mmo.menu.edit_character.edit_name"));
		this.nameEdit.setText(character.getName());
		this.nameEdit.setResponder((p_214301_1_) -> {
			String trimmed = p_214301_1_.trim();
			this.saveButton.active = !trimmed.isEmpty();
		});
		this.children.add(this.nameEdit);
		this.setFocusedDefault(this.nameEdit);
	}

	public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
		String s = this.nameEdit.getText();
		this.init(p_resize_1_, p_resize_2_, p_resize_3_);
		this.nameEdit.setText(s);
	}

	public void removed() {
		this.minecraft.keyboardListener.enableRepeatEvents(false);
	}

	private void saveChanges() {
		if(character.id == null) throw new NullPointerException("Character id is null!");

		String name = this.nameEdit.getText().trim();
		if(!character.getName().equals(name)) {
			JsonObject body = new JsonObject();
			body.addProperty("characterId", character.id);
			body.addProperty("newName", name);

			LCLPNetwork.post("api/ls5/rename-character", body, response -> {
				if(response.isNoConnection()) {
					SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
							new TranslationTextComponent("mmo.menu.edit_character.edit_failed"),
							new TranslationTextComponent("mmo.no_internet"));
				} else if(response.getResponseCode() == 200) {
					SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
							new TranslationTextComponent("mmo.menu.edit_character.edit_success"),
							null);
					CharacterChooserScreen.updateContentAndShow(minecraft, prevScreen);
				} else {
					String s = response.getJsonStatusMessage();
					if(s != null && s.equalsIgnoreCase("Name cannot be changed yet.")) {
						String days = JsonSerializeable.parse(response.getRawError(), JsonObject.class)
								.get("extra")
								.getAsString()
								.split(" ")[0];
						
						SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
								new TranslationTextComponent("mmo.menu.edit_character.name_not_yet_changeable"),
								new TranslationTextComponent("mmo.menu.edit_character.name_not_yet_changeable_days", days));
					} else {
						System.err.println(response);
						
						ITextComponent reason = new TranslationTextComponent("error.unknown");
						if(response.hasValidationViolations()) {
							reason = new StringTextComponent(response.getValidationViolations().getFirst());
						}
						
						SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
								new TranslationTextComponent("mmo.menu.edit_character.edit_failed"),
								reason);
					}
				}
			});
		}
	}

	public static void createBackup(SaveFormat saveFormat, String worldName) {
		ToastGui toastgui = Minecraft.getInstance().getToastGui();
		long i = 0L;
		IOException ioexception = null;

		try {
			i = saveFormat.createBackup(worldName);
		} catch (IOException ioexception1) {
			ioexception = ioexception1;
		}

		ITextComponent itextcomponent;
		ITextComponent itextcomponent1;
		if (ioexception != null) {
			itextcomponent = new TranslationTextComponent("selectWorld.edit.backupFailed");
			itextcomponent1 = new StringTextComponent(ioexception.getMessage());
		} else {
			itextcomponent = new TranslationTextComponent("selectWorld.edit.backupCreated", worldName);
			itextcomponent1 = new TranslationTextComponent("selectWorld.edit.backupSize", MathHelper.ceil((double)i / 1048576.0D));
		}

		toastgui.add(new SystemToast(SystemToast.Type.WORLD_BACKUP, itextcomponent, itextcomponent1));
	}

	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
		this.drawString(this.font, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
		this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}

}
