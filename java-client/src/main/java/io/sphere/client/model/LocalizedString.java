package io.sphere.client.model;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.codehaus.jackson.annotate.JsonCreator;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper around an attribute which can be translated into a number of locales.
 * Note that even if your project only uses one language some attributes (name and description for example) will be
 * always be LocalizableStrings.
 */
public class LocalizedString {

    private Map<Locale, String> strings;

    @JsonCreator
    public LocalizedString(Map<Locale, String> strings){
        this.strings = strings;
    }

    /**
     * @param loc
     * @return The raw map lookup value which may be null.
     */
    public String getRaw(Locale loc){
        return strings.get(loc);
    }

    /**
     * Null-safe variant of `getRaw`. Tries to retrieve the translation for the specified locale
     * but if that is null or the empty string it will use a random translation. If there are no translation it will
     * return the empty string.
     * @param loc
     * @return
     */
    @Nonnull public String get(Locale loc){
        String output = strings.get(loc);
        if (Strings.isNullOrEmpty(output)){
            Set<Locale> locales = strings.keySet();
            if (locales.isEmpty()){
                output = "";
            }
            else {
                loc = Iterables.get(locales, 0);
                output = strings.get(loc);
            }
        }
        return output;
    }

    /**
     * @return If the localized string contains only one translation it returns this. If there are more than one
     * a random one will be returned. If there are no translations an empty string is returned.
     */
    @Nonnull public String get(){ return get(Locale.ENGLISH); }

    @Override public String toString(){
        return "[LocalizedString " + strings.toString() + "]";
    }
}
