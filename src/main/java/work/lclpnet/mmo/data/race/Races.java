package work.lclpnet.mmo.data.race;

import work.lclpnet.mmo.data.race.demihuman.*;
import work.lclpnet.mmo.data.race.heteromorph.*;
import work.lclpnet.mmo.data.race.humanoid.*;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class Races {

    private static final Set<MMORace> races = new HashSet<>();

    public static final RaceHuman HUMAN = register(new RaceHuman());
    public static final RaceDwarf DWARF = register(new RaceDwarf());
    public static final RaceElven ELVEN = register(new RaceElven());
    public static final RaceDarkElven DARK_ELVEN = register(new RaceDarkElven());
    public static final RaceDarkDwarf DARK_DWARF = register(new RaceDarkDwarf());
    public static final RaceHighElven HIGH_ELVEN = register(new RaceHighElven());
    public static final RaceHalfElven HALF_ELVEN = register(new RaceHalfElven());
    public static final RaceBeastTamer BEAST_TAMER = register(new RaceBeastTamer());
    public static final RaceWingedHuman WINGED_HUMAN = register(new RaceWingedHuman());

    public static final RaceOrc ORC = register(new RaceOrc());
    public static final RaceTroll TROLL = register(new RaceTroll());
    public static final RaceLizardman LIZARDMAN = register(new RaceLizardman());
    public static final RaceGhul GHUL = register(new RaceGhul());
    public static final RaceHalfGiant HALF_GIANT = register(new RaceHalfGiant());
    public static final RaceWerewolf WEREWOLF = register(new RaceWerewolf());
    public static final RaceGoblin GOBLIN = register(new RaceGoblin());
    public static final RaceBeastman BEASTMAN = register(new RaceBeastman());
    public static final RaceOgre OGRE = register(new RaceOgre());

    public static final RaceAngel ANGEL = register(new RaceAngel());
    public static final RaceGhost GHOST = register(new RaceGhost());
    public static final RaceDemon DEMON = register(new RaceDemon());
    public static final RaceVampire VAMPIRE = register(new RaceVampire());
    public static final RaceSlime SLIME = register(new RaceSlime());
    public static final RaceDragonborn DRAGONBORN = register(new RaceDragonborn());
    public static final RaceHalfGolem HALF_GOLEM = register(new RaceHalfGolem());
    public static final RaceWitch WITCH = register(new RaceWitch());
    public static final RaceUndead UNDEAD = register(new RaceUndead());

    private static <T extends MMORace> T register(T race) {
        if (races.stream().map(MMORace::toString).anyMatch(race.getUnlocalizedName()::equals))
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

    @Nullable
    public static MMORace getByNameNullable(String unlocalizedName) {
        try {
            return getByName(unlocalizedName);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
