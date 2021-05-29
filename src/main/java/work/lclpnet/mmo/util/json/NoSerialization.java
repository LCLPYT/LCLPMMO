package work.lclpnet.mmo.util.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.util.DistSpecifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate a field with this annotation to make it invisible in the JSON tree created with {@link MMOGson}.
 * The {@link JsonSerializeable} type also makes use of it.<br>
 * <br>
 * You may also specify a {@link DistSpecifier} in which this annotation should work and in which not (see {@link #in()}).
 *
 * @author LCLP
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface NoSerialization {

    /**
     * Set a {@link DistSpecifier} to specify in which distributions this annotation should work and in which not.<br>
     * <br>
     * <strong>Default:</strong> {@link DistSpecifier#ALL}
     */
    DistSpecifier in() default DistSpecifier.ALL;

    class Strategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            NoSerialization annotation = f.getAnnotation(NoSerialization.class);
            if (annotation == null) return false;

            return annotation.in().isApplicable();
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
