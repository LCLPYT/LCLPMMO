package work.lclpnet.mmo.asm.type.client;

import work.lclpnet.mmo.block.fake.FakeStructure;
import work.lclpnet.mmo.client.render.fakeblock.IFakeStructureRenderer;

import java.util.Set;

public interface IWorldRenderer {

    Set<FakeStructure> getFakeStructures();

    void setFakeStructureRenderer(IFakeStructureRenderer fakeStructureRenderer);

    IFakeStructureRenderer getFakeStructureRenderer();
}
