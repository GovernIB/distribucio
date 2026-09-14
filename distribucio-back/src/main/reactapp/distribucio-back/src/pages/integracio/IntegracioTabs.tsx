import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import { useResourceApiService } from 'reactlib';
import { StyledBadge } from '../../components/StyledBadge';

/**
 * Codis d'integració actualment actius (mateix conjunt tancat que l'enum IntegracioCodi del backend).
 */
export const INTEGRACIO_CODIS = [
    'USUARIS',
    'UNITATS',
    'ARXIU',
    'DADESEXT',
    'SIGNATURA',
    'VALIDASIG',
    'GESDOC',
    'BUSTIAWS',
    'PROCEDIMENT',
    'SERVEI',
    'DISTRIBUCIO',
    'BACKOFFICE',
] as const;

export type IntegracioCodi = (typeof INTEGRACIO_CODIS)[number];

const ACTION_COUNT_ERRORS = 'COUNT_ERRORS';

/**
 * Nombre d'errors per codi d'integració, per pintar els xips de les pestanyes.
 */
const useIntegracioErrorCounts = (filterData: any) => {
    const { isReady: apiIsReady, artifactAction } = useResourceApiService(
        'monitorIntegracioResource'
    );
    const [counts, setCounts] = useState<Record<string, number>>({});

    const refresh = () => {
        if (!apiIsReady) {
            return;
        }
        artifactAction(undefined, { code: ACTION_COUNT_ERRORS, data: filterData })
            .then((result: Record<string, number>) => {
                setCounts(result ?? {});
            })
            .catch(() => setCounts({}));
    };

    useEffect(() => {
        refresh();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady, filterData]);

    return { counts, refresh };
};

/** Pestanyes del monitor d'integracions: una per codi, amb el nombre d'errors si n'hi ha. */
export const useIntegracioTabs = (filterData?: any) => {
    const { t } = useTranslation();
    const [value, setValue] = useState<IntegracioCodi>(INTEGRACIO_CODIS[0]);
    const { counts, refresh: refreshCounts } = useIntegracioErrorCounts(filterData);

    const handleChange = (_event: React.SyntheticEvent, newValue: IntegracioCodi) => {
        setValue(newValue);
    };

    const tabElement = (
        <Tabs value={value} onChange={handleChange} variant="scrollable" sx={{ px: 1 }}>
            {INTEGRACIO_CODIS.map((codi) => (
                <Tab
                    key={codi}
                    value={codi}
                    label={
                        <StyledBadge badgeContent={counts[codi] ?? 0} badgecolor="error">
                            {t(`page.integracio.pipella.${codi}`)}
                        </StyledBadge>
                    }
                    sx={{ textTransform: 'none' }}
                />
            ))}
        </Tabs>
    );

    return { value, tabElement, refreshCounts };
};

export default useIntegracioTabs;
