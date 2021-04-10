package work.lclpnet.mmo.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class GenericSelectionList<T extends MMOSelectionItem, S extends Screen & GenericSelectionSetup<T>> extends ExtendedList<GenericSelectionList<T, S>.Entry> {

    private static final ResourceLocation unknownTexture = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation selectionTextures = new ResourceLocation("textures/gui/world_selection.png");
    private final S screen;
    private List<T> entries;
    private final Supplier<List<T>> entrySupplier;
    private ResourceLocation bgTexture = AbstractGui.BACKGROUND_LOCATION;
    private T preSelected = null;

    public GenericSelectionList(S screen, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, Supplier<List<T>> entries, T preSelected) {
        this(screen, mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn, entries, null, null, preSelected);
    }

    public GenericSelectionList(S screen, Minecraft mc, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, Supplier<List<T>> entries, Supplier<String> query, @Nullable GenericSelectionList<T, S> copyFrom, T preSelected) {
        super(mc, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.screen = screen;
        this.entrySupplier = entries;
        if (copyFrom != null) this.entries = copyFrom.entries;

        this.preSelected = preSelected;
        if (query != null) this.search(query, false);
    }

    public void setBgTexture(ResourceLocation bgTexture) {
        this.bgTexture = bgTexture;
    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 20;
    }

    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    protected boolean isFocused() {
        return this.screen.getListener() == this;
    }

    public void setSelected(@Nullable GenericSelectionList<T, S>.Entry selected) {
        super.setSelected(selected);
        if (selected != null)
            NarratorChatListener.INSTANCE.say(new TranslationTextComponent("mmo.narrator.selection.selected", selected.entry.getTitle().getString()).getString());
    }

    protected void moveSelection(AbstractList.Ordering ordering) {
        this.filterList(ordering, (p_241652_0_) -> true);
        this.screen.setButtonsActive(true);
    }

    public Optional<GenericSelectionList<T, S>.Entry> getSelection() {
        return Optional.ofNullable(this.getSelected());
    }

    public void search(Supplier<String> searchQuery, boolean renewAvailable) {
        this.clearEntries();
        if (this.entries == null || renewAvailable) this.entries = new ArrayList<>(entrySupplier.get());

        String s = searchQuery.get().toLowerCase(Locale.ROOT);

        for (T entry : this.entries) {
            if (entry.getTitle().getUnformattedComponentText().toLowerCase(Locale.ROOT).contains(s) || entry.getUnlocalizedName().toLowerCase(Locale.ROOT).contains(s)) {
                this.addEntry(new Entry(this, entry, entry.equals(preSelected)));
            }
        }

    }

    public S getGuiScreen() {
        return this.screen;
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }

    protected int getRowBottom(int p_getRowBottom_1_) {
        return this.getRowTop(p_getRowBottom_1_) + this.itemHeight;
    }

    @SuppressWarnings("deprecation")
    protected void renderWholeBackground(int p_renderHoleBackground_1_, int p_renderHoleBackground_2_, int p_renderHoleBackground_4_) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(bgTexture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(this.x0, p_renderHoleBackground_2_, 0.0D).tex(0.0F, (float) p_renderHoleBackground_2_ / 32.0F).color(64, 64, 64, p_renderHoleBackground_4_).endVertex();
        bufferbuilder.pos(this.x0 + this.width, p_renderHoleBackground_2_, 0.0D).tex((float) this.width / 32.0F, (float) p_renderHoleBackground_2_ / 32.0F).color(64, 64, 64, p_renderHoleBackground_4_).endVertex();
        bufferbuilder.pos(this.x0 + this.width, p_renderHoleBackground_1_, 0.0D).tex((float) this.width / 32.0F, (float) p_renderHoleBackground_1_ / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(this.x0, p_renderHoleBackground_1_, 0.0D).tex(0.0F, (float) p_renderHoleBackground_1_ / 32.0F).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(MatrixStack mStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        int i = this.getScrollbarPosition();
        int j = i + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(bgTexture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(this.x0, this.y1, 0.0D).tex((float) this.x0 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.pos(this.x1, this.y1, 0.0D).tex((float) this.x1 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.pos(this.x1, this.y0, 0.0D).tex((float) this.x1 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.pos(this.x0, this.y0, 0.0D).tex((float) this.x0 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        tessellator.draw();
        int k = this.getRowLeft();
        int l = this.y0 + 4 - (int) this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(mStack, k, l, tessellator);
        }

        this.renderList(mStack, k, l, p_render_1_, p_render_2_, p_render_3_);
        RenderSystem.disableDepthTest();
        this.renderWholeBackground(0, this.y0, 255);
        this.renderWholeBackground(this.y1, this.height, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(this.x0, this.y0 + 4, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.pos(this.x1, this.y0 + 4, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.pos(this.x1, this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(this.x0, this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        tessellator.draw();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(this.x0, this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(this.x1, this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.pos(this.x1, this.y1 - 4, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.pos(this.x0, this.y1 - 4, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        tessellator.draw();
        int j1 = this.getMaxScroll();
        if (j1 > 0) {
            int k1 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            k1 = MathHelper.clamp(k1, 32, this.y1 - this.y0 - 8);
            int l1 = (int) this.getScrollAmount() * (this.y1 - this.y0 - k1) / j1 + this.y0;
            if (l1 < this.y0) {
                l1 = this.y0;
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(i, this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(j, this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(j, this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(i, this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(i, l1 + k1, 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos(j, l1 + k1, 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos(j, l1, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            bufferbuilder.pos(i, l1, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(i, l1 + k1 - 1, 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos(j - 1, l1 + k1 - 1, 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos(j - 1, l1, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            bufferbuilder.pos(i, l1, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
        }

        this.renderDecorations(mStack, p_render_1_, p_render_2_);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderList(MatrixStack mStack, int p_renderList_1_, int p_renderList_2_, int p_renderList_3_, int p_renderList_4_,
                              float p_renderList_5_) {
        int i = this.getItemCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for (int j = 0; j < i; ++j) {
            int k = this.getRowTop(j);
            int l = this.getRowBottom(j);
            if (l >= this.y0 && k <= this.y1) {
                int i1 = p_renderList_2_ + j * this.itemHeight + this.headerHeight;
                int j1 = this.itemHeight - 4;
                Entry e = this.getEntry(j);
                int k1 = this.getRowWidth();
                boolean selItem = this.isSelectedItem(j);
                if (this.renderSelection && (selItem || e.preSelected)) {
                    int l1 = this.x0 + this.width / 2 - k1 / 2;
                    int i2 = this.x0 + this.width / 2 + k1 / 2;
                    RenderSystem.disableTexture();
                    if (selItem) {
                        float f = this.isFocused() ? 1.0F : 0.5F;
                        RenderSystem.color4f(f, f, f, 1.0F);
                    } else { // preSelected
                        RenderSystem.color4f(0.196F, 0.476F, 0.659F, 1.0F); // rgba(50, 121, 168, 255)
                    }
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
                    bufferbuilder.pos(l1, i1 + j1 + 2, 0.0D).endVertex();
                    bufferbuilder.pos(i2, i1 + j1 + 2, 0.0D).endVertex();
                    bufferbuilder.pos(i2, i1 - 2, 0.0D).endVertex();
                    bufferbuilder.pos(l1, i1 - 2, 0.0D).endVertex();
                    tessellator.draw();
                    RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
                    bufferbuilder.pos(l1 + 1, i1 + j1 + 1, 0.0D).endVertex();
                    bufferbuilder.pos(i2 - 1, i1 + j1 + 1, 0.0D).endVertex();
                    bufferbuilder.pos(i2 - 1, i1 - 1, 0.0D).endVertex();
                    bufferbuilder.pos(l1 + 1, i1 - 1, 0.0D).endVertex();
                    tessellator.draw();
                    RenderSystem.enableTexture();
                }

                int j2 = this.getRowLeft();
                e.render(mStack, j, k, j2, k1, j1, p_renderList_3_, p_renderList_4_, this.isMouseOver(p_renderList_3_, p_renderList_4_) && Objects.equals(this.getEntryAtPosition(p_renderList_3_, p_renderList_4_), e), p_renderList_5_);
            }
        }
    }

    public final class Entry extends ExtendedList.AbstractListEntry<GenericSelectionList<T, S>.Entry> {

        private final Minecraft minecraft;
        private final S screen;
        private final T entry;
        private long field_214455_h;
        protected boolean preSelected = false;

        public Entry(GenericSelectionList<T, S> p_i50631_2_, T entry, boolean preSelected) {
            this.screen = p_i50631_2_.getGuiScreen();
            this.entry = entry;
            this.minecraft = Minecraft.getInstance();
            this.preSelected = preSelected;
        }

        @SuppressWarnings("deprecation")
        public void render(MatrixStack mStack, int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            this.minecraft.fontRenderer.drawTextWithShadow(mStack, this.entry.getTitle(), (float) (p_render_3_ + 32 + 3), (float) (p_render_2_ + 1), 16777215);
            this.minecraft.fontRenderer.drawString(mStack, this.entry.getFirstLine(), (float) (p_render_3_ + 32 + 3), (float) (p_render_2_ + 9 + 3), 8421504);
            this.minecraft.fontRenderer.drawString(mStack, this.entry.getSecondLine(), (float) (p_render_3_ + 32 + 3), (float) (p_render_2_ + 9 + 9 + 3), 8421504);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(this.entry.getIcon() != null ? this.entry.getIcon() : GenericSelectionList.unknownTexture);
            RenderSystem.enableBlend();
            AbstractGui.blit(mStack, p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (this.minecraft.gameSettings.touchscreen || p_render_8_) {
                this.minecraft.getTextureManager().bindTexture(GenericSelectionList.selectionTextures);
                AbstractGui.fill(mStack, p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                int j = p_render_6_ - p_render_3_;
                int i = j < 32 ? 32 : 0;

                if (entry.getToolTip() != null)
                    screen.setTooltip(this.minecraft.fontRenderer.trimStringToWidth(entry.getToolTip(), 175));
                AbstractGui.blit(mStack, p_render_3_, p_render_2_, 0.0F, (float) i, 32, 32, 256, 256);
            }
        }

        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            GenericSelectionList.this.setSelected(this);
            this.screen.setButtonsActive(GenericSelectionList.this.getSelection().isPresent());
            if (p_mouseClicked_1_ - (double) GenericSelectionList.this.getRowLeft() <= 32.0D) {
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

        public T getEntry() {
            return entry;
        }

    }

}
