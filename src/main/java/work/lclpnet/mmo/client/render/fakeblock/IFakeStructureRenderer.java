package work.lclpnet.mmo.client.render.fakeblock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import work.lclpnet.mmo.block.fake.FakeStructure;
import work.lclpnet.mmo.client.render.RenderContext;

@Environment(EnvType.CLIENT)
public interface IFakeStructureRenderer {

    void render(FakeStructure structure, BlockPos position, RenderContext ctx);

    void setWorld(World world);
}
