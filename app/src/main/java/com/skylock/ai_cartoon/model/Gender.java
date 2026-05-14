package com.skylock.ai_cartoon.model;

public enum Gender {

    MALE("man"),
    FEMALE("woman"),
    OTHER("other"),
    NONE("none");

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------

    private final String value;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------

    Gender(String value) {
        this.value = value;
    }

    // ----------------------------------------------------------------
    // Getter
    // ----------------------------------------------------------------

    public String getValue() {
        return value;
    }

    // ----------------------------------------------------------------
    // Companion — mirrors Kotlin's Gender.Companion.fromValue()
    // Maps API string values back to enum constants.
    // Accepts both display strings ("man"/"woman") and raw names
    // ("male"/"female") — exactly as the original bytecode did.
    // Falls back to OTHER for any unrecognised input.
    // ----------------------------------------------------------------

    public static final class Companion {

        private static final Companion INSTANCE = new Companion();

        private Companion() {}

        public static Companion getInstance() {
            return INSTANCE;
        }

        /**
         * Converts an API value string to the corresponding {@link Gender}.
         *
         * Recognised inputs (case-sensitive, matching original bytecode):
         * <ul>
         *   <li>"man"    → {@link Gender#MALE}</li>
         *   <li>"male"   → {@link Gender#MALE}</li>
         *   <li>"woman"  → {@link Gender#FEMALE}</li>
         *   <li>"female" → {@link Gender#FEMALE}</li>
         *   <li>"none"   → {@link Gender#NONE}</li>
         *   <li>anything else → {@link Gender#OTHER}</li>
         * </ul>
         *
         * @param value non-null string to look up
         * @return matching {@link Gender}, never null
         */
        public Gender fromValue(String value) {
            if (value == null) throw new NullPointerException("value must not be null");

            switch (value) {
                case "man":
                case "male":
                    return MALE;

                case "woman":
                case "female":
                    return FEMALE;

                case "none":
                    return NONE;

                default:
                    return OTHER;
            }
        }
    }

    // ----------------------------------------------------------------
    // Convenience accessor — use Gender.fromValue("man") directly
    // ----------------------------------------------------------------

    /**
     * Shorthand so callers don't need to go through {@link Companion}:
     * <pre>Gender g = Gender.fromValue("woman"); // → FEMALE</pre>
     */
    public static Gender fromValue(String value) {
        return Companion.getInstance().fromValue(value);
    }
}