package work.lclpnet.mmo.asm.type;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IMMOModelRenderer {

    void postTranslateRotate(MatrixStack matrixStack);
}
