package work.lclpnet.mmo.asm;

import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MMOMixinConnector implements IMixinConnector{

	@Override
	public void connect() {
		System.out.println("Invoking MMO Mixin Connectors...");
		Mixins.addConfiguration("mixins.lclpmmo.json");

		if (FMLEnvironment.production) {
			System.out.println("Detected production environment. Adding production only mixins...");
			Mixins.addConfiguration("mixins.lclpmmo.prod.json");
		} else {
			System.out.println("Detected development environment. Adding development only mixins...");
			Mixins.addConfiguration("mixins.lclpmmo.dev.json");
		}
	}

}
