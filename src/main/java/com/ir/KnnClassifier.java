package main.java.com.ir;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.classification.ClassificationResult;
import org.apache.lucene.classification.document.KNearestNeighborDocumentClassifier;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

public class KnnClassifier {
    EnglishAnalyzer analyzer = new EnglishAnalyzer();
    Directory index = new RAMDirectory();
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    KNearestNeighborDocumentClassifier knn;
    Map<String,Analyzer> field2analyzer;

    // This maps the document field to an analyzer
    // We used the build it 'EnglishAnalyzer' for
    // Stemming and stop word removal
    public KnnClassifier() {
        field2analyzer = new HashMap<String,Analyzer>();
        field2analyzer.put("title", analyzer);
        field2analyzer.put("body", analyzer);
    }

    // Adds a list of documents to the index
    public void addTrainDocuments(List<MyDocument> documents) throws IOException  {
        IndexWriter w = new IndexWriter(index, config);
        for (MyDocument doc : documents) {
            w.addDocument(createDocument(doc));
        }
        w.close();
    }

    // This isn't really 'training' as KNN is a lazy model
    // but i named it like that out of habit
    public void train(int k) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        // Create a classifier using the default BM25 similarity
        // To replace to TF-IDF, we need to change the second parameter to
        // new ClassicSimilarity()
        knn = new KNearestNeighborDocumentClassifier(reader, null, null, k,
                // First and second params in this line are
                // minDocsFreq and minTermFreq - we tried to 'play' with them
                // but it didn't have much effect
                0, 0, "category",
                field2analyzer, "title", "body");
    }

    private Document createDocument(MyDocument doc) throws IOException {
        Document luceneDoc = new Document();

        luceneDoc.add(new StoredField("docId", doc.docId));
        luceneDoc.add(new StringField("category", doc.realCategory, Field.Store.YES));

        // We don't need to retrieve the title/body, so set store to "NO"
        luceneDoc.add(new TextField("title", doc.title, Field.Store.NO));
        luceneDoc.add(new TextField("body", doc.body, Field.Store.NO));

        return luceneDoc;
    }

    public String classify(MyDocument doc) throws IOException {
        // Assign the class to the documents using the 'trained' KNN model
        String category = knn.assignClass(createDocument(doc)).getAssignedClass().utf8ToString();
        return category;
    }

}
