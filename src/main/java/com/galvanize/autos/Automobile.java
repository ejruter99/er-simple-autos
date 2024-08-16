package com.galvanize.autos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.util.Date;

@SuppressWarnings("JpaDataSourceOORMInspection")
@Entity// This class reprisents persistant date
@Table(name = "automobiles")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Automobile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Model Year")
    private int year;
    private String make;
    private String model;
    private String color;
    private String owner;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date purchseDate;
    private String vin;

    public Automobile() {
    }

    @Override
    public String toString() {
        return "Automobile{" +
                "id=" + id +
                ", year=" + year +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", owner='" + owner + '\'' +
                ", purchseDate=" + purchseDate +
                ", vin='" + vin + '\'' +
                '}';
    }

    public Automobile(int year, String model, String make, String vin) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.vin = vin;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public boolean isEmpty() {
        return false;
    }
}