// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/ProfileCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.model.UserJob;
import com.aurafarmer.model.UserInventoryItem; // Import UserInventoryItem
import com.aurafarmer.service.GameService;

public class ProfileCommand extends Command {

    public ProfileCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public String getDescription() {
        return "👤 View your player profile and stats.";
    }

    @Override
    public void execute(User user) {
        UserJob currentJob = user.getCurrentJob();
        String jobName = currentJob != null ? currentJob.getJob().getName() : "Unemployed";
        String shifts = currentJob != null ? String.valueOf(currentJob.getShiftsWorked()) : "N/A";

        // Calculate total items (sum of quantities)
        int totalItems = user.getInventory().values().stream()
                .mapToInt(UserInventoryItem::getQuantity)
                .sum();

        String title = "👤 Player Profile";
        String line1 = "Username: " + user.getUsername();
        String line2 = "💰 Aura Balance: " + user.getAuraBalance();
        String line3 = "🎒 Items Count: " + totalItems; // Changed label and logic
        String line4 = "💼 Job: " + jobName + " (Shifts: " + shifts + ")";
        String line5 = "📈 Total Shifts Worked: " + user.getTotalShiftsWorked();

        int maxWidth = Math.max(title.length(), Math.max(line1.length(), Math.max(line2.length(), Math.max(line3.length(), Math.max(line4.length(), line5.length())))));
        int boxWidth = maxWidth + 4;

        System.out.println("\n╔" + "═".repeat(boxWidth) + "╗");
        System.out.println(getFormattedLine(title, boxWidth));
        System.out.println("╠" + "═".repeat(boxWidth) + "╣");
        System.out.println(getFormattedLine(line1, boxWidth));
        System.out.println(getFormattedLine(line2, boxWidth));
        System.out.println(getFormattedLine(line3, boxWidth));
        System.out.println(getFormattedLine(line4, boxWidth));
        System.out.println(getFormattedLine(line5, boxWidth));
        System.out.println("╚" + "═".repeat(boxWidth) + "╝");
    }

    private String getFormattedLine(String text, int totalWidth) {
        int padding = totalWidth - text.length();
        return "║ " + text + " ".repeat(padding - 1) + "║";
    }
}