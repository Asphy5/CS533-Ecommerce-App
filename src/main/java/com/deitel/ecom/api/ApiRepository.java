package com.deitel.ecom.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ApiRepository {

    private static final String BASE_URL = "http://10.0.2.2/api/";

    public static JSONObject post(String endpoint, JSONObject data) {
        try { // Attempt to POST to backend
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Initialize writer & write data to backend
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data.toString());
            writer.flush();

            // Get & return response
            Scanner scanner = new Scanner(conn.getInputStream());
            return new JSONObject(scanner.nextLine());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray getArray(String endpoint) throws Exception {
        try { // Attempt to GET a JSON array from backend
            URL url = new URL(BASE_URL + endpoint);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Initialize scanner & string builder
            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder sb = new StringBuilder();

            // Assemble the JSON into a string
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
            }

            scanner.close();

            // Return full JSON string as a JSON array
            return new JSONArray(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}