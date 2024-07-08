package org.example.analysis;

import org.example.result.ResultsMaker;
import org.example.service.DataFragmentation;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class DataAnalysis {

    public static Map<String, List<String>> analyzePopulationData(Map<String, List<String[]>> dataMap) {
        Map<String, List<String>> analysisResults = new HashMap<>();

        // Группировка данных по городам с заголовками
        Map<String, List<String[]>> cityDataMap = DataFragmentation.fragmentateByCity(dataMap);

        // Анализ данных для каждого города
        for (Map.Entry<String, List<String[]>> entry : cityDataMap.entrySet()) {
            String city = entry.getKey();
            List<String[]> cityData = entry.getValue();
            List<String> cityResults = analyzeCityPopulation(city, cityData);
            analysisResults.put(city, cityResults);
        }

        return analysisResults;
    }

    public static List<String> analyzeCityPopulation(String city, List<String[]> cityData) {
        List<String> cityResults = new ArrayList<>();
        cityResults.add(String.format("Город: %s", city));
        cityData.remove(0); // Удаляем заголовки из данных для анализа

        // Суммарные значения для среднего прогнозного и фактического населения
        double[] totalModelFactPopulations = new double[2];
        int[] countsModelFact = new int[2];

        // Минимальные и максимальные значения для прогнозов и фактических данных
        double[] minMaxModelPopulations = {Double.MAX_VALUE, Double.MIN_VALUE};
        double[] minMaxFactPopulations = {Double.MAX_VALUE, Double.MIN_VALUE};

        List<Double> factPopulationList = new ArrayList<>();
        List<Double> modelPopulationList = new ArrayList<>();

        for (String[] row : cityData) {
            if (!row[2].equals("NA")) { // Фильтрация строк без фактических данных
                processRow(row, totalModelFactPopulations, countsModelFact, minMaxModelPopulations, minMaxFactPopulations, factPopulationList, modelPopulationList);
            }
        }

        ResultsMaker.addSummary(city, cityResults, totalModelFactPopulations, countsModelFact, minMaxModelPopulations, minMaxFactPopulations, factPopulationList, modelPopulationList);

        return cityResults;
    }


    public static void processRow(String[] row, double[] totalModelFactPopulations, int[] countsModelFact,
                                  double[] minMaxModelPopulations, double[] minMaxFactPopulations,
                                  List<Double> factPopulationList, List<Double> modelPopulationList) {
        String factStr = row[2];
        double modelPopulation = Double.parseDouble(row[3]);

        // Обработка фактического населения
        if (!factStr.equals("NA")) {
            double factPopulation = Double.parseDouble(factStr);
            factPopulationList.add(factPopulation);
            totalModelFactPopulations[1] += factPopulation;
            countsModelFact[1]++;
            updateMinMax(factPopulation, minMaxFactPopulations);
        }

        modelPopulationList.add(modelPopulation);
        totalModelFactPopulations[0] += modelPopulation;
        countsModelFact[0]++;
        updateMinMax(modelPopulation, minMaxModelPopulations);
    }


    public static void updateMinMax(double value, double[] minMax) {
        if (value < minMax[0]) {
            minMax[0] = value;
        }
        if (value > minMax[1]) {
            minMax[1] = value;
        }
    }

    public static double calculateStandardDeviation(List<Double> populationList) {
        double mean = populationList.stream().mapToDouble(val -> val).average().orElse(0.0);
        double variance = populationList.stream().mapToDouble(val -> Math.pow(val - mean, 2)).average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public static double calculateMeanAbsoluteError(List<Double> factPopulationList, List<Double> modelPopulationList) {
        double totalAbsoluteError = 0.0;
        for (int i = 0; i < factPopulationList.size(); i++) {
            double error = factPopulationList.get(i) - modelPopulationList.get(i);
            totalAbsoluteError += Math.abs(error);
        }
        return totalAbsoluteError / factPopulationList.size();
    }

    public static double calculateMeanSquaredError(List<Double> factPopulationList, List<Double> modelPopulationList) {
        double totalSquaredError = 0.0;
        for (int i = 0; i < factPopulationList.size(); i++) {
            double error = factPopulationList.get(i) - modelPopulationList.get(i);
            totalSquaredError += error * error;
        }
        return totalSquaredError / factPopulationList.size();
    }

    public static double calculateCorrelation(List<Double> factPopulationList, List<Double> modelPopulationList) {
        double meanFact = factPopulationList.stream().mapToDouble(val -> val).average().orElse(0.0);
        double meanModel = modelPopulationList.stream().mapToDouble(val -> val).average().orElse(0.0);
        double covariance = 0.0;
        double varianceFact = 0.0;
        double varianceModel = 0.0;
        for (int i = 0; i < factPopulationList.size(); i++) {
            double factDiff = factPopulationList.get(i) - meanFact;
            double modelDiff = modelPopulationList.get(i) - meanModel;
            covariance += factDiff * modelDiff;
            varianceFact += factDiff * factDiff;
            varianceModel += modelDiff * modelDiff;
        }
        return covariance / Math.sqrt(varianceFact * varianceModel);
    }
}
