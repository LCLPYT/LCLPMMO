package work.lclpnet.mmo.facade.character;

import work.lclpnet.mmo.facade.DynamicData;
import work.lclpnet.mmo.facade.quest.QuestBook;

public class DynamicCharacterData extends DynamicData {

	private QuestBook questBook;
	
	public DynamicCharacterData(QuestBook questBook) {
		this.questBook = questBook;
	}
	
	public QuestBook getQuestBook() {
		return questBook;
	}

	public static DynamicCharacterData empty() {
		return new DynamicCharacterData(new QuestBook());
	}
	
}