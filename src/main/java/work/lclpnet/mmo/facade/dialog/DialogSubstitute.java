package work.lclpnet.mmo.facade.dialog;

import net.minecraft.network.PacketBuffer;

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

	public void serialize(PacketBuffer buffer) {
		buffer.writeString(this.substitute);
		buffer.writeBoolean(this.translationKey);
	}

	public static DialogSubstitute deserialize(PacketBuffer buffer) {
		String substitute = buffer.readString();
		boolean translationKey = buffer.readBoolean();
		return new DialogSubstitute(substitute, translationKey);
	}
	
}
