# mongodb-queryable-encryption
A demonstration and sandbox for MongoDB Queryable Encryption.

## First things first

### Installing MongoDB

For this to work, you'll need MongoDB 8.0 or later. QE will work with MongoDB 7,
but you'll be limited to equality queries only. I'm going to use my Atlas
instance, but you could also point to a local instance.
See https://www.mongodb.com/docs/manual/installation/ and
https://www.mongodb.com/docs/atlas/getting-started/.

#### Installing the MongoDB Java drivers

You can find the MongoDB driver installation information at
https://www.mongodb.com/docs/drivers/java/sync/current/quick-start/. For QE,
you'll also need the encryption dependendency (see
https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/encrypt-fields/).
I have added both of these to the included gradle file.

#### Installing the MongoDB crypt_shared library

To perform automatic encryption (not available in our Community Edition),
you'll need the query analysis library, crypt_shared. See
https://www.mongodb.com/docs/manual/core/queryable-encryption/install-library/.
This library doesn't actually do any encryption, rather it does the query
analysis required for automatic encryption and calls out to your OS for encryption.

If you use this on MacOS and it's blocked by your security policy, try
`xattr -d com.apple.quarantine ./mongo_crypt_v1.dylib`.

### Configuring your environment

See env_template in the root directory of this project and copy it to
a new file named .env. Configure your connection parameters in that file.
If you're new to MongoDB, you can find more about connecting to MongoDB
Atlas at https://www.mongodb.com/docs/atlas/tutorial/connect-to-your-cluster/.

You'll also need to provide the full path to the crypt_shared that you downloaded
in the previous step. On my Mac, I have this set as
`SHARED_LIB_PATH="/Users/joel.odom/lib/mongo_crypt_v1.dylib"`.

### Other dependencies

You'll need to install a JDK, which I won't explain here.
I use gradle as my build system. If you're new to gradle (as I am), see
https://docs.gradle.org/current/userguide/installation.html.

## Building and running the project

I have provided the gradle wrapper for this project. To run it from the command
line type `./gradlew --console plain run`. I've also commited my .vscode folder
for those using VS Code, so Command + Shift + B works for me on my Mac.

## What to do when it runs

First type `help`. This will show you these commands:

```
  exit                  Exit the application.
  help                  Show this help message.
  status                Show the status of pretty much everything.
  create-collection     Creates the test database.
  destroy-database      Destroys the test database.
  add-members [number]  Insert [number] random member records.
  find-one              Query for one member record (semi-randomly).
  equality-query     Demonstrate an equality query on encrypted data.
```

Here is my demonstration flow:

1. I use a tool like the Atlas UI, Compass or mongosh to show that my encrypted
collection doesn't exist yet. The database name and the collection name are
in the .env file you created above.

2. Next I run `create-collection`. This creates the encrypted collection with the
encrypted schema. I can now show this in the Atlas UI (or other tool) and
I can also use `status` at this point to show the server-side encrypted schema
map on the  server itself. This is a good time to explain the difference
between server-side schemas and client-side schemas.

3. It's time to add an item to the collection. `add-members` adds random items
to the collection. Browse to the database in one of the tools to show that the
ssn field is encrypted.

4. Use `find-one` to show that a random record can be pulled down with
encryption.

5. Use `equality-query` to demonstrate an equality query on encrypted data.

**TODO: This demo is underwhelming right now. I need to make it tell a story. I
could also add client-side schema validation. And range encryption.
Lots to do...**

## Closing thoughts

I have added extensive comments in the source code for those who are learning
to use Queryable Encryption in MongoDB. They should be mostly up to date
with the code itself, but please submit a PR if you notice a comment is
de-synched.

Speaking of pull requests, I'd love your PRs and ideas!

This is not a production-ready application. I skimped on exception and error
handling and things like that. I'm not a Java expert, so some of what I'm
doing is at best non-idiomatic and at worst idiotic.

The best place to go for help is https://www.mongodb.com/community/forums/.
If you get too stuck, you can try to contact me at joel.odom@mongodb.com, but
please understand that I'm unable to respond to every question and I tend
to be really slow to reply to emails.
