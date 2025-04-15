package tn.entities;

import java.time.LocalDate;

public class Course {

    private int id;
    private String NomCours;
    private String description;
    private String duree;
    private String type;
    private String Filename;
    private String objectifs;
    private LocalDate date;
    private String image;
    private Float price;
    // Constructor
    public Course(){}
    public Course(int id, String NomCours, String description, String duree, String type, String Filename, String objectifs, LocalDate date, String image, Float price) {
        this.id = id;
        this.NomCours = NomCours;
        this.description = description;
        this.duree = duree;
        this.type = type;
        this.Filename = Filename;
        this.objectifs = objectifs;
        this.date = date;
        this.image = image;
        this.price = price;
    }
    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomCours() {
        return NomCours;
    }

    public void setNomCours(String NomCours) {
        this.NomCours = NomCours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String Filename) {
        this.Filename = Filename;
    }

    public String getObjectifs() {
        return objectifs;
    }

    public void setObjectifs(String objectifs) {
        this.objectifs = objectifs;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Float getPrice() {
        return price;
    }
    public void setPrice(Float price) {
        this.price = price;
    }

}

