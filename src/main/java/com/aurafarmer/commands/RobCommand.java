package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import java.util.Scanner;

public class RobCommand extends Command {

    public RobCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "rob";
    }

    @Override
    public String getDescription() {
        return "🎭 Attempt to steal Auras from another player.";
    }

    @Override
    public void execute(User user) {
        System.out.print("Enter the username of the player you want to rob: ");
        Scanner scanner = new Scanner(System.in);
        String targetUsername = scanner.nextLine();

        gameService.robPlayer(user, targetUsername);
    }
}
