package com.quanlycuahangxe.model;

public class Product {

    private int id;
    private String name;
    private int brandId;
    private int categoryId;
    private double price;
    private int stockQuantity;
    private String description;

    public Product() {
    }

    // Constructor đầy đủ (có id)
    public Product(int id, String name, int brandId, int categoryId, double price, int stockQuantity, String description) {
        this.id = id;
        this.name = name;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    // Constructor tiện lợi khi tạo mới (id chưa có)
    public Product(String name, int brandId, int categoryId, double price, int stockQuantity, String description) {
        this.name = name;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    // Getter & Setter
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

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
