// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/JobsCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.Job;
import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import java.util.List;
import java.util.Scanner;

public class JobsCommand extends Command {

    public JobsCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "jobs";
    }

    @Override
    public String getDescription() {
        return "📋 View the list of available jobs and choose a career.";
    }

    @Override
    public void execute(User user) {
        // Check for cooldown before proceeding
        if (gameService.isCoolingDown(user, "job_application")) {
            System.out.println("❌ You recently resigned. You must wait before applying for a new job.");
            return;
        }

        // Check if the user already has a job
        if (user.getCurrentJob() != null) {
            System.out.println("❌ You already have a job! You must resign first with the 'resign' command.");
            return;
        }

        List<Job> jobs = gameService.getAvailableJobs();
        System.out.println("\n--- 📋 Job Listings ---");
        System.out.println("Your total shifts worked: " + user.getTotalShiftsWorked());

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            String qualification = user.getTotalShiftsWorked() >= job.getRequiredShifts() ? "✅" : "❌";
            System.out.printf("%d. %s %s - %d Auras/shift (Requires %d total shifts)\n",
                    (i + 1), qualification, job.getName(), job.getSalary(), job.getRequiredShifts());
        }
        System.out.println("-----------------------");
        System.out.print("Enter the number of the job you want to take (or 0 to cancel): ");

        Scanner scanner = new Scanner(System.in);
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                System.out.println("You decided not to look for a new job.");
                return;
            }
            if (choice > 0 && choice <= jobs.size()) {
                Job selectedJob = jobs.get(choice - 1);
                gameService.changeJob(user, selectedJob);
            } else {
                System.out.println("❓ Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❓ Please enter a valid number.");
        }
    }
}