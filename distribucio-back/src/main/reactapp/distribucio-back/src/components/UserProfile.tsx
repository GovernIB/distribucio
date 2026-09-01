import React from 'react';
import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Icon from '@mui/material/Icon';
import Grid from '@mui/material/Grid';
import Divider from '@mui/material/Divider';
import TextField from '@mui/material/TextField';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import FormControl from '@mui/material/FormControl';
import InputAdornment from '@mui/material/InputAdornment';
import Typography from '@mui/material/Typography';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import {
    MuiFormDialog,
    MuiDataFormDialogApi,
    useAuthContext,
    useResourceApiContext,
    useFormContext,
} from 'reactlib';
import { TemaAplicacio, MenuEstil } from '../theme';
import { useDistribucioContext } from './DistribucioContext';
import { desarTemaCache, useSetTemaAplicacio } from './TemaProvider';
import GridFormField from './GridFormField';

const selectorLabelSx = {
    display: 'block',
    ml: 1.75,
    mb: 0.75,
    fontSize: '0.75rem',
    lineHeight: 1,
    color: 'text.secondary',
};

// Adornament d'icona a la dreta del camp (es passa via slotProps tal com espera FormFieldText).
const endIcon = (name: string) => ({
    slotProps: {
        input: {
            endAdornment: (
                <InputAdornment position="end">
                    <Icon fontSize="small">{name}</Icon>
                </InputAdornment>
            ),
        },
    },
});

// ============================================================================
// Preferències de l'usuari, aplicades a tota l'aplicació.
//
// La font de veritat és el perfil desat a la base de dades: DistribucioProvider carrega el
// recurs de l'usuari en arrencar (useCurrentUser) i no deixa pintar res fins a tenir-lo, de
// manera que aquí ja hi són disponibles al primer render. Damunt d'aquests valors s'hi pot
// posar una previsualització transitòria mentre el diàleg de perfil és obert (el tema i
// l'estil de menú s'apliquen a l'instant per poder-los veure); en tancar el diàleg la
// previsualització desapareix i tornen a manar els valors desats.
//
// Per afegir una preferència nova n'hi ha prou amb declarar-la a UserPreferences i llegir-la
// a preferenciesDesades: qui la necessiti la té amb useUserPreferences().
export type UserPreferences = {
    /** Codi de dues lletres en minúscules ("ca"/"es"), normalitzat des del perfil. */
    idioma?: string;
    temaAplicacio?: TemaAplicacio;
    estilMenu?: MenuEstil;
    /** Mida de pàgina per defecte de tots els llistats (veure StyledMuiGrid). */
    numElementsPagina?: number;
};

/**
 * El perfil desa l'idioma com el nom de la constant d'IdiomaEnumDto ("CA"/"ES") i pot arribar
 * en minúscules de l'alta automàtica d'usuaris, mentre que i18next i la capçalera
 * Accept-Language volen el codi de dues lletres en minúscules. Sense normalitzar, "ES" i "es"
 * (o "es-ES") es considerarien idiomes diferents i es rellançarien consultes sense necessitat.
 */
const normalitzaIdioma = (idioma?: string): string | undefined =>
    idioma != null && idioma.length > 0 ? idioma.substring(0, 2).toLowerCase() : undefined;

const preferenciesDesades = (usuari: any): UserPreferences => ({
    idioma: normalitzaIdioma(usuari?.idioma),
    temaAplicacio: usuari?.temaAplicacio,
    estilMenu: usuari?.estilMenu,
    numElementsPagina: usuari?.numElementsPagina,
});

const UserPreferencesContext = React.createContext<
    { preferences: UserPreferences; setPreview: (preview: UserPreferences) => void } | undefined
>(undefined);

