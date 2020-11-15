package work.lclpnet.mmo.gui;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.entity.MMOEntities;
import work.lclpnet.mmo.entity.PixieEntity;
import work.lclpnet.mmo.util.Color;

public class DialogScreen<T extends LivingEntity> extends MMOScreen {

	private static final Map<EntityType<? extends LivingEntity>, EntityTypeAdapter<? extends LivingEntity>> adapters = new HashMap<>();

	private long firstRenderTime = 0L;
	private boolean dismissable = true;
	private T entity;
	private EntityTypeAdapter<T> adapter;

	@SuppressWarnings("unchecked")
	public DialogScreen(boolean dismissable, T entity) {
		super(getDialogTitle(entity));
		
		this.dismissable = dismissable;
		this.entity = entity;
		this.adapter = (EntityTypeAdapter<T>) adapters.get(this.entity.getType());
	}

	private static ITextComponent getDialogTitle(LivingEntity le) {
		ITextComponent name = le.hasCustomName() ? le.getCustomName() : le.getType().getName();
		return name != null ? name : new TranslationTextComponent("mmo.screen.dialog.title");
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

		int scale;
		int x;
		int y;
		if(adapter != null) {
			scale = adapter.getScale(entity);
			x = adapter.getScreenX(entity, this.width);
			y = adapter.getScreenY(entity, this.height);
		} else {
			scale = 75;
			x = (int) (this.width * 0.75);
			y = this.height - (int) (this.height * 0.2F);
		}

		InventoryScreen.drawEntityOnScreen(x, y, scale, (float) x - mouseX, (float) (y - 100) - mouseY, entity);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return dismissable;
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
	}

}
