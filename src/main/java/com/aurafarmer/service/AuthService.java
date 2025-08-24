package com.aurafarmer.service;

import com.aurafarmer.db.DatabaseManager;
import com.aurafarmer.model.User;
import com.aurafarmer.util.ValidationUtils;

// This is the "Service" layer. It contains the business logic.
public class AuthService {

    private final DatabaseManager dbManager;

    public AuthService() {
        this.dbManager = new DatabaseManager();
    }

    public boolean register(String username, String password) {
        if (!ValidationUtils.isValidInput(username) || !ValidationUtils.isValidInput(password)) {
            System.out.println("Error: Username and password must be 4-30 characters and can only contain letters, numbers, '@', or '_'.");
            return false;
        }
        return dbManager.registerUser(username, password);
    }

    public User login(String username, String password) {
        if (!ValidationUtils.isValidInput(username) || !ValidationUtils.isValidInput(password)) {
            System.out.println("Invalid username or password format.");
            return null;
        }
        return dbManager.validateLogin(username, password);
    }
}
