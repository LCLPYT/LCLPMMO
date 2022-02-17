package work.lclpnet.mmo.client.gui.character;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import work.lclpnet.mmo.client.gui.select.IMMOSelectionItem;
import work.lclpnet.mmo.data.character.MMOCharacter;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CharacterSelectionItem implements IMMOSelectionItem {

    protected final MMOCharacter character;

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
    public String getUnlocalizedName() {
        return character.getUnlocalizedName();
    }
}
