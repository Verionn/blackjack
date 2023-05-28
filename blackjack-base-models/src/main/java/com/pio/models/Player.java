package com.pio.models;

public class Player {
    private int accountBalance;

    private int betAmount;

    public Player(int accountBalance) {
        this.accountBalance = accountBalance;
    }

    public void placeBet(int amount) {
        betAmount += amount;
        accountBalance -= amount;
    }
}
