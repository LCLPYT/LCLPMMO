package work.lclpnet.mmo.facade.quest;

import java.util.List;
import java.util.Objects;

import work.lclpnet.mmo.facade.JsonSerializeable;

public class Quest {

	private String identifier;
	private List<QuestStep> sequence;
	
	Quest(String identifier, List<QuestStep> sequence) {
		this.identifier = Objects.requireNonNull(identifier);
		this.sequence = Objects.requireNonNull(sequence);
		if(this.sequence.isEmpty()) throw new IllegalArgumentException("The quest sequence must consist of at least one step.");
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
		return JsonSerializeable.stringify(this);
	}
	
}
