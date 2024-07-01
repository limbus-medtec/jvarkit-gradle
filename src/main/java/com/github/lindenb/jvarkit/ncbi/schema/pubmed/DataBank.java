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
    "dataBankName",
    "accessionNumberList"
})
@XmlRootElement(name = "DataBank")
public class DataBank {

    @XmlElement(name = "DataBankName", required = true)
    protected String dataBankName;
    @XmlElement(name = "AccessionNumberList")
    protected AccessionNumberList accessionNumberList;

    /**
     * Gets the value of the dataBankName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataBankName() {
        return dataBankName;
    }

    /**
     * Sets the value of the dataBankName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataBankName(String value) {
        this.dataBankName = value;
    }

    /**
     * Gets the value of the accessionNumberList property.
     * 
     * @return
     *     possible object is
     *     {@link AccessionNumberList }
     *     
     */
    public AccessionNumberList getAccessionNumberList() {
        return accessionNumberList;
    }

    /**
     * Sets the value of the accessionNumberList property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessionNumberList }
     *     
     */
    public void setAccessionNumberList(AccessionNumberList value) {
        this.accessionNumberList = value;
    }

}
