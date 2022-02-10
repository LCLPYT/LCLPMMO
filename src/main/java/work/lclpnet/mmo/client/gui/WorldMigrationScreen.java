package work.lclpnet.mmo.client.gui;

import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.world.level.storage.LevelStorage;
import work.lclpnet.mmo.client.util.Color;
import work.lclpnet.mmo.migrator.WorldMigrator;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorldMigrationScreen extends MMOScreen {

    protected final LevelStorage.Session session;
    protected final AtomicBoolean aBoolean = new AtomicBoolean(false);
    protected long lastDotTick = -1;
    protected int dots = 3;
    protected final EditWorldScreen parent;
    protected String text = getText();

    public WorldMigrationScreen(EditWorldScreen parent, LevelStorage.Session session) {
        super(new LiteralText("World Migration"));
        this.session = Objects.requireNonNull(session);
        this.parent = Objects.requireNonNull(parent);
    }

    @Override
    protected void init() {
        super.init();
        startMigration();
    }

    protected void startMigration() {
        WorldMigrator.create(Objects.requireNonNull(client), session)
                .thenCompose(WorldMigrator::migrate)
                .thenRun(() -> aBoolean.set(true));
    }

    @Override
    public void tick() {
        super.tick();
        if (aBoolean.get()) {
            displayToast(new LiteralText("Migration complete."));
            Objects.requireNonNull(this.client).openScreen(parent);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        Objects.requireNonNull(this.client);

        if (lastDotTick == -1 || System.currentTimeMillis() - lastDotTick > 600) {
            lastDotTick = System.currentTimeMillis();
            dots = (dots + 1) % 4;
            this.text = this.getText();
        }

        drawCenteredString(matrices, this.client.textRenderer, this.text, this.width / 2, this.height / 2, Color.WHITE);

        super.render(matrices, mouseX, mouseY, delta);
    }

    protected String getText() {
        final StringBuilder builder = new StringBuilder("Migration in progress");
        for (int i = 0; i < dots; i++) builder.append('.');
        return builder.toString();
    }
}
