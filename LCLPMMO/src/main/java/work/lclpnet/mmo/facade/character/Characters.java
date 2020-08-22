package work.lclpnet.mmo.facade.character;

import java.util.ArrayList;
import java.util.List;

public class Characters {

	private static List<MMOCharacter> characters = new ArrayList<>();

	public static List<MMOCharacter> getCharacters() {
		return characters;
	}

	public static MMOCharacter getByName(String name) {
		for(MMOCharacter mmo : characters) 
			if(mmo.getTitle().getUnformattedComponentText().equalsIgnoreCase(name)) 
				return mmo;
		
		return null;
	}

	public static boolean doesNameExist(String name) {
		return getByName(name) != null;
	}

}
