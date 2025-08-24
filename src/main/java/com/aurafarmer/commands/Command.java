package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;

// An abstract class that defines the structure for all game commands.
// This is a core OOP principle: defining a contract for a family of objects.
public abstract class Command {
    protected final GameService gameService;

    public Command(GameService gameService) {
        this.gameService = gameService;
    }

    public abstract String getName();
    public abstract String getDescription();
    public abstract void execute(User user);
}
