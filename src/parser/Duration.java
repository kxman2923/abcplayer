package parser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Represents a duration of a note as a fractional multiplier of the default note length.
 */
public class Duration {
    private final int numerator;
    private final int denominator;

    /**
     * Constructs a duration.
     * @param num The numerator.
     * @param den The denominator.
     */
    public Duration (int num, int den){
        numerator = num;
        denominator = den;
    }

    /**
     * The numerator.
     */
    public int getNumerator() {
        return numerator;
    }

    /**
     * The denominator.
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Multiplies two durations.
     * @param other The duration to multiply by.
     * @returns A Duration representing the product of this duration and other.
     */
    public Duration multiply(Duration other) {
        return new Duration(numerator * other.numerator,
                            denominator * other.denominator);
    }

    /**
     * Factory method to produce a Duration object from a string representing a fraction.
     * If the slash and denoninator are missing, the denominator is assumed to be 1.
     * If the numerator is missing, the numerator is assumed to be 1.
     * If the denominator is missing, the denominator is assumed to be 2.
     * If only a slash is specified, the Duration is assumed to be 1/2.
     * @param durationString A string representing a fraction. Must be in the format of:
     [digit] [/ [digit]].
     * @returns The parsed duration object.
     */
    public static Duration fromString(String durationString) {
        Pattern durationPattern = Pattern.compile("(\\d*)(/?)(\\d*)");
        Matcher matcher = durationPattern.matcher(durationString);

        boolean found = matcher.find();
        assert found;
        String numString = matcher.group(1);
        String slash = matcher.group(2);
        String denString = matcher.group(3);

        int numerator;
        int denominator;
        // If numerator not specified, assume 1
        if (numString.isEmpty()) {
            numerator = 1;
        }
        else {
            numerator = Integer.parseInt(numString);
        }
        // No slash is present
        if (slash.isEmpty()) {
            denominator = 1;
        }
        // If slash is present and no denominator is specified, denominator should be 2
        else {
            if (denString.isEmpty()) {
                denominator = 2;
            }
            else {
                denominator = Integer.parseInt(denString);
            }
        }
        return new Duration(numerator, denominator);
    }

    @Override
    public String toString(){
    	return this.numerator+"/"+this.denominator;
    }
}
