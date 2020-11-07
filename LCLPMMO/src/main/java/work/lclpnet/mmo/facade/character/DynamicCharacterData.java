package work.lclpnet.mmo.facade.character;

import work.lclpnet.mmo.facade.DynamicData;
import work.lclpnet.mmo.facade.quest.QuestBook;

public class DynamicCharacterData extends DynamicData {

	public QuestBook questBook = null; // TODO make protected when debug finished
	
	public QuestBook getQuestBook() {
		return questBook;
	}
	
}
