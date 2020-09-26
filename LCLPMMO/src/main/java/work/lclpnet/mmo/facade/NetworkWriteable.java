package work.lclpnet.mmo.facade;

import java.util.function.Consumer;

import work.lclpnet.mmo.util.LCLPNetwork;

public abstract class NetworkWriteable extends JsonSerializeable {

	protected abstract String getSavePath();
	
	public void save(Consumer<Boolean> callback) {
		save(getSavePath(), callback);
	}
	
	protected void save(String path, Consumer<Boolean> callback) {
		if(path == null) {
			callback.accept(false);
			return;
		}
		
		LCLPNetwork.post(path, toJson(), response -> {
			if(response.isNoConnection()) {
				callback.accept(null);
				return;
			}

			callback.accept(response.getResponseCode() == 200);
		});
	}
	
}
