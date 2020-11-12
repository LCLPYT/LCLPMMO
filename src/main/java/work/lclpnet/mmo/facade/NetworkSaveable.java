package work.lclpnet.mmo.facade;

import java.util.function.Consumer;

import com.google.gson.JsonElement;

import work.lclpnet.mmo.util.network.LCLPNetwork;

public abstract class NetworkSaveable extends JsonSerializeable {

	protected abstract String getSavePath();
	
	public void save(Consumer<Boolean> callback) {
		postSave(getSavePath(), toJson(), callback);
	}
	
	protected void postSave(String path, JsonElement body, Consumer<Boolean> callback) {
		if(path == null) {
			callback.accept(false);
			return;
		}
		
		LCLPNetwork.post(path, body, response -> {
			if(response.isNoConnection()) {
				callback.accept(null);
				return;
			}

			callback.accept(response.getResponseCode() == 200);
		});
	}
	
}
