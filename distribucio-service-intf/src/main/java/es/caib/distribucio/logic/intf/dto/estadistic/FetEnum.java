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
	ANT_ARX_PND_OKY("Correctes pendents d'annexos", "Les anotacions correctes processades per l'estat 'Pendent de guardar annexos'"),
	ANT_ARX_PND_OKY_TOT("Totals correctes pendents annexos", "Les anotacions correctes totals fins la data per l'estat 'Pendent de guardar annexos'"),
	ANT_ARX_PND_ERR("Errors pendents d'annexos", "Les anotacions amb error processades per l'estat 'Pendent de guardar annexos'"),
	ANT_ARX_PND_ERR_TOT("Totals errors pendents annexos", "Les anotacions amb error totals fins la data per l'estat 'Pendent de guardar annexos'"),
	ANT_ARX_PND_EST_TOT("Totals anotacions pendents annexos", "Les anotacions totals amb i sense error per l'estat 'Pendent de guardar annexos'"),

	// REGLA_PENDENT
	ANT_REG_PND_OKY("Correctes pendents d'aplicar regla", "Les anotacions correctes processades per l'estat 'Pendent d'aplicar regla'"),
	ANT_REG_PND_OKY_TOT("Totals correctes pendents regla", "Les anotacions correctes totals fins la data per l'estat 'Pendent d'aplicar regla'"),
	ANT_REG_PND_ERR("Errors pendents d'aplicar regla", "Les anotacions amb error processades per l'estat 'Pendent d'aplicar regla'"),
	ANT_REG_PND_ERR_TOT("Totals errors pendents regla", "Les anotacions amb error totals fins la data per l'estat 'Pendent d'aplicar regla'"),
	ANT_REG_PND_EST_TOT("Totals anotacions pendents regla", "Les anotacions totals amb i sense error per l'estat 'Pendent d'aplicar regla'"),

	// BUSTIA_PENDENT
	ANT_BST_PND_OKY("Correctes pendents a bústia", "Les anotacions correctes processades per l'estat 'Pendent a bústia'"),
	ANT_BST_PND_OKY_TOT("Totals correctes pendents bústia", "Les anotacions correctes totals fins la data per l'estat 'Pendent a bústia'"),
	ANT_BST_PND_ERR("Errors pendents a bústia", "Les anotacions amb error processades per l'estat 'Pendent a bústia'"),
	ANT_BST_PND_ERR_TOT("Totals errors pendents bústia", "Les anotacions amb error totals fins la data per l'estat 'Pendent a bústia'"),
	ANT_BST_PND_EST_TOT("Totals anotacions pendents bústia", "Les anotacions totals amb i sense error per l'estat 'Pendent a bústia'"),
	
	// BUSTIA_PROCESSADA
	ANT_BST_PRC_OKY("Correctes distribuït processat", "Les anotacions correctes processades per l'estat 'Distribuït marcant com a processat'"),
	ANT_BST_PRC_OKY_TOT("Totals correctes distrib. processat", "Les anotacions correctes totals fins la data per l'estat 'Distribuït marcant com a processat'"),
	ANT_BST_PRC_ERR("Errors distribuït processat", "Les anotacions amb error processades per l'estat 'Distribuït marcant com a processat'"),
	ANT_BST_PRC_ERR_TOT("Totals errors distrib. processat", "Les anotacions amb error totals fins la data per l'estat 'Distribuït marcant com a processat'"),
	ANT_BST_PRC_EST_TOT("Totals anotacions distrib. processat", "Les anotacions totals amb i sense error per l'estat 'Distribuït marcant com a processat'"),
	
	// BACK_PENDENT
	ANT_BCK_PND_OKY("Correctes pendents aplicació de client", "Les anotacions correctes processades per l'estat 'Anotació pendent d’enviar a l’aplicació de client'"),
	ANT_BCK_PND_OKY_TOT("Totals correctes pendents aplicació de client", "Les anotacions correctes totals fins la data per l'estat 'Anotació pendent d’enviar a l’aplicació de client'"),
	ANT_BCK_PND_ERR("Errors pendents aplicació de client", "Les anotacions amb error processades per l'estat 'Anotació pendent d’enviar a l’aplicació de client'"),
	ANT_BCK_PND_ERR_TOT("Totals errors pendents aplicació de client", "Les anotacions amb error totals fins la data per l'estat 'Anotació pendent d’enviar a l’aplicació de client'"),
	ANT_BCK_PND_EST_TOT("Totals anotacions pendents aplicació de client", "Les anotacions totals amb i sense error per l'estat 'Anotació pendent d’enviar a l’aplicació de client'"),
	
	// BACK_COMUNICADA
	ANT_BCK_COM_OKY("Correctes comunicades aplicació de client", "Les anotacions correctes processades per l'estat 'Anotació comunicada a l’aplicació de client'"),
	ANT_BCK_COM_OKY_TOT("Totals correctes comunicades aplicació de client", "Les anotacions correctes totals fins la data per l'estat 'Anotació comunicada a l’aplicació de client'"),
	ANT_BCK_COM_ERR("Errors comunicades aplicació de client", "Les anotacions amb error processades per l'estat 'Anotació comunicada a l’aplicació de client'"),
	ANT_BCK_COM_ERR_TOT("Totals errors comunicades aplicació de client", "Les anotacions amb error totals fins la data per l'estat 'Anotació comunicada a l’aplicació de client'"),
	ANT_BCK_COM_EST_TOT("Totals anotacions comunicades aplicació de client", "Les anotacions totals amb i sense error per l'estat 'Anotació comunicada a l’aplicació de client'"),
	
	// BACK_REBUDA
	ANT_BCK_REB_OKY("Correctes rebudes aplicació de client", "Les anotacions correctes processades per l'estat 'Anotació rebuda a l’aplicació de client'"),
	ANT_BCK_REB_OKY_TOT("Totals correctes rebudes aplicació de client", "Les anotacions correctes totals fins la data per l'estat 'Anotació rebuda a l’aplicació de client'"),
	ANT_BCK_REB_ERR("Errors rebudes aplicació de client", "Les anotacions amb error processades per l'estat 'Anotació rebuda a l’aplicació de client'"),
	ANT_BCK_REB_ERR_TOT("Totals errors rebudes aplicació de client", "Les anotacions amb error totals fins la data per l'estat 'Anotació rebuda a l’aplicació de client'"),
	ANT_BCK_REB_EST_TOT("Totals anotacions rebudes aplicació de client", "Les anotacions totals amb i sense error per l'estat 'Anotació rebuda a l’aplicació de client'"),
	
	// BACK_PROCESSADA
	ANT_BCK_PRC_OKY("Correctes processades aplicació de client", "Les anotacions correctes processades per l'estat 'Anotació processada correctament per l’aplicació de client'"),
	ANT_BCK_PRC_OKY_TOT("Totals correctes processades aplicació de client", "Les anotacions correctes totals fins la data per l'estat 'Anotació processada correctament per l’aplicació de client'"),
	ANT_BCK_PRC_ERR("Errors processades aplicació de client", "Les anotacions amb error processades per l'estat 'Anotació processada correctament per l’aplicació de client'"),
	ANT_BCK_PRC_ERR_TOT("Totals errors processades aplicació de client", "Les anotacions amb error totals fins la data per l'estat 'Anotació processada correctament per l’aplicació de client'"),
	ANT_BCK_PRC_EST_TOT("Totals anotacions processades aplicació de client", "Les anotacions totals amb i sense error per l'estat 'Anotació processada correctament per l’aplicació de client'"),
	
	// BACK_REBUTJADA
	ANT_BCK_RBJ_OKY("Correctes rebutjades aplicació de client", "Les anotacions correctes processades per l'estat 'Anotació rebutjada per l’aplicació de client'"),
	ANT_BCK_RBJ_OKY_TOT("Totals correctes rebutjades aplicació de client", "Les anotacions correctes totals fins la data per l'estat 'Anotació rebutjada per l’aplicació de client'"),
	ANT_BCK_RBJ_ERR("Errors rebutjades aplicació de client", "Les anotacions amb error processades per l'estat 'Anotació rebutjada per l’aplicació de client'"),
	ANT_BCK_RBJ_ERR_TOT("Totals errors rebutjades aplicació de client", "Les anotacions amb error totals fins la data per l'estat 'Anotació rebutjada per l’aplicació de client'"),
	ANT_BCK_RBJ_EST_TOT("Totals anotacions rebutjades aplicació de client", "Les anotacions totals amb i sense error per l'estat 'Anotació rebutjada per l’aplicació de client'"),
	
	// BACK_ERROR
	ANT_BCK_ERR_OKY("Correctes amb errors aplicació de client", "Les anotacions correctes processades per l'estat 'Anotació processada a l’aplicació de client amb errors'"),
	ANT_BCK_ERR_OKY_TOT("Totals correctes amb errors aplicació de client", "Les anotacions correctes totals fins la data per l'estat 'Anotació processada a l’aplicació de client amb errors'"),
	ANT_BCK_ERR_ERR("Errors amb errors aplicació de client", "Les anotacions amb error processades per l'estat 'Anotació processada a l’aplicació de client amb errors'"),
	ANT_BCK_ERR_ERR_TOT("Totals errors amb errors aplicació de client", "Les anotacions amb error totals fins la data per l'estat 'Anotació processada a l’aplicació de client amb errors'"),
	ANT_BCK_ERR_EST_TOT("Totals anotacions amb errors aplicació de client", "Les anotacions totals amb i sense error per l'estat 'Anotació processada a l’aplicació de client amb errors'"),

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
