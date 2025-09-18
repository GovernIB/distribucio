package es.caib.distribucio.logic.intf.dto.estadistic;

public enum DimEnum {
    ENT ("Entitat", "Codi de l'entitat a la que pertany la comunicació/notificació"),
    UNT ("Unitat Organitzativa", "Unitat organitzativa al que pertany l'anotació"),
    BST ("Bústia", "Bústia de la qual es volen consultar els permisos"),
    TIP ("Tipus", "Tipus de dades (context d'agregació): DIARI o MENSUAL");

    private String nom;
    private String descripcio;

    DimEnum(String nom, String descripcio) {
        this.nom = nom;
        this.descripcio = descripcio;
    }

    public String getNom() {
        return nom;
    }
    public String getDescripcio() {
        return descripcio;
    }
}