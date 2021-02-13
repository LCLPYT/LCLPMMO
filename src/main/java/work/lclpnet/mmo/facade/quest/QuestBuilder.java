package work.lclpnet.mmo.facade.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class QuestBuilder {

	private String identifier = null;
	private List<QuestStep> sequence = null;
	private Consumer<Quest> buildCallback = null;
	
	public QuestBuilder setBuildCallback(Consumer<Quest> callback) {
		this.buildCallback = callback;
		return this;
	}
	
	public QuestBuilder identifier(String identifier) {
		this.identifier = identifier;
		return this;
	}
	
	public QuestSequenceBuilder beginSequence() {
		return new QuestSequenceBuilder(this);
	}
	
	public Quest build() {
		Objects.requireNonNull(identifier, "Set an identifier first.");
		Objects.requireNonNull(sequence, "Begin a sequence first.");
		Quest quest = new Quest(identifier, sequence);
		if(buildCallback != null) buildCallback.accept(quest);
		return quest;
	}
	
	public static class QuestSequenceBuilder {
		
		private final QuestBuilder parent;
		private final List<QuestStep> steps = new ArrayList<>();
		
		public QuestSequenceBuilder(QuestBuilder builder) {
			this.parent = builder;
		}
		
		public QuestSequenceBuilder then(String subIdent) {
			QuestStep step = new QuestStep(subIdent);
			steps.add(step);
			return this;
		}
		
		public QuestBuilder endSequence() {
			parent.sequence = this.steps;
			return parent;
		}
		
	}

}
