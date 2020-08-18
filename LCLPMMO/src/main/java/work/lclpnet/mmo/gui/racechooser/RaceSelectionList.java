package work.lclpnet.mmo.gui.racechooser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.race.Race;
import work.lclpnet.mmo.facade.race.Races;

public class RaceSelectionList extends ExtendedList<RaceSelectionList.Entry>{

	private static final ResourceLocation unknownTexture = new ResourceLocation("textures/misc/unknown_server.png");
	private static final ResourceLocation selectionTextures = new ResourceLocation("textures/gui/world_selection.png");
	private final RaceSelectionScreen raceSelection;
	private List<Race> races;

	public RaceSelectionList(RaceSelectionScreen screen, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.raceSelection = screen;
	}

	public RaceSelectionList(RaceSelectionScreen screen, Minecraft mc, int width, int maxHeight, int heightStart, int heightEnd, int p_i49846_7_, Supplier<String> query, @Nullable RaceSelectionList copyFrom) {
		super(mc, width, maxHeight, heightStart, heightEnd, p_i49846_7_);
		this.raceSelection = screen;
		if (copyFrom != null) this.races = copyFrom.races;

		this.search(query, false);
	}

	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	protected boolean isFocused() {
		return this.raceSelection.getFocused() == this;
	}

	public void setSelected(@Nullable RaceSelectionList.Entry selected) {
		super.setSelected(selected);
		if (selected != null) 
			NarratorChatListener.INSTANCE.say(new TranslationTextComponent("mmo.narrator.select.race", selected.race.getTitle().getString()).getString());
	}

	protected void moveSelection(int p_moveSelection_1_) {
		super.moveSelection(p_moveSelection_1_);
		this.raceSelection.setButtonsActive(true);
	}

	public Optional<RaceSelectionList.Entry> getSelection() {
		return Optional.ofNullable(this.getSelected());
	}

	public void search(Supplier<String> searchQuery, boolean renewAvailable) {
		this.clearEntries();
		if (this.races == null || renewAvailable) this.races = new ArrayList<>(Races.getRaces());

		String s = searchQuery.get().toLowerCase(Locale.ROOT);

		for(Race race : this.races) {
			if (race.getTitle().getUnformattedComponentText().toLowerCase(Locale.ROOT).contains(s) || race.getName().toLowerCase(Locale.ROOT).contains(s)) {
				this.addEntry(new RaceSelectionList.Entry(this, race));
			}
		}

	}

	public RaceSelectionScreen getGuiWorldSelection() {
		return this.raceSelection;
	}

	public final class Entry extends ExtendedList.AbstractListEntry<RaceSelectionList.Entry> {

		private final Minecraft minecraft;
		private final RaceSelectionScreen screen;
		private final Race race;
		private long field_214455_h;

		public Entry(RaceSelectionList p_i50631_2_, Race race) {
			this.screen = p_i50631_2_.getGuiWorldSelection();
			this.race = race;
			this.minecraft = Minecraft.getInstance();
		}

		public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
			this.minecraft.fontRenderer.drawString(this.race.getTitle().getFormattedText(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
			this.minecraft.fontRenderer.drawString(this.race.getFirstLine(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 9 + 3), 8421504);
			this.minecraft.fontRenderer.drawString(this.race.getSecondLine(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 9 + 9 + 3), 8421504);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(this.race.getIcon() != null ? this.race.getIcon() : RaceSelectionList.unknownTexture);
			RenderSystem.enableBlend();
			AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
			if (this.minecraft.gameSettings.touchscreen || p_render_8_) {
				this.minecraft.getTextureManager().bindTexture(RaceSelectionList.selectionTextures);
				AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int j = p_render_6_ - p_render_3_;
				int i = j < 32 ? 32 : 0;

				if(race.getToolTip() != null) this.screen.setTooltip(race.getToolTip());
				AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, (float) i, 32, 32, 256, 256);
			}
		}

		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			RaceSelectionList.this.setSelected(this);
			this.screen.setButtonsActive(RaceSelectionList.this.getSelection().isPresent());
			if (p_mouseClicked_1_ - (double)RaceSelectionList.this.getRowLeft() <= 32.0D) {
				this.onSelect();
				return true;
			} else if (Util.milliTime() - this.field_214455_h < 250L) {
				this.onSelect();
				return true;
			} else {
				this.field_214455_h = Util.milliTime();
				return false;
			}
		}

		public void onSelect() {
			System.out.println("SELECTED: " + race);
		}

	}

}
