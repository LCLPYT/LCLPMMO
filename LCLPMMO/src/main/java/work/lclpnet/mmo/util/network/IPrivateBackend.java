package work.lclpnet.mmo.util.network;

public interface IPrivateBackend {

	String getCharacterSavePath();

	/* --- */
	
	public static IPrivateBackend NONE = new IPrivateBackend() {
		@Override
		public String getCharacterSavePath() {
			return null;
		}
	};
	
}
