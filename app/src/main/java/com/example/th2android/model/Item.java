package com.example.th2android.model;

import java.io.Serializable;

public class Item implements Serializable {
    private String id;
    private String title,category,price,date,image;

    public Item(String id, String title, String category, String price, String date, String image) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.date = date;
        this.image = image;
    }

    public Item() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

