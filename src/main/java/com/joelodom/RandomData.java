package com.joelodom;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
}
