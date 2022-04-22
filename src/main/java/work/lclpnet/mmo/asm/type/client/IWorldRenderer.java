package work.lclpnet.mmo.asm.type.client;

import work.lclpnet.mmo.client.render.fakeblock.FakeStructureManager;
import work.lclpnet.mmo.client.render.fakeblock.IFakeStructureRenderer;

public interface IWorldRenderer {

    FakeStructureManager getFakeStructureManager();

    void setFakeStructureRenderer(IFakeStructureRenderer fakeStructureRenderer);

    IFakeStructureRenderer getFakeStructureRenderer();
}
