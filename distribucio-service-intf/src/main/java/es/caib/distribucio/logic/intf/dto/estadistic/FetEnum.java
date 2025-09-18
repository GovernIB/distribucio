package es.caib.distribucio.logic.intf.dto.estadistic;

public enum FetEnum {
	
	// Anotacions
    ANT_UO_NOV ("Anotacions noves", "Anotacions noves per unitat organitzativa"),
    ANT_UO_TOT ("Anotacions noves totals", "Anotacions totals per unitat organitzativa"),
    ANT_UO_RNV ("Anotacions reenviades", "Anotacions reenviades per unitat organitzativa"),
    ANT_UO_EML ("Anotacions enviades per email", "Anotacions enviades per email per unitat organitzativa"),
    ANT_UO_JST ("Justificants per unitat organitzativa", "Justificant creats per unitat organitzativa"),
    ANT_UO_ANX ("Annexos per unitat organitzativa", "Annexos creats per unitat organitzativa"),
    ANT_UO_BST ("Bústies unitat organitzativa", "Bústies per unitat organitzativa"),
    ANT_UO_USR ("Usuaris unitat organitzativa", "Usuaris per unitat organitzativa"),
    
    // Anotacions per ESTAT
    
    // ARXIU_PENDENT
    ANT_ARX_PND_OKY("Anotacions correctes processades per l'estat 'Pendent de guardar annexos'", "Les anotacions correctes processades per l'estat 'Pendent de guardar annexos'"),
    ANT_ARX_PND_OKY_TOT("Anotacions correctes totals per l'estat 'Pendent de guardar annexos'", "Les anotacions correctes totals fins la data per l'estat 'Pendent de guardar annexos'"),
    ANT_ARX_PND_ERR("Anotacions amb error per l'estat 'Pendent de guardar annexos'", "Les anotacions amb error processades per l'estat 'Pendent de guardar annexos'"),
    ANT_ARX_PND_ERR_TOT("Anotacions amb error totals per l'estat 'Pendent de guardar annexos'", "Les anotacions amb error totals fins la data per l'estat 'Pendent de guardar annexos'"),
    ANT_ARX_PND_EST_TOT("Anotacions totals per l'estat 'Pendent de guardar annexos'", "Les anotacions totals amb i sense error per l'estat 'Pendent de guardar annexos'"),

    // REGLA_PENDENT
    ANT_REG_PND_OKY("Anotacions correctes processades per l'estat 'Pendent d'aplicar regla'", "Les anotacions correctes processades per l'estat 'Pendent d'aplicar regla'"),
    ANT_REG_PND_OKY_TOT("Anotacions correctes totals per l'estat 'Pendent d'aplicar regla'", "Les anotacions correctes totals fins la data per l'estat 'Pendent d'aplicar regla'"),
    ANT_REG_PND_ERR("Anotacions amb error per l'estat 'Pendent d'aplicar regla'", "Les anotacions amb error processades per l'estat 'Pendent d'aplicar regla'"),
    ANT_REG_PND_ERR_TOT("Anotacions amb error totals per l'estat 'Pendent d'aplicar regla'", "Les anotacions amb error totals fins la data per l'estat 'Pendent d'aplicar regla'"),
    ANT_REG_PND_EST_TOT("Anotacions totals per l'estat 'Pendent d'aplicar regla'", "Les anotacions totals amb i sense error per l'estat 'Pendent d'aplicar regla'"),

    // BUSTIA_PENDENT
    ANT_BST_PND_OKY("Anotacions correctes processades per l'estat 'Pendent a bústia'", "Les anotacions correctes processades per l'estat 'Pendent a bústia'"),
    ANT_BST_PND_OKY_TOT("Anotacions correctes totals per l'estat 'Pendent a bústia'", "Les anotacions correctes totals fins la data per l'estat 'Pendent a bústia'"),
    ANT_BST_PND_ERR("Anotacions amb error per l'estat 'Pendent a bústia'", "Les anotacions amb error processades per l'estat 'Pendent a bústia'"),
    ANT_BST_PND_ERR_TOT("Anotacions amb error totals per l'estat 'Pendent a bústia'", "Les anotacions amb error totals fins la data per l'estat 'Pendent a bústia'"),
    ANT_BST_PND_EST_TOT("Anotacions totals per l'estat 'Pendent a bústia'", "Les anotacions totals amb i sense error per l'estat 'Pendent a bústia'"),