export const UserPreferencesProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { currentUser } = useDistribucioContext();
    const setTemaAplicacio = useSetTemaAplicacio();
    const [preview, setPreview] = React.useState<UserPreferences>({});
    const desades = React.useMemo(() => preferenciesDesades(currentUser), [currentUser]);
    // En desar el perfil, DistribucioProvider actualitza currentUser: la previsualització ha de
    // desaparèixer perquè no tapi els valors que acaben d'arribar del servidor.
    React.useEffect(() => setPreview({}), [desades]);
    // Només es recorda el tema desat, no la previsualització: així la propera arrencada ja pinta
    // amb el tema correcte (veure TemaProvider) sense ressuscitar cap canvi descartat.
    React.useEffect(
        () => desarTemaCache(currentUser?.id, desades.temaAplicacio),
        [currentUser?.id, desades.temaAplicacio]
    );
    const preferences = React.useMemo(() => {
        // Només tapen les claus previsualitzades amb valor; una clau a undefined ha de deixar
        // veure la preferència desada, no esborrar-la.
        const efectives: UserPreferences = { ...desades };
        (Object.keys(preview) as (keyof UserPreferences)[]).forEach((clau) => {
            if (preview[clau] !== undefined) {
                (efectives as any)[clau] = preview[clau];
            }
        });
        return efectives;
    }, [desades, preview]);
    // El tema efectiu (inclosa la previsualització del diàleg de perfil, que s'ha de veure a
    // l'instant) puja al TemaProvider, que és qui munta el ThemeProvider de tota l'aplicació.
    // Sense perfil no s'hi puja res: amb l'API caiguda DistribucioProvider pinta igualment els
    // fills (mode offline) i s'esborraria el tema que TemaProvider ha recuperat de la memòria cau.
    React.useEffect(() => {
        if (currentUser != null) {
            setTemaAplicacio(preferences.temaAplicacio);
        }
    }, [currentUser, preferences.temaAplicacio, setTemaAplicacio]);
    return (
        <UserPreferencesContext.Provider value={{ preferences, setPreview }}>
            {children}
        </UserPreferencesContext.Provider>
    );
};

export const useUserPreferences = (): UserPreferences => {
    const context = React.useContext(UserPreferencesContext);
    if (context === undefined) {
        throw new Error('useUserPreferences must be used within a UserPreferencesProvider');
    }
    return context.preferences;
};

const useSetUserPreferencesPreview = () => {
    const context = React.useContext(UserPreferencesContext);
    if (context === undefined) {
        throw new Error('useSetUserPreferencesPreview must be used within a UserPreferencesProvider');
    }
    return context.setPreview;
};

export const UserProfileMenu: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
    const { getUserId: authGetUserId } = useAuthContext();
    const { setCurrentUser } = useDistribucioContext();
    // La promesa de show() es resol amb el recurs desat (i no es resol si es cancel·la). En
    // desar-lo s'actualitza l'usuari de la sessió, que és d'on pengen totes les preferències:
    // així el canvi d'idioma s'aplica en desar i no mentre s'edita el formulari.
    const showUserProfileDialog = () => {
        formDialogApiRef.current
            ?.show(authGetUserId())
            .then((desat: any) => desat != null && setCurrentUser(desat))
            .catch(() => null);
    };

    return (
        <MenuItem onClick={() => showUserProfileDialog()} sx={{ width: '100%' }}>
            <ListItemIcon>
                <Icon fontSize="small">account_circle</Icon>
            </ListItemIcon>
            <ListItemText>{t('component.UserProfile.perfil')}</ListItemText>
        </MenuItem>
    );
};

// Selector del tema de l'aplicació (clar / obscur / dracula / sistema).
const ThemeSelector: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();
    const handleChange = (_event: React.MouseEvent<HTMLElement>, newValue: TemaAplicacio | null) => {
        if (newValue !== null) {
            apiRef?.current?.setFieldValue('temaAplicacio', newValue);
        }
    };
    return (
        <>
            <Typography component="label" sx={selectorLabelSx}>
                {t('component.UserProfile.tema.label')}
            </Typography>
            <ToggleButtonGroup
                value={data?.temaAplicacio ?? TemaAplicacio.SISTEMA}
                exclusive
                onChange={handleChange}
                size="small"
                sx={{ display: 'flex', width: '100%' }}
            >
                <ToggleButton value={TemaAplicacio.CLAR} sx={{ flex: 1, gap: 1 }}>
                    <Icon>light_mode</Icon> {t('component.UserProfile.tema.clar')}
                </ToggleButton>
                <ToggleButton value={TemaAplicacio.OBSCUR} sx={{ flex: 1, gap: 1 }}>
                    <Icon>dark_mode</Icon> {t('component.UserProfile.tema.obscur')}
                </ToggleButton>
                <ToggleButton value={TemaAplicacio.DRACULA} sx={{ flex: 1, gap: 1 }}>
                    <Icon>auto_awesome</Icon> {t('component.UserProfile.tema.dracula')}
                </ToggleButton>
                <ToggleButton value={TemaAplicacio.SISTEMA} sx={{ flex: 1, gap: 1 }}>
                    <Icon>settings_brightness</Icon> {t('component.UserProfile.tema.sistema')}
                </ToggleButton>
            </ToggleButtonGroup>
        </>
    );
};

// Selector de l'estil del menú lateral (segueix el tema / invertit / fix).
const MenuStyleSelector: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();
    const handleChange = (_event: React.MouseEvent<HTMLElement>, newValue: MenuEstil | null) => {
        if (newValue !== null) {
            apiRef?.current?.setFieldValue('estilMenu', newValue);
        }
    };
    return (
        <>
            <Typography component="label" sx={selectorLabelSx}>
                {t('component.UserProfile.estilMenu.label')}
            </Typography>
            <ToggleButtonGroup
                value={data?.estilMenu ?? MenuEstil.TEMA}
                exclusive
                onChange={handleChange}
                size="small"
                sx={{ display: 'flex', width: '100%' }}
            >
                <ToggleButton value={MenuEstil.TEMA} sx={{ flex: 1, gap: 1 }}>
                    <Icon>palette</Icon> {t('component.UserProfile.estilMenu.tema')}
                </ToggleButton>
                <ToggleButton value={MenuEstil.TEMA_INVERTIT} sx={{ flex: 1, gap: 1 }}>
                    <Icon>invert_colors</Icon> {t('component.UserProfile.estilMenu.temaInvertit')}
                </ToggleButton>
                <ToggleButton value={MenuEstil.PEU} sx={{ flex: 1, gap: 1 }}>
                    <Icon>vertical_align_bottom</Icon> {t('component.UserProfile.estilMenu.peu')}
                </ToggleButton>
            </ToggleButtonGroup>
        </>
    );
};

/**
 * Previsualitza el tema i l'estil de menú mentre s'edita el perfil, perquè es vegin a l'instant
 * sense haver de desar. Són canvis purament de client i no toquen cap consulta.
 *
 * L'idioma NO es previsualitza a posta: canviar-lo obliga a tornar a demanar les etiquetes i els
 * enumerats al servidor (base-react refà l'índex de l'API quan canvia Accept-Language), i fer-ho
 * a cada tecla del formulari recarregava la pantalla de darrere. S'aplica en desar, quan
 * DistribucioProvider actualitza currentUser (veure UserProfileMenu).
 */
const PreferencesSync: React.FC = () => {
    const { data } = useFormContext();
    const setPreview = useSetUserPreferencesPreview();
    React.useEffect(() => {
        setPreview({ temaAplicacio: data?.temaAplicacio, estilMenu: data?.estilMenu });
    }, [data?.temaAplicacio, data?.estilMenu, setPreview]);
    // En tancar el diàleg (desant o cancel·lant) es retira la previsualització i tornen a manar
    // els valors del perfil desat.
    React.useEffect(() => () => setPreview({}), [setPreview]);
    return null;
};

// Camp de només lectura amb els rols amb què l'usuari pot operar. Es mostren els del context
// (els mateixos que ofereix el selector de rol) i no data.rols: el backend construeix aquest camp
// a partir de les autoritats de la petició, i RolSeleccionatFilter les restringeix al rol actiu,
// de manera que només s'hi veuria el rol amb què s'està operant. No es tradueixen.
const RolesField: React.FC = () => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const { rolesAvailable } = useDistribucioContext();
    return (
        <TextField
            fullWidth
            size="small"
            label={t('component.UserProfile.rols')}
            value={(rolesAvailable ?? data?.rols ?? []).join(', ')}
            disabled
            slotProps={{
                input: {
                    readOnly: true,
                    endAdornment: (
                        <InputAdornment position="end">
                            <Icon fontSize="small">recent_actors</Icon>
                        </InputAdornment>
                    ),
                },
            }}
        />
    );
};

type OptionItem = { id: number; nom: string };

/**
 * Opcions d'un desplegable del perfil. No es publiquen com a camp d'opcions del recurs
 * (`@ResourceField(enumType = true)`, com `idioma`) sinó com a endpoints propis de
 * `usuariPreferencies`: els llistats d'entitats i bústies depenen de l'usuari autenticat -- no del
 * recurs administratiu `entitatResource`, restringit a DIS_SUPER -- i les mides de pàgina són
 * numèriques, mentre que el motor genèric només sap publicar opcions de text.
 */
const useOpcions = (href?: string, hrefParams?: any): OptionItem[] => {
    const { apiUrl, requestHref } = useResourceApiContext();
    const [options, setOptions] = React.useState<OptionItem[]>([]);
    // Els paràmetres es comparen serialitzats: l'objecte literal que passa qui crida el hook és nou
    // a cada render i, com a dependència, rellançaria la consulta indefinidament.
    const hrefParamsKey = JSON.stringify(hrefParams ?? null);
    React.useEffect(() => {
        if (href == null) {
            setOptions([]);
            return;
        }
        requestHref(apiUrl + href, hrefParams)
            .then((state) => setOptions((state.data as OptionItem[]) ?? []))
            .catch(() => setOptions([]));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiUrl, href, hrefParamsKey]);
    return options;
};

/**
 * Desplegable d'una preferència del perfil, alimentat per {@link useOpcions}. El valor es manté
 * numèric (l'id de l'opció tal com arriba del servidor) perquè el camp del recurs també ho és.
 */
const PreferenciaSelect: React.FC<{
    name: string;
    label: string;
    value?: number | null;
    options: OptionItem[];
    /** Afegeix l'opció buida per poder deixar la preferència sense valor. */
    emptyOption?: boolean;
    disabled?: boolean;
    onChange: (value: number | null) => void;
}> = (props) => {
    const { name, label, value, options, emptyOption, disabled, onChange } = props;
    const { t } = useTranslation();
    const labelId = name + '-label';
    return (
        <FormControl fullWidth size="small" disabled={disabled}>
            <InputLabel id={labelId}>{label}</InputLabel>
            <Select<number | ''>
                labelId={labelId}
                label={label}
                value={value ?? ''}
                onChange={(event) => {
                    const seleccionat = event.target.value;
                    onChange(seleccionat !== '' ? Number(seleccionat) : null);
                }}
            >
                {emptyOption && (
                    <MenuItem value="">
                        <em>{t('comu.empty.option')}</em>
                    </MenuItem>
                )}
                {options.map((option) => (
                    <MenuItem key={option.id} value={option.id}>
                        {option.nom}
                    </MenuItem>
                ))}
            </Select>
        </FormControl>
    );
};

// Selector de l'entitat per defecte.
const EntitatPerDefecteSelect: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();
    const options = useOpcions('usuariPreferencies/entitats');
    return (
        <PreferenciaSelect
            name="entitatPerDefecte"
            label={t('component.UserProfile.entitatPerDefecte')}
            value={data?.entitatPerDefecteId}
            options={options}
            emptyOption
            onChange={(value) => {
                apiRef?.current?.setFieldValue('entitatPerDefecteId', value);
                apiRef?.current?.setFieldValue('bustiaPerDefecte', null);
            }}
        />
    );
};

// Selector de la bústia per defecte -- depèn de l'entitat per defecte seleccionada (es guarda per
// parella entitat+usuari, veure UsuariResourceServiceImpl).
const BustiaPerDefecteSelect: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();
    const entitatPerDefecteId = data?.entitatPerDefecteId;
    const options = useOpcions(
        entitatPerDefecteId != null ? 'usuariPreferencies/busties?entitatId={entitatId}' : undefined,
        entitatPerDefecteId != null ? { entitatId: entitatPerDefecteId } : undefined
    );
    return (
        <PreferenciaSelect
            name="bustiaPerDefecte"
            label={t('component.UserProfile.bustiaPerDefecte')}
            value={data?.bustiaPerDefecte}
            options={options}
            emptyOption
            disabled={entitatPerDefecteId == null}
            onChange={(value) => apiRef?.current?.setFieldValue('bustiaPerDefecte', value)}
        />
    );
};

// Mida de pàgina per defecte dels llistats. Com a la interfície JSP (usuariForm.jsp) és un
// desplegable amb els valors d'OpcionsPaginacio i sense opció buida, no un camp numèric lliure.
// L'etiqueta surt del `_prompt` del recurs, la mateixa que faria servir un GridFormField.
const NumElementsPaginaSelect: React.FC = () => {
    const { data, apiRef, fields } = useFormContext();
    const options = useOpcions('usuariPreferencies/opcionsPaginacio');
    const label = fields?.find((field) => field.name === 'numElementsPagina')?.label ?? '';
    return (
        <PreferenciaSelect
            name="numElementsPagina"
            label={label}
            value={data?.numElementsPagina}
            options={options}
            onChange={(value) => apiRef?.current?.setFieldValue('numElementsPagina', value)}
        />
    );
};

export const UserProfileFormDialog: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();

    return (
        <MuiFormDialog
            resourceName="usuariResource"
            title={t('component.UserProfile.perfil')}
            apiRef={formDialogApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
            formComponentProps={{ commonFieldComponentProps: { size: 'small' } }}
        >
            <PreferencesSync />
            <Grid container spacing={2} sx={{ px: 1 }}>
                <Grid size={12}>
                    <Divider>{t('component.UserProfile.seccioDades')}</Divider>
                </Grid>
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="id" disabled componentProps={endIcon('tag')} />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nom" disabled componentProps={endIcon('person')} />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nif" disabled componentProps={endIcon('badge')} />
                <GridFormField
                    size={{ xs: 12, sm: 6, md: 3 }}
                    name="email"
                    disabled
                    componentProps={endIcon('alternate_email')}
                />
                <Grid size={12}>
                    <RolesField />
                </Grid>
                <GridFormField
                    size={{ xs: 12, sm: 6, md: 4 }}
                    name="emailAlternatiu"
                    componentProps={endIcon('alternate_email')}
                />
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="rebreEmailsBustia" />
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="rebreEmailsAgrupats" />
                <Grid size={12}>
                    <Divider>{t('component.UserProfile.seccioConfig')}</Divider>
                </Grid>
                {/* Sense endIcon: `idioma` és un camp d'opcions (UsuariResource.idioma duu
                    @ResourceField(enumType = true)) i es dibuixa com a desplegable, que ja té la
                    seva fletxa. A més, FormFieldEnum fixa els seus propis slotProps després
                    d'escampar componentProps, de manera que l'adornament no s'hi aplicaria. */}
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="idioma" />
                <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                    <NumElementsPaginaSelect />
                </Grid>
                <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                    <EntitatPerDefecteSelect />
                </Grid>
                <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                    <BustiaPerDefecteSelect />
                </Grid>
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="emailErrorAnotacio" />
                <Grid size={12}>
                    <ThemeSelector />
                </Grid>
                <Grid size={12}>
                    <MenuStyleSelector />
                </Grid>
            </Grid>
        </MuiFormDialog>
    );
};
