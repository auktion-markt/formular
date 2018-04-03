package de.auktionmarkt.formular.test;

import de.auktionmarkt.formular.test.matchers.RegexMatcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomMatchers {

    public static RegexMatcher matchesPattern(String pattern) {
        return new RegexMatcher(Pattern.compile(pattern));
    }
}
