package de.superdupermarkt.shelflife.data.view;

/**
 * Interface for handling error reporting.
 * <p>
 * Usually this is done via logging (like java.util.logging or log4j), but in some applications it is desirable
 * to report errors "inline", i.e. at the same place as the actual program output.
 * This is usually the case when errors and warnings do not end execution (e.g. to still provide actionable output).
 * These usually still need some action, so users have to see it during regular work to be able to notify the responsible party.
 * <p>
 * By doing error Reporting via interface, it can also be easily switched out with a different implementation,
 * for example putting them into a database for review by different people.
 * <p>
 * Some logging frameworks can do most of this as well, but they are usually icky to configure and hard to include in test.
 * <p>
 * If instead we used the decorator pattern (not implemented here) multiple ways of reporting errors could also be composed on top of each other.
 */
public interface ErrorReporter {
    void error(Error error);
    void error(Exception ex);

    void warning(Error warning);
    void warning(Exception warning);

    /**
     * Interface encapsulating a concrete Error.
     *
     * Using Error classes instead of strings for parameters enables message independent testing, using error specific fields in subclasses,
     * and even easier i18n translation. Very similar to Exceptions.
     */
    interface Error {
        String getMessage();
    }
}
