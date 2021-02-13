package work.lclpnet.mmo.facade.quest;

import work.lclpnet.mmo.facade.JsonSerializeable;

import java.util.ArrayList;
import java.util.List;

public class QuestBook {

	private final List<QuestState> activeQuests = new ArrayList<>();
	private QuestState currentQuest = null;

	public QuestState getCurrentQuest() {
		return currentQuest;
	}
	
	public void setCurrentQuest(QuestState currentQuest) {
		this.currentQuest = currentQuest;
	}

	public List<QuestState> getActiveQuests() {
		return activeQuests;
	}
	
	@Override
	public String toString() {
		return JsonSerializeable.stringify(this);
	}
	
}
