//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.github.lindenb.jvarkit.ncbi.schema.gb;

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
    "gbSeqid"
})
@XmlRootElement(name = "GBSeq_other-seqids")
public class GBSeqOtherSeqids {

    @XmlElement(name = "GBSeqid")
    protected List<GBSeqid> gbSeqid;

    /**
     * Gets the value of the gbSeqid property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gbSeqid property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getGBSeqid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GBSeqid }
     * </p>
     * 
     * 
     * @return
     *     The value of the gbSeqid property.
     */
    public List<GBSeqid> getGBSeqid() {
        if (gbSeqid == null) {
            gbSeqid = new ArrayList<>();
        }
        return this.gbSeqid;
    }

}
