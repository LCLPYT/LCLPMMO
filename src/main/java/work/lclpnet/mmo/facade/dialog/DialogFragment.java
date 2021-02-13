package work.lclpnet.mmo.facade.dialog;

import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.List;

public class DialogFragment {

	protected final String translationKey;
	protected final DialogSubstitute[] substitutes;
	
	public DialogFragment(String translationKey, DialogSubstitute... substitutes) {
		this.translationKey = translationKey;
		this.substitutes = substitutes;
	}
	
	public String getTranslationKey() {
		return translationKey;
	}
	
	public DialogSubstitute[] getSubstitutes() {
		return substitutes;
	}
	
	public static class Serializer {

		public static void serialize(DialogFragment f, PacketBuffer buffer) {
			if(DialogFragment.class.equals(f.getClass())) {
				buffer.writeByte(0);
				
				buffer.writeString(f.getTranslationKey());
				buffer.writeInt(f.getSubstitutes().length);
				
				for(int i = 0; i < f.getSubstitutes().length; i++) 
					f.getSubstitutes()[i].serialize(buffer);
			}
			else if(DecisionDialogFragment.class.equals(f.getClass())) {
				DecisionDialogFragment df = (DecisionDialogFragment) f;
				
				buffer.writeByte(1);
				
				buffer.writeString(f.getTranslationKey());
				buffer.writeInt(f.getSubstitutes().length);
				
				for(int i = 0; i < f.getSubstitutes().length; i++) 
					f.getSubstitutes()[i].serialize(buffer);
				
				buffer.writeInt(df.getOptions().size());
				df.getOptions().forEach(option -> {
					buffer.writeString(option.getTranslationKey());	
				});
			}
			else 
				throw new IllegalStateException("Unimplemented class: " + f.getClass().getCanonicalName());
		}
		
		public static DialogFragment deserialize(PacketBuffer buffer) {
			byte id = buffer.readByte();
			if(id == 0) {
				String translationKey = buffer.readString();
				int len = buffer.readInt();
				DialogSubstitute[] substitute = new DialogSubstitute[len];
				for(int i = 0; i < len; i++) 
					substitute[i] = DialogSubstitute.deserialize(buffer);
				
				return new DialogFragment(translationKey, substitute);
			}
			else if(id == 1) {
				String translationKey = buffer.readString();
				int len = buffer.readInt();
				DialogSubstitute[] substitute = new DialogSubstitute[len];
				for(int i = 0; i < len; i++) 
					substitute[i] = DialogSubstitute.deserialize(buffer);
				
				List<DecisionOption> options = new ArrayList<>();
				int size = buffer.readInt();
				for (int i = 0; i < size; i++) {
					String optionTranslationKey = buffer.readString();
					options.add(new DecisionOption(optionTranslationKey));
				}
				
				return new DecisionDialogFragment(translationKey, options, substitute);
			}
			else 
				throw new IllegalStateException("Unimplemented id: " + id);
		}
		
	}
	
}
