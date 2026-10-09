import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import { useResourceApiService } from 'reactlib';
import { StyledBadge } from '../../components/StyledBadge';

export type IntegracioCodi = string;

const ACTION_COUNT_ERRORS = 'COUNT_ERRORS';

interface CountErrorsResult {
    codis: string[];
    errors: Record<string, number>;
}

/**
 * Codis d'integració a mostrar com a pestanyes i el nombre d'errors de cadascun.
 */
const useIntegracioTabsData = (filterData: any) => {
    const { isReady: apiIsReady, artifactAction } = useResourceApiService(
        'monitorIntegracioResource'
    );
    const [codis, setCodis] = useState<string[]>([]);
    const [counts, setCounts] = useState<Record<string, number>>({});

    const refresh = () => {
        if (!apiIsReady) {
            return;
        }
        artifactAction(undefined, { code: ACTION_COUNT_ERRORS, data: filterData })
            .then((result: CountErrorsResult) => {
                setCodis(result?.codis ?? []);
                setCounts(result?.errors ?? {});
            })
            .catch(() => {
                setCodis([]);
                setCounts({});
            });
    };

    useEffect(() => {
        refresh();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady, filterData]);

    return { codis, counts, refresh };
};

/** Pestanyes del monitor d'integracions: una per codi, amb el nombre d'errors si n'hi ha. */
export const useIntegracioTabs = (filterData?: any) => {
    const { t } = useTranslation();
    const [value, setValue] = useState<IntegracioCodi | undefined>(undefined);
    const { codis, counts, refresh: refreshCounts } = useIntegracioTabsData(filterData);

    useEffect(() => {
        if (codis.length > 0 && (value === undefined || !codis.includes(value))) {
            setValue(codis[0]);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [codis]);

    const handleChange = (_event: React.SyntheticEvent, newValue: IntegracioCodi) => {
        setValue(newValue);
    };

    const tabElement = (
        <Tabs value={value ?? false} onChange={handleChange} variant="scrollable" sx={{ px: 1 }}>
            {codis.map((codi) => (
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
