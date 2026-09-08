import React from 'react';
import { useTranslation } from 'react-i18next';
import TabComponent from '../../../components/TabComponent';
import ContingutLogGrid from './ContingutLogGrid';
import ContingutMovimentGrid from './ContingutMovimentGrid';
import ContingutAuditoriaTab from './ContingutAuditoriaTab';

type ContingutHistorialContentProps = {
    contingutId: any;
    contingutRow: any;
};

/** Contingut del diàleg "Historial": les 3 pestanyes (Accions/Moviments/Auditoria). */
export const ContingutHistorialContent: React.FC<ContingutHistorialContentProps> = ({
    contingutId,
    contingutRow,
}) => {
    const { t } = useTranslation();

    const tabs = [
        {
            value: 'accions',
            label: t('page.contingut.historial.tab.accions'),
            content: <ContingutLogGrid contingutId={contingutId} />,
        },
        {
            value: 'moviments',
            label: t('page.contingut.historial.tab.moviments'),
            content: <ContingutMovimentGrid contingutId={contingutId} />,
        },
        {
            value: 'auditoria',
            label: t('page.contingut.historial.tab.auditoria'),
            content: <ContingutAuditoriaTab contingutRow={contingutRow} />,
        },
    ];

    return (
        <TabComponent
            indicatorColor="primary"
            textColor="primary"
            aria-label="contingut historial tabs"
            tabs={tabs}
            variant="scrollable"
        />
    );
};

export default ContingutHistorialContent;
