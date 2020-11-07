package work.lclpnet.mmo.gui.questbook;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.facade.User;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.quest.Quest;
import work.lclpnet.mmo.facade.quest.QuestBook;
import work.lclpnet.mmo.facade.quest.QuestState;
import work.lclpnet.mmo.facade.quest.QuestStep;
import work.lclpnet.mmo.facade.quest.Quests;
import work.lclpnet.mmo.util.Color;

public class QuestOverlay {

	private static ResourceLocation MARKER_DEFAULT_LOCATION = new ResourceLocation(LCLPMMO.MODID, "textures/gui/questbook/marker_default.png");

	public static void render(Minecraft mc, MatrixStack mStack, int x, int y, int width) {
		MMOCharacter character = User.getSelectedCharacter();
		if(character == null) return;

		QuestBook questBook = character.getData().getQuestBook();
		//if(questBook == null) return; TODO undo
		if(questBook == null) {
			character.getData().questBook = new QuestBook();
			return;
		}

		QuestState current = questBook.getCurrentQuest();
		//if(quest == null) return; TODO undo
		if(current == null) {
			questBook.setCurrentQuest(Quests.getNullableQuestByIdentifier("start").newState());
			return;
		}

		mStack.push();
		float scale = 1F;
		if(mc.gameSettings.guiScale > 2) {
			scale = 0.75F;
			mStack.scale(scale, scale, scale);
		}
		x = (int) (x / scale);
		y = (int) (y / scale);
		width = (int) (width / scale);

		mc.getTextureManager().bindTexture(MARKER_DEFAULT_LOCATION);
		AbstractGui.blit(mStack, x, y, 8, 8, 0F, 0F, 8, 8, 8, 8);

		Quest currentQuest = current.getQuest();
		IFormattableTextComponent questTitle = new TranslationTextComponent(String.format("mmo.quest.%s.title", currentQuest.getIdentifier()));

		for (IReorderingProcessor s : mc.fontRenderer.trimStringToWidth(questTitle, width)) {
			mc.fontRenderer.func_238407_a_(mStack, s, x + 10, y, Color.YELLOW);
			y += mc.fontRenderer.FONT_HEIGHT;
		}

		QuestStep currentStep = currentQuest.getSequence().get(current.getStep());
		y += 1; // margin

		IFormattableTextComponent questDescription = new TranslationTextComponent(String.format("mmo.quest.%s.step.%s", currentQuest.getIdentifier(), currentStep.getSubIdentifier()));

		for (IReorderingProcessor s : mc.fontRenderer.trimStringToWidth(questDescription, width)) {
			mc.fontRenderer.func_238407_a_(mStack, s, x + 10, y, 16777215);
			y += mc.fontRenderer.FONT_HEIGHT;
		}

		mStack.pop();
	}

}
