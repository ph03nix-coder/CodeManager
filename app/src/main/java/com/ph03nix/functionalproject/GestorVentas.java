package com.ph03nix.functionalproject;

import com.ph03nix.functionalproject.Security.UniqueCode;

public class GestorVentas {
    private final int id;
    private final String name;
    private final String uc;

    public GestorVentas(int id, String name, String uc) {
        this.id = id;
        this.name = name;
        this.uc = uc;
    }

    public static GestorVentas createNew(int id, String name) {
        String uc = new UniqueCode().generateUniqueCode(id);
        return new GestorVentas(id, name, uc);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUniqueCode() {
        return this.uc;
    }

}