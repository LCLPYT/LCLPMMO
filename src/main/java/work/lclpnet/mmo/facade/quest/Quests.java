package work.lclpnet.mmo.facade.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Quests {

    private static final List<Quest> quests = new ArrayList<>();

    static {
        // Start quest
        builder().identifier("start").beginSequence()
                .then("find_spawn").then("find_shop")
                .endSequence().build();
    }

    private static QuestBuilder builder() {
        return new QuestBuilder().setBuildCallback(quests::add);
    }

    public static List<Quest> getQuests() {
        return quests;
    }

    public static Quest getQuestByIdentifier(String identifier) {
        return quests.stream()
                .filter(q -> q.getIdentifier().equals(identifier))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no registered request with identifier '%s'", identifier)));
    }

    public static Quest getNullableQuestByIdentifier(String identifier) {
        try {
            return getQuestByIdentifier(identifier);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
