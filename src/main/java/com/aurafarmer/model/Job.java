package com.aurafarmer.model;

public class Job {
    private final int id;
    private final String name;
    private final int salary;
    private final int requiredShifts;
    private final int cooldownMinutes;

    public Job(int id, String name, int salary, int requiredShifts, int cooldownMinutes) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.requiredShifts = requiredShifts;
        this.cooldownMinutes = cooldownMinutes;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getSalary() { return salary; }
    public int getRequiredShifts() { return requiredShifts; }
    public int getCooldownMinutes() { return cooldownMinutes; }
}
