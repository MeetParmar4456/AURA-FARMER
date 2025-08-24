package com.aurafarmer.model;

public class UserJob {
    private final Job job;
    private int shiftsWorked;

    public UserJob(Job job, int shiftsWorked) {
        this.job = job;
        this.shiftsWorked = shiftsWorked;
    }

    public Job getJob() { return job; }
    public int getShiftsWorked() { return shiftsWorked; }
    public void incrementShifts() { this.shiftsWorked++; }
}
