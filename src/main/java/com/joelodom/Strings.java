package com.joelodom;

/**
 * This is just a data class for strings used elsewhere. Nothing much to see
 * here.
 */

public final class Strings {

    public static final String WELCOME_MESSAGE =
"""
Welcome to the Queryable Encryption demonstration by Joel Odom. Please
see the README.md file.
""";

    public static final String HELP_MESSAGE =
"""
  exit                  Exit the application.
  help                  Show this help message.
  status                Show the status of pretty much everything.
  create-collection     Creates the test database.
  destroy-database      Destroys the test database.
  add-members [number]  Insert [number] random member records.
  find-one              Query for one member record (semi-randomly).
  demonstrate-query     Demonstrate an equality query on encrypted data.
""";
}
