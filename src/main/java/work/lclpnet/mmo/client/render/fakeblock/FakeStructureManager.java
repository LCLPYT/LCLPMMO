package work.lclpnet.mmo.client.render.fakeblock;

import net.minecraft.util.math.BlockPos;
import work.lclpnet.mmo.block.fake.FakeStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FakeStructureManager {

    protected final Map<FakeStructure, BlockPos> fakeStructureContainer = new HashMap<>();

    public void add(FakeStructure fakeStructure, BlockPos pos) {
        fakeStructureContainer.put(fakeStructure, pos);
    }

    public void remove(FakeStructure fakeStructure) {
        fakeStructureContainer.remove(fakeStructure);
    }

    public void clear() {
        fakeStructureContainer.clear();
    }

    public Set<Map.Entry<FakeStructure, BlockPos>> getFakeStructures() {
        return fakeStructureContainer.entrySet();
    }
}
