package org.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFragmentation {

    public static Map<String, List<String[]>> fragmentateByCity(Map<String, List<String[]>> dataMap) {
        List<String[]> data = dataMap.get("data");
        List<String[]> headersList = dataMap.get("headers");
        String[] headers = headersList.get(0);

        // Группировка данных по городам
        Map<String, List<String[]>> cityDataMap = new HashMap<>();
        for (String[] row : data) {
            String city = row[0];
            cityDataMap.computeIfAbsent(city, k -> new ArrayList<>()).add(row);
        }

        // Вставка заголовков в начало каждого списка данных для города
        for (Map.Entry<String, List<String[]>> entry : cityDataMap.entrySet()) {
            entry.getValue().add(0, headers);
        }

        return cityDataMap;
    }
}
