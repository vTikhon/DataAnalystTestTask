package org.example;

import org.example.service.DataReader;
import org.example.service.DataSaver;
import java.util.List;
import java.util.Map;
import static org.example.analysis.DataAnalysis.analyzePopulationData;

public class Main {
    public static void main(String[] args) {
        String filePath = "data/CityDataTest.csv";
        Map<String, List<String[]>> dataMap = DataReader.readCSVFileData(filePath);
        Map<String, List<String>> analysisResults = analyzePopulationData(dataMap);

        for (Map.Entry<String, List<String>> entry : analysisResults.entrySet()) {
            String city = entry.getKey();
            List<String> cityResults = entry.getValue();
            String outputFilePath = String.format("results/%s.txt", city);
            DataSaver.saveAnalysisResults(outputFilePath, cityResults);
        }
    }
}
