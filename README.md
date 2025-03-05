# mongodb-queryable-encryption
A demonstration and sandbox for MongoDB Queryable Encryption.

## First things first

### Installing MongoDB

For this to work, we'll need MongoDB 8.0 or later. I'm going to use my Atlas
instance, but you could also point to a local instance.
See https://www.mongodb.com/docs/manual/installation/ and
https://www.mongodb.com/docs/atlas/getting-started/ (I like using the
Atlas UI).

### Configuring your environment

See env_template in the root directory of this project and copy it to
a new file named .env. Configure your connection parameters in that file.
If you're new to MongoDB, you can find more about connecting to MongoDB
Atlas at https://www.mongodb.com/docs/atlas/tutorial/connect-to-your-cluster/.

### Other dependencies

You'll need to install a JDK, which I won't explain in detail here.
I use gradle as my build system. If you're new to gradle (as I am), see
https://docs.gradle.org/current/userguide/installation.html.

### Building and running the project

**EXPLAIN THIS HERE**

`./gradlew run`

## Closing thoughts

The best place to go for help is https://www.mongodb.com/community/forums/.
If you get too stuck, you can try to contact me at joel.odom@mongodb.com, but
please understand that I'm unable to respond to every question and I tend
to be really slow to reply to emails.
