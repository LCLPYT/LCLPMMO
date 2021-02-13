package work.lclpnet.mmo.facade.dialog;

import java.util.List;

public class DecisionDialogFragment extends DialogFragment {

	protected final List<DecisionOption> options;
	
	public DecisionDialogFragment(String questionTranslationKey, List<DecisionOption> options, DialogSubstitute... substitutes) {
		super(questionTranslationKey, substitutes);
		this.options = options;
	}

	public List<DecisionOption> getOptions() {
		return options;
	}
	
}
