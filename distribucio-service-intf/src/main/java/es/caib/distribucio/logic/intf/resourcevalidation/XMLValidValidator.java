package es.caib.distribucio.logic.intf.resourcevalidation;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;

public class XMLValidValidator implements ConstraintValidator<XMLValid, String> {

    @Override
    public void initialize(XMLValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cadena, ConstraintValidatorContext context) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.parse(new InputSource(new StringReader( cadena )));
        } catch (final Exception ex) {
            return false;
        }
        return true;
    }
}