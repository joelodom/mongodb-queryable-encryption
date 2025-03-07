# mongodb-queryable-encryption
A demonstration and sandbox for MongoDB Queryable Encryption.

## First things first

### Installing MongoDB

For this to work, you'll need MongoDB 8.0 or later. I'm going to use my Atlas
instance, but you could also point to a local instance.
See https://www.mongodb.com/docs/manual/installation/ and
https://www.mongodb.com/docs/atlas/getting-started/ (I like using the
Atlas UI).

#### Installing the MongoDB Java drivers

You can find the MongoDB driver installation information at
https://www.mongodb.com/docs/drivers/java/sync/current/quick-start/. For QE,
you'll also need the encryption dependendency. See
https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/encrypt-fields/.
I have added both of these to the included gradle file.

#### Installing the MongoDB crypt_shared library

To perform automatic encryption (not available in Community), you'll need the
query analysis component, crypt_shared. See
https://www.mongodb.com/docs/manual/core/queryable-encryption/install-library/.
This component doesn't actually do any encryption, rather it does the query
analysis required for automatic encryption.

If you use this on MacOS and it's blocked by security policy, try
`xattr -d com.apple.quarantine ./mongo_crypt_v1.dylib`.

### Configuring your environment

See env_template in the root directory of this project and copy it to
a new file named .env. Configure your connection parameters in that file.
If you're new to MongoDB, you can find more about connecting to MongoDB
Atlas at https://www.mongodb.com/docs/atlas/tutorial/connect-to-your-cluster/.

### Other dependencies

You'll need to install a JDK, which I won't explain in detail here.
I use gradle as my build system. If you're new to gradle (as I am), see
https://docs.gradle.org/current/userguide/installation.html.

## Building and running the project

I have provided the gradle wrapper for this project. To run it from the command
line type `./gradlew --console plain run`. I've also commited my .vscode folder
for those using VS Code.

## Closing thoughts

This is not a production-ready application. I skimped on exception and error
handling and things like that. I'm not a Java expert, so some of what I'm
doing is at best non-idiomatic and at worst idiotic.

The best place to go for help is https://www.mongodb.com/community/forums/.
If you get too stuck, you can try to contact me at joel.odom@mongodb.com, but
please understand that I'm unable to respond to every question and I tend
to be really slow to reply to emails.
