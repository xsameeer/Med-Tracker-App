package com.example.medtrackerapp.model;

public class Medication {
    private int id; // Unique ID for each medication (Primary key)
    private String name; // Medication name
    private int dosage; // Dosage in mg
    private int frequency; // Frequency per day

    // Constructor
    public Medication(int id, String name, int dosage, int frequency) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
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

    // Optional: Override toString() for easy display of medication details
    @Override
    public String toString() {
        return "Medication{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dosage=" + dosage +
                " mg, frequency=" + frequency +
                "x/day}";
    }
}