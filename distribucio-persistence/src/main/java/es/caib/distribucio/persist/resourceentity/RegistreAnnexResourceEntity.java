package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.RegistreAnnexResource;
import es.caib.distribucio.logic.intf.registre.*;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import es.caib.distribucio.persist.converter.RegistreAnnexElaboracioEstatConverter;
import es.caib.distribucio.persist.converter.RegistreAnnexNtiTipusDocumentConverter;
import es.caib.distribucio.persist.converter.RegistreAnnexOrigenConverter;
import es.caib.distribucio.persist.converter.RegistreAnnexSicresTipusDocumentConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitat de base de dades del recurs {@link RegistreAnnexResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci
 * {@link es.caib.distribucio.persist.entity.RegistreAnnexEntity} ({@code dis_registre_annex}),
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 * <p>
 * Els camps {@code registreNumero}, {@code registreNumeroCopia} i {@code dataAnotacio} pertanyen a
 * l'anotació de registre pare (taula {@code dis_registre}), no a l'annex; es mapegen amb
 * {@link Formula} (subconsulta escalar per {@code registre_id}, sempre com a màxim una fila) perquè
 * calen tant per mostrar-los com per poder-hi filtrar (el motor genèric de filtres només sap
 * consultar atributs mapejats de l'entitat, no camps calculats en Java després de la consulta).
 * {@code tipusFirma} fa el mateix amb la primera firma de l'annex (taula
 * {@code dis_registre_annex_firma}) simplificant amb {@code min()} -- portable entre motors de BBDD
 * sense necessitat de limitar files -- ja que un annex pot tenir més d'una firma i aquí només cal un
 * valor per poder filtrar; per a la resta de dades derivades de les firmes (p. ex.
 * {@code signaturaInfo}) es fa servir el hook {@code afterConversion} en comptes d'SQL, veure
 * {@link es.caib.distribucio.logic.resourceservice.RegistreAnnexResourceServiceImpl}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre_annex")
@Getter
@Setter
@NoArgsConstructor
public class RegistreAnnexResourceEntity extends BaseResourceEntity<RegistreAnnexResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
	private Long id;

	@Column(name = "titol", length = 200, nullable = false)
	private String titol;
	@Column(name = "fitxer_nom", length = 256, nullable = false)
	private String fitxerNom;
	@Column(name = "fitxer_tamany", nullable = false)
	private int fitxerTamany;
	@Column(name = "fitxer_mime", length = 30)
	private String fitxerTipusMime;
	@Column(name = "fitxer_arxiu_uuid", length = 256)
	private String fitxerArxiuUuid;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_captura", nullable = false)
	private Date dataCaptura;
	@Column(name = "localitzacio", length = 80)
	private String localitzacio;
	@Column(name = "origen_ciuadm", length = 1, nullable = false)
    @Convert(converter = RegistreAnnexOrigenConverter.class)
	private RegistreAnnexOrigenEnum origenCiutadaAdmin;
	@Column(name = "nti_tipus_doc", length = 4, nullable = false)
    @Convert(converter = RegistreAnnexNtiTipusDocumentConverter.class)
	private RegistreAnnexNtiTipusDocumentEnum ntiTipusDocument;
	@Column(name = "sicres_tipus_doc", length = 2)
    @Convert(converter = RegistreAnnexSicresTipusDocumentConverter.class)
	private RegistreAnnexSicresTipusDocumentEnum sicresTipusDocument;
	@Column(name = "nti_elaboracio_estat", length = 4)
    @Convert(converter = RegistreAnnexElaboracioEstatConverter.class)
	private RegistreAnnexElaboracioEstatEnum ntiElaboracioEstat;
	@Column(name = "observacions", length = 50)
	private String observacions;
	@Column(name = "firma_mode")
	private Integer firmaMode;
	@Column(name = "firma_csv", length = 256)
	private String firmaCsv;
	@Column(name = "timestamp", length = 100)
	private String timestamp;
	@Column(name = "validacio_ocsp", length = 100)
	private String validacioOCSP;
	@Column(name = "gesdoc_doc_id")
	private String gesdocDocumentId;
	@Column(name = "sign_detalls_descarregat")
	private boolean signaturaDetallsDescarregat;
	@Column(name = "meta_dades", length = 4000)
	private String metaDades;
	@Enumerated(EnumType.STRING)
	@Column(name = "val_firma_estat")
	private ValidacioFirmaEnum validacioFirmaEstat;
	@Column(name = "val_firma_error", length = 1000)
	private String validacioFirmaError;
	@Enumerated(EnumType.STRING)
	@Column(name = "arxiu_estat")
	private AnnexEstat arxiuEstat;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "registre_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "registre_annex_reg_fk"))
	private ContingutResourceEntity<?> registre;

	@Formula("(select r.numero from " + BaseConfig.DB_PREFIX + "registre r where r.id = registre_id)")
	private String registreNumero;

	@Formula("(select r.numero_copia from " + BaseConfig.DB_PREFIX + "registre r where r.id = registre_id)")
	private Integer registreNumeroCopia;

	@Formula("(select r.data from " + BaseConfig.DB_PREFIX + "registre r where r.id = registre_id)")
	private Date dataAnotacio;

	@Formula("(select min(f.tipus) from " + BaseConfig.DB_PREFIX + "registre_annex_firma f where f.annex_id = id)")
	private String tipusFirma;

}
