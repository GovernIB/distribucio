import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import translationCa from './translationCa';
import translationEs from './translationEs';

const resources = {
    ca: { translation: translationCa },
    es: { translation: translationEs },
};

// L'atribut lang de <html> (idioma de la pàgina, WCAG 3.1.1) segueix l'idioma de la interfície.
// index.html ja duu lang="ca" per a la càrrega inicial i per als idiomes detectats que l'aplicació
// no té, en què es mostra el fallback (català). Es registra abans d'init perquè la detecció inicial
// també dispara l'esdeveniment.
i18next.on('languageChanged', (lng) => {
    const idioma = lng?.substring(0, 2).toLowerCase();
    if (idioma === 'ca' || idioma === 'es') {
        document.documentElement.lang = idioma;
    }
});

i18next.use(LanguageDetector).use(initReactI18next).init({
    resources,
    fallbackLng: 'ca',
    interpolation: {
        escapeValue: false,
    },
});

export default i18next;