    // BUSTIA_PROCESSADA
    ANT_BST_PRC_OKY("Anotacions correctes processades per l'estat 'Distribuït marcant com a processat'", "Les anotacions correctes processades per l'estat 'Distribuït marcant com a processat'"),
    ANT_BST_PRC_OKY_TOT("Anotacions correctes totals per l'estat 'Distribuït marcant com a processat'", "Les anotacions correctes totals fins la data per l'estat 'Distribuït marcant com a processat'"),
    ANT_BST_PRC_ERR("Anotacions amb error per l'estat 'Distribuït marcant com a processat'", "Les anotacions amb error processades per l'estat 'Distribuït marcant com a processat'"),
    ANT_BST_PRC_ERR_TOT("Anotacions amb error totals per l'estat 'Distribuït marcant com a processat'", "Les anotacions amb error totals fins la data per l'estat 'Distribuït marcant com a processat'"),
    ANT_BST_PRC_EST_TOT("Anotacions totals per l'estat 'Distribuït marcant com a processat'", "Les anotacions totals amb i sense error per l'estat 'Distribuït marcant com a processat'"),

    // BACK_PENDENT
    ANT_BCK_PND_OKY("Anotacions correctes processades per l'estat 'Anotació pendent d’enviar al backoffice'", "Les anotacions correctes processades per l'estat 'Anotació pendent d’enviar al backoffice'"),
    ANT_BCK_PND_OKY_TOT("Anotacions correctes totals per l'estat 'Anotació pendent d’enviar al backoffice'", "Les anotacions correctes totals fins la data per l'estat 'Anotació pendent d’enviar al backoffice'"),
    ANT_BCK_PND_ERR("Anotacions amb error per l'estat 'Anotació pendent d’enviar al backoffice'", "Les anotacions amb error processades per l'estat 'Anotació pendent d’enviar al backoffice'"),
    ANT_BCK_PND_ERR_TOT("Anotacions amb error totals per l'estat 'Anotació pendent d’enviar al backoffice'", "Les anotacions amb error totals fins la data per l'estat 'Anotació pendent d’enviar al backoffice'"),
    ANT_BCK_PND_EST_TOT("Anotacions totals per l'estat 'Anotació pendent d’enviar al backoffice'", "Les anotacions totals amb i sense error per l'estat 'Anotació pendent d’enviar al backoffice'"),

    // BACK_COMUNICADA
    ANT_BCK_COM_OKY("Anotacions correctes processades per l'estat 'Anotació comunicada al backoffice'", "Les anotacions correctes processades per l'estat 'Anotació comunicada al backoffice'"),
    ANT_BCK_COM_OKY_TOT("Anotacions correctes totals per l'estat 'Anotació comunicada al backoffice'", "Les anotacions correctes totals fins la data per l'estat 'Anotació comunicada al backoffice'"),
    ANT_BCK_COM_ERR("Anotacions amb error per l'estat 'Anotació comunicada al backoffice'", "Les anotacions amb error processades per l'estat 'Anotació comunicada al backoffice'"),
    ANT_BCK_COM_ERR_TOT("Anotacions amb error totals per l'estat 'Anotació comunicada al backoffice'", "Les anotacions amb error totals fins la data per l'estat 'Anotació comunicada al backoffice'"),
    ANT_BCK_COM_EST_TOT("Anotacions totals per l'estat 'Anotació comunicada al backoffice'", "Les anotacions totals amb i sense error per l'estat 'Anotació comunicada al backoffice'"),

    // BACK_REBUDA
    ANT_BCK_REB_OKY("Anotacions correctes processades per l'estat 'Anotació rebuda al backoffice'", "Les anotacions correctes processades per l'estat 'Anotació rebuda al backoffice'"),
    ANT_BCK_REB_OKY_TOT("Anotacions correctes totals per l'estat 'Anotació rebuda al backoffice'", "Les anotacions correctes totals fins la data per l'estat 'Anotació rebuda al backoffice'"),
    ANT_BCK_REB_ERR("Anotacions amb error per l'estat 'Anotació rebuda al backoffice'", "Les anotacions amb error processades per l'estat 'Anotació rebuda al backoffice'"),
    ANT_BCK_REB_ERR_TOT("Anotacions amb error totals per l'estat 'Anotació rebuda al backoffice'", "Les anotacions amb error totals fins la data per l'estat 'Anotació rebuda al backoffice'"),
    ANT_BCK_REB_EST_TOT("Anotacions totals per l'estat 'Anotació rebuda al backoffice'", "Les anotacions totals amb i sense error per l'estat 'Anotació rebuda al backoffice'"),

