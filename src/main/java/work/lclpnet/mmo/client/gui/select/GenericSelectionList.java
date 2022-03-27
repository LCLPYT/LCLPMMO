package work.lclpnet.mmo.client.gui.select;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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
        this.setRenderBackground(false);
        this.setRenderHorizontalShadows(false);
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
        return Optional.ofNullable(this.getSelectedOrNull());
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

    @Override
    protected void renderBackground(MatrixStack matrices) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, bgTexture);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.bottom, 0.0).texture((float)this.left / 32.0f, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.right, this.bottom, 0.0).texture((float)this.right / 32.0f, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.right, this.top, 0.0).texture((float)this.right / 32.0f, (float)(this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.left, this.top, 0.0).texture((float)this.left / 32.0f, (float)(this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        tessellator.draw();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        renderCustomHorizontalShadows();
    }

    private void renderCustomHorizontalShadows() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, bgTexture);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.top, -100.0).texture(0.0f, (float)this.top / 32.0f).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.left + this.width, this.top, -100.0).texture((float)this.width / 32.0f, (float)this.top / 32.0f).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.left + this.width, 0.0, -100.0).texture((float)this.width / 32.0f, 0.0f).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.left, 0.0, -100.0).texture(0.0f, 0.0f).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.left, this.height, -100.0).texture(0.0f, (float)this.height / 32.0f).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.left + this.width, this.height, -100.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.left + this.width, this.bottom, -100.0).texture((float)this.width / 32.0f, (float)this.bottom / 32.0f).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.left, this.bottom, -100.0).texture(0.0f, (float)this.bottom / 32.0f).color(64, 64, 64, 255).next();
        tessellator.draw();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(this.left, this.top + 4, 0.0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.right, this.top + 4, 0.0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.right, this.top, 0.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.left, this.top, 0.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.left, this.bottom, 0.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.right, this.bottom, 0.0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.right, this.bottom - 4, 0.0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.left, this.bottom - 4, 0.0).color(0, 0, 0, 0).next();
        tessellator.draw();
    }

    @Override
    protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        int i = this.getEntryCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (int j = 0; j < i; ++j) {
            int p;
            int k = this.getRowTop(j);
            int l = this.getRowBottom(j);
            if (l < this.top || k > this.bottom) continue;
            int m = y + j * this.itemHeight + this.headerHeight;
            int n = this.itemHeight - 4;
            Entry entry = this.getEntry(j);
            int o = this.getRowWidth();
            if (((EntryListWidgetAccessor) this).shouldRenderSelection() && (entry.isPreSelected() || this.isSelectedEntry(j))) {
                p = this.left + this.width / 2 - o / 2;
                int q = this.left + this.width / 2 + o / 2;
                RenderSystem.disableTexture();
                RenderSystem.setShader(GameRenderer::getPositionShader);

                if (entry.isPreSelected()) {
                    RenderSystem.setShaderColor(0.196F, 0.476F, 0.659F, 1.0F); // rgba(50, 121, 168, 255)
                } else {
                    float f = this.isFocused() ? 1.0f : 0.5f;
                    RenderSystem.setShaderColor(f, f, f, 1.0f);
                }

                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
                bufferBuilder.vertex(p, m + n + 2, 0.0).next();
                bufferBuilder.vertex(q, m + n + 2, 0.0).next();
                bufferBuilder.vertex(q, m - 2, 0.0).next();
                bufferBuilder.vertex(p, m - 2, 0.0).next();
                tessellator.draw();
                RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
                bufferBuilder.vertex(p + 1, m + n + 1, 0.0).next();
                bufferBuilder.vertex(q - 1, m + n + 1, 0.0).next();
                bufferBuilder.vertex(q - 1, m - 1, 0.0).next();
                bufferBuilder.vertex(p + 1, m - 1, 0.0).next();
                tessellator.draw();
                RenderSystem.enableTexture();
            }
            p = this.getRowLeft();
            entry.render(matrices, j, k, p, o, n, mouseX, mouseY, Objects.equals(this.getHoveredEntry(), entry), delta);
        }
    }

    @Environment(EnvType.CLIENT)
    public final class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {

        private final MinecraftClient minecraft;
        private final S screen;
        private final T entry;
        private long lastClicked;
        private final boolean preSelected;

        public Entry(GenericSelectionList<T, S> p_i50631_2_, T entry, boolean preSelected) {
            this.screen = p_i50631_2_.getGuiScreen();
            this.entry = entry;
            this.minecraft = MinecraftClient.getInstance();
            this.preSelected = preSelected;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Text text;
            if ((text = this.entry.getTitle()) != null)
                this.minecraft.textRenderer.draw(matrices, text, (float) (x + 32 + 3), (float) (y + 1), 16777215);

            if ((text = this.entry.getFirstLine()) != null)
                this.minecraft.textRenderer.draw(matrices, text, (float) (x + 32 + 3), (float) (y + 9 + 3), 8421504);

            if ((this.entry.getSecondLine()) != null)
                this.minecraft.textRenderer.draw(matrices, text, (float) (x + 32 + 3), (float) (y + 9 + 9 + 3), 8421504);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, this.entry.getIcon() != null ? this.entry.getIcon() : GenericSelectionList.unknownTexture);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();

            if (this.minecraft.options.touchscreen || hovered) {
                RenderSystem.setShaderTexture(0, GenericSelectionList.selectionTextures);
                DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                int j = mouseX - x;
                int i = j < 32 ? 32 : 0;

                if (entry.getToolTip() != null)
                    screen.setTooltip(this.minecraft.textRenderer.wrapLines(entry.getToolTip(), 175));

                DrawableHelper.drawTexture(matrices, x, y, 0.0F, (float) i, 32, 32, 256, 256);
            }
        }

        @Override
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

        public boolean isPreSelected() {
            return preSelected;
        }

        @Override
        public Text getNarration() {
            return entry.getTitle();
        }
    }
}
