package org.example.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReader {

    public static Map<String, List<String[]>> readCSVFileData(String filePath) {
        Map<String, List<String[]>> dataMap = new HashMap<>();
        List<String[]> dataList = new ArrayList<>();
        String[] headers = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            if ((line = br.readLine()) != null) {
                headers = line.split(";");
                dataMap.put("headers", new ArrayList<>());
                dataMap.get("headers").add(headers);
            }
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].trim();
                }
                dataList.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataMap.put("data", dataList);

        return dataMap;
    }
}