    // BACK_PROCESSADA
    ANT_BCK_PRC_OKY("Anotacions correctes processades per l'estat 'Anotació processada correctament pel backoffice'", "Les anotacions correctes processades per l'estat 'Anotació processada correctament pel backoffice'"),
    ANT_BCK_PRC_OKY_TOT("Anotacions correctes totals per l'estat 'Anotació processada correctament pel backoffice'", "Les anotacions correctes totals fins la data per l'estat 'Anotació processada correctament pel backoffice'"),
    ANT_BCK_PRC_ERR("Anotacions amb error per l'estat 'Anotació processada correctament pel backoffice'", "Les anotacions amb error processades per l'estat 'Anotació processada correctament pel backoffice'"),
    ANT_BCK_PRC_ERR_TOT("Anotacions amb error totals per l'estat 'Anotació processada correctament pel backoffice'", "Les anotacions amb error totals fins la data per l'estat 'Anotació processada correctament pel backoffice'"),
    ANT_BCK_PRC_EST_TOT("Anotacions totals per l'estat 'Anotació processada correctament pel backoffice'", "Les anotacions totals amb i sense error per l'estat 'Anotació processada correctament pel backoffice'"),

    // BACK_REBUTJADA
    ANT_BCK_RBJ_OKY("Anotacions correctes processades per l'estat 'Anotació rebutjada pel backoffice'", "Les anotacions correctes processades per l'estat 'Anotació rebutjada pel backoffice'"),
    ANT_BCK_RBJ_OKY_TOT("Anotacions correctes totals per l'estat 'Anotació rebutjada pel backoffice'", "Les anotacions correctes totals fins la data per l'estat 'Anotació rebutjada pel backoffice'"),
    ANT_BCK_RBJ_ERR("Anotacions amb error per l'estat 'Anotació rebutjada pel backoffice'", "Les anotacions amb error processades per l'estat 'Anotació rebutjada pel backoffice'"),
    ANT_BCK_RBJ_ERR_TOT("Anotacions amb error totals per l'estat 'Anotació rebutjada pel backoffice'", "Les anotacions amb error totals fins la data per l'estat 'Anotació rebutjada pel backoffice'"),
    ANT_BCK_RBJ_EST_TOT("Anotacions totals per l'estat 'Anotació rebutjada pel backoffice'", "Les anotacions totals amb i sense error per l'estat 'Anotació rebutjada pel backoffice'"),

    // BACK_ERROR
    ANT_BCK_ERR_OKY("Anotacions correctes processades per l'estat 'Anotació processada al backoffice amb errors'", "Les anotacions correctes processades per l'estat 'Anotació processada al backoffice amb errors'"),
    ANT_BCK_ERR_OKY_TOT("Anotacions correctes totals per l'estat 'Anotació processada al backoffice amb errors'", "Les anotacions correctes totals fins la data per l'estat 'Anotació processada al backoffice amb errors'"),
    ANT_BCK_ERR_ERR("Anotacions amb error per l'estat 'Anotació processada al backoffice amb errors'", "Les anotacions amb error processades per l'estat 'Anotació processada al backoffice amb errors'"),
    ANT_BCK_ERR_ERR_TOT("Anotacions amb error totals per l'estat 'Anotació processada al backoffice amb errors'", "Les anotacions amb error totals fins la data per l'estat 'Anotació processada al backoffice amb errors'"),
    ANT_BCK_ERR_EST_TOT("Anotacions totals per l'estat 'Anotació processada al backoffice amb errors'", "Les anotacions totals amb i sense error per l'estat 'Anotació processada al backoffice amb errors'"),

    ANT_EST_UNKNOWN("Anotacions totals amb estat desconegut", "Les anotacions totals per un estat desconegut"),
    
	// Permisos bústies
	BST_PRM_TOT ("Permisos totals", "Els permisos totals que hi ha sobre una bústia"),
	BST_PRM_USR ("Usuaris amb permís directe", "El total d'usuaris que tenen donat permís directe sobre una bústia"),
	BST_PRM_ROL ("Usuaris amb permís per rol", "El total d'usuaris que tenen donat permís per rol sobre una bústia");
	
    private String nom;
    private String descripcio;
    
    FetEnum(String nom, String descripcio) {
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
