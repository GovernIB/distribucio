import React from 'react';

export const ROLE_PREFIX = 'DIS_';
export const ROLE_SUPER = ROLE_PREFIX + 'SUPER';
export const ROLE_ADMIN = ROLE_PREFIX + 'ADMIN';
export const ROLE_ADMIN_LECTURA = ROLE_PREFIX + 'ADMIN_LECTURA';
export const ROLE_USER = 'tothom';

export type DistribucioContextType = {
    isReady: boolean;
    currentUser: any;
    setCurrentUser: (currentUser: any | undefined) => void;
    rolesAvailable?: string[];
    entitatsAvailable?: any[];
    currentRole?: string;
    setCurrentRole: (currentRole: string | undefined) => void;
    currentEntitatId?: number;
    setCurrentEntitatId: (currentEntitatId: number | undefined) => void;
    currentEntitatLoading?: boolean;
    currentEntitat?: any;
};

export const DistribucioContext = React.createContext<DistribucioContextType | undefined>(undefined);

export const useDistribucioContext = () => {
    const context = React.useContext(DistribucioContext);
    if (context === undefined) {
        throw new Error('useDistribucioContext must be used within a DistribucioProvider');
    }
    return context;
};
