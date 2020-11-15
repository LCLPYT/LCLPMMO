package work.lclpnet.mmo.facade.dialog;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.PacketBuffer;

public class DialogFragment {

	protected String translationKey;
	
	public DialogFragment(String translationKey) {
		this.translationKey = translationKey;
	}
	
	public String getTranslationKey() {
		return translationKey;
	}

	public static class Serializer {

		public static void serialize(DialogFragment f, PacketBuffer buffer) {
			if(DialogFragment.class.equals(f.getClass())) {
				buffer.writeByte(0);
				buffer.writeString(f.getTranslationKey());
			}
			else if(DecisionDialogFragment.class.equals(f.getClass())) {
				buffer.writeByte(1);
				buffer.writeString(f.getTranslationKey());
				
				DecisionDialogFragment df = (DecisionDialogFragment) f;
				List<DecisionOption> options = df.getOptions();
				
				buffer.writeInt(options.size());
				options.forEach(option -> {
					buffer.writeString(option.getTranslationKey());	
				});
			}
			else throw new IllegalStateException("Unimplemented class: " + f.getClass().getCanonicalName());
		}
		
		public static DialogFragment deserialize(PacketBuffer buffer) {
			byte id = buffer.readByte();
			if(id == 0) {
				String translationKey = buffer.readString();
				return new DialogFragment(translationKey);
			}
			else if(id == 1) {
				String translationKey = buffer.readString();
				List<DecisionOption> options = new ArrayList<>();
				int size = buffer.readInt();
				for (int i = 0; i < size; i++) {
					String optionTranslationKey = buffer.readString();
					options.add(new DecisionOption(optionTranslationKey));
				}
				return new DecisionDialogFragment(translationKey, options);
			}
			else throw new IllegalStateException("Unimplemented id: " + id);
		}
		
	}
	
}
