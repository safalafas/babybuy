package com.safal.babybuy;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Item implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int id;
    String name;
    String price;
    String description;
    byte[] image;
    String saveDate;
    String status;

    @Ignore
    public Item() {
    }

    Item(String name, String price, String description, byte[] image, String saveDate, String status) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.saveDate = saveDate;
        this.status = status;
    }

    @Ignore
    Item(int id, String name, String price, String description, byte[] image, String saveDate, String status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.saveDate = saveDate;
        this.status = status;
    }

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(String saveDate) {
        this.saveDate = saveDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
