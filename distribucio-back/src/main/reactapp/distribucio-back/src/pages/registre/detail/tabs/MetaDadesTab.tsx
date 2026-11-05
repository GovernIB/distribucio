import StyledMuiGrid, { ToolbarButton } from '../../../../components/StyledMuiGrid.tsx';
import { FormField, MuiForm, useBaseAppContext, useFormApiRef, useFormContext, useResourceApiService } from 'reactlib';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Load from '../../../../components/Load.tsx';
import * as builder from '../../../../util/springFilterUtils';
import { fieldType } from '../../../metadada/MetaDadaGrid.tsx';
import { icons } from '../../../../util/icons';
import Box from '@mui/material/Box';

const DadaField = ({ index, metaDada, value, onChange, required, disabled, ...other }: any) => {
    const { fields } = useFormContext();

    const fieldDomini = fields?.filter((i) => i.name == 'domini')[0];
    const isDomini = metaDada.tipus == 'DOMINI';

    const additionalProps = isDomini
        ? { field: fieldDomini, autocomplete: true, requestParams: { domini: metaDada.domini?.id } }
        : {};

    const type = isDomini ? 'enum' : fieldType(metaDada.tipus);

    const decimalScale = metaDada.tipus === 'SENCER' ? 0 : undefined;

    return (
        <FormField
            label={''}
            name={`${metaDada.id}-${index}`}
            type={type}
            value={value}
            onChange={(e: any) => {
                const newValue = e?.target ? e.target.value : e;
                onChange?.(newValue);
            }}
            componentProps={{ size: 'small' }}
            required={required}
            decimalScale={decimalScale}
            disabled={disabled || metaDada.readOnly}
            readOnly={disabled || metaDada.readOnly}
            {...additionalProps}
            {...other}
        />
    );
};

const MetaDadaColumn = ({ metaDada }: any) => {
    const { data, apiRef } = useFormContext();

    const handleRemove = (index: number) => {
        const dadesActual = data.dades;
        const arrayActual = dadesActual[metaDada.id] || [];

        const nouArray = arrayActual.filter((_: any, i: any) => i !== index);

        apiRef.current?.setFieldValue('dades', {
            ...dadesActual,
            [metaDada.id]: nouArray,
        });
    };

    const handleAdd = (newValue: string = '') => {
        const dadesActual = data.dades;
        const arrayActual = dadesActual[metaDada.id] || [];

        apiRef.current?.setFieldValue('dades', {
            ...dadesActual,
            [metaDada.id]: [...arrayActual, newValue],
        });
    };

    const handleUpdateValue = (index: number, newValue: string) => {
        const dadesActual = data.dades;
        const arrayActual = dadesActual[metaDada.id] || [];
        const nouArray = [...arrayActual];
        nouArray[index] = newValue;

        apiRef.current?.setFieldValue('dades', {
            ...dadesActual,
            [metaDada.id]: nouArray,
        });
    };

    const values = data.dades[metaDada.id] || [];
    const isMultiple = metaDada.multiplicitat === 'M_0_N' || metaDada.multiplicitat === 'M_1_N';
    const isRequired = metaDada.multiplicitat === 'M_1' || metaDada.multiplicitat === 'M_1_N';
    const disabled = !data.potModificar

    return (
        <Grid container direction="column" rowSpacing={1} width="100%">
            {values.map((value: any, index: number) => (
                <Grid container direction="row" size={12} columnSpacing={1} key={`${metaDada.id}-${index}`}>
                    <Grid size={11}>
                        <DadaField
                            index={index}
                            metaDada={metaDada}
                            value={value}
                            onChange={(newValue: string) => handleUpdateValue(index, newValue)}
                            disabled={disabled}
                        />
                    </Grid>
                    {isMultiple && (
                        <Grid size={1} sx={{ display: 'flex', alignItems: 'center' }}>
                            <IconButton onClick={() => handleRemove(index)} size="small">
                                <Icon>{icons.esborra}</Icon>
                            </IconButton>
                        </Grid>
                    )}
                </Grid>
            ))}

            {isMultiple && (
                <Grid container direction={'row'} size={12} columnSpacing={1} rowSpacing={1}>
                    <Grid size={11}>
                        <DadaField index={'new'} value={data[`${metaDada.id}-new`]} metaDada={metaDada} disabled={disabled} />
                    </Grid>
                    <Grid size={1}>
                        <IconButton
                            onClick={() => {
                                handleAdd(data[`${metaDada.id}-new`]);
                                apiRef.current?.setFieldValue(`${metaDada.id}-new`, null);
                            }}
                            disabled={data[`${metaDada.id}-new`] == null}
                        >
                            <Icon>{icons.crea}</Icon>
                        </IconButton>
                    </Grid>
                </Grid>
            )}

            {values.length === 0 && !isMultiple && (
                <Grid container direction="row" size={12}>
                    <Grid size={11}>
                        <DadaField
                            index={0}
                            metaDada={metaDada}
                            required={isRequired}
                            onChange={(newValue: string) => handleUpdateValue(0, newValue)}
                            disabled={disabled}
                        />
                    </Grid>
                </Grid>
            )}
        </Grid>
    );
};

const columns = [
    { field: 'nom', flex: 1 },
    {
        field: 'id',
        flex: 1,
        headerName: '',
        renderCell: (params: any) => <MetaDadaColumn metaDada={params.row} />,
    },
];
const sortModel: any = [{ field: 'ordre', sort: 'asc' }];

export const MetaDadesTab = ({ entity, refresh }: any) => {
    const { t } = useBaseAppContext();
    const apiRef = useFormApiRef();

    const { isReady: apiIsReady, artifactAction: apiAction } = useResourceApiService('registreResource');
    const { temporalMessageShow } = useBaseAppContext();

    const save = () => {
        if (apiIsReady) {
            apiAction(entity.id, { code: 'UPDATE_DADES', data: apiRef.current?.getData() })
                .then(() => {
                    temporalMessageShow(null, t('page.registre.dades.success'), 'success');
                    refresh?.();
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    };

    const desaButton = (
        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 1 }}>
            <ToolbarButton icon={icons.desa} onClick={save}>
                {t('common.save')}
            </ToolbarButton>
        </Box>
    );

    return (
        <Load value={entity} noEffect>
            <MuiForm
                apiRef={apiRef}
                key={entity.id}
                id={entity.id}
                title={t('page.registre.dades.title')}
                resourceName={'registreResource'}
                resourceType={'ACTION'}
                resourceTypeCode={'UPDATE_DADES'}
                hiddenToolbar
                componentProps={{ sx: { mt: 0 } }}
                additionalData={{ potModificar: entity?.potModificar }}
                hiddenSaveButton
                hiddenBackButton
                hiddenRevertButton
                hiddenDeleteButton
                initOnChangeRequest
            >
                {entity?.potModificar && desaButton}
                <StyledMuiGrid
                    resourceName={'metaDadaResource'}
                    columns={columns}
                    filter={builder.eq('activa', true)}
                    fixedSortModel={sortModel}
                    disableColumnSorting
                    toolbarHide
                    autoHeight
                    readOnly
                />
            </MuiForm>
        </Load>
    );
};
