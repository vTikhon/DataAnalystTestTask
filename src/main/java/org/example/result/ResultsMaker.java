package org.example.result;

import java.util.List;
import static org.example.analysis.DataAnalysis.calculateStandardDeviation;
import static org.example.analysis.DataAnalysis.calculateMeanAbsoluteError;
import static org.example.analysis.DataAnalysis.calculateMeanSquaredError;
import static org.example.analysis.DataAnalysis.calculateCorrelation;

public class ResultsMaker {

    public static void addSummary(String city, List<String> cityResults, double[] totalModelFactPopulations, int[] countsModelFact,
                                  double[] minMaxModelPopulations, double[] minMaxFactPopulations,
                                  List<Double> factPopulationList, List<Double> modelPopulationList) {

        addModelPopulationSummary(cityResults, totalModelFactPopulations, countsModelFact, minMaxModelPopulations, modelPopulationList);

        if (countsModelFact[1] > 0) {
            addFactPopulationSummary(cityResults, totalModelFactPopulations, countsModelFact, minMaxFactPopulations, factPopulationList);
        } else {
            cityResults.add("Нет доступных данных для фактической численности населения.");
        }

        addForecastErrors(cityResults, factPopulationList, modelPopulationList);
        addCorrelation(cityResults, factPopulationList, modelPopulationList);

        addAccuracyConclusions(cityResults, factPopulationList, modelPopulationList);
        addTrendConclusions(cityResults, factPopulationList);
        addHypotheses(cityResults);
    }

    private static void addModelPopulationSummary(List<String> cityResults, double[] totalModelFactPopulations, int[] countsModelFact,
                                                  double[] minMaxModelPopulations, List<Double> modelPopulationList) {
        double averageModelPopulation = totalModelFactPopulations[0] / countsModelFact[0];
        double stdModel = calculateStandardDeviation(modelPopulationList);
        cityResults.add(String.format("Минимальная прогнозная численность населения: %.2f", minMaxModelPopulations[0]));
        cityResults.add(String.format("Средняя прогнозная численность населения: %.2f", averageModelPopulation));
        cityResults.add(String.format("Максимальная прогнозная численность населения: %.2f", minMaxModelPopulations[1]));
        cityResults.add(String.format("Стандартное отклонение прогнозной численности населения: %.2f", stdModel));
    }

    private static void addFactPopulationSummary(List<String> cityResults, double[] totalModelFactPopulations, int[] countsModelFact,
                                                 double[] minMaxFactPopulations, List<Double> factPopulationList) {
        double averageFactPopulation = totalModelFactPopulations[1] / countsModelFact[1];
        double stdFact = calculateStandardDeviation(factPopulationList);
        cityResults.add(String.format("Минимальная фактическая численность населения: %.2f", minMaxFactPopulations[0]));
        cityResults.add(String.format("Средняя фактическая численность населения: %.2f", averageFactPopulation));
        cityResults.add(String.format("Максимальная фактическая численность населения: %.2f", minMaxFactPopulations[1]));
        cityResults.add(String.format("Стандартное отклонение фактической численности населения: %.2f", stdFact));
    }

    private static void addForecastErrors(List<String> cityResults, List<Double> factPopulationList, List<Double> modelPopulationList) {
        if (factPopulationList.size() == modelPopulationList.size() && !factPopulationList.isEmpty()) {
            double meanAbsoluteError = calculateMeanAbsoluteError(factPopulationList, modelPopulationList);
            double meanSquaredError = calculateMeanSquaredError(factPopulationList, modelPopulationList);
            cityResults.add(String.format("Средняя абсолютная ошибка прогноза: %.2f", meanAbsoluteError));
            cityResults.add(String.format("Средняя квадратная ошибка прогноза: %.2f", meanSquaredError));
        }
    }

    private static void addCorrelation(List<String> cityResults, List<Double> factPopulationList, List<Double> modelPopulationList) {
        if (factPopulationList.size() == modelPopulationList.size() && !factPopulationList.isEmpty()) {
            double correlation = calculateCorrelation(factPopulationList, modelPopulationList);
            cityResults.add(String.format("Корреляция между фактическими и прогнозными данными: %.2f", correlation));
        }
    }

    private static void addAccuracyConclusions(List<String> cityResults, List<Double> factPopulationList, List<Double> modelPopulationList) {
        cityResults.add("\nСоциально-экономические выводы:");
        cityResults.add("1. Точность прогнозов:");
        if (factPopulationList.size() > 0 && modelPopulationList.size() > 0) {
            double meanAbsoluteError = calculateMeanAbsoluteError(factPopulationList, modelPopulationList);
            if (meanAbsoluteError < 0.05 * factPopulationList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0)) {
                cityResults.add("   Прогнозы достаточно точные и могут быть использованы для долгосрочного планирования.");
            } else {
                cityResults.add("   Прогнозы имеют значительное расхождение с фактическими данными. Необходимо пересмотреть модель прогнозирования.");
            }
        } else {
            cityResults.add("   Недостаточно данных для оценки точности прогнозов.");
        }
    }

    private static void addTrendConclusions(List<String> cityResults, List<Double> factPopulationList) {
        cityResults.add("2. Тренды в численности населения:");
        if (factPopulationList.size() > 1) {
            double first = factPopulationList.get(0);
            double last = factPopulationList.get(factPopulationList.size() - 1);
            if (last > first) {
                cityResults.add("   Численность населения растет, что свидетельствует о благоприятных экономических условиях.");
            } else if (last < first) {
                cityResults.add("   Численность населения снижается, что может указывать на экономические или социальные проблемы.");
            } else {
                cityResults.add("   Численность населения стабильна.");
            }
        } else {
            cityResults.add("   Недостаточно данных для определения тренда.");
        }
    }

    private static void addHypotheses(List<String> cityResults) {
        cityResults.add("\nГипотезы для дальнейших исследований:");
        cityResults.add("1. Факторы роста: Исследовать, какие факторы способствуют росту численности населения.");
        cityResults.add("2. Факторы снижения: Выявить причины снижения численности населения и предложить меры по улучшению ситуации.");
        cityResults.add("3. Точность моделей: Анализировать и улучшать точность моделей прогнозирования для обеспечения более точных данных.");
    }
}
