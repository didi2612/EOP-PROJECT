package enableapi;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PetrolStationWithApi {
    private double[][] fuelAndPetrol; // 2D array to store both fuel prices and petrol levels
    private String[] dispenserNames;   // 1D array to store dispenser names
    private final double MIN_PETROL_LEVEL = 0.1;
    private int receiptNumber = 1;

    public PetrolStationWithApi(String[] names) {
        int numDispensers = names.length;
        fuelAndPetrol = new double[numDispensers][2]; // Each dispenser has fuel price and petrol level
        dispenserNames = names;
        updateFuelPrices();
        for (int i = 0; i < numDispensers; i++) {
            fuelAndPetrol[i][1] = 100.0; // Initialize petrol levels
        }
    }

    public void purchaseFuel(int dispenserIndex, double amount, String fuelType) {
        updateFuelPrices();
        if (dispenserIndex < 0 || dispenserIndex >= fuelAndPetrol.length) {
            System.out.println("Invalid dispenser. Please try again.");
            return;
        }

        if (fuelAndPetrol[dispenserIndex][1] < MIN_PETROL_LEVEL) {
            System.out.println("Warning: Low petrol level at dispenser " + dispenserNames[dispenserIndex] + ". Please top up more fuel.");
        } else {
            double fuelPrice = getFuelPrice(fuelType);

            if (fuelPrice > 0) {
                double litres = amount / fuelPrice;
                if (litres > fuelAndPetrol[dispenserIndex][1]) {
                    System.out.println("Error: Not enough petrol in dispenser " + dispenserNames[dispenserIndex] + ". Please top up more fuel.");
                    return;
                }
                fuelAndPetrol[dispenserIndex][1] -= litres;
                double totalPrice = litres * fuelPrice;

                printReceipt(receiptNumber++, dispenserNames[dispenserIndex], litres, fuelType, totalPrice);

                System.out.printf("Filling up %.2f litres of %s at dispenser %s. Total price: RM%.2f\n", litres, fuelType, dispenserNames[dispenserIndex], totalPrice);
            } else {
                System.out.println("Error: Invalid fuel price. Please try again.");
            }
        }
    }

    public void fillUpDispenser(int dispenserIndex) {
        if (dispenserIndex < 0 || dispenserIndex >= fuelAndPetrol.length) {
            System.out.println("Invalid dispenser. Please try again.");
            return;
        }

        double fillAmount = 100.0 - fuelAndPetrol[dispenserIndex][1];
        fuelAndPetrol[dispenserIndex][1] = 100.0;

        System.out.printf("Dispenser %s filled up with %.2f litres.\n", dispenserNames[dispenserIndex], fillAmount);
    }

    private double getFuelPrice(String fuelType) {
        switch (fuelType.toLowerCase()) {
            case "ron95":
                return fuelAndPetrol[0][0];
            case "ron97":
                return fuelAndPetrol[1][0];
            case "diesel":
                return fuelAndPetrol[2][0];
            default:
                throw new IllegalArgumentException("Invalid fuel type: " + fuelType);
        }
    }

    private void printReceipt(int currentReceiptNumber, String dispenserName, double litres, String fuelType, double totalPrice) {
        try {
            String folderName = "azmi,haikal,ubaid-receipt";
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdir();
            }

            String fileName = folderName + "/receiptwithapi_" + currentReceiptNumber + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("Receipt Number: " + currentReceiptNumber);
                writer.println("Dispenser: " + dispenserName);
                writer.println("Fuel Type: " + fuelType);
                writer.println("Litres: " + String.format("%.2f", litres));
                writer.println("Total Price: RM" + String.format("%.2f", totalPrice));
            }

            System.out.println("Receipt saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }

    public void displayPetrolLevels() {
        for (int i = 0; i < fuelAndPetrol.length; i++) {
            System.out.printf("Petrol level at dispenser %s: %.2f litres\n", dispenserNames[i], fuelAndPetrol[i][1]);
        }
    }

    private void updateFuelPrices() {
        try {
            URL url = new URL("https://api.data.gov.my/data-catalogue/?id=fuelprice&limit=1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response and update fuel prices
                fuelAndPetrol[0][0] = parseFuelPriceFromJson(response.toString(), "ron95");
                fuelAndPetrol[1][0] = parseFuelPriceFromJson(response.toString(), "ron97");
                fuelAndPetrol[2][0] = parseFuelPriceFromJson(response.toString(), "diesel");

                System.out.println("RON95 Price: RM" + fuelAndPetrol[0][0]);
                System.out.println("RON97 Price: RM" + fuelAndPetrol[1][0]);
                System.out.println("Diesel Price: RM" + fuelAndPetrol[2][0]);

            } else {
                System.out.println("Failed to get fuel prices. HTTP response code: " + responseCode);

                fuelAndPetrol[0][0] = 2.03;
                fuelAndPetrol[1][0] = 2.28;
                fuelAndPetrol[2][0] = 1.99;
                System.out.println("Using latest updated price 2017:");
                System.out.println("RON95 Price: RM" + fuelAndPetrol[0][0]);
                System.out.println("RON97 Price: RM" + fuelAndPetrol[1][0]);
                System.out.println("Diesel Price: RM" + fuelAndPetrol[2][0]);
            }

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Error updating fuel prices: " + e.getMessage());

            fuelAndPetrol[0][0] = 2.03;
            fuelAndPetrol[1][0] = 2.28;
            fuelAndPetrol[2][0] = 1.99;
            System.out.println("Using latest updated price 2017:");
            System.out.println("RON95 Price: RM" + fuelAndPetrol[0][0]);
            System.out.println("RON97 Price: RM" + fuelAndPetrol[1][0]);
            System.out.println("Diesel Price: RM" + fuelAndPetrol[2][0]);
        }
    }

    private double parseFuelPriceFromJson(String jsonResponse, String fuelType) {
        double fuelPrice = -1; // Default value if not found
        try {

            // I Assume the JSON response is an array with a single element
            if (jsonResponse.startsWith("[") && jsonResponse.endsWith("]")) {
                jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
            }

            // Parse JSON
            String[] keyValuePairs = jsonResponse.split(",");
            for (String pair : keyValuePairs) {
                String[] entry = pair.trim().split(":");
                if (entry.length == 2) {
                    String key = entry[0].trim();
                    String value = entry[1].trim();


                    if (("\"" + fuelType + "\"").equals(key)) {
                        fuelPrice = Double.parseDouble(value);
                        break;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error parsing fuel price for " + fuelType + ": " + e.getMessage());
        }
        return fuelPrice;
    }

    public static void main(String[] args) {
        System.out.println("==== ++ azmi / haikal / ubaid petrol station system (WITH API) ++ ====");
        System.out.println("Fetching fuel prices from https://api.data.gov.my/");
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nFetched!\n");

        String[] dispenserNames = {"Dispenser1", "Dispenser2", "Dispenser3"}; // Customize dispenser names
        PetrolStationWithApi station = new PetrolStationWithApi(dispenserNames);
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Choose a dispenser (0-2) or -1 to exit:");
            int dispenserIndex = scanner.nextInt();
            if (dispenserIndex == -1) {
                exit = true;
                break;
            } else if (dispenserIndex < 0 || dispenserIndex >= station.fuelAndPetrol.length) {
                System.out.println("Invalid dispenser. Please try again.");
                continue;
            }

            if (station.fuelAndPetrol[dispenserIndex][1] < 20.0) {
                System.out.println("Dispenser " + dispenserNames[dispenserIndex] + " needs to be filled up. Do you want to fill it up? (yes/no):");
                String fillUpChoice = scanner.next().toLowerCase();
                if (fillUpChoice.equals("yes")) {
                    station.fillUpDispenser(dispenserIndex);
                    continue;
                } else {
                    System.out.println("Choose a fuel type (ron95, ron97, diesel):");
                    String fuelType = scanner.next().toLowerCase();
                    if (!fuelType.equals("ron95") && !fuelType.equals("ron97") && !fuelType.equals("diesel")) {
                        System.out.println("Invalid fuel type. Please try again.");
                        continue;
                    }
                    System.out.println("Enter the amount of money to purchase fuel (in RM):");
                    double amount = scanner.nextDouble();
                    station.purchaseFuel(dispenserIndex, amount, fuelType);
                    station.displayPetrolLevels();
                }
            } else {
                System.out.println("Choose a fuel type (ron95, ron97, diesel):");
                String fuelType = scanner.next().toLowerCase();
                if (!fuelType.equals("ron95") && !fuelType.equals("ron97") && !fuelType.equals("diesel")) {
                    System.out.println("Invalid fuel type. Please try again.");
                    continue;
                }
                System.out.println("Enter the amount of money to purchase fuel (in RM):");
                double amount = scanner.nextDouble();
                station.purchaseFuel(dispenserIndex, amount, fuelType);
                station.displayPetrolLevels();
            }
        }
        scanner.close();
    }
}
