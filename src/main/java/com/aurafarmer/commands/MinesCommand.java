// File: aurafarmer_final/src/main/java/com/aurafarmer/commands/MinesCommand.java

package com.aurafarmer.commands;

import com.aurafarmer.model.User;
import com.aurafarmer.service.GameService;
import com.aurafarmer.util.GambleUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;

public class MinesCommand extends Command {

    public MinesCommand(GameService gameService) {
        super(gameService);
    }

    @Override
    public String getName() {
        return "mines";
    }

    @Override
    public String getDescription() {
        return "💣 Click tiles to find gems. Avoid the bombs!";
    }

    @Override
    public void execute(User user) {
        if (gameService.isCoolingDown(user, getName())) {
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int bet = GambleUtils.getBetAmount(scanner, user);
        if (bet <= 0) {
            if (bet == 0) System.out.println("You can't bet 0 Auras.");
            return;
        }

        int gridSize = 5;
        int totalTiles = gridSize * gridSize;
        int numBombs;

        while (true) {
            System.out.print("How many bombs (1-" + (totalTiles - 1) + ") do you want? ");
            try {
                numBombs = Integer.parseInt(scanner.nextLine());
                if (numBombs >= 1 && numBombs < totalTiles) {
                    break;
                } else {
                    System.out.println("❌ Invalid number of bombs. Please choose between 1 and " + (totalTiles - 1) + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }

        List<Boolean> board = createBoard(gridSize, numBombs);
        boolean[][] revealed = new boolean[gridSize][gridSize];
        int gemsFound = 0;
        boolean playing = true;

        System.out.println("💣 Mines! Try to find gems and avoid the " + numBombs + " bombs.");

        gameService.updateUserBalance(user, -bet);
        System.out.println("Your balance is now " + user.getAuraBalance() + " Auras after placing your bet.");


        while (playing) {
            displayBoard(revealed, gridSize);
            double currentMultiplier = getMultiplier(gemsFound, numBombs, gridSize);
            long potentialWinnings = Math.round(bet * currentMultiplier);

            if (gemsFound > 0) {
                System.out.printf("Gems found: %d. Current Multiplier: %.2fx. Potential Payout: %d Auras.\n",
                        gemsFound, currentMultiplier, potentialWinnings);
                System.out.print("Enter your next move (1-" + totalTiles + ") or type 'cashout' to quit: ");
            } else {
                System.out.print("Enter your first move (1-" + totalTiles + "): ");
            }

            String input = scanner.nextLine().toLowerCase();

            try {
                System.out.print("Revealing tile...");
                for (int i = 0; i < 4; i++) {
                    if (System.in.available() > 0) { // Check for input
                        System.out.println("\nAction cancelled.");
                        playing = false;
                        break;
                    }
                    Thread.sleep(1000);
                    System.out.print(".");
                }
                System.out.println();
                if(!playing) continue;
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                System.out.println("\nAction interrupted. Please enter a command.");
                continue;
            }

            if (input.equals("cashout") && gemsFound > 0) {
                gameService.updateUserBalance(user, (int) potentialWinnings);
                System.out.println("✅ Cashed out! You won back " + potentialWinnings + " Auras!");
                playing = false;
            } else if (input.matches("^(1[0-9]|2[0-5]|[1-9])$")) {
                int tileNumber = Integer.parseInt(input);
                if (tileNumber < 1 || tileNumber > totalTiles) {
                    System.out.println("❌ Invalid tile number. Please enter a number between 1 and " + totalTiles + ".");
                    continue;
                }

                int row = (tileNumber - 1) / gridSize;
                int col = (tileNumber - 1) % gridSize;

                if (revealed[row][col]) {
                    System.out.println("❌ That tile is already revealed. Choose another one.");
                } else {
                    int index = row * gridSize + col;
                    revealed[row][col] = true;

                    if (board.get(index)) {
                        System.out.println("\n💥 KABOOM! You hit a bomb!");
                        displayFinalBoard(board, revealed, gridSize);
                        System.out.println("😭 You lost your bet of " + bet + " Auras.");
                        playing = false;
                    } else {
                        gemsFound++;
                        if (gemsFound == (totalTiles - numBombs)) {
                            currentMultiplier = getMultiplier(gemsFound, numBombs, gridSize);
                            potentialWinnings = Math.round(bet * currentMultiplier);
                            gameService.updateUserBalance(user, (int) potentialWinnings);
                            System.out.println("🎉 Congratulations! You found all gems! You won back " + potentialWinnings + " Auras!");
                            playing = false;
                        } else {
                            System.out.println("💎 You found a gem! Keep going or cash out.");
                        }
                    }
                }
            } else {
                System.out.println("❌ Invalid input. Please enter a tile number between 1 and " + totalTiles + " or 'cashout'.");
            }
        }
        System.out.println("Your new balance is: " + user.getAuraBalance());
        gameService.setCooldown(user, getName(), 60);
    }

    private List<Boolean> createBoard(int gridSize, int numBombs) {
        List<Boolean> board = new ArrayList<>(Collections.nCopies(gridSize * gridSize, false));
        for (int i = 0; i < numBombs; i++) {
            board.set(i, true);
        }
        Collections.shuffle(board);
        return board;
    }

    private void displayBoard(boolean[][] revealed, int gridSize) {
        System.out.println("Current board:");
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (revealed[i][j]) {
                    System.out.print("[💎] ");
                } else {
                    System.out.printf("[%2d] ", (i * gridSize + j + 1));
                }
            }
            System.out.println();
        }
    }

    private void displayFinalBoard(List<Boolean> board, boolean[][] revealed, int gridSize) {
        System.out.println("Final board:");
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int index = i * gridSize + j;
                if (board.get(index)) {
                    System.out.print("[💥] ");
                } else if (revealed[i][j]) {
                    System.out.print("[💎] ");
                } else {
                    System.out.print("[⬜] ");
                }
            }
            System.out.println();
        }
    }

    private double getMultiplier(int gemsFound, int numBombs, int gridSize) {
        if (gemsFound == 0) return 1.0;

        int totalTiles = gridSize * gridSize;
        double currentMultiplier = 1.0;

        for (int i = 0; i < gemsFound; i++) {
            int safeTilesRemaining = (totalTiles - numBombs) - i;
            int unrevealedTiles = totalTiles - i;
            if (safeTilesRemaining > 0) {
                currentMultiplier *= (double)unrevealedTiles / safeTilesRemaining;
            }
        }
        return currentMultiplier;
    }
}