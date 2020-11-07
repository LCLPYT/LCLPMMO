package work.lclpnet.mmo.util.json;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import work.lclpnet.mmo.util.DistSpecifier;

@Retention(RUNTIME)
@Target(FIELD)
public @interface NoSerialization {

	DistSpecifier in() default DistSpecifier.ALL;

	public static class Strategy implements ExclusionStrategy {

		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			NoSerialization annotation = f.getAnnotation(NoSerialization.class);
			if(annotation == null) return false;
			
			return annotation.in().isApplicable();
		}
		
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

	}
	
}
