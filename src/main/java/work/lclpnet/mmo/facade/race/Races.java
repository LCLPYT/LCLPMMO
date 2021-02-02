package work.lclpnet.mmo.facade.race;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class Races {

	private static Set<MMORace> races = new HashSet<>();
	
	public static final RaceHuman HUMAN = register(new RaceHuman());
	public static final RaceDwarf DWARF = register(new RaceDwarf());
	
	private static <T extends MMORace> T register(T race) {
		if(!races.stream().map(MMORace::toString).noneMatch(race.getUnlocalizedName()::equals)) 
			throw new IllegalArgumentException(String.format("Race with name '%s' already registered.", race.getUnlocalizedName()));
		
		races.add(race);
		return race;
	}
	
	public static Set<MMORace> getRaces() {
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
