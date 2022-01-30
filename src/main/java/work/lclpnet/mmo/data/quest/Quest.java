package work.lclpnet.mmo.data.quest;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import work.lclpnet.lclpnetwork.model.JsonSerializable;
import work.lclpnet.mmo.util.json.EasyTypeAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Quest {

    private final String identifier;
    private final List<QuestStep> sequence;

    Quest(String identifier, List<QuestStep> sequence) {
        this.identifier = Objects.requireNonNull(identifier);
        this.sequence = Objects.requireNonNull(sequence);
        if (this.sequence.isEmpty())
            throw new IllegalArgumentException("The quest sequence must consist of at least one step.");
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<QuestStep> getSequence() {
        return sequence;
    }

    public QuestState newState() {
        return new QuestState(this);
    }

    @Override
    public String toString() {
        return JsonSerializable.stringify(this);
    }

    public static class Adapter extends EasyTypeAdapter<Quest> {

        public Adapter() {
            super(Quest.class);
        }

        @Override
        public void write(JsonWriter out, Quest value) throws IOException {
            out.beginObject();

            addField("identifier", out, w -> w.value(value.identifier));

            out.endObject();
        }

        @Override
        public Quest read(JsonObject json) {
            String identifier = json.get("identifier").getAsString();
            return Quests.getQuestByIdentifier(identifier);
        }
    }
}
