package work.lclpnet.mmo.block.fake;

import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public class FakeBlock {

    protected final World world;
    /**
     * Relative to parent origin.
     */
    protected final FakeBlockPos pos;
    protected final BlockState state;
    protected final boolean collision;

    public FakeBlock(World world, FakeBlockPos pos, BlockState state) {
        this(world, pos, state, true);
    }

    public FakeBlock(World world, FakeBlockPos pos, BlockState state, boolean collision) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.collision = collision;
    }

    public World getWorld() {
        return world;
    }

    public FakeBlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public boolean hasCollision() {
        return collision;
    }
}
