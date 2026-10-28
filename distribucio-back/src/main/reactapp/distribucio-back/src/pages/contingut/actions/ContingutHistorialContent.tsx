import React from 'react';
import { useTranslation } from 'react-i18next';
import TabComponent from '../../../components/TabComponent';
import ContingutLogGrid from './ContingutLogGrid';
import ContingutMovimentGrid from './ContingutMovimentGrid';
import ContingutAuditoriaTab from './ContingutAuditoriaTab';
import {useDetailContext} from "reactlib";
import Load from "../../../components/Load.tsx";

/** Contingut del diàleg "Historial": les 3 pestanyes (Accions/Moviments/Auditoria). */
export const ContingutHistorialContent: React.FC = () => {
    const { t } = useTranslation();
    const { data: contingutRow } = useDetailContext()

    const tabs = [
        {
            value: 'accions',
            label: t('page.contingut.historial.tab.accions'),
            content: <ContingutLogGrid contingutRow={contingutRow} />,
        },
        {
            value: 'moviments',
            label: t('page.contingut.historial.tab.moviments'),
            content: <ContingutMovimentGrid contingutId={contingutRow?.id} />,
        },
        {
            value: 'auditoria',
            label: t('page.contingut.historial.tab.auditoria'),
            content: <ContingutAuditoriaTab contingutRow={contingutRow} />,
        },
    ];

    return (<Load value={contingutRow}>
        <TabComponent
            indicatorColor="primary"
            textColor="primary"
            aria-label="contingut historial tabs"
            tabs={tabs}
            variant="scrollable"
        />
    </Load>);
};

export default ContingutHistorialContent;
