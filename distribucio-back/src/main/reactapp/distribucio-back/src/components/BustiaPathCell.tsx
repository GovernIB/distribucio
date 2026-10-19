import React from 'react';
import { useTranslation } from 'react-i18next';
import { Icon, Tooltip } from '@mui/material';
import { icons } from '../util/icons';

/**
 * Ruta d'una bústia: unitat organitzativa / bústia (/ subbústia...). Primer element amb la icona
 * d'unitat i la resta amb la icona de bústia. Compartit per les graelles de contingut i d'anotacions.
 */
const BustiaPathCell: React.FC<{ path?: string[]; children?: React.ReactNode }> = ({ path = [], children }) => {
    const { t } = useTranslation();

    return (
        <div style={{ whiteSpace: 'normal', lineHeight: 1.4 }}>
            {path.map((nom, index) => (
                <React.Fragment key={index}>
                    {index > 0 && ' / '}
                    <Tooltip
                        title={
                            index === 0
                                ? t('page.contingut.grid.icona.unitat')
                                : t('page.contingut.grid.icona.bustia')
                        }
                    >
                        <Icon
                            fontSize="small"
                            sx={{ fontSize: '18px', verticalAlign: 'text-bottom', mr: 0.5 }}
                        >
                            {index === 0 ? icons.unitatOrganitzativa : icons.bustia}
                        </Icon>
                    </Tooltip>
                    {nom}
                </React.Fragment>
            ))}
            {children}
        </div>
    );
};

export default BustiaPathCell;
