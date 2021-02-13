package work.lclpnet.mmo.facade.quest;

import java.util.Objects;

public class QuestState {

	private final Quest quest;
	private final int step = 0;
	
	public QuestState(Quest quest) {
		this.quest = Objects.requireNonNull(quest);
	}
	
	public Quest getQuest() {
		return quest;
	}
	
	public int getStep() {
		return step;
	}
	
}
