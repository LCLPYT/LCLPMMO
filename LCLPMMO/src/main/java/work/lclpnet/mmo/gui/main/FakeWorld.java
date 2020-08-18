package work.lclpnet.mmo.gui.main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FakeWorld extends ClientWorld{

	public FakeWorld(ClientPlayNetHandler netHandler, WorldSettings worldSettingsIn) {
		super(netHandler, worldSettingsIn, DimensionType.OVERWORLD, 16, Minecraft.getInstance().getProfiler(), null);
	}

}
