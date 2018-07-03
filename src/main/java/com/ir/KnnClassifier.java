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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

public class KnnClassifier {
    EnglishAnalyzer analyzer = new EnglishAnalyzer();
    Directory index = new RAMDirectory();
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    KNearestNeighborDocumentClassifier knn;
    Map<String,Analyzer> field2analyzer;

    public KnnClassifier() {
        field2analyzer = new HashMap<String,Analyzer>();
        field2analyzer.put("title", analyzer);
        field2analyzer.put("body", analyzer);
    }

    public void addTrainDocuments(List<MyDocument> documents) throws IOException  {
        IndexWriter w = new IndexWriter(index, config);
        for (MyDocument doc : documents) {
            w.addDocument(createDocument(doc));
        }
        w.close();
    }

    public void train(int k) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        knn = new KNearestNeighborDocumentClassifier(reader, null, null, k,
                0,0, "category",
                field2analyzer, "title", "body");
    }

    private Document createDocument(MyDocument doc) throws IOException {
        Document luceneDoc = new Document();

        luceneDoc.add(new StoredField("docId", doc.docId));
        luceneDoc.add(new StringField("category", doc.realCategory, Field.Store.YES));

        luceneDoc.add(new TextField("title", doc.title, Field.Store.NO));
        luceneDoc.add(new TextField("body", doc.body, Field.Store.NO));

        return luceneDoc;
    }

    public String classify(MyDocument doc) throws IOException {
        List<ClassificationResult<BytesRef>> result = knn.getClasses(createDocument(doc), 1);
        String category = result.get(0).getAssignedClass().utf8ToString();
        return category;
    }

}
