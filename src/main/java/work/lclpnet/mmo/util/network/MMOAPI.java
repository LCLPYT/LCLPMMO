package work.lclpnet.mmo.util.network;

import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.ext.LCLPMinecraftAPI;

public class MMOAPI extends LCLPMinecraftAPI {

    public static final MMOAPI PUBLIC = new MMOAPI(APIAccess.PUBLIC);

    /**
     * Construct a new MMOAPI object.
     *
     * @param access The API accessor to use.
     */
    public MMOAPI(APIAccess access) {
        super(access);
    }


}
