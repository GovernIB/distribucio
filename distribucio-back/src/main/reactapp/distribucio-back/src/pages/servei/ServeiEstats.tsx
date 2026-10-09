import { TFunction } from 'i18next';

export interface UpdateProgressDto {
	estat?: 'INICIALITZANT' | 'ACTUALITZANT' | 'FINALITZAT' | 'ERROR';
	total?: number;
	processats?: number;
	progres?: number;
	errorMsg?: string;
}

/**
 * Construeix el missatge HTML (Estat / Total / Processats) que es mostra dins
 * BackdropLoading (que només accepta un string 'progressMessage' renderitzat amb
 * dangerouslySetInnerHTML). S'usa durant/després del polling.
 */
export const buildEstatServeisMessage = (t: TFunction, progres?: UpdateProgressDto): string => {
	const estatLabel = progres?.estat
		? t(`page.serveis.accio.actualitzarTots.estats.${progres.estat}`)
		: '-';
	const total = progres?.total ?? '-';
	const processats = progres?.processats ?? '-';

	let html =
		`<b>${t('page.serveis.accio.actualitzarTots.estat')}</b>: ${estatLabel}<br/>` +
		`<b>${t('page.serveis.accio.actualitzarTots.total')}</b>: ${total}<br/>` +
		`<b>${t('page.serveis.accio.actualitzarTots.processats')}</b>: ${processats}`;

	if (progres?.estat === 'ERROR') {
		html += `<br/><span style="color: var(--mui-palette-error-main, #d32f2f)">` +
			(progres?.errorMsg || t('page.serveis.accio.error')) +
			`</span>`;
	}

	return html;
};