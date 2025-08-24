package com.aurafarmer.menus;

import com.aurafarmer.commands.Command;
import com.aurafarmer.commands.JobsCommand;
import com.aurafarmer.commands.ResignCommand;
import com.aurafarmer.commands.WorkCommand;
import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WorkMenu {
    private final User currentUser;
    private final GameService gameService;
    private final List<Command> commands = new ArrayList<>();

    public WorkMenu(User user, GameService gameService) {
        this.currentUser = user;
        this.gameService = gameService;
        initializeCommands();
    }

    private void initializeCommands() {
        commands.add(new WorkCommand(gameService));
        commands.add(new JobsCommand(gameService));
        commands.add(new ResignCommand(gameService));
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n--- 💼 Work & Careers ---");
            String currentJob = currentUser.getCurrentJob() != null ? currentUser.getCurrentJob().getJob().getName() : "Unemployed";
            System.out.println("Current Job: " + currentJob);

            for (int i = 0; i < commands.size(); i++) {
                Command cmd = commands.get(i);
                System.out.printf("%d. %-11s : %s\n", (i + 1), cmd.getName(), cmd.getDescription());
            }
            System.out.println("--------------------------");
            System.out.print("Enter a command number (or 0 to go back): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) {
                    inMenu = false;
                    continue;
                }
                if (choice > 0 && choice <= commands.size()) {
                    commands.get(choice - 1).execute(currentUser);
                    clearInputBuffer();
                } else {
                    System.out.println("❓ Invalid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❓ Please enter a valid number.");
            }
        }
    }

    private void clearInputBuffer() {
        try {
            while (System.in.available() > 0) { System.in.read(); }
        } catch (IOException e) { /* Ignore */ }
    }
}
