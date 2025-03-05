package com.joelodom;

public class QEDemonstration {
    public static void main(String[] args) {
        System.out.println(Strings.WELCOME_MESSAGE);
        printStatus();
        DatabaseManagement.createDatabaseWithTestCollection();
    }

    private static void printStatus() {
        System.out.println("MONGODB_URI: " + Env.MONGODB_URI);
        System.out.println("SHARED_LIB_PATH: " + Env.SHARED_LIB_PATH);
    }
}
