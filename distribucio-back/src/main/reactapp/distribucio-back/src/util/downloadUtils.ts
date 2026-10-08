export const iniciaDescarga = (url:string, fileName:string) => {
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link); // Limpieza
    URL.revokeObjectURL(url);
}
export const iniciaDescargaBlob = (result: any) => {
    const url = URL.createObjectURL(result.blob);
    iniciaDescarga(url, result.fileName)
}
export const iniciaDescargaJSON = (result: any) => {
    const data = result.blob;

    const fileName = result.fileName;

    // 1. Convertir el objeto a una cadena JSON
    const jsonStr = JSON.stringify(data, null, 2); // `null, 2` para formato legible

    // 2. Crear un Blob con el contenido
    const blob = new Blob([jsonStr], { type: "application/json" });

    iniciaDescargaBlob({fileName, blob})
}