package com.joelodom;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * I won't document this utitily class in detail. It's just for creating random
 * data for demonstration purposes.
 */

public class RandomData {
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
        int area = ThreadLocalRandom.current().nextInt(100, 1000);
        int group = ThreadLocalRandom.current().nextInt(10, 100);
        int serial = ThreadLocalRandom.current().nextInt(1000, 10000);
        
        return String.format("%03d-%02d-%04d", area, group, serial);
    }
}
