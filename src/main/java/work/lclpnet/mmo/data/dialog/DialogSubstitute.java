package work.lclpnet.mmo.data.dialog;

import net.minecraft.network.PacketByteBuf;

public class DialogSubstitute {

    protected final String substitute;
    protected final boolean translationKey;

    public DialogSubstitute(String substitute, boolean translationKey) {
        this.substitute = substitute;
        this.translationKey = translationKey;
    }

    public String getSubstitute() {
        return substitute;
    }

    public boolean isTranslationKey() {
        return translationKey;
    }

    public void serialize(PacketByteBuf buffer) {
        buffer.writeString(this.substitute);
        buffer.writeBoolean(this.translationKey);
    }

    public static DialogSubstitute deserialize(PacketByteBuf buffer) {
        String substitute = buffer.readString();
        boolean translationKey = buffer.readBoolean();
        return new DialogSubstitute(substitute, translationKey);
    }
}
