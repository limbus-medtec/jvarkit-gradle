//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.github.lindenb.jvarkit.ncbi.schema.pubmed;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "registryNumber",
    "nameOfSubstance"
})
@XmlRootElement(name = "Chemical")
public class Chemical {

    @XmlElement(name = "RegistryNumber", required = true)
    protected String registryNumber;
    @XmlElement(name = "NameOfSubstance", required = true)
    protected String nameOfSubstance;

    /**
     * Gets the value of the registryNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistryNumber() {
        return registryNumber;
    }

    /**
     * Sets the value of the registryNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistryNumber(String value) {
        this.registryNumber = value;
    }

    /**
     * Gets the value of the nameOfSubstance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOfSubstance() {
        return nameOfSubstance;
    }

    /**
     * Sets the value of the nameOfSubstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOfSubstance(String value) {
        this.nameOfSubstance = value;
    }

}
