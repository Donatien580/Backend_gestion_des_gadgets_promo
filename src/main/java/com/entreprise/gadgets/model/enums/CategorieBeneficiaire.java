package com.entreprise.gadgets.model.enums;

public enum CategorieBeneficiaire {
    CHEF_DEPARTEMENT("Chef de département", true),
    CHEF_SERVICE("Chef de service", true),
    CHEF_SECTION("Chef de section", true),
    CHEF_DIVISION("Chef de division", true),
    PERSONNEL("Personnels", false);

    private final String libelle;
    private final boolean chef;

    CategorieBeneficiaire(String libelle, boolean chef) {
        this.libelle = libelle;
        this.chef = chef;
    }

    public String getLibelle() { return libelle; }
    public boolean isChef() { return chef; }
}