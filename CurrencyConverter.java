package com.alex;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the currency to convert (eg, USD): ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Enter the currency you want to convert to (eg, EUR): ");
        String targetCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Enter the amount to convert: ");
        double amount = scanner.nextDouble();

        double convertedAmount = fetchConversionRate(baseCurrency, targetCurrency, amount);

        if (convertedAmount >= 0) {
            System.out.printf("Result: %.2f %s = %.2f %s%n",
                    amount, baseCurrency, convertedAmount, targetCurrency);
        } else {
            System.out.println("Error during conversion.");
        }
    }

    public static double fetchConversionRate(String baseCurrency, String targetCurrency, double amount) {
        String apiKey = "API";
        String apiUrl = "LINK" + apiKey + "/latest/" + baseCurrency;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Error: Unable to retrieve data. Response code: " + responseCode);
                return -1;
            }

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();

                JsonObject conversionRates = jsonResponse.getAsJsonObject("conversion_rates");

                if (conversionRates == null || !conversionRates.has(targetCurrency)) {
                    System.out.println("Error: Currency " + targetCurrency + " not found.");
                    return -1;
                }

                double rate = conversionRates.get(targetCurrency).getAsDouble();
                return amount * rate;
            }

        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
            return -1;
        }
    }
}
