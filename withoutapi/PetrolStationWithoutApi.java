import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class PetrolStationWithoutApi {
    private double[][] fuelAndPetrol; // 2D array to store both fuel prices and petrol levels
    private final double MIN_PETROL_LEVEL = 0.1;
    private int receiptNumber = 1;

    public PetrolStationWithoutApi(int numDispensers) {
        fuelAndPetrol = new double[numDispensers][2];
        // Initialize fuel prices manually for the version without API
        fuelAndPetrol[0][0] = 2.03;
        fuelAndPetrol[1][0] = 2.28;
        fuelAndPetrol[2][0] = 1.99;

        for (int i = 0; i < numDispensers; i++) {
            fuelAndPetrol[i][1] = 100.0; // Initialize petrol levels
        }
    }

    public void purchaseFuel(int dispenser, double amount, String fuelType) {
        if (dispenser < 0 || dispenser >= fuelAndPetrol.length) {
            System.out.println("Invalid dispenser. Please try again.");
            return;
        }

        if (fuelAndPetrol[dispenser][1] < MIN_PETROL_LEVEL) {
            System.out.println("Warning: Low petrol level at dispenser " + dispenser + ". Please top up more fuel.");
        } else {
            double fuelPrice = getFuelPrice(fuelType);

            if (fuelPrice > 0) {
                double litres = amount / fuelPrice;
                if (litres > fuelAndPetrol[dispenser][1]) {
                    System.out.println("Error: Not enough petrol in dispenser " + dispenser + ". Please top up more fuel.");
                    return;
                }
                fuelAndPetrol[dispenser][1] -= litres;
                double totalPrice = litres * fuelPrice;

                printReceipt(receiptNumber++, dispenser, litres, fuelType, totalPrice);

                System.out.printf("Filling up %.2f litres of %s at dispenser %d. Total price: RM%.2f\n", litres, fuelType, dispenser, totalPrice);
            } else {
                System.out.println("Error: Invalid fuel price. Please try again.");
            }
        }
    }

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

    private void printReceipt(int currentReceiptNumber, int dispenser, double litres, String fuelType, double totalPrice) {
        try {

            String folderName = "azmi,haikal,ubaid-receipt";
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdir();
            }

            String fileName = folderName + "/receiptwithoutapi_" + currentReceiptNumber + ".txt";

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

    public void displayPetrolLevels() {
        for (int i = 0; i < fuelAndPetrol.length; i++) {
            System.out.printf("Petrol level at dispenser %d: %.2f litres\n", i, fuelAndPetrol[i][1]);
        }
    }

    public static void main(String[] args) {
        System.out.println("==== ++ azmi / haikal / ubaid petrol station system (Without API) ++ ====");
        PetrolStationWithoutApi station = new PetrolStationWithoutApi(3);
        Scanner scanner = new Scanner(System.in);
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

            if (station.fuelAndPetrol[dispenser][1] < 20.0) {
                System.out.println("Dispenser " + dispenser + " needs to be filled up. Do you want to fill it up? (yes/no):");
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
