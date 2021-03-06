package work.lclpnet.mmo.facade.race.humanoid;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.facade.race.MMORace;

import java.util.Map;

public class RaceElven extends MMORace {

    public static final transient Map<Pose, EntitySize> SIZES = ImmutableMap.<Pose, EntitySize>builder()
            .put(Pose.STANDING, EntitySize.flexible(0.5F, 2.125F))
            .put(Pose.CROUCHING, EntitySize.flexible(0.5F, 1.75F))
            .build();

    public RaceElven() {
        super("elven", new TranslationTextComponent("mmo.race.elven.title"));
    }

    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation(LCLPMMO.MODID, "textures/entity/elven/icon.png");
    }

    @Override
    public Map<Pose, EntitySize> getEntitySizeOverrides() {
        return SIZES;
    }

    @Override
    public float getEyeHeightOverride(Pose pose, EntitySize size) {
        switch (pose) {
            case STANDING:
                return 1.85F;
            case CROUCHING:
                return 1.48F;
            default:
                return super.getEyeHeightOverride(pose, size);
        }
    }

    @Override
    public void doTridentTranslation(MatrixStack matrixStack) {
        matrixStack.translate(0F, -4F / 16F, 0F);
    }

    @Override
    public void doElytraTranslation(MatrixStack matrixStack) {
        matrixStack.translate(0F, -3F / 16F, 0F);
    }

    @Override
    public void doCapeTranslation(MatrixStack matrixStack) {
        matrixStack.translate(0F, -4F / 16F, 0F);
    }

    @Override
    public void doParrotTranslation(MatrixStack matrixStack) {
        matrixStack.translate(0F, -4F / 16F, 0F);
    }

    @Override
    public void doHeadItemTranslation(MatrixStack matrixStack) {
        matrixStack.translate(0F, -4F / 16F, 0F);
    }
}
