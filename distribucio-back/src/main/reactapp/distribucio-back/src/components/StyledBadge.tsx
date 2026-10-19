import { styled } from '@mui/material/styles';
import Badge from '@mui/material/Badge';
import { accentColor } from '../theme';

type ThemeColorKey = 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success';

interface StyledBadgeProps {
    badgecolor?: ThemeColorKey | string;
    textcolor?: ThemeColorKey | string;
}

export const StyledBadge = styled(Badge)<StyledBadgeProps>(({ theme, badgecolor = 'primary', textcolor = 'white' }) => {
    // El badge "primary" és el color "torrat" de la identitat (veure accentColor), com la resta de
    // badges de l'aplicació; la resta de colors són els del tema.
    const resolveColor = (value: string | undefined) =>
        value === 'primary' ? accentColor(theme) : theme.palette[value as ThemeColorKey]?.main || value;

    return {
        paddingLeft: theme.spacing(1),
        paddingRight: theme.spacing(1),
        '& .MuiBadge-badge': {
            backgroundColor: resolveColor(badgecolor),
            color: resolveColor(textcolor),
        },
    };
});