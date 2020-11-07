package work.lclpnet.mmo.facade.race;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Races {

	private static List<MMORace> races = new ArrayList<>();
	
	static {
		register(new RaceHuman());
		register(new RaceDwarf());
	}
	
	private static MMORace register(MMORace race) {
		if(!races.stream().map(MMORace::toString).noneMatch(race.getUnlocalizedName()::equals)) 
			throw new IllegalArgumentException(String.format("Race with name '%s' already registered.", race.getUnlocalizedName()));
		
		races.add(race);
		return race;
	}
	
	public static List<MMORace> getRaces() {
		return races;
	}
	
	public static MMORace getByName(String unlocalizedName) {
		return races.stream()
				.filter(r -> r.getUnlocalizedName().equalsIgnoreCase(unlocalizedName))
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException(String.format("Race with name '%s' is not registered.", unlocalizedName)));
	}
	
	public static MMORace getNullableByName(String unlocalizedName) {
		try {
			return getByName(unlocalizedName);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
}
