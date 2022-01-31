package work.lclpnet.mmo.client.gui.select;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import work.lclpnet.mmo.asm.mixin.client.EntryListWidgetAccessor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class GenericSelectionList<T extends IMMOSelectionItem, S extends Screen & GenericSelectionSetup<T>> extends AlwaysSelectedEntryListWidget<GenericSelectionList<T, S>.Entry> {

    private static final Identifier unknownTexture = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier selectionTextures = new Identifier("textures/gui/world_selection.png");
    private final S screen;
    private List<T> entries;
    private final Supplier<List<T>> entrySupplier;
    private Identifier bgTexture = DrawableHelper.OPTIONS_BACKGROUND_TEXTURE;
    private T preSelected = null;

    public GenericSelectionList(S screen, MinecraftClient mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, Supplier<List<T>> entries, T preSelected) {
        this(screen, mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn, entries, null, null, preSelected);
    }

    public GenericSelectionList(S screen, MinecraftClient client, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, Supplier<List<T>> entries, Supplier<String> query, @Nullable GenericSelectionList<T, S> copyFrom, T preSelected) {
        super(client, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.screen = screen;
        this.entrySupplier = entries;
        if (copyFrom != null) this.entries = copyFrom.entries;

        this.preSelected = preSelected;
        if (query != null) this.search(query, false);
    }

    public void setBgTexture(Identifier bgTexture) {
        this.bgTexture = bgTexture;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 20;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable Entry selected) {
        super.setSelected(selected);
        if (selected != null)
            NarratorManager.INSTANCE.narrate(new TranslatableText("mmo.narrator.selection.selected", selected.entry.getTitle().getString()).getString());
    }

    @Override
    protected void moveSelection(MoveDirection direction) {
        this.moveSelectionIf(direction, entry -> true);
        this.screen.setButtonsActive(true);
    }

    public Optional<Entry> getSelection() {
        return Optional.ofNullable(this.getSelected());
    }

    public void search(Supplier<String> searchQuery, boolean renewAvailable) {
        this.clearEntries();
        if (this.entries == null || renewAvailable) this.entries = new ArrayList<>(entrySupplier.get());

        String s = searchQuery.get().toLowerCase(Locale.ROOT);

        for (T entry : this.entries) {
            // not a full text search, but is okay
            if (entry.getTitle().asString().toLowerCase(Locale.ROOT).contains(s)
                    || entry.getUnlocalizedName().toLowerCase(Locale.ROOT).contains(s)) {
                this.addEntry(new Entry(this, entry, entry.equals(preSelected)));
            }
        }
    }

    public S getGuiScreen() {
        return this.screen;
    }

    @Override
    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    protected int getRowBottom(int index) {
        return this.getRowTop(index) + this.itemHeight;
    }

    @SuppressWarnings("deprecation")
    protected void renderWholeBackground(int y0, int y2, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.client.getTextureManager().bindTexture(bgTexture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferbuilder.vertex(this.left, y2, 0.0D)
                .texture(0.0F, (float) y2 / 32.0F)
                .color(64, 64, 64, alpha)
                .next();
        bufferbuilder.vertex(this.left + this.width, y2, 0.0D)
                .texture((float) this.width / 32.0F, (float) y2 / 32.0F)
                .color(64, 64, 64, alpha)
                .next();
        bufferbuilder.vertex(this.left + this.width, y0, 0.0D)
                .texture((float) this.width / 32.0F, (float) y0 / 32.0F)
                .color(64, 64, 64, 255)
                .next();
        bufferbuilder.vertex(this.left, y0, 0.0D)
                .texture(0.0F, (float) y0 / 32.0F)
                .color(64, 64, 64, 255)
                .next();
        tessellator.draw();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = this.getScrollbarPositionX();
        int j = i + 6;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        this.client.getTextureManager().bindTexture(bgTexture);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferbuilder.vertex(this.left, this.bottom, 0.0D)
                .texture((float) this.left / 32.0F, (float) (this.bottom + (int) this.getScrollAmount()) / 32.0F)
                .color(32, 32, 32, 255)
                .next();
        bufferbuilder.vertex(this.right, this.bottom, 0.0D)
                .texture((float) this.right / 32.0F, (float) (this.bottom + (int) this.getScrollAmount()) / 32.0F)
                .color(32, 32, 32, 255)
                .next();
        bufferbuilder.vertex(this.right, this.top, 0.0D)
                .texture((float) this.right / 32.0F, (float) (this.top + (int) this.getScrollAmount()) / 32.0F)
                .color(32, 32, 32, 255)
                .next();
        bufferbuilder.vertex(this.left, this.top, 0.0D)
                .texture((float) this.left / 32.0F, (float) (this.top + (int) this.getScrollAmount()) / 32.0F)
                .color(32, 32, 32, 255)
                .next();
        tessellator.draw();

        int k = this.getRowLeft();
        int l = this.top + 4 - (int) this.getScrollAmount();
        if (((EntryListWidgetAccessor) this).shouldRenderHeader()) {
            this.renderHeader(matrices, k, l, tessellator);
        }

        this.renderList(matrices, k, l, mouseX, mouseY, delta);
        RenderSystem.disableDepthTest();
        this.renderWholeBackground(0, this.top, 255);
        this.renderWholeBackground(this.bottom, this.height, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();

        bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferbuilder.vertex(this.left, this.top + 4, 0.0D)
                .texture(0.0F, 1.0F)
                .color(0, 0, 0, 0)
                .next();
        bufferbuilder.vertex(this.right, this.top + 4, 0.0D)
                .texture(1.0F, 1.0F)
                .color(0, 0, 0, 0)
                .next();
        bufferbuilder.vertex(this.right, this.top, 0.0D)
                .texture(1.0F, 0.0F)
                .color(0, 0, 0, 255)
                .next();
        bufferbuilder.vertex(this.left, this.top, 0.0D)
                .texture(0.0F, 0.0F)
                .color(0, 0, 0, 255)
                .next();
        tessellator.draw();

        bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferbuilder.vertex(this.left, this.bottom, 0.0D)
                .texture(0.0F, 1.0F)
                .color(0, 0, 0, 255).next();
        bufferbuilder.vertex(this.right, this.bottom, 0.0D)
                .texture(1.0F, 1.0F)
                .color(0, 0, 0, 255).next();
        bufferbuilder.vertex(this.right, this.bottom - 4, 0.0D)
                .texture(1.0F, 0.0F)
                .color(0, 0, 0, 0)
                .next();
        bufferbuilder.vertex(this.left, this.bottom - 4, 0.0D)
                .texture(0.0F, 0.0F)
                .color(0, 0, 0, 0)
                .next();
        tessellator.draw();

        int j1 = this.getMaxScroll();
        if (j1 > 0) {
            int k1 = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxPosition());
            k1 = MathHelper.clamp(k1, 32, this.bottom - this.top - 8);
            int l1 = (int) this.getScrollAmount() * (this.bottom - this.top - k1) / j1 + this.top;
            if (l1 < this.top) {
                l1 = this.top;
            }

            bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferbuilder.vertex(i, this.bottom, 0.0D)
                    .texture(0.0F, 1.0F)
                    .color(0, 0, 0, 255)
                    .next();
            bufferbuilder.vertex(j, this.bottom, 0.0D)
                    .texture(1.0F, 1.0F)
                    .color(0, 0, 0, 255)
                    .next();
            bufferbuilder.vertex(j, this.top, 0.0D)
                    .texture(1.0F, 0.0F)
                    .color(0, 0, 0, 255)
                    .next();
            bufferbuilder.vertex(i, this.top, 0.0D)
                    .texture(0.0F, 0.0F)
                    .color(0, 0, 0, 255)
                    .next();
            tessellator.draw();

            bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferbuilder.vertex(i, l1 + k1, 0.0D)
                    .texture(0.0F, 1.0F)
                    .color(128, 128, 128, 255)
                    .next();
            bufferbuilder.vertex(j, l1 + k1, 0.0D)
                    .texture(1.0F, 1.0F)
                    .color(128, 128, 128, 255)
                    .next();
            bufferbuilder.vertex(j, l1, 0.0D)
                    .texture(1.0F, 0.0F)
                    .color(128, 128, 128, 255)
                    .next();
            bufferbuilder.vertex(i, l1, 0.0D)
                    .texture(0.0F, 0.0F)
                    .color(128, 128, 128, 255)
                    .next();
            tessellator.draw();

            bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferbuilder.vertex(i, l1 + k1 - 1, 0.0D)
                    .texture(0.0F, 1.0F)
                    .color(192, 192, 192, 255)
                    .next();
            bufferbuilder.vertex(j - 1, l1 + k1 - 1, 0.0D)
                    .texture(1.0F, 1.0F)
                    .color(192, 192, 192, 255)
                    .next();
            bufferbuilder.vertex(j - 1, l1, 0.0D)
                    .texture(1.0F, 0.0F)
                    .color(192, 192, 192, 255)
                    .next();
            bufferbuilder.vertex(i, l1, 0.0D)
                    .texture(0.0F, 0.0F)
                    .color(192, 192, 192, 255)
                    .next();
            tessellator.draw();
        }

        this.renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        int i = this.getEntryCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for (int j = 0; j < i; ++j) {
            int k = this.getRowTop(j);
            int l = this.getRowBottom(j);

            if (l >= this.top && k <= this.bottom) {
                int i1 = y + j * this.itemHeight + this.headerHeight;
                int j1 = this.itemHeight - 4;
                Entry e = this.getEntry(j);
                int k1 = this.getRowWidth();
                boolean selItem = this.isSelectedEntry(j);

                if (((EntryListWidgetAccessor) this).shouldRenderSelection() && (selItem || e.preSelected)) {
                    int l1 = this.left + this.width / 2 - k1 / 2;
                    int i2 = this.left + this.width / 2 + k1 / 2;

                    RenderSystem.disableTexture();

                    if (selItem) {
                        float f = this.isFocused() ? 1.0F : 0.5F;
                        RenderSystem.color4f(f, f, f, 1.0F);
                    } else { // preSelected
                        RenderSystem.color4f(0.196F, 0.476F, 0.659F, 1.0F); // rgba(50, 121, 168, 255)
                    }

                    bufferbuilder.begin(7, VertexFormats.POSITION);
                    bufferbuilder.vertex(l1, i1 + j1 + 2, 0.0D).next();
                    bufferbuilder.vertex(i2, i1 + j1 + 2, 0.0D).next();
                    bufferbuilder.vertex(i2, i1 - 2, 0.0D).next();
                    bufferbuilder.vertex(l1, i1 - 2, 0.0D).next();
                    tessellator.draw();

                    RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);

                    bufferbuilder.begin(7, VertexFormats.POSITION);
                    bufferbuilder.vertex(l1 + 1, i1 + j1 + 1, 0.0D).next();
                    bufferbuilder.vertex(i2 - 1, i1 + j1 + 1, 0.0D).next();
                    bufferbuilder.vertex(i2 - 1, i1 - 1, 0.0D).next();
                    bufferbuilder.vertex(l1 + 1, i1 - 1, 0.0D).next();
                    tessellator.draw();

                    RenderSystem.enableTexture();
                }

                int j2 = this.getRowLeft();
                e.render(matrices, j, k, j2, k1, j1, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPosition(mouseX, mouseY), e), delta);
            }
        }
    }

    public final class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {

        private final MinecraftClient minecraft;
        private final S screen;
        private final T entry;
        private long lastClicked;
        protected boolean preSelected;

        public Entry(GenericSelectionList<T, S> p_i50631_2_, T entry, boolean preSelected) {
            this.screen = p_i50631_2_.getGuiScreen();
            this.entry = entry;
            this.minecraft = MinecraftClient.getInstance();
            this.preSelected = preSelected;
        }

        @SuppressWarnings("deprecation")
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.minecraft.textRenderer.drawWithShadow(matrices, this.entry.getTitle(), (float) (x + 32 + 3), (float) (y + 1), 16777215);
            this.minecraft.textRenderer.draw(matrices, this.entry.getFirstLine(), (float) (x + 32 + 3), (float) (y + 9 + 3), 8421504);
            this.minecraft.textRenderer.draw(matrices, this.entry.getSecondLine(), (float) (x + 32 + 3), (float) (y + 9 + 9 + 3), 8421504);

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            this.minecraft.getTextureManager().bindTexture(this.entry.getIcon() != null ? this.entry.getIcon() : GenericSelectionList.unknownTexture);

            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
            RenderSystem.disableBlend();

            if (this.minecraft.options.touchscreen || hovered) {
                this.minecraft.getTextureManager().bindTexture(GenericSelectionList.selectionTextures);
                DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);

                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                int j = mouseX - x;
                int i = j < 32 ? 32 : 0;

                if (entry.getToolTip() != null)
                    screen.setTooltip(this.minecraft.textRenderer.wrapLines(entry.getToolTip(), 175));

                DrawableHelper.drawTexture(matrices, x, y, 0.0F, (float) i, 32, 32, 256, 256);
            }
        }

        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            GenericSelectionList.this.setSelected(this);
            this.screen.setButtonsActive(GenericSelectionList.this.getSelection().isPresent());
            if (p_mouseClicked_1_ - (double) GenericSelectionList.this.getRowLeft() <= 32.0D) {
                this.onSelect();
                return true;
            } else if (Util.getMeasuringTimeMs() - this.lastClicked < 250L) {
                this.onSelect();
                return true;
            } else {
                this.lastClicked = Util.getMeasuringTimeMs();
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
