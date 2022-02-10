package work.lclpnet.mmo.client.gui;

import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import work.lclpnet.mmo.client.util.Color;
import work.lclpnet.mmo.migrator.WorldMigrator;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorldMigrationScreen extends MMOScreen implements WorldMigrator.ProgressListener {

    protected final LevelStorage.Session session;
    protected final AtomicBoolean aBoolean = new AtomicBoolean(false);
    protected long lastDotTick = -1;
    protected int dots = 3;
    protected final EditWorldScreen parent;
    protected int steps = 0, currentStep = 0;
    protected Text text = getText();
    protected String progressText = getProgressText();
    protected int percentHash = 0;

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
        WorldMigrator.create(Objects.requireNonNull(client), session, this)
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

        drawMultiLineCenteredString(matrices, this.client.textRenderer, this.text, 1.5F, this.width / 2, this.height / 2 - 20, Color.AQUA);
        drawCenteredString(matrices, this.client.textRenderer, this.progressText, this.width / 2, this.height / 2, Color.WHITE);

        super.render(matrices, mouseX, mouseY, delta);
    }

    protected Text getText() {
        final StringBuilder builder = new StringBuilder("Migration in progress");
        for (int i = 0; i < dots; i++) builder.append('.');
        return new LiteralText(builder.toString());
    }

    protected String getProgressText() {
        StringBuilder builder = new StringBuilder(String.format("%.2f%% done", percentHash / 10e+2));
        if (steps > 0) builder.append(String.format(" (Dimension %s/%s)", currentStep, steps));
        return builder.toString();
    }

    @Override
    public void setSteps(int steps) {
        this.steps = steps;
    }

    @Override
    public void updateCurrentStep(int currentStep) {
        this.currentStep = currentStep;
        this.percentHash = 0;
        this.progressText = getProgressText();
    }

    @Override
    public void updateProgress(float progress) {
        int hash = (int) (progress * 10e+4);

        if (hash > percentHash) {
            percentHash = hash;
            this.progressText = getProgressText();
        }
    }
}
