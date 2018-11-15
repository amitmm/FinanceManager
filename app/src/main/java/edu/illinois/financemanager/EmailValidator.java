package edu.illinois.financemanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Written with the assistance of:
 * http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
 */
public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern;

    public EmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    /**
     * Determines if test matches a valid email address pattern
     * @param test - String to compare
     * @return boolean indicating if test matches a valid email address
     */
    public boolean validate(String test) {
        Matcher matcher = pattern.matcher(test);
        return matcher.matches();
    }
}
