package work.lclpnet.mmo.gui.character;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.character.DynamicCharacterData;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.gui.MMOScreen;
import work.lclpnet.mmo.util.Colors;
import work.lclpnet.mmo.util.LCLPNetwork;
import work.lclpnet.mmo.util.ValidationViolations;

public class CharacterCreatorScreen extends MMOScreen{

	protected ITextComponent error = null;
	protected Button btnRaceSel;
	protected Button btnCreateCharacter;
	protected TextFieldWidget characterNameField;
	protected String characterName = "";
	protected CharacterChooserScreen prevScreen;
	protected MMORace selectedRace = null;
	protected boolean createFirst = false;

	public CharacterCreatorScreen(CharacterChooserScreen prevScreen, boolean createFirst) {
		super(new TranslationTextComponent("mmo.menu.create_character.title"));
		this.prevScreen = prevScreen;
		this.createFirst = createFirst;
	}

	@Override
	protected void init() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.characterNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, new TranslationTextComponent("mmo.menu.create_character.character_name"));
		this.characterNameField.setText(this.characterName);
		this.characterNameField.setResponder((p_214319_1_) -> {
			this.characterName = p_214319_1_.trim();
			this.validate();
		});
		this.children.add(this.characterNameField);

		this.btnRaceSel = this.addButton(new Button(this.width / 2 - 75, 110, 150, 20, new TranslationTextComponent("mmo.menu.create_character.choose_race"), (p_214321_1_) -> {
			this.minecraft.displayGuiScreen(new RaceSelectionScreen(this));
		}));
		this.btnCreateCharacter = this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslationTextComponent("mmo.menu.create_character.create"), (p_214318_1_) -> {
			this.createCharacter();
		}));
		this.btnCreateCharacter.active = !this.characterName.isEmpty();
		this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, new TranslationTextComponent("gui.cancel"), (p_214317_1_) -> {
			this.minecraft.displayGuiScreen(this.prevScreen);
		}));

		this.setFocusedDefault(this.characterNameField);
	}

	@Override
	public void tick() {
		this.characterNameField.tick();
	}

	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackgroundTexture(BACKGROUND_LOCATION_ALT);
		drawCenteredString(mStack, this.font, this.title, this.width / 2, 20, -1);
		
		drawString(mStack, this.font, I18n.format("mmo.menu.create_character.character_name"), this.width / 2 - 100, 47, -6250336);
		this.characterNameField.render(mStack, mouseX, mouseY, partialTicks);
		if(error != null) 
			drawString(mStack, this.font, error, this.width / 2 - 100, 85, Colors.RED.toARGBInt());
		
		drawString(mStack, this.font, I18n.format("mmo.menu.create_character.choose_race_label"),this.width / 2 - 75, 98, -6250336);
		if(this.selectedRace != null) 
			this.btnRaceSel.setMessage(new StringTextComponent(String.format("\u270E %s", this.selectedRace.getTitle().getString())));
		
		super.render(mStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		super.onClose();
		this.minecraft.keyboardListener.enableRepeatEvents(false);
	}
	
	public void setError(ITextComponent error) {
		this.error = error;
	}

	private boolean validate() {
		boolean valid = !this.characterName.isEmpty() && this.selectedRace != null;
		
		this.btnCreateCharacter.active = valid;
		return valid;
	}
	
	public void createCharacter() {
		if(!validate()) return;
		
		MMOCharacter character = new MMOCharacter(this.characterName, this.selectedRace, new DynamicCharacterData());
		
		LCLPNetwork.post("api/ls5/add-character", character.toJson(), response -> {
			if(response.isNoConnection()) {
				displayToast(new TranslationTextComponent("mmo.menu.create_character.error_creation_failed"),
	                    new TranslationTextComponent("mmo.no_internet"));
			}
			else if(response.getResponseCode() == 201) {
				displayToast(new TranslationTextComponent("mmo.menu.create_character.created"));
				CharacterChooserScreen.updateContentAndShow(this.minecraft, prevScreen.getPrevScreen(), createFirst);
			} else {
				ITextComponent reason;
				if(response.hasValidationViolations()) {
					ValidationViolations violations = response.getValidationViolations();
					if(violations.has("name", "The name has already been taken.")) reason = new TranslationTextComponent("mmo.menu.create_character.error_name_taken");
					else reason = new StringTextComponent(violations.getFirst());
				} 
				else if(response.hasJsonStatusMessage() && "Too many characters.".equals(response.getJsonStatusMessage())) {
					reason = new TranslationTextComponent("mmo.menu.create_character.error_too_many");
				} else {
					System.err.println(response);
					reason = new TranslationTextComponent("error.unknown");
				}
				
				displayToast(new TranslationTextComponent("mmo.menu.create_character.error_creation_failed"), reason);
				
				this.setError(reason);
			}
		});
	}

	public void setSelectedRace(MMORace selected) {
		this.selectedRace = selected;
		this.validate();
	}

}
