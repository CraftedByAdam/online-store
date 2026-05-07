package com.pluralsight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Store {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";


    public static void main(String[] args) {
        displayStoreName();

        // Create lists for inventory and the shopping cart
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        // Load inventory from the data file (pipe-delimited: id|name|price)
        loadInventory("products.csv", inventory);

        // Main menu loop
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to " + RED + "One-Stop Shop" + RESET + "!");
            System.out.println(YELLOW + "1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit" + RESET);
            System.out.print(BLUE + "Your choice: " + RESET);

            if (!scanner.hasNextInt()) {
                System.out.println(RED + "Please enter 1, 2, or 3." + RESET);
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> displayProducts(inventory, cart, scanner);
                case 2 -> displayCart(cart, scanner);
                case 3 -> System.out.println(GREEN + "Thank you for shopping with us!" + RESET);
                default -> System.out.println(RED + "Invalid choice!" + RESET);
            }
        }
        scanner.close();
    }

    /**
     * Reads product data from a file and populates the inventory list.
     * File format (pipe-delimited):
     * id|name|price
     * <p>
     * Example line:
     * A17|Wireless Mouse|19.99
     */
    public static void loadInventory(String fileName, ArrayList<Product> inventory) {
        File file = new File(fileName);

        //check if file exist
        if (!file.exists()) {
            System.out.println(RED + "File " + fileName + " Not Found" + RESET);
            //exits here and lets user know file was not fund right away so it doesn't have to go through the try catch
            return;
           }
            //read the file
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                String productId = data[0];
                String productName = data[1];
                double price = Double.parseDouble(data[2]);

                Product product = new Product(productId, productName, price);
                inventory.add(product);
            }
        }catch (Exception e) {
            System.out.println(RED + "Error opening file! " + RESET + e.getMessage());
        }
    }

    /**
     * Displays all products and lets the user add one to the cart.
     * Typing X returns to the main menu.
     */
    public static void displayProducts(ArrayList<Product> inventory, ArrayList<Product> cart, Scanner scanner) {

        for (Product product : inventory) {
            String formatted = String.format(PURPLE + "%s|%s|$%.2f" + RESET, product.getProductId(), product.getProductName(), product.getProductPrice());
            System.out.println(formatted);
        }

        String productId;
        boolean running = true;

        while (running) {
            //reset for new input
            boolean found = false;
            System.out.println("\nAdd Product to cart");
            System.out.print(BLUE + "Enter product ID or enter (X) for main menu: " + RESET);
            productId = scanner.nextLine();

            if (productId.equalsIgnoreCase("X")) {
                    running = false;
            }

            //find the product
            Product product = findProductById(productId,  inventory);

            //if found add it to cart
            if (product != null) {
                cart.add(product);
                found = true;
            }

            //show if nothing matched and the input isn't "X"
            if  (!found && !productId.equalsIgnoreCase("X")) {
                System.out.println(RED + "Product not found!"  + RESET);
            }
        }
    }

    /**
     * Shows the contents of the cart, calculates the total,
     * and offers the option to check out.
     */
    public static void displayCart(ArrayList<Product> cart, Scanner scanner) {

        double totalAmount = 0.0;

        if (cart.isEmpty()) {
            System.out.println(RED + "\nNo items in Cart" + RESET);
            return;
        }
        System.out.println(BLUE + "\nItems in Cart" + RESET);
        for (Product product : cart) {
            System.out.println(PURPLE + product.getProductName() + RESET+ RED + " - " + product.getProductPrice() + RESET);

            totalAmount += product.getProductPrice();
        }
        String formattedTotalAmount = String.format("$%.2f", totalAmount);
        System.out.println(GREEN + "Total Cost: " + formattedTotalAmount + RESET + "\n");

        boolean running = true;
        String userInput;
        while (running) {
            System.out.print(BLUE + "To check out enter(C) or (X)for main menu: " + RESET );
            userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("X")) {
                running = false;
            }
            if (userInput.equalsIgnoreCase("C")) {
                checkOut(cart, totalAmount, scanner);
                running = false;
            }
        }
    }

    /**
     * Handles the checkout process:
     * 1. Confirm that the user wants to buy.
     * 2. Accept payment and calculate change.
     * 3. Display a simple receipt.
     * 4. Clear the cart.
     */
    public static void checkOut(ArrayList<Product> cart, double totalAmount, Scanner scanner) {
        double changeAmount;
        double userInput;

        boolean running = true;
        while (running) {
            System.out.println("Total Cost: " + totalAmount);
            System.out.print(BLUE + "Enter Payment: " + RESET);
            userInput = scanner.nextDouble();

            if (userInput == totalAmount) {
                cart.clear();
                System.out.println(GREEN + "\nThank you for shopping with us!" + RESET);
                running = false;
            } else if (userInput > totalAmount) {
                cart.clear();
                changeAmount  = userInput - totalAmount;
                String formattedChangeAmount = String.format("$%.2f", changeAmount);
                System.out.println("Thank you for  shopping with us! Here is your change " + RED + formattedChangeAmount + RESET);
                running = false;
            }else if (userInput < totalAmount) {
                System.out.println(RED + "Insufficient Payment! Please try again." + RESET);
            }
        }
    }

    /**
     * Searches a list for a product by its id.
     *
     * @return the matching Product, or null if not found
     */
    public static Product findProductById(String id, ArrayList<Product> inventory) {

        for (Product product : inventory) {
            if (product.getProductId().equalsIgnoreCase(id)) {
                System.out.println(GREEN + "Item added to cart!" + RESET);
                return product;
            }
        }
        return null;
    }

    private static void displayStoreName() {
        String storeName ="""
                 ________  ________   _______                  ________  _________  ________  ________        ________  ___  ___  ________  ________  \s
                |\\   __  \\|\\   ___  \\|\\  ___ \\                |\\   ____\\|\\___   ___\\\\   __  \\|\\   __  \\      |\\   ____\\|\\  \\|\\  \\|\\   __  \\|\\   __  \\ \s
                \\ \\  \\|\\  \\ \\  \\\\ \\  \\ \\   __/|   ____________\\ \\  \\___|\\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\|\\  \\     \\ \\  \\___|\\ \\  \\\\\\  \\ \\  \\|\\  \\ \\  \\|\\  \\\s
                 \\ \\  \\\\\\  \\ \\  \\\\ \\  \\ \\  \\_|/__|\\____________\\ \\_____  \\   \\ \\  \\ \\ \\  \\\\\\  \\ \\   ____\\     \\ \\_____  \\ \\   __  \\ \\  \\\\\\  \\ \\   ____\\
                  \\ \\  \\\\\\  \\ \\  \\\\ \\  \\ \\  \\_|\\ \\|____________|\\|____|\\  \\   \\ \\  \\ \\ \\  \\\\\\  \\ \\  \\___|      \\|____|\\  \\ \\  \\ \\  \\ \\  \\\\\\  \\ \\  \\___|
                   \\ \\_______\\ \\__\\\\ \\__\\ \\_______\\               ____\\_\\  \\   \\ \\__\\ \\ \\_______\\ \\__\\           ____\\_\\  \\ \\__\\ \\__\\ \\_______\\ \\__\\  \s
                    \\|_______|\\|__| \\|__|\\|_______|              |\\_________\\   \\|__|  \\|_______|\\|__|          |\\_________\\|__|\\|__|\\|_______|\\|__|  \s
                                                                 \\|_________|                                   \\|_________|                          \s
                """;
        String red = "\u001B[31m";
        String reset = "\u001B[0m";
        System.out.println(red + storeName + reset);
    }
}

