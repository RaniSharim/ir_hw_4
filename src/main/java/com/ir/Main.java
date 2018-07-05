package main.java.com.ir;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("path to parameters.txt is missing");
            System.exit(1);
        }


        try {
            Configuration config = Configuration.readConfiguration(args[0]);

            KnnClassifier classifier = new KnnClassifier();
            classifier.addTrainDocuments(MyDocument.readDocuments(config.trainFile));

            List<MyDocument> testDocuments = MyDocument.readDocuments(config.testFile);

            classifier.train(config.k);
            for (MyDocument document : testDocuments) {
                document.assignedCategory = classifier.classify(document);
            }

            // This calculates Micro/Macro F1 score for the classification
//            ResultAnalyzer resultAnalyzer = new ResultAnalyzer();
//            double[] results = resultAnalyzer.analyzeResults(testDocuments);
//
//            System.out.println("K: " + k + " macro: " + results[0] + " micro: " + results[1]);



            StringBuilder resultStr = new StringBuilder();
            // Sort the results
            testDocuments.sort((a,b) -> a.docId > b.docId?1:-1);
            // Create the output
            for (MyDocument document : testDocuments) {
                resultStr.append(document.docId);
                resultStr.append(",");
                resultStr.append(document.assignedCategory);
                resultStr.append(",");
                resultStr.append(document.realCategory);
                resultStr.append("\n");
            }

            // Write it to the output file
            Files.write(Paths.get(config.outputFile), resultStr.toString().getBytes());



        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}