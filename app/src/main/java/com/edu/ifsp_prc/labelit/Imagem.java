package com.edu.ifsp_prc.labelit;

import java.util.ArrayList;

public class Imagem {
    private String database;
    private String filename;
    private String height;
    private String width;
    private ArrayList<Rotulo> rotulos = new ArrayList<Rotulo>();

    public Imagem() {}

    public String getDatabase() {
        return database;
    }

    public String getFilename() {
        return filename;
    }

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public ArrayList<Rotulo> getRotulos() {
        return rotulos;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setRotulos(ArrayList<Rotulo> rotulos) {
        this.rotulos = rotulos;
    }
}
