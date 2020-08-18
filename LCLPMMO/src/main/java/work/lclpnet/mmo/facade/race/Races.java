package work.lclpnet.mmo.facade.race;

import java.util.ArrayList;
import java.util.List;

public class Races {

	private static List<Race> races = new ArrayList<>();
	
	static {
		races.add(new RaceHuman());
		races.add(new RaceDwarf());
	}
	
	public static List<Race> getRaces() {
		return races;
	}
	
}
