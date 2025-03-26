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

This application simulates a simple gym membership management application.
Imagine that you own a gym and you have members that you need to manage.

Type "help" to see available commands. It isn't very forgiving if you make a
mistake because it's a demonstration. Sorry about that.
""";

    public static final String HELP_MESSAGE =
"""
  exit                  Exit the application.
  help                  Show this help message.
  status                Show the status of pretty much everything.
  create-collection     Creates the database. Run this on first use.
  destroy-database      Destroys the test database.
  add-members [number]  Insert [number] random member records.
  find-one              Query for one member record (semi-randomly).
  no-ssn-query          Query for members who don't have an SSN on file.
  age-query [age]       Query for members who are [age] or younger.
  compound-query        Query for members who are 18+ with no SSN on file.
""";
}
