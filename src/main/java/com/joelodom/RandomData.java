package com.joelodom;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * I won't document this utitily class in detail. It's just for creating random
 * data for demonstration purposes.
 */
public class RandomData {

    public final static String NO_SSN = "000-00-0000"; // reserved SSN
    public final static int CHANCE_OF_NO_SSN = 1; // percent

    public final static int MAX_AGE = 150; // optimism!
    public final static double AGE_MEAN = 50.0;
    public final static double AGE_STD_DEV = 18.0;

    private final static List<String> NAMES = List.of(
            "Alice",
            "Bob",
            "Charlie",
            "David",
            "Eve",
            "Frank",
            "Grace",
            "Heidi",
            "Ivan",
            "Judy",
            "Karl",
            "Laura",
            "Mallory",
            "Niaj",
            "Olivia",
            "Peggy",
            "Quinn",
            "Rupert",
            "Sybil",
            "Trent",
            "Uma",
            "Victor",
            "Walter",
            "Xena",
            "Yvonne",
            "Zach"
    );

    public static String generateRandomName() {
        return NAMES.get(ThreadLocalRandom.current().nextInt(NAMES.size()));
    }

    public static String generateRandomFullName() {
        return generateRandomName() + " " + generateRandomName();
    }

    public static String generateRandomSSN() {
        int area = 0;
        int group = 0;
        int serial = 0;

        /**
         * We have reserved NO_SSN for reconds that have NO_SSN. This is nice
         * because it's a reserved number in real life and because QE uses
         * non-detrministic encryption, the records with NO_SSN are
         * indistinguishable from those with an SSN.
         */
        if (ThreadLocalRandom.current().nextInt(100) > CHANCE_OF_NO_SSN) {
            area = ThreadLocalRandom.current().nextInt(100, 1000);
            group = ThreadLocalRandom.current().nextInt(10, 100);
            serial = ThreadLocalRandom.current().nextInt(1000, 10000);
        }

        return String.format("%03d-%02d-%04d", area, group, serial);
    }

    public static int generateRandomAge() {
        int age = (int) Math.round(
            AGE_MEAN + AGE_STD_DEV * ThreadLocalRandom.current().nextGaussian());
        
        // Clamp the age between 0 and 100
        if (age < 0) {
            age = 0;
        } else if (age > MAX_AGE) {
            age = MAX_AGE;
        }

        return age;
    }
}
