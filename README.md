# EOP-PROJECT
<img src="https://i.ibb.co/Bwc2hbT/latest-logo-no-background.png" alt="LOGO AZMI PRODUCTIONS" width="200"/><img src="https://seeklogo.com/images/I/international-islamic-university-malaysia-logo-221DAA8603-seeklogo.com.png" alt="LOGO IIUM" width="200"/><br>
this repository made by Azmi(2211387) for haikal and Ubaid for sharing and updating our project code of details below :

I update fuel price using API from https://api.data.gov.my/data-catalogue/?id=fuelprice&limit=1 

references : <br>
https://developer.data.gov.my/static-api/opendosm <br>
https://docs.oracle.com/javase/8/docs/api/java/util/logging/Handler.html <br>
https://docs.oracle.com/javase/8/docs/api/org/xml/sax/Parser.html <br>


<br>
MADE POSSIBLE BY AZMI PRODUCTIONS
<br>



Explaination below generated using blackbox AI : 
 # Petrol Station System

This is a Java program that simulates a petrol station with multiple dispensers. It allows users to purchase fuel, fill up dispensers, and view petrol levels. The program also fetches real-time fuel prices from an external API.

## Step-by-Step Explanation

### 1. Importing Necessary Libraries

```java
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
```

These lines import the necessary libraries and classes for the program to function.

### 2. `PetrolStation` Class

The `PetrolStation` class is the main class of the program. It contains the logic for purchasing fuel, filling up dispensers, and displaying petrol levels.

```java
public class PetrolStation {

    // Instance variables
    private double[] petrolLevels;
    private double[] fuelPrices;
    private final double MIN_PETROL_LEVEL = 0.1;
    private int receiptNumber = 1;

    // Constructor
    public PetrolStation(int numDispensers) {
        petrolLevels = new double[numDispensers];
        fuelPrices = new double[numDispensers];
        updateFuelPrices();
        for (int i = 0; i < numDispensers; i++) {
            petrolLevels[i] = 100.0;
        }
    }

    // Methods
    public void purchaseFuel(int dispenser, double amount, String fuelType) {
        // ...
    }

    public void fillUpDispenser(int dispenser) {
        // ...
    }

    private double getFuelPrice(String fuelType) {
        // ...
    }

    private void printReceipt(int currentReceiptNumber, int dispenser, double litres, String fuelType, double totalPrice) {
        // ...
    }

    public void displayPetrolLevels() {
        // ...
    }

    private void updateFuelPrices() {
        // ...
    }

    private double parseFuelPriceFromJson(String jsonResponse, String fuelType) {
        // ...
    }
