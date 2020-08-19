package work.lclpnet.mmo.gui;

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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class GenericSelectionList<T extends MMOSelectionItem, S extends Screen & GenericSelectionSetup<T>> extends ExtendedList<GenericSelectionList<T, S>.Entry>{

	private static final ResourceLocation unknownTexture = new ResourceLocation("textures/misc/unknown_server.png");
	private static final ResourceLocation selectionTextures = new ResourceLocation("textures/gui/world_selection.png");
	private final S screen;
	private List<T> entries;
	private Supplier<List<T>> entrySupplier;

	public GenericSelectionList(S screen, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, Supplier<List<T>> entries) {
		this(screen, mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn, entries, null, null);
	}

	public GenericSelectionList(S screen, Minecraft mc, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, Supplier<List<T>> entries, Supplier<String> query, @Nullable GenericSelectionList<T, S> copyFrom) {
		super(mc, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.screen = screen;
		this.entrySupplier = entries;
		if (copyFrom != null) this.entries = copyFrom.entries;

		if(query != null) this.search(query, false);
	}

	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	protected boolean isFocused() {
		return this.screen.getFocused() == this;
	}

	public void setSelected(@Nullable GenericSelectionList<T, S>.Entry selected) {
		super.setSelected(selected);
		if (selected != null) 
			NarratorChatListener.INSTANCE.say(new TranslationTextComponent("mmo.narrator.selection.selected", selected.entry.getTitle().getString()).getString());
	}

	protected void moveSelection(int p_moveSelection_1_) {
		super.moveSelection(p_moveSelection_1_);
		this.screen.setButtonsActive(true);
	}

	public Optional<GenericSelectionList<T, S>.Entry> getSelection() {
		return Optional.ofNullable(this.getSelected());
	}

	public void search(Supplier<String> searchQuery, boolean renewAvailable) {
		this.clearEntries();
		if (this.entries == null || renewAvailable) this.entries = new ArrayList<>(entrySupplier.get());

		String s = searchQuery.get().toLowerCase(Locale.ROOT);

		for(T entry : this.entries) {
			if (entry.getTitle().getUnformattedComponentText().toLowerCase(Locale.ROOT).contains(s) || entry.getName().toLowerCase(Locale.ROOT).contains(s)) {
				this.addEntry(new Entry(this, entry));
			}
		}

	}

	public S getGuiScreen() {
		return this.screen;
	}

	public final class Entry extends ExtendedList.AbstractListEntry<GenericSelectionList<T, S>.Entry> {

		private final Minecraft minecraft;
		private final S screen;
		private final T entry;
		private long field_214455_h;

		public Entry(GenericSelectionList<T, S> p_i50631_2_, T entry) {
			this.screen = p_i50631_2_.getGuiScreen();
			this.entry = entry;
			this.minecraft = Minecraft.getInstance();
		}

		public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
			this.minecraft.fontRenderer.drawString(this.entry.getTitle().getFormattedText(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
			this.minecraft.fontRenderer.drawString(this.entry.getFirstLine(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 9 + 3), 8421504);
			this.minecraft.fontRenderer.drawString(this.entry.getSecondLine(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 9 + 9 + 3), 8421504);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(this.entry.getIcon() != null ? this.entry.getIcon() : GenericSelectionList.unknownTexture);
			RenderSystem.enableBlend();
			AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
			RenderSystem.disableBlend();
			if (this.minecraft.gameSettings.touchscreen || p_render_8_) {
				this.minecraft.getTextureManager().bindTexture(GenericSelectionList.selectionTextures);
				AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int j = p_render_6_ - p_render_3_;
				int i = j < 32 ? 32 : 0;

				if(entry.getToolTip() != null) this.screen.setTooltip(entry.getToolTip());
				AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, (float) i, 32, 32, 256, 256);
			}
		}

		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			GenericSelectionList.this.setSelected(this);
			this.screen.setButtonsActive(GenericSelectionList.this.getSelection().isPresent());
			if (p_mouseClicked_1_ - (double)GenericSelectionList.this.getRowLeft() <= 32.0D) {
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
			screen.onSelected(entry);
		}

	}

}
