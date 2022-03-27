package work.lclpnet.mmo.client.gui.character;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.client.gui.select.IMMOSelectionItem;
import work.lclpnet.mmo.data.character.MMOCharacter;

import javax.annotation.Nonnull;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public record CharacterSelectionItem(MMOCharacter character) implements IMMOSelectionItem {

    public CharacterSelectionItem(MMOCharacter character) {
        this.character = Objects.requireNonNull(character);
    }

    @Nonnull
    public MMOCharacter getCharacter() {
        return character;
    }

    @Override
    public Text getTitle() {
        return new LiteralText(character.getName());
    }

    @Override
    public Identifier getIcon() {
        return character.getRace().getIcon();
    }

    @Override
    public Text getFirstLine() {
        return character.getRace().getTitle();
    }

    // second line (in the future): maybe some information about level etc.

    @Override
    public String getUnlocalizedName() {
        return character.getUnlocalizedName();
    }
}
