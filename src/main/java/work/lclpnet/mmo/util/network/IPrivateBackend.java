package work.lclpnet.mmo.util.network;

public interface IPrivateBackend {

    String getCharacterDataSavePath();

    /* --- */

    IPrivateBackend NONE = new IPrivateBackend() {
        @Override
        public String getCharacterDataSavePath() {
            return null;
        }
    };
}
