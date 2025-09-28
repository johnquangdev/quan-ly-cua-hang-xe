/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.model;

/**
 *
 * @author gunnguyen
 */
public class Brand {

    private int id;
    private String name;
    private String description;

    public Brand() {
    }

    @Override
    public String toString() {
        return name; 
    }

    public Brand(int id, String name, String description) {
        this.id = id;
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
