package work.lclpnet.mmo.client.gui.dialog;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import work.lclpnet.mmo.asm.type.IMMOPlayer;
import work.lclpnet.mmo.client.gui.MMOScreen;
import work.lclpnet.mmo.client.util.Color;
import work.lclpnet.mmo.data.dialog.Dialog;
import work.lclpnet.mmo.data.dialog.DialogData;
import work.lclpnet.mmo.data.dialog.DialogSubstitute;
import work.lclpnet.mmo.entity.PixieEntity;
import work.lclpnet.mmo.module.PixieModule;
import work.lclpnet.mmo.network.packet.DialogPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DialogScreen<T extends LivingEntity> extends MMOScreen {

    private static final Map<EntityType<? extends LivingEntity>, EntityTypeAdapter<? extends LivingEntity>> adapters = new HashMap<>();

    private long firstRenderTime = 0L;
    private final int id;
    private boolean dismissible;
    private final T entity;
    private final EntityTypeAdapter<T> adapter;
    private final DialogWrapper dialog;
    private ButtonWidget nextButton;

    @SuppressWarnings("unchecked")
    public DialogScreen(Dialog dialog) {
        this((T) dialog.getPartner(), dialog.getData(), dialog.isDismissable(), dialog.getId());
    }

    @SuppressWarnings("unchecked")
    public DialogScreen(T entity, DialogData data, boolean dismissible, int id) {
        super(getDialogTitle(entity));

        this.id = id;
        this.dismissible = dismissible;
        this.entity = entity;
        this.adapter = (EntityTypeAdapter<T>) adapters.get(this.entity.getType());
        this.dialog = new DialogWrapper(data);
    }

    private static <T extends LivingEntity> Text getDialogTitle(T le) {
        Text name;

        @SuppressWarnings("unchecked")
        EntityTypeAdapter<T> adapter = (EntityTypeAdapter<T>) adapters.get(le.getType());
        if (adapter != null) name = adapter.getName(le);
        else name = le.hasCustomName() ? le.getCustomName() : le.getType().getName();

        return name != null ? name : new TranslatableText("mmo.screen.dialog.title");
    }

    @Override
    protected void init() {
        nextButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 30, this.height - 50, 100, 20,
                new TranslatableText(dialog.hasNext() ? "mmo.screen.dialog.next" : "mmo.screen.dialog.end"), buttonWidget -> {
            if (!this.dialog.hasNext()) {
                DialogPacket.sendDialogCompleteToServer(this.id);
                onClose();
                return;
            }

            this.dialog.next();
            update();
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (client == null) return;

        if (this.firstRenderTime <= 0L) this.firstRenderTime = Util.getMeasuringTimeMs();

        float alpha = MathHelper.clamp((float) (Util.getMeasuringTimeMs() - this.firstRenderTime) / 1000.0F * 4F, 0.0F, 1.0F);
        Color c1 = Color.fromARGBInt(-1072689136), c2 = Color.fromARGBInt(-804253680);
        c1.alpha *= alpha;
        c2.alpha *= alpha;

        this.fillGradient(matrices, 0, 0, this.width, this.height, c1.toARGBInt(), c2.toARGBInt());

        drawMultiLineCenteredString(matrices, this.textRenderer, this.title, 2F, this.width / 2, 20, 16777215);

        int entityScale;
        int entitX;
        int entityY;
        if (adapter != null) {
            entityScale = adapter.getScale(entity);
            entitX = adapter.getScreenX(entity, this.width);
            entityY = adapter.getScreenY(entity, this.height);
        } else {
            entityScale = 75;
            entitX = (int) (this.width * 0.75);
            entityY = this.height - (int) (this.height * 0.2F);
        }

        InventoryScreen.drawEntity(entitX, entityY, entityScale, (float) entitX - mouseX, (float) (entityY - 100) - mouseY, entity);

        int dialogX = (int) (this.width * 0.1F);
        int dialogY = 75;
        DialogSubstitute[] substitutes = dialog.getCurrent().getSubstitutes();
        Object[] processed = new Object[substitutes.length];
        for (int i = 0; i < substitutes.length; i++) {
            DialogSubstitute substitute = substitutes[i];
            processed[i] = substitute.isTranslationKey() ? I18n.translate(substitute.getSubstitute()) : substitute.getSubstitute();
        }

        Text dialogUpper = new TranslatableText(dialog.getCurrent().getTranslationKey(), processed);

        for (OrderedText s : client.textRenderer.wrapLines(dialogUpper, (int) (this.width * 0.6F) - dialogX)) {
            client.textRenderer.drawWithShadow(matrices, s, dialogX + 10, dialogY, Color.WHITE);
            dialogY += client.textRenderer.fontHeight;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return dismissible;
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(IMMOPlayer.of(MinecraftClient.getInstance().player))
                .closeMMODialog();
    }

    private void update() {
        nextButton.setMessage(new TranslatableText(this.dialog.hasNext() ? "mmo.screen.dialog.next" : "mmo.screen.dialog.end"));
    }

    public static <T extends LivingEntity> void registerEntityTypeAdapater(EntityType<T> entityType, EntityTypeAdapter<T> adapter) {
        adapters.put(entityType, adapter);
    }

    public interface EntityTypeAdapter<T extends LivingEntity> {

        default int getScale(T entity) {
            return 75;
        }

        default int getScreenX(T entity, int screenWidth) {
            return (int) (screenWidth * 0.75F);
        }

        default int getScreenY(T entity, int screenHeight) {
            return screenHeight - (int) (screenHeight * 0.2F);
        }

        default Text getName(T entity) {
            return entity.hasCustomName() ? entity.getCustomName() : entity.getType().getName();
        }
    }

    static {
        registerEntityTypeAdapater(PixieModule.pixieEntityType, new EntityTypeAdapter<PixieEntity>() {
            @Override
            public int getScale(PixieEntity entity) {
                return EntityTypeAdapter.super.getScale(entity) * 3;
            }

            @Override
            public int getScreenY(PixieEntity entity, int screenHeight) {
                return EntityTypeAdapter.super.getScreenY(entity, screenHeight) - (int) (screenHeight * 0.1F);
            }
        });

        registerEntityTypeAdapater(EntityType.PLAYER, new EntityTypeAdapter<PlayerEntity>() {
            @Override
            public Text getName(PlayerEntity entity) {
                Text displayName = entity.getDisplayName();
                return displayName != null ? displayName : entity.getName();
            }
        });
    }
}
