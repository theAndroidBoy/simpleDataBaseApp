package com.easyapps.simpleDataBase.data;

public class SinglePet {
    int id;
    String name;
    String breed;
    int gender;
    int measurement;

    public SinglePet(int id, String name, String breed, int gender, int measurement) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.measurement = measurement;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public int getGender() {
        return gender;
    }

    public int getMeasurement() {
        return measurement;
    }
}