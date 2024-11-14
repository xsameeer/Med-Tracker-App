package com.example.medtrackerapp.model;

public class Medication {
    private int id; // Unique ID for each medication (Primary key)
    private String name; // Medication name
    private int dosage; // Dosage in mg
    private int frequency; // Frequency per day
    private String daysOfWeek; // Selected days of the week as a comma-separated string
    private String endDate; // End date for reminders
    private boolean indefinite; // Indicates if the reminder should continue indefinitely

    // Constructor
    public Medication(int id, String name, int dosage, int frequency, String daysOfWeek, String endDate, boolean indefinite) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.daysOfWeek = daysOfWeek;
        this.endDate = endDate;
        this.indefinite = indefinite;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isIndefinite() {
        return indefinite;
    }

    public void setIndefinite(boolean indefinite) {
        this.indefinite = indefinite;
    }

    // Optional: Override toString() for easy display of medication details
    @Override
    public String toString() {
        return "Medication{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dosage=" + dosage +
                " mg, frequency=" + frequency +
                "x/day, daysOfWeek='" + daysOfWeek + '\'' +
                ", endDate='" + endDate + '\'' +
                ", indefinite=" + indefinite +
                '}';
    }
}