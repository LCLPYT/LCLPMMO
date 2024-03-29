package work.lclpnet.mmo.data.character;

import com.google.gson.annotations.Expose;
import work.lclpnet.mmo.data.DynamicData;
import work.lclpnet.mmo.data.quest.QuestBook;

public class DynamicCharacterData extends DynamicData {

    /*
     *  Make sure the JSON representation of this object is as small as possible.
     *  If possible, set default values to null.
     *  Also make sure field names aren't too long.
     */
    @Expose
    public Integer tutorialState = null;
    @Expose
    private final QuestBook questBook = null;

    public QuestBook getQuestBook() {
        return questBook;
    }

    public static DynamicCharacterData empty() {
        return new DynamicCharacterData();
    }
}
