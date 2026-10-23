import { useBaseAppContext } from 'reactlib';
import { useHref } from 'react-router-dom';
import { Icon, IconButton } from '@mui/material';
import { formatDate } from '../../../../util/dateUtils.ts';
import StyledMuiGrid from '../../../../components/StyledMuiGrid.tsx';
import BustiaPathCell from '../../../../components/BustiaPathCell.tsx';
import * as builder from '../../../../util/springFilterUtils';
import { icons } from '../../../../util/icons';

const copiesColumns = (t: any, id: any, registreUrl: string) => [
    { field: 'numero', flex: 1 },
    {
        field: 'darrerMovimentResource.createdDate',
        flex: 1,
        headerName: t('page.registre.grid.darrerMoviment.createdDate'),
        renderCell: (params: any) => formatDate(params.formattedValue),
    },
    { field: 'procesEstat', flex: 1 },
    {
        field: 'darrerMovimentResource.createdBy',
        flex: 1,
        headerName: t('page.registre.grid.darrerMoviment.createdBy'),
        renderCell: (params: any) => <>{params.row.procesEstat == 'BUSTIA_PROCESSADA' && params.formattedValue}</>,
    },
    {
        field: 'pare',
        flex: 2,
        renderCell: (params: any) => (
            <BustiaPathCell path={[params.row.unitatAdministrativaDescripcio, params.formattedValue]} />
        ),
    },
    {
        field: 'numeroCopia',
        flex: 0.5,
        renderCell: (params: any) => (
            <>{params.row.numeroCopia == 0 ? t('component.RegistreDetail.titles.original') : params.formattedValue}</>
        ),
    },
    {
        field: 'id',
        flex: 0.5,
        headerName: t('page.registre.grid.isCopia'),
        renderCell: (params: any) => (
            <>
                {params.id == id ? (
                    <IconButton disabled>
                        <Icon color={'success'}>{icons.correcte}</Icon>
                    </IconButton>
                ) : (
                    <IconButton
                        component="a"
                        href={`${registreUrl}?registreID=${params.id}`}
                        target="_blank"
                        rel="noopener noreferrer"
                    >
                        <Icon>{icons.obreNovaFinestra}</Icon>
                    </IconButton>
                )}
            </>
        ),
    },
];

const copiesPerspectives = ['DARRER_MOVIMENT'];

export const Copies = ({ entity }: any) => {
    const { t } = useBaseAppContext();
    const registreUrl = useHref('/registre');

    return (
        <StyledMuiGrid
            resourceName="registreResource"
            columns={copiesColumns(t, entity.id, registreUrl)}
            filter={builder.eq('numero', `'${entity.numero}'`)}
            perspectives={copiesPerspectives}
            toolbarHide
            autoHeight
            readOnly
        />
    );
};
