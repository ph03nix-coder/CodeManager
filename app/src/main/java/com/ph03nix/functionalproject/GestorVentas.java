package com.ph03nix.functionalproject;

public class GestorVentas {
    private final int id;
    private final String name;
    private final UniqueCode uc;

    public GestorVentas(int id, String name, UniqueCode uc) {
        this.id = id;
        this.name = name;
        this.uc = uc;
    }

    public static GestorVentas createNew(int id, String name, String address) {
        return new GestorVentas(id, name, UniqueCode.createNew());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UniqueCode getUniqueCode() {
        return this.uc;
    }

}