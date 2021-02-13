package work.lclpnet.mmo.gui.dialog;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.asm.type.IMMOPlayer;
import work.lclpnet.mmo.entity.MMOEntities;
import work.lclpnet.mmo.entity.PixieEntity;
import work.lclpnet.mmo.facade.dialog.Dialog;
import work.lclpnet.mmo.facade.dialog.DialogData;
import work.lclpnet.mmo.facade.dialog.DialogSubstitute;
import work.lclpnet.mmo.gui.MMOScreen;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageDialog;
import work.lclpnet.mmo.util.Color;

import java.util.HashMap;
import java.util.Map;

public class DialogScreen<T extends LivingEntity> extends MMOScreen {

	private static final Map<EntityType<? extends LivingEntity>, EntityTypeAdapter<? extends LivingEntity>> adapters = new HashMap<>();

	private long firstRenderTime = 0L;
	private final int id;
	private boolean dismissable = true;
	private T entity;
	private EntityTypeAdapter<T> adapter;
	private DialogWrapper dialog;
	private Button nextButton;

	@SuppressWarnings("unchecked")
	public DialogScreen(Dialog dialog) {
		this((T) dialog.getPartner(), dialog.getData(), dialog.isDismissable(), dialog.getId());
	}

	@SuppressWarnings("unchecked")
	public DialogScreen(T entity, DialogData data, boolean dismissable, int id) {
		super(getDialogTitle(entity));

		this.id = id;
		this.dismissable = dismissable;
		this.entity = entity;
		this.adapter = (EntityTypeAdapter<T>) adapters.get(this.entity.getType());
		this.dialog = new DialogWrapper(data);
	}

	private static <T extends LivingEntity> ITextComponent getDialogTitle(T le) {
		ITextComponent name;

		@SuppressWarnings("unchecked")
		EntityTypeAdapter<T> adapter = (EntityTypeAdapter<T>) adapters.get(le.getType());
		if(adapter != null) name = adapter.getName(le);
		else name = le.hasCustomName() ? le.getCustomName() : le.getType().getName();

		return name != null ? name : new TranslationTextComponent("mmo.screen.dialog.title");
	}

	@Override
	protected void init() {
		nextButton = this.addButton(new Button(this.width / 2 - 30, this.height - 50, 100, 20, new TranslationTextComponent(dialog.hasNext() ? "mmo.screen.dialog.next" : "mmo.screen.dialog.end"), (p_213031_1_) -> {
			if(!this.dialog.hasNext()) {
				MMOPacketHandler.INSTANCE.sendToServer(MessageDialog.getCompleteMessage(this.id));
				closeScreen();
				return;
			}
			
			this.dialog.next();
			update();
		}));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if(this.firstRenderTime <= 0L) this.firstRenderTime = Util.milliTime();

		float alpha = MathHelper.clamp((float) (Util.milliTime() - this.firstRenderTime) / 1000.0F * 4F, 0.0F, 1.0F);
		Color c1 = Color.fromARGBInt(-1072689136), c2 = Color.fromARGBInt(-804253680);
		c1.alpha *= alpha;
		c2.alpha *= alpha;

		this.fillGradient(matrixStack, 0, 0, this.width, this.height, c1.toARGBInt(), c2.toARGBInt());
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this, matrixStack));

		drawMultiLineCenteredString(matrixStack, this.font, this.title, 2F, this.width / 2, 20, 16777215);

		int entityScale;
		int entitX;
		int entityY;
		if(adapter != null) {
			entityScale = adapter.getScale(entity);
			entitX = adapter.getScreenX(entity, this.width);
			entityY = adapter.getScreenY(entity, this.height);
		} else {
			entityScale = 75;
			entitX = (int) (this.width * 0.75);
			entityY = this.height - (int) (this.height * 0.2F);
		}

		InventoryScreen.drawEntityOnScreen(entitX, entityY, entityScale, (float) entitX - mouseX, (float) (entityY - 100) - mouseY, entity);

		int dialogX = (int) (this.width * 0.1F);
		int dialogY = 75;
		DialogSubstitute[] substitutes = dialog.getCurrent().getSubstitutes();
		Object[] processed = new Object[substitutes.length];
		for(int i = 0; i < substitutes.length; i++) {
			DialogSubstitute substitute = substitutes[i];
			processed[i] = substitute.isTranslationKey() ? I18n.format(substitute.getSubstitute()) : substitute.getSubstitute();
		}
		
		ITextComponent dialogUpper = new TranslationTextComponent(dialog.getCurrent().getTranslationKey(), processed);

		for (IReorderingProcessor s : minecraft.fontRenderer.trimStringToWidth(dialogUpper, (int) (this.width * 0.6F) - dialogX)) {
			minecraft.fontRenderer.func_238407_a_(matrixStack, s, dialogX + 10, dialogY, Color.WHITE);
			dialogY += minecraft.fontRenderer.FONT_HEIGHT;
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return dismissable;
	}

	@Override
	public void closeScreen() {
		IMMOPlayer.get(Minecraft.getInstance().player).closeMMODialog();
	}
	
	private void update() {
		nextButton.setMessage(new TranslationTextComponent(this.dialog.hasNext() ? "mmo.screen.dialog.next" : "mmo.screen.dialog.end"));
	}

	public static <T extends LivingEntity> void registerEntityTypeAdapater(EntityType<T> entityType, EntityTypeAdapter<T> adapter) {
		adapters.put(entityType, adapter);
	}

	public static interface EntityTypeAdapter<T extends LivingEntity> {

		default int getScale(T entity) {
			return 75;
		}

		default int getScreenX(T entity, int screenWidth) {
			return (int) (screenWidth * 0.75F);
		}

		default int getScreenY(T entity, int screenHeight) {
			return screenHeight - (int) (screenHeight * 0.2F);
		}

		default ITextComponent getName(T entity) {
			return entity.hasCustomName() ? entity.getCustomName() : entity.getType().getName();
		}

	}

	static {
		registerEntityTypeAdapater(MMOEntities.PIXIE, new EntityTypeAdapter<PixieEntity>() {
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
			public ITextComponent getName(PlayerEntity entity) {
				ITextComponent displayName = entity.getDisplayName();
				return displayName != null ? displayName : entity.getName();
			}
		});
	}

}
