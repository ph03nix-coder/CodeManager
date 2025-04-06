package com.ph03nix.functionalproject;

import com.ph03nix.functionalproject.Security.UniqueCode;

public class GestorVentas {
    private final int id;
    private final String ci;
    private final String name;
    private final String uc;

    public GestorVentas(int id, String ci, String name, String uc) {
        this.id = id;
        this.ci = ci;
        this.name = name;
        this.uc = uc;
    }

    public static GestorVentas createNew(String ci, String name) {
        String uc = new UniqueCode().generateUniqueCode(ci);
        return new GestorVentas(-1, ci, name, uc);
    }

    public int getId() {
        return id;
    }

    public String getCi() {
        return ci;
    }

    public String getName() {
        return name;
    }

    public String getUniqueCode() {
        return this.uc;
    }

}