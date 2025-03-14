package com.joelodom;

/**
 * This is to demonstrate how $lookup and QE behave together.
 * Comments are inline.
 * 
 * TODO: Reference new documentation here.
 */

public final class LookupDemonstration {
    public static void lookupDemonstration() {
        /**
         * This demonstration is intended to be run with a clean database,
         * so first we create the members collection and add some members.
         * If the database is already created, destroy it first.
         */

        System.out.println("Creating members collection...");
        DatabaseManagement.createEncryptedCollection();

        final int MEMBERS_TO_ADD = 5000;
        System.out.println("Adding " + MEMBERS_TO_ADD + " members...");
        Members.addRandomMembers(MEMBERS_TO_ADD);

        /**
         * TODO
         */

        System.out.println();
    }
}
