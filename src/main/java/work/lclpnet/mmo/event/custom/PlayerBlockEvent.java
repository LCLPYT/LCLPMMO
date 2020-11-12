package work.lclpnet.mmo.event.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;

public class PlayerBlockEvent extends BlockEvent{

	protected PlayerEntity player;
	
	public PlayerBlockEvent(IWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
		super(world, pos, state);
		this.player = player;
	}
	
	public PlayerEntity getPlayer() {
		return player;
	}

}
