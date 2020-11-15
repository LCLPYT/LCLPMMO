package work.lclpnet.mmo.facade.dialog;

import java.util.List;

public class DecisionDialogFragment extends DialogFragment {

	protected List<DecisionOption> options;
	
	public DecisionDialogFragment(String questionTranslationKey, List<DecisionOption> options) {
		super(questionTranslationKey);
		this.options = options;
	}

	public List<DecisionOption> getOptions() {
		return options;
	}
	
}
