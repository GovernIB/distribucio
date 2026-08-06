import { Breakpoint, Grid, GridSize } from '@mui/material';
import { FormField, FormFieldProps } from 'reactlib';

type ResponsiveStyleValue<T> = T | Array<T | null> | { [key in Breakpoint]?: T | null };

type GridFormFieldProps = FormFieldProps & {
    size: ResponsiveStyleValue<GridSize>;
};

const GridFormField: React.FC<GridFormFieldProps> = (props) => {
    const { size } = props;
    return (
        <Grid size={size}>
            <FormField {...props} />
        </Grid>
    );
};

export default GridFormField;
