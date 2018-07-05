package main.java.com.ir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class MyDocument {
    int docId;
    String realCategory;
    String title;
    String body;

    String assignedCategory;

    static List<MyDocument> readDocuments(String filename) throws IOException {
        List<String> documentLines = Files.readAllLines(Paths.get(filename));

        List<MyDocument> documents = new ArrayList<>();
        int i = 0;

        // Go over all the lines in the document file
        for (String documentLine : documentLines) {
            i++;
            String[] splittedCsv = documentLine.split("\\,");

            MyDocument currentDocument = new MyDocument();
            currentDocument.docId = Integer.parseInt(splittedCsv[0]);
            currentDocument.realCategory = splittedCsv[1];
            currentDocument.title = splittedCsv[2];
            currentDocument.body = splittedCsv[3];

            documents.add(currentDocument);

            // unmark this if you want to limit number of documents, for debugging purpuses
//            if (i>1000) {
//                return documents;
//            }
        }

        return documents;
    }
}