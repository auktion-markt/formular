package de.auktionmarkt.formular.test.matchers;

import lombok.RequiredArgsConstructor;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegexMatcher extends BaseMatcher<CharSequence> {

    private final Pattern pattern;

    @Override
    public boolean matches(Object item) {
        CharSequence str = (CharSequence) item;
        return pattern.matcher(str).find();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Match of regular expression").appendValue(pattern).appendText("expected");
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText("but value").appendValue(item).appendText("does not match");
    }
}
