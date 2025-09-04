/**
 * 
 */
package es.caib.distribucio.plugin.caib.validacio;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.pluginsib.validatesignature.api.TimeStampInfo;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureResponse;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;
import lombok.Synchronized;

/**
 * Implementació del plugin de validació de firmes emprant el client del portafirmes
 * per comunicar-se amb Afirma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioFirmaPluginAfirma extends DistribucioAbstractPluginProperties implements ValidacioSignaturaPlugin {	  
		
	public ValidacioFirmaPluginAfirma() {
		super();
	}
	
	public ValidacioFirmaPluginAfirma(Properties properties) {
		super(properties);
	}

	@Override
	public String getUsuariIntegracio() {
		return this.getProperty("es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.username");
	}

	@Override
	public ValidaSignaturaResposta validaSignatura(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut
			) throws es.caib.distribucio.plugin.SistemaExternException {
		ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
		
		ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
		
		if (documentContingut != null && firmaContingut == null) {
			firmaContingut = documentContingut;
			documentContingut = null;
		}
		if (firmaContingut != null) {
			validationRequest.setSignedDocumentData(documentContingut);
			validationRequest.setSignatureData(firmaContingut);
		} else {
			validationRequest.setSignatureData(documentContingut);
		}
		SignatureRequestedInformation sri = new SignatureRequestedInformation();
		sri.setReturnSignatureTypeFormatProfile(true);
		sri.setReturnCertificateInfo(true);
		sri.setReturnValidationChecks(false);
		sri.setValidateCertificateRevocation(false);
		sri.setReturnCertificates(false);
		sri.setReturnTimeStampInfo(true);
		validationRequest.setSignatureRequestedInformation(sri);
		ValidateSignatureResponse validateSignatureResponse;
		try {
			validateSignatureResponse = new org.fundaciobit.pluginsib.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin(
					"es.caib.distribucio.",
					this.getProperties()).
			validateSignature(validationRequest);
			incrementarOperacioOk();
		} catch (Exception e) {
			incrementarOperacioError();
			throw new es.caib.distribucio.plugin.SistemaExternException(e);
		}
		
		// Completa la resposta
		resposta.setStatus(validateSignatureResponse.getValidationStatus().getStatus());
		resposta.setErrMsg(validateSignatureResponse.getValidationStatus().getErrorMsg());
		resposta.setErrException(validateSignatureResponse.getValidationStatus().getErrorException());
		
		if (validateSignatureResponse.getSignatureDetailInfo() != null) {
			for (SignatureDetailInfo signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
				ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
				TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
				if (timeStampInfo != null) {
					detall.setData(timeStampInfo.getCreationTime());
				} else {
					detall.setData(signatureInfo.getSignDate());
				}
				InformacioCertificat certificateInfo = signatureInfo.getCertificateInfo();
				if (certificateInfo != null) {
					detall.setResponsableNif(certificateInfo.getNifResponsable());
					detall.setResponsableNom(certificateInfo.getNomCompletResponsable());
					detall.setEmissorCertificat(certificateInfo.getEmissorOrganitzacio());
				}
				resposta.getFirmaDetalls().add(detall);
			}
			resposta.setPerfil(ArxiuConversions.toPerfilFirmaArxiu(validateSignatureResponse.getSignProfile()));
			resposta.setTipus(ArxiuConversions.toFirmaTipus(
					validateSignatureResponse.getSignType(),
					validateSignatureResponse.getSignFormat()));
		}
		return resposta;
	}

    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private boolean configuracioEspecifica = false;
    private int operacionsOk = 0;
    private int operacionsError = 0;

    @Synchronized
    private void incrementarOperacioOk() {
        operacionsOk++;
    }

    @Synchronized
    private void incrementarOperacioError() {
        operacionsError++;
    }

    @Synchronized
    private void resetComptadors() {
        operacionsOk = 0;
        operacionsError = 0;
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return this.configuracioEspecifica;
    }

    @Override
    public EstatSalut getEstatPlugin() {
        try {
            Instant start = Instant.now();
            String inputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dss:VerifyRequest xmlns:dss=\"urn:oasis:names:tc:dss:1.0:core:schema\" xmlns:ades=\"urn:oasis:names:tc:dss:1.0:profiles:AdES:schema#\" xmlns:afxp=\"urn:afirma:dss:1.0:profile:XSS:schema\" xmlns:cmism=\"http://docs.oasis-open.org/ns/cmis/messaging/200908/\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:vr=\"urn:oasis:names:tc:dss:1.0:profiles:verificationreport:schema#\" Profile=\"urn:afirma:dss:1.0:profile:XSS\">\n" +
                    "\t<dss:OptionalInputs>\n" +
                    "\t\t<dss:ClaimedIdentity>\n" +
                    "\t\t\t<dss:Name>CAIBDEV.RIPEA</dss:Name>\n" +
                    "\t\t</dss:ClaimedIdentity>\n" +
                    "\t\t<afxp:ReturnReadableCertificateInfo/>\n" +
                    "\t\t<vr:ReturnVerificationReport>\n" +
                    "\t\t\t<vr:ReportOptions>\n" +
                    "\t\t\t\t<vr:ReportDetailLevel>urn:oasis:names:tc:dss:1.0:reportdetail:allDetails</vr:ReportDetailLevel>\n" +
                    "\t\t\t</vr:ReportOptions>\n" +
                    "\t\t</vr:ReturnVerificationReport>\n" +
                    "\t\t<afxp:AdditionalReportOption>\n" +
                    "\t\t\t<afxp:IncludeProperties>\n" +
                    "\t\t\t\t<afxp:IncludeProperty Type=\"urn:afirma:dss:1.0:profile:XSS:SignatureProperty:SignatureTimeStamp\"/>\n" +
                    "\t\t\t</afxp:IncludeProperties>\n" +
                    "\t\t</afxp:AdditionalReportOption>\n" +
                    "\t</dss:OptionalInputs>\n" +
                    "\t<dss:SignatureObject>\n" +
                    "\t\t<dss:Base64Signature>" +
                    "JVBERi0xLjYKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDMgMCBSL0ZpbHRlci9GbGF0ZURlY29kZT4+CnN0cmVhbQp4nCWKuwoCMRRE+/sVUwsb52bzuIEQcEEL" +
                    "u4WAhdj56AS38ffdRaY65wyd4isfEHT0hlii8znCgjpLiuUhlx3e/8e65SVTl5icIefRFSvod+xPCvXoz2ultljpOTatDG3IlbH5yrRxprVhzYUHBk6bt3brZzl2mWXG" +
                    "D1qDHkIKZW5kc3RyZWFtCmVuZG9iagoKMyAwIG9iagoxMjYKZW5kb2JqCgo1IDAgb2JqCjw8L0xlbmd0aCA2IDAgUi9GaWx0ZXIvRmxhdGVEZWNvZGUvTGVuZ3RoMSA5" +
                    "Nzk2Pj4Kc3RyZWFtCnic5Th7dFvlfb/vXsmSbTmSHFuWokS6inAelWXZURLivHxjW7KDHVt+BSkQLNmSLYEtCUl2CI9hVh6Z05Q0pbRQNtKNsqzNGdck3QKHFneFdl3X" +
                    "Qrd25ZWRntLT09OkuJTSrhB5v++7V4qSBjjb2X+79nfv7/3+Pl0pl5mOgQ5mgQdxbCqSXl1lqgaAfwUg1WMzOWFHX+02hM8BcP82np6YevQfb3wXQHUaQHN6YvLg+Im3" +
                    "PIcAdHGA8sV4LBJ9s/kzbgBTEG1sjiNhIH9Qg/hRxK+JT+Vue517kfJRH5omU2ORXr2wDPG3EF8xFbkt/QnVLg6gDlEQkpGp2B8e/VYUcQGgMptOZXNROLQEsPohyk9n" +
                    "YumeR0dfRBzt8dQHwT966RAsozjHq9RlGm15RSX8v7zUR6AWutQ7QA9pdr/s4k+CBR4BWDpPsUv3fM/SH/8vo9DKjy/Ak3AajsCrsF9h+CEACZhGSun1TfghUukVgH3w" +
                    "FZj7ELMn4QzyZbkwPEgzueoVgM/DKfjOZV4CMAV3YCxfg1dJM3wXRyUF7xAt3AMvotV3kLbnaqY4nFYYZ+B4CfV1+CJ3GK7j6Bw/QjmchzPAC/AYuQkt5zDPI8WMt/+J" +
                    "0QfgLrwPQhxmEGaXescHr0H50m8xq7vgOvhz2AWTJRrPkcf5CuzfEDyONf0mo3kKTE0XfzP3Dxx38bOIfAYmcEUI5s4d4Xd9SIX+xxc/DFVkPV8P5VfjchtBn/8jt2Hp" +
                    "Xf4aqIDhpcUCbal76bd8JJ9UjahWqneovvdRPso+o5pCbVj6ef6OfFTdq34Su3UCQOy8YV8oODw0ONAf6Ovd09N93e6uTr+vo71tl9i6c8f2bVtbtly7eVNzk6fR3bBu" +
                    "7Zr6a5yrHXZzjdGgX1ZVWVGu1ZSpVTxHoEGQSNgn8fWC0R9x+pyRLneD4DPHO9wNPqc/LAkRQcKHao2zq4uRnBFJCAvSGnxESshhSUTJ8SskRVlSLEoSg7AdtlMXTkH6" +
                    "fodTOEP29QcRPtLhDAnSBQbvYbBqDUOqEHE4UINFRaMVfJJ/Jj7nC2OMZL6yot3ZHqtwN8B8RSWClQhJ65zpebJuJ2EAt863dZ4DbRV1i5n6IlEp0B/0dVgdjpC7Ybe0" +
                    "zNnBWNDOTEpl7ZKGmRQSNHQ4LMw3LMx96owBRsMuXdQZjdwYlPgI6s7xvrm5BySjS1rv7JDW3/6WGTOPSQ3ODp/kola7B4p+ui+5JJK63uAU5n4HmI7zwvnLKRGFUlZv" +
                    "+B1QUOLaJTIQdNDL6sdaz835nYJ/LjwXObM0O+oUDM65eZ1uLu3DckMgiCbOLD172Cr5PxWSDOE42RpSUvcPdEvL+28ISly9X4hHkIL/rU7HFqvDWJQJfBgbsCxYHKyw" +
                    "w0HLcPiMCKOISLP9QRkXYNT6NIgeV0jiwpSzUODUDlPObIFTVA87sbfdg8E5SVW/O+r0YcUPR6TZUZyum2ljnAZp2XtWh3Ou2ii0eEJMVsCodkcTgqReg0VCrVIFnBuq" +
                    "MmdgyLL35McFKzpYY6wWWpxohtrxOX1h5X8mbkYDAha6yyUPwlBQEjsQECNKx3zzTR7UiISxYYkO1kzJ40xLNc62YndpWL7EYJCpKGpSTbsE4TFFS/L42L4SfHPhDjkE" +
                    "asvZH3wGvEvn5jcK1lNe2AihDipsascpW+ObC0bHJXvYGsV9Ny4ErQ5JDGGHQ85gLETHDiu0/pyVDUeIzcpQsHvQ2d2/L7hFCURmUHOqet8VZpxBq2wGB1DS1muFIGfl" +
                    "QyhoQILgR8DZth3vkqZei8uABWdUOrht24UgsUJBGsOQ1gu+WIciR/HLjKrpOLV3FayVURTttHdZHSGHfLkbOGQLimPU0NKidhVYeEwhQ4vz2d7FSLSWZjr0QtAZc4ac" +
                    "cUESA0GaGy0Pq7JSDFZzpVdDl2ElxcIygQPZBYQWU/K7rKXFlToZXkS7rmDvLrCFOa2ze3COGncqBgEj3y0BHWFxi9HKzgK6oZ149goG3NJsQ8/NiyLdzPGt1Ihzd3TO" +
                    "ORjczqTxPLnLejv1VQ3dpHuozd2AR1vbvJMc6p8XyaHBfcFnDPheeGgo+DRHuPZwW2j+GuQFnxHwQ4NROUqlRIoIFKGWBhDRMnnrMyLALOOqGIHhY2cIMJq2QCMwdoaT" +
                    "aQbZ0RrmSAQOOSqZIxakVUjTyrRZRmPXPNCSiRVqUSuWizquirPOE0p6GinP4ntsOYFTOlJFrPOoNcDIZ8jsfLlolSVmUUKUIzw0fMn18L7gKR1+OlvZHR210QvHxRzH" +
                    "ZuPHik+I0kG5MxSfC4foZgMTtgb/iUScO7FNzp0YSJlOqnDG2qRKZxult1J6q0wvo3QNjigxEVSfxd4HJEIn4IagA7eksOK71jnDBdqpEB4qc4afuzG4bUsfqKfxHbQc" +
                    "X+97RI+6BqpqqsyWutqRUJ0qHKrjDTUjIYMmHDJUg4W0ihYiWMg5CzluIWkL2b9//62Zm/ZDq8sFZrwZq0lLi9Fr9DY3EQM4nEaTd0M1EcCISL1z07XGtZtUT+R/mP/F" +
                    "6du+/N4vL/6BZMl4/m/zf5dfffLkSe4EsZDV79+hJav5F/Nfy5/OS/knVZusqr9asYm+vkDv0nn+af5FfPswwbPiPUZ1JaihzqxdFghpDVxNIMSZBDMBMzlnJgEzaTIT" +
                    "g5ksMvRlM1kwE8lMjpvJUTOZNZO0mYTNRDQTWWXb44wUYKQmRjUwRqn+caYpq+F9/63KlVGum/aXXjIHa0NLc1llHKvXbNq42bvBpNm4xrm6rLYGy7SZfzrf9aNXXnnj" +
                    "P147/Wf3f3L6wD33zpLX88b8b379we9/+8o/PXvuZ19/gb6AEVaHXqyDCcLidqyCSW3CKugDIZ3WYKrha/pDvAkj31maySLLQU4A6U+ZyQgmsL8YPmuiEbzmVm9prPXL" +
                    "iFMw0ljrjM61axCmsV7L9zaf3Je/9pevPnD8WtdgLv/u33z12GTLNevJb3510Z7/45OefPxHX3PQWK0Y61n8RmOCz4kjUF2lUpVXl9eZ1ctNywMhjUmvwq06EKoymHTl" +
                    "GH/tcVbthULxW86V9ANYA4t9kwrpyBSBpnOp7IVu3CrX31vMDbzF9FgzajAdHEuHsa7WsRazXW0hiJGW43dOfpp4D+R/re18tnXxNmIjupN27hcW9wePWtw9a1tIDTdu" +
                    "cbN+uPCt2IL9aCZPi0tGXdnKlQ5Yt87tduh474bmxkCoWb/OsdKoc7vcgZBd76q1lJWVl9cMhMoNa/Glna8fCPGGGS/Z6yWbveQaLzF5SZmXvOclb3nJj7zk217yhJc8" +
                    "7CWjXkICXtLhJU1MrsZLVF4SXywInvaSnJeIXrKRsZH3rpe87iULXiIxG/d6SdSrmJBlDAWxl73kBS/5qpccZWK3eMk2LxEKPrbIDo57SdhLhgo+apjmW0zzIS+ZRfei" +
                    "q4RvZbpvsQA4iQmkmXv0qvcSrbJfRvYXd9KHbKZbb72KQOaSeomQPMZGr7n49LKu00veh3VK85UB2ElwI9bRu4XgDOAhtdG5ehmnMdUaFRT3qEaB6Wj4u0+IvulVe17q" +
                    "WDyYH/7U8RU+X2ut8Ui+7fDwcPCTR/J7Dxwgy/mwa+vGFldb/lcXH7a43RYueFJbUaXavKuADoZWXbRQkBfYGOH5FsC94sc5qoWVcETcZyFEv0Jbq69dZbNAIKS32C2c" +
                    "jrdYdNXVpkCo2qBT94d0pgUbkWzkuI0ctZFZG0nbSNhGAjYCNrITH6KNNNmIYCMGG1lkcih02Q5hV/GUghazZ+Sm/YUzoFCk2hobFmjztbV0h6yhB4JgrCV4dDk2riGq" +
                    "HXdPbH6oqenLe1//3g+eJ4n85+MpcuxG8mr13COB6sot9sbzRP3eO/nxAfLYiSdOPUL3DH4v5n+Kua6EBfEuWL7cXKnTacyaVbaVlkBopX45IiZzIFRhqq2mW8RAt8gT" +
                    "NvKWjbxgIxiMykZaEHnIRnI2ErWRIRvpsJGNNnKNjVgZG6vCldYEK/GyjRTLVaSXTs/IpeG6dHwUxkieotKzvHSCPmxaOvb8/dbb78zkb7mrf3jfJ+/O33zrrUTHhxta" +
                    "Pv1AcRRGVl1cXhwFAjVL5zm36h48MzvFtRXLlmmW83ydWaWr1AVC5ZpKfQ2AsT8EpsfZIdhqJh529mWKJ3jxmKtu2bCBhqjGDxyjc1Mr8dZ6a53yGY6tJL3hkTvuirX+" +
                    "5CfbmrYOOu+tyUxwn3Wv/fGPhy7evavNsMtsZ3MJ9HNXfQQqYRXMiv3VGs0qqFtVZ7OvwDN7hamsurqmhu8P1Rj0GKAeRDuZtROwk5ajdmKwk3N2smAnkp0cZYywnYiM" +
                    "IsOCnZTscFb24mdmYR9fdmCzj0wNfbPYTE9qY42GfpLW1nA4mGu5/Oz923Irhqbn7rx4+C+Ipyz6hYXv//THe1/qJYtnTtfqLtYZXlE1mt15afPR3l+ev5j/rzVyjniG" +
                    "c9XqHsyxFv5SHAedrsxorDPx5YMh4ImB52vF2upAqFavM+qNmGVtTR1R1ZGWd+vI0TrCpetIuI4E6ohYRxbqiFRHjjNUqCOGOgJ1ZJFRULRU8vLTjQ7dCCtAoZEuWGE2" +
                    "/KCwHdlupJ9YZc7C+wN/ac7uEN0NotjgFiu+lLccv4+4VG/KuPj+1pLZwjQtj0wL1fUj+u2/A7v8G+A/d7z8g0u/8CydL7Oo6S9jWtp7kN85QOPI++D6ohC54mchXVkL" +
                    "gPo7sE0F0MsfgV58WpHmQjiAsIdrgRpqTkVDaKH1xmsvvEaayF9z3+a38/+iqsa/e1V/UKzrYIPinwMDnhc3IvAt/tvAM66NJIsx7C3GQ1ByrwJzoIFxBebBClMKrEKZ" +
                    "Qwqshir4ggKXgR6+rMAauB1OK7AWakijApfDMtKmwBUkSQIKXAkruW8Uf+Vu5F5T4CrYxGsVeBms4HfQ6FX017mT/PUKTEBQ8QrMwTKVU4F52KxqVmAVykwosBpWqB5Q" +
                    "4DKwqb6kwBp4V/W8AmthnfqUApfDSvXrClzBvaH+vQJXwhbtvyuwDm4sr1TgKri5vOBrGWws/2FHYiKRS9weiwrRSC4ijKXSBzOJiXhOWDe2XtjQ1NwkdKZSE5MxoT2V" +
                    "SacykVwilWysaL9SbIMwgCa6IrkGYXdyrLEnMRqTZYXBWCYxPhCbmJ6MZHZlx2LJaCwjuIUrJa7E98YyWYpsaGxu9F5iXimbyAoRIZeJRGNTkcwtQmr88jiETGwikc3F" +
                    "MkhMJIXhxsFGIRDJxZI5IZKMCkNFxb7x8cRYjBHHYplcBIVTuThGevN0JpGNJsaot2xjMYGSagzmYjMxYU8kl4tlU8m2SBZ9YWRDiWQq2yAciCfG4sKBSFaIxrKJiSQy" +
                    "Rw8Kl+sIyI1gLslkagZNzsQaMO7xTCwbTyQnhCxNWdEWcvFIjiY9FctlEmORycmD2LKpNGqNYo8OJHJxdDwVywq9sQPCQGoqkvxKoxwK1mYcayokptKZ1AyL0Z0dy8Ri" +
                    "SXQWiUZGE5OJHFqLRzKRMawYli0xlmUVwUII6UjS7ZvOpNIxjPT6zp5LghigXM1sanIGPVPpZCwWpR4x7JnYJCqh48lU6haaz3gqg4FGc3F3SeTjqWQOVVNCJBrFxLFa" +
                    "qbHpKdonLHOuEFxkLJNCXnoykkMrU9nGeC6X3urxHDhwoDGitGYMO9OIlj0fxcsdTMeUfmSolanJHmx/krZumvWXJjG4u0foS2N9/BicoAg0CIXJbG5sVlxgGRPpXLYx" +
                    "m5hsTGUmPH3+HuiABEzgyuG6HWIQBQFXBPEIQmOQgjQchAyTiiNVgHVIXY/PDdAEzbgE6ESpFPInUV+AdoQzqEXvEWY3BUloxC/M7R9rbQNCA0oUXUy7AaHdqD+GFnpQ" +
                    "bxS5pXYFGGSUBB6zVHMCpjGOCFJ2QRa1YigTZRICuHF9nI2P4+9lULbI2YBxNePyXlXz4+wm0JLAKp1jHBrpFIv+FqSlUO+j6iGgXIx1L4ucGMOizCq1PYwSg0wqwDRp" +
                    "JXLMW5JJDV3FYx96HEf9MdbJguQYs00nQracQjiu1PRmrHeGRRBleoXcsuj5Tztw9dkYZNHNMJ97GJ3iWcZrQzyr5CXXbIhFkUIqrcUBjIT6jTM4wuoZZdp0xpKK5ihO" +
                    "nfCRfgRFN6L0Jcl8zChRUp0Gpd7j7J5lfpPoQ2DxyV2+3LfA6hRhVZc7PYXcHJMdQ/ok/h1UdtkUVkX2NarsowNsV8aVjKeYXQF68XmATUWK9S3pWM16fKkq8tyMK3Mq" +
                    "MN00wimWRaGObtYbmkmMRUqhCNv5o6gxyXzLscXZdERYb2NKr3Msg0K9okqmNOo0o7jBx+aC7veYUtPr8ZzouapFuYKls0l7MsnizZbYTrJoo8Uc5WpTqUnFk5zxJDuP" +
                    "bin2Z5zNm1zRKLPm/pCaj7Pa5BSvKRZRFP/kjsuzlULdadYPeT/J05z7k8pFWH1Til6anUo5JZYptj/ibALTsBVfLD0YHf1rZHNYumvGlD3TqMTs+V/r0bjSrIKl+yNT" +
                    "jGUKY+xRdn+yuOumS/ZvoRODeAb1sPMircyPX6mccIUFumuuPDOb2Zl5eRbyNCYQz7F4sqyWjSyHCeT3oYce+g4tfzu4D0O6yjVfHtg1SmJASJxMwHL8+heGXjICw2QX" +
                    "7CAiPkXkteGzHXH6bCQ7YBbldiB9J+Lbkb4Nz0473ltx9eF6EJcKlyzRhBIefHoU3I14A2q8hHfCFqW2IpU+r0O8C5+dytOPdB8+fQq+G3F8Qpho8CW8ld2fJyrxFDl3" +
                    "kbx0kQgXyd3vk8D7ZPado+9wv1lcb39q8flFru/tkbefeptvepvo3yZauGC4ELgQvpC+cPxCWYX+PNHBr4jxZ+e22N/ccXb4P3e8MQxnMbOzTWcDZ2fPSmfVZwk//AZv" +
                    "shsWhIWmhfTC7MLLC+cWFhe0s984+g3u68957Prn7M9x9lN9p+4+xYdPEP0J+wku8MXwF7mjjxH9Y/bHPI/xjz7SaH+k02b//MNr7eceXnyYO7O0cOrhKqP/OdJHemAH" +
                    "1rD3FL9kf2pXLdmDaenxbsflwdWHK4XrQVz4nQfF7bg8pEfcwo98jlQesx5zHbvj2OFj6vT9s/cfvZ+fve/ofdxTM8/PcNnAensq6bInOz9ht3jNwxovP1yGbtC7uHu0" +
                    "fp0/PCLaR1Dohn1N9n2d6+3LvdXDakxYhYJ63s638n18in+Qf57XaAcCNns/rnOBxQAnBsp1fn2fvc/Tx59ZOifGuh1o7br0dbPX8bv96+1dnVvs+k57p6fzpc43O9/u" +
                    "LBvpJI/jv/8p//N+XvSv9/hFv83hX9llHTZ5a4eNRD9s8OqHOYKN9sKwR7+k5/T6Ef3del4PrcDNmoianCFH54cGXa7uM5qlgW5JG7hBIoek+kF6F/v3SWWHJBjed0Nw" +
                    "npBPh+47cgTaVnVLGwaDUnhVqFuKIiBSYBYBw6p5E7SFstmci13E5UJ4Gu/gmnYh8aasTIUiH1xZksUjKsuUiIsKyDjBu4vykED1CGrflAV6o0yXrES1s4o5pizfGGC+" +
                    "6b8BvyiflgplbmRzdHJlYW0KZW5kb2JqCgo2IDAgb2JqCjU2NTgKZW5kb2JqCgo3IDAgb2JqCjw8L1R5cGUvRm9udERlc2NyaXB0b3IvRm9udE5hbWUvQkFBQUFBK0xp" +
                    "YmVyYXRpb25TZXJpZgovRmxhZ3MgNAovRm9udEJCb3hbLTU0MyAtMzAzIDEyNzcgOTgxXS9JdGFsaWNBbmdsZSAwCi9Bc2NlbnQgMAovRGVzY2VudCAwCi9DYXBIZWln" +
                    "aHQgOTgxCi9TdGVtViA4MAovRm9udEZpbGUyIDUgMCBSCj4+CmVuZG9iagoKOCAwIG9iago8PC9MZW5ndGggMjc0L0ZpbHRlci9GbGF0ZURlY29kZT4+CnN0cmVhbQp4" +
                    "nF2Rz27DIAzG7zwFx+5QhaRpu0pRpK5dpBz2R8v2AAScDGkBRMghbz8w3SbtAPoZf59lm+zSXlutfPbqjOjA00Fp6WA2ixNAexiVJnlBpRL+FuEtJm5JFrzdOnuYWj2Y" +
                    "qiLZW8jN3q10c5amhzuSvTgJTumRbj4uXYi7xdovmEB7ykhdUwlDqPPE7TOfIEPXtpUhrfy6DZY/wftqgRYY56kVYSTMlgtwXI9AKsZqWjVNTUDLf7n8ZukH8cldkOZB" +
                    "ylhZ1oEL5EMTeZd4F7lEPu4j79P7NfIhMb4fEz9Gvk96rHlCLljkc9IUkR8Sn7DJWzex3bjPnzVQsTgXVoBLx9nj1ErD779YY6MLzzdIPYT8CmVuZHN0cmVhbQplbmRv" +
                    "YmoKCjkgMCBvYmoKPDwvVHlwZS9Gb250L1N1YnR5cGUvVHJ1ZVR5cGUvQmFzZUZvbnQvQkFBQUFBK0xpYmVyYXRpb25TZXJpZgovRmlyc3RDaGFyIDAKL0xhc3RDaGFy" +
                    "IDExCi9XaWR0aHNbNzc3IDcyMiA1MDAgNDQzIDUwMCA3NzcgNDQzIDUwMCAyNzcgMjUwIDUwMCAyNzcgXQovRm9udERlc2NyaXB0b3IgNyAwIFIKL1RvVW5pY29kZSA4" +
                    "IDAgUgo+PgplbmRvYmoKCjEwIDAgb2JqCjw8L0YxIDkgMCBSCj4+CmVuZG9iagoKMTEgMCBvYmoKPDwvRm9udCAxMCAwIFIKL1Byb2NTZXRbL1BERi9UZXh0XQo+Pgpl" +
                    "bmRvYmoKCjEgMCBvYmoKPDwvVHlwZS9QYWdlL1BhcmVudCA0IDAgUi9SZXNvdXJjZXMgMTEgMCBSL01lZGlhQm94WzAgMCA1OTUuMzAzOTM3MDA3ODc0IDg0MS44ODk3" +
                    "NjM3Nzk1MjhdL0dyb3VwPDwvUy9UcmFuc3BhcmVuY3kvQ1MvRGV2aWNlUkdCL0kgdHJ1ZT4+L0NvbnRlbnRzIDIgMCBSPj4KZW5kb2JqCgo0IDAgb2JqCjw8L1R5cGUv" +
                    "UGFnZXMKL1Jlc291cmNlcyAxMSAwIFIKL01lZGlhQm94WyAwIDAgNTk1IDg0MSBdCi9LaWRzWyAxIDAgUiBdCi9Db3VudCAxPj4KZW5kb2JqCgoxMiAwIG9iago8PC9U" +
                    "eXBlL0NhdGFsb2cvUGFnZXMgNCAwIFIKL09wZW5BY3Rpb25bMSAwIFIgL1hZWiBudWxsIG51bGwgMF0KL0xhbmcoY2EtRVMpCj4+CmVuZG9iagoKMTMgMCBvYmoKPDwv" +
                    "Q3JlYXRvcjxGRUZGMDA1NzAwNzIwMDY5MDA3NDAwNjUwMDcyPgovUHJvZHVjZXI8RkVGRjAwNEMwMDY5MDA2MjAwNzIwMDY1MDA0RjAwNjYwMDY2MDA2OTAwNjMwMDY1" +
                    "MDAyMDAwMzcwMDJFMDAzMj4KL0NyZWF0aW9uRGF0ZShEOjIwMjExMDE5MTUzNTQ5KzAyJzAwJyk+PgplbmRvYmoKCnhyZWYKMCAxNAowMDAwMDAwMDAwIDY1NTM1IGYg" +
                    "CjAwMDAwMDY4MjMgMDAwMDAgbiAKMDAwMDAwMDAxOSAwMDAwMCBuIAowMDAwMDAwMjE2IDAwMDAwIG4gCjAwMDAwMDY5OTIgMDAwMDAgbiAKMDAwMDAwMDIzNiAwMDAw" +
                    "MCBuIAowMDAwMDA1OTc4IDAwMDAwIG4gCjAwMDAwMDU5OTkgMDAwMDAgbiAKMDAwMDAwNjE4OSAwMDAwMCBuIAowMDAwMDA2NTMyIDAwMDAwIG4gCjAwMDAwMDY3MzYg" +
                    "MDAwMDAgbiAKMDAwMDAwNjc2OCAwMDAwMCBuIAowMDAwMDA3MDkxIDAwMDAwIG4gCjAwMDAwMDcxODggMDAwMDAgbiAKdHJhaWxlcgo8PC9TaXplIDE0L1Jvb3QgMTIg" +
                    "MCBSCi9JbmZvIDEzIDAgUgovSUQgWyA8Mjk1QjFGNDNFNDRDQzU0MEMyQzlFRjEzMDQ1M0NEMTk+CjwyOTVCMUY0M0U0NENDNTQwQzJDOUVGMTMwNDUzQ0QxOT4gXQov" +
                    "RG9jQ2hlY2tzdW0gLzc4MTE1NzhCOTlFQjk4Q0NCRTdDNEVBNjlEMURFRTcyCj4+CnN0YXJ0eHJlZgo3MzYzCiUlRU9GCg==</dss:Base64Signature>\n" +
                    "\t</dss:SignatureObject>\n" +
                    "</dss:VerifyRequest>";
            
            // Crida directament al WS
            new org.fundaciobit.pluginsib.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin(
					"es.caib.distribucio.",
					this.getProperties()).cridadaWs(inputXml);
            
            return EstatSalut.builder()
                    .latencia((int) Duration.between(start, Instant.now()).toMillis())
                    .estat(EstatSalutEnum.UP)
                    .build();
        } catch (Exception ex) {
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
        }
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
                .totalOk(operacionsOk)
                .totalError(operacionsError)
                .build();
        resetComptadors();
        return integracioPeticions;
    }
}
