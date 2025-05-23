# mongodb-queryable-encryption
A demonstration and sandbox for MongoDB Queryable Encryption. This is a work
in progress, so take it all with a grain.

## First things first

### Installing MongoDB

For this to work, you'll need MongoDB 8.0 or later. QE will work with MongoDB 7,
but you'll be limited to equality queries only, and this application uses range
queries as well as equality queries . I'm going to use my Atlas
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
This library doesn't actually do any encryption, rather it does the parsing
and query analysis required for automatic encryption.

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

This application simulates a simple call center application. Imagine that you
own a gym and you have members that you need to manage.

First type `help`. This will show you these commands:

```
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
  compact-collection    Compacts the encrypted metadata.
```

Here is my demonstration flow:

1. I use a tool like the Atlas UI, Compass or mongosh to show that my encrypted
collection doesn't exist yet. The database name and the collection name are
in the .env file you created above.

2. Next I run `create-collection`. This creates the encrypted collection with the
encrypted schema. I can now show this in the Atlas UI (or other tool) and
I can also use `status` at this point to show the server-side encrypted schema
map on. This is a good time to explain the difference between server-side
schemas and client-side schemas.

3. `add-members` adds random members to the collection. Browse to the database
in one of the tools to show that the ssn field and age are encrypted.

4. Use `find-one` to show that a random record can be pulled down and show what
it looks like decrypted.

5. Use `no-ssn-query` to demonstrate an equality query on encrypted data.

6. Use `age-query` to demonstrate a range query on encrypted data.

7. Use `compound-query` to demonstrate a compound query.

This demo is underwhelming right now. More enhancements are coming, but it's a
place to start.

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

## Advanced material

I've been using this project to try out some things that are more advanced than
the demonstration documented above. I'll talk about some of those here. Keep
in mind that these things may void your warranty.

### Client- vs. server-side schema enforcement

Queryabe Encryption offers two types of schema validation, on the server and
on the client. Most MongoDB users prefer server-side validation because it
will present an error if a client application bug ever causes plaintext data
to be sent when the data should be encrypted.

But client-side validation is nice because it prevents a malicious database
administrator from changing the underlying schema and possibly causing
plaintext data to be sent from a client (this is scenario dependent, but is a
real concern). By having both kinds of validation you get the best of both
worlds.

The trick is that you have to synchronize the data key ids. This requires some
minor but managable gymnastics. See `Schemas.java`. To get both in this
demonstration, you have to create the encrypted collection before anything else.
You shouldn't try to implement client-side schema validation alone because queries
may not be performant and you can't run compaction on the database if you do
that. See
https://www.mongodb.com/docs/manual/core/queryable-encryption/fundamentals/enable-qe/

### Secret commands

I'm not going to document all of the secret commands here, but take a peek at
`QEDemonstration.java` and you'll find some secret commands that you can
reverse engineer as I'll not document everything here.

### Using a cloud KMS

One of the (maybe not-so-secret) secrets of this demonstration is that it can
be connfigured to use AWS KMS. See KeyManagement.java for more about that.

### Building a fat JAR file that includes the automatic encryption shared library

I've included a build task called `fatJar` to create a JAR file that includes
the MongoDB dependencies AND that you can include your crypt_shared library in.
I'm not an expert on packaging Java projects, so take it with a grain.
I've only tested this on macOS. Here's what I know.

There is a commented out `fatJar` task in `build.gradle`. Uncomment it. Note
that the line `from(lib)` pulls the contents of a `lib` folder in the project
root into a `lib` folder in the JAR. Copy your crypt_shared library into a new
`lib` folder into the project root.

Build the JAR with `gradle clean fatJar`. Now, on my Mac, when I run
`jar tf build/libs/QEDemonstration.jar`, I can see `lib/mongo_crypt_v1.dylib` in
the contents.

We need to let the application know that we're using the shared library
from the JAR file. To do that, modify your `.env` (see above) to set
SHARED_LIB_PATH to a folder that exists followed by the name of the shared
library for your system. The shared library will be copied out
of the JAR file into that folder and file at runtime.
Next, switch SHARED_LIB_JAR_PATH from `""` to `/lib/<name of your shared lib>`.

On my Mac, I have these set to

```
SHARED_LIB_PATH="mongo_crypt_v1.dylib"
SHARED_LIB_JAR_PATH="/lib/mongo_crypt_v1.dylib"
```

which copies the shared library out of the JAR file into whatever folder
I'm running it from.

To run the JAR, try `java -jar QEDemonstration.jar`. You can copy the JAR file
to some random folder to prove that it actually contains and loads the
crypt_shared out of the JAR. Remember that you'll need the `.env` file there,
too.

### Advanced QE documentation

* MongoDB Client-Side Encryption API Specification (applies to _both_ Queryable Encryption and Client-Side Field Level Encryption) - this is primarily intended as a resource for low-level drivers developers, but might be useful: [https://github.com/mongodb/specifications/blob/89e91523b58e9de06d45da9329d3d2595307557c/source/client-side-encryption/client-side-encryption.md#clientencryption](https://github.com/mongodb/specifications/blob/master/source/client-side-encryption/client-side-encryption.md#clientencryption)

* Peer-reviewed papers and whitepapers published by the MongoDB Cryptography Research Group: https://www.mongodb.com/company/research/cryptography-research-group

