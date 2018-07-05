package main.java.com.ir;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultAnalyzer {
     class Category {
        int tp;
        int fp;
        int fn;

        double f1;
    }

    Map<String, Category> initCategories() {
        Map<String, Category> categories = new HashMap<String, Category>();

        categories.put("1", new Category());
        categories.put("2", new Category());
        categories.put("3", new Category());
        categories.put("4", new Category());
        categories.put("5", new Category());
        categories.put("6", new Category());
        categories.put("7", new Category());
        categories.put("8", new Category());
        categories.put("9", new Category());
        categories.put("10", new Category());
        categories.put("11", new Category());
        categories.put("12", new Category());
        categories.put("13", new Category());
        categories.put("14", new Category());

        return categories;
    }

    public double[] analyzeResults(List<MyDocument> documents) {
        Map<String, Category> categories = initCategories();

        for (MyDocument document : documents) {
            if (document.assignedCategory.equals(document.realCategory)) {
                categories.get(document.assignedCategory).tp++;
            }
            else {
                categories.get(document.assignedCategory).fp++;
                categories.get(document.realCategory).fn++;
            }
        }

        double macroaveraging = getMacro(categories);
        double microaveraging = getMicro(categories);

        double[] result = new double[2];
        result[0] = macroaveraging;
        result[1] = microaveraging;

        return  result;
    }

    private double getMicro(Map<String, Category> categories) {

         int tpCount = 0;
         int fpCount = 0;
         int fnCount = 0;

         for (Category category : categories.values()) {
             tpCount += category.tp;
             fpCount += category.fp;
             fnCount += category.fn;
         }

        double tpAvarage = (double)tpCount / categories.values().size();
        double fpAvarage = (double)fpCount / categories.values().size();
        double fnAvarage = (double)fnCount / categories.values().size();

        double p = (double)tpAvarage / (double)(tpAvarage + fpAvarage);
        double r = (double)tpAvarage / (double)(tpAvarage + fnAvarage);

        return 2 * p * r / (p + r);
    }

    private double getMacro(Map<String, Category> categories) {

         for (Category category : categories.values()) {
             double p = (double)category.tp / (double)(category.tp + category.fp);
             double r = (double)category.tp / (double)(category.tp + category.fn);

             category.f1 = 2 * p * r / (p + r);
         }

         double f1Avarage = 0.0;
         for (Category category : categories.values()) {
             f1Avarage += category.f1;
         }

         f1Avarage /= categories.values().size();

         return f1Avarage;
    }
}
