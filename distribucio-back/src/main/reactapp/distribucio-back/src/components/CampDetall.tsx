import { TableCell, TableRow } from '@mui/material';

const CampDetall: React.FC<{ label: string; value?: React.ReactNode }> = ({ label, value }) => (
    <TableRow>
        <TableCell sx={{ fontWeight: 'bold', width: 260, verticalAlign: 'top' }}>{label}</TableCell>
        <TableCell>{value}</TableCell>
    </TableRow>
);
export default CampDetall;
