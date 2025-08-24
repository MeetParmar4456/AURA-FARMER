package com.aurafarmer;

import com.aurafarmer.model.User;
import com.aurafarmer.service.AuthService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();
        boolean isRunning = true;

        System.out.println("🌾 Welcome to Aura Farmer! 🌾");

        while (isRunning) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login 🔑");
            System.out.println("2. Register ✨");
            System.out.println("3. Exit 🚪");
            System.out.print("Please choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLogin(scanner, authService);
                    break;
                case "2":
                    handleRegister(scanner, authService);
                    break;
                case "3":
                    isRunning = false;
                    System.out.println("Thank you for playing Aura Farmer. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void handleLogin(Scanner scanner, AuthService authService) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = authService.login(username, password);

        if (user != null) {
            System.out.println("\n✅ Login successful! Welcome back, " + user.getUsername() + "!");

            GameDashboard dashboard = new GameDashboard(user);
            dashboard.start();

        } else {
            System.out.println("\n❌ Login failed. Please check your username and password.");
        }
    }

    private static void handleRegister(Scanner scanner, AuthService authService) {
        System.out.print("Enter a new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter a new password: ");
        String password = scanner.nextLine();

        boolean success = authService.register(username, password);

        if (success) {
            System.out.println("\n✅ Registration successful! You can now log in.");
        } else {
            System.out.println("\n❌ Registration failed. The username might be taken or your input is invalid.");
        }
    }
}