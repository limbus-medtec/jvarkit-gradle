//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.github.lindenb.jvarkit.ncbi.schema.pubmed;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "QualifierName")
public class QualifierName {

    @XmlAttribute(name = "MajorTopicYN")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String majorTopicYN;
    @XmlValue
    protected String value;

    /**
     * Gets the value of the majorTopicYN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMajorTopicYN() {
        if (majorTopicYN == null) {
            return "N";
        } else {
            return majorTopicYN;
        }
    }

    /**
     * Sets the value of the majorTopicYN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMajorTopicYN(String value) {
        this.majorTopicYN = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getvalue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setvalue(String value) {
        this.value = value;
    }

}
