package com.joelodom;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

public class Utils {
    private static final JsonWriterSettings JSON_WRITER_SETINGS
        = JsonWriterSettings.builder().indent(true).build();

    public static String docToPrettyJSON(Document doc) {
        return docToPrettyJSON(doc.toBsonDocument());
    }

    public static String docToPrettyJSON(BsonDocument doc) {
        return doc.toJson(JSON_WRITER_SETINGS);
    }
}
