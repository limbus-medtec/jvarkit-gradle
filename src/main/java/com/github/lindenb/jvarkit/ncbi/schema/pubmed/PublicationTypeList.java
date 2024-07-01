//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.github.lindenb.jvarkit.ncbi.schema.pubmed;

import java.util.ArrayList;
import java.util.List;
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
    "publicationType"
})
@XmlRootElement(name = "PublicationTypeList")
public class PublicationTypeList {

    @XmlElement(name = "PublicationType", required = true)
    protected List<PublicationType> publicationType;

    /**
     * Gets the value of the publicationType property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the publicationType property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPublicationType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PublicationType }
     * </p>
     * 
     * 
     * @return
     *     The value of the publicationType property.
     */
    public List<PublicationType> getPublicationType() {
        if (publicationType == null) {
            publicationType = new ArrayList<>();
        }
        return this.publicationType;
    }

}
