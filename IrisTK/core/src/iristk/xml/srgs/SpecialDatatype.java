//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.12.16 at 08:54:18 AM CET 
//


package iristk.xml.srgs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Special.datatype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Special.datatype">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="NULL"/>
 *     &lt;enumeration value="VOID"/>
 *     &lt;enumeration value="GARBAGE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Special.datatype")
@XmlEnum
public enum SpecialDatatype {

    NULL,
    VOID,
    GARBAGE;

    public String value() {
        return name();
    }

    public static SpecialDatatype fromValue(String v) {
        return valueOf(v);
    }

}
