package work.lclpnet.mmo.util;

import java.util.List;
import java.util.Objects;

public class ValidationViolations {

	private final List<ElementError> violations;
	
	public ValidationViolations(List<ElementError> violations) {
		this.violations = Objects.requireNonNull(violations);
	}
	
	public List<ElementError> getViolations() {
		return violations;
	}
	
	public boolean has(String element, String error) {
		ElementError err = null;
		for(ElementError e : violations) {
			if(e.getElement().equals(element)) {
				err = e;
				break;
			}
		}
		if(err == null) return false;
		
		for(String s : err.getErrors()) {
			if(s.equalsIgnoreCase(error)) return true;
		}
		return false;
	}

	public String getFirst() {
		if(violations.isEmpty()) return "error";
		
		ElementError eErr = violations.get(0);
		if(eErr == null) return "error";
		
		List<String> errs = eErr.getErrors();
		if(errs.isEmpty()) return "error";
		
		return errs.get(0);
	}
	
}
