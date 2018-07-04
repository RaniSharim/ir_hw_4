package main.java.com.ir;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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
            classifier.train(config.k);

            List<MyDocument> testDocuments = MyDocument.readDocuments(config.testFile);

            for (MyDocument document : testDocuments) {
                document.assignedCategory = classifier.classify(document);
            }

            StringBuilder resultStr = new StringBuilder();
            testDocuments.sort((a,b) -> a.docId > b.docId?1:-1);
            for (MyDocument document : testDocuments) {
                resultStr.append(document.docId);
                resultStr.append(",");
                resultStr.append(document.realCategory);
                resultStr.append(",");
                resultStr.append(document.assignedCategory);
                resultStr.append("\n");
            }

            Files.write(Paths.get(config.outputFile), resultStr.toString().getBytes());

//            ResultAnalyzer resultAnalyzer = new ResultAnalyzer();
//            double[] results = resultAnalyzer.analzeResults(testDocuments);
//
//            System.out.println("macro: " + results[0] + " micro: " + results[1]);

        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}