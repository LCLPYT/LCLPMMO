package work.lclpnet.mmo.facade.quest;

import java.util.Objects;

public class QuestStep {

	private final String subIdentifier;
	
	public QuestStep(String subIdentifier) {
		this.subIdentifier = Objects.requireNonNull(subIdentifier);
	}
	
	public String getSubIdentifier() {
		return subIdentifier;
	}
	
}
