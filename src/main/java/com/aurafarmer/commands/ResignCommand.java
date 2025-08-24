package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import java.util.Scanner;

public class ResignCommand extends Command {

    public ResignCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "resign";
    }

    @Override
    public String getDescription() {
        return "👋 Quit your current job.";
    }

    @Override
    public void execute(User user) {
        if (user.getCurrentJob() == null) {
            System.out.println("You don't have a job to resign from.");
            return;
        }

        System.out.print("Are you sure you want to resign from your job as a " + user.getCurrentJob().getJob().getName() + "? (y/n): ");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y")) {
            gameService.resignJob(user);
        } else {
            System.out.println("You decided to keep your job.");
        }
    }
}
