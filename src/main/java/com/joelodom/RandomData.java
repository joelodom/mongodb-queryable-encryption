package com.joelodom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public static final int NUMBER_OF_ZIP_CODES = 10;
    public static List<String> zipList; // our gym locations

    static {
        final int MIN_ZIP = 501;   // Represents "00501"
        final int MAX_ZIP = 99950; // The highest valid ZIP code

        Set<String> pickSet = new HashSet<>(NUMBER_OF_ZIP_CODES);

        while (pickSet.size() < NUMBER_OF_ZIP_CODES) {
            int zipCode = ThreadLocalRandom.current().nextInt(
                    MIN_ZIP, MAX_ZIP + 1);
            pickSet.add(String.format("%05d", zipCode));
        }

        zipList = new ArrayList<>(pickSet);
    }

    public static String generateRandomZipCode() {
        int index = ThreadLocalRandom.current().nextInt(zipList.size());
        return zipList.get(index);
    }

    private static final String[] CITIES = {
        "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
        "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose",
        "Austin", "Jacksonville", "Fort Worth", "Columbus", "Charlotte",
        "San Francisco", "Indianapolis", "Seattle", "Denver", "Washington",
        "Boston", "El Paso", "Detroit", "Nashville", "Portland", "Memphis",
        "Oklahoma City", "Las Vegas", "Louisville", "Baltimore",
        "Milwaukee", "Albuquerque", "Tucson", "Fresno", "Sacramento",
        "Kansas City", "Mesa", "Atlanta", "Omaha", "Raleigh",
        "Colorado Springs", "Miami", "Long Beach", "Virginia Beach", "Oakland",
        "Minneapolis", "Tulsa", "Wichita", "New Orleans", "Arlington",
        "London", "Paris", "Berlin", "Madrid", "Rome", "Vienna", "Barcelona",
        "Amsterdam", "Brussels", "Munich",
        "Milan", "Prague", "Warsaw", "Lisbon", "Dublin", "Zurich", "Stockholm",
        "Copenhagen", "Oslo", "Helsinki",
        "Manchester", "Edinburgh", "Birmingham", "Glasgow", "Naples", "Seville",
        "Valencia", "Bordeaux", "Lyon", "Marseille",
        "Frankfurt", "Hamburg", "Geneva", "Athens",
        "Sydney", "Melbourne", "Brisbane", "Perth", "Adelaide", "Gold Coast",
        "Canberra", "Newcastle", "Wollongong", "Geelong",
        "Hobart", "Townsville", "Cairns", "Toowoomba", "Ballarat"
    };

    public static String getRandomCity() {
        int index = ThreadLocalRandom.current().nextInt(CITIES.length);
        return CITIES[index];
    }
}
