import java.io.*;
import java.net.*;
import java.util.Scanner;


public class PetrolStation {
    private double[][] fuelAndPetrol; // 2D array to store both fuel prices and petrol levels
    private String[] dispenserNames; // 1D array to store dispenser names
    private final double MIN_PETROL_LEVEL = 20.0;
    private int receiptNumber = 1;

    // Constructor for the version without API
    public PetrolStation(int numDispensers, boolean useApi) {
        if (useApi) {
            fuelAndPetrol = new double[numDispensers][2];
            dispenserNames = new String[]{"Dispenser A", "Dispenser B", "Dispenser C"};
            updateFuelPrices();
        } else {
            fuelAndPetrol = new double[numDispensers][2];
            dispenserNames = new String[]{"Dispenser 1", "Dispenser 2", "Dispenser 3"};
            // Initialize fuel prices manually for the version without API
            fuelAndPetrol[0][0] = 2.05;
            fuelAndPetrol[1][0] = 3.47;
            fuelAndPetrol[2][0] = 2.15;
        }

        for (int i = 0; i < numDispensers; i++) {
            fuelAndPetrol[i][1] = 100.0; // Initialize petrol levels
        }
    }
    // methods in purchasing the fuel
    public void purchaseFuel(int dispenser, double amount, String fuelType) {
        updateFuelPrices();
        if (dispenser < 0 || dispenser >= fuelAndPetrol.length) {
            System.out.println("Invalid dispenser. Please try again.");
            return;
        }

        if (fuelAndPetrol[dispenser][1] < MIN_PETROL_LEVEL) {
            System.out.println("Warning: Low petrol level at " + dispenserNames[dispenser] + ". Please top up more fuel.");
        } else {
            double fuelPrice = getFuelPrice(fuelType);

            if (fuelPrice > 0) {
                double litres = amount / fuelPrice;
                if (litres > fuelAndPetrol[dispenser][1]) {
                    System.out.println("Error: Not enough petrol in " + dispenserNames[dispenser] + ". Topping up the fuel.");
                    fillUpDispenser(dispenser);
                    return;
                }
                fuelAndPetrol[dispenser][1] -= litres;
                double totalPrice = litres * fuelPrice;

                printReceipt(receiptNumber++, dispenser, litres, fuelType, totalPrice);

                System.out.printf("Filling up %.2f litres of %s at %s. Total price: RM%.2f\n", litres, fuelType, dispenserNames[dispenser], totalPrice);
            } else {
                System.out.println("Error: Invalid fuel price. Please try again.");
            }
        }
    }
    // filling up the dispenser methods
    public void fillUpDispenser(int dispenser) {
        if (dispenser < 0 || dispenser >= fuelAndPetrol.length) {
            System.out.println("Invalid dispenser. Please try again.");
            return;
        }

        double fillAmount = 100.0 - fuelAndPetrol[dispenser][1];
        fuelAndPetrol[dispenser][1] = 100.0;

        System.out.printf("Dispenser %d filled up with %.2f litres.\n", dispenser, fillAmount);
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
    // receipt printing
    private void printReceipt(int currentReceiptNumber, int dispenser, double litres, String fuelType, double totalPrice) {
        try {

            String folderName = "azmi,haikal,ubaid-receipt";
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdir();
            }

            String fileName = folderName + "/receipt_" + currentReceiptNumber + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("Receipt Number: " + currentReceiptNumber);
                writer.println("Dispenser: " + dispenser);
                writer.println("Fuel Type: " + fuelType);
                writer.println("Litres: " + String.format("%.2f", litres));
                writer.println("Total Price: RM" + String.format("%.2f", totalPrice));
            }

            System.out.println("Receipt saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }
    // displaying the petrol level in dispenser
    public void displayPetrolLevels() {
        for (int i = 0; i < fuelAndPetrol.length; i++) {
            System.out.printf("Petrol level at dispenser %d: %.2f litres\n", i, fuelAndPetrol[i][1]);
        }
    }
    // updating the fuel price
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
    // parsing JSON data from the api endpoint into a readable data
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
        System.out.println("==== ++ azmi / haikal / ubaid petrol station system ++ ====");
        Scanner scanner = new Scanner(System.in);

        // Choose between using API or not
        System.out.println("Choose the version (1: with API, 2: without API):");
        int versionChoice = scanner.nextInt();

        boolean useApi;
        if (versionChoice == 1) {
            useApi = true;
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
        } else if (versionChoice == 2) {
            useApi = false;
            System.out.println("Using manually initialized fuel prices.\n");
        } else {
            System.out.println("Invalid choice. Exiting program.");
            return; //check this part, resource may be leak. Produce solution but it working
        }

        PetrolStation station = new PetrolStation(3, useApi);
        boolean exit = false;

        while (!exit) {
            System.out.println("Choose a dispenser (0-2) or -1 to exit:");
            int dispenser = scanner.nextInt();
            if (dispenser == -1) {
                exit = true;
                break;
            } else if (dispenser < 0 || dispenser >= station.fuelAndPetrol.length) {
                System.out.println("Invalid dispenser. Please try again.");
                continue;
            }

            System.out.println("Dispenser Name: " + station.dispenserNames[dispenser]);

            if (station.fuelAndPetrol[dispenser][1] < 20.0) {
                System.out.println(station.dispenserNames[dispenser] + " needs to be filled up. Do you want to fill it up? (yes/no):");
                String fillUpChoice = scanner.next().toLowerCase();
                if (fillUpChoice.equals("yes")) {
                    station.fillUpDispenser(dispenser);
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
                    station.purchaseFuel(dispenser, amount, fuelType);
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
                station.purchaseFuel(dispenser, amount, fuelType);
                station.displayPetrolLevels();
            }
        }
        scanner.close();
    }
}
