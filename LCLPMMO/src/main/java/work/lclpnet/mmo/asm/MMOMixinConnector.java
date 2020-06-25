package work.lclpnet.mmo.asm;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MMOMixinConnector implements IMixinConnector{

	@Override
	public void connect() {
		System.out.println("Invoking MMO Mixin Connectors...");
		Mixins.addConfiguration("mixins.lclpmmo.json");
	}

}
