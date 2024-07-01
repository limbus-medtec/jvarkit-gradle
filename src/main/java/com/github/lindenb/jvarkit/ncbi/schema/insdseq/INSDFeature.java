//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.github.lindenb.jvarkit.ncbi.schema.insdseq;

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
    "insdFeatureKey",
    "insdFeatureLocation",
    "insdFeatureIntervals",
    "insdFeatureOperator",
    "insdFeaturePartial5",
    "insdFeaturePartial3",
    "insdFeatureQuals",
    "insdFeatureXrefs"
})
@XmlRootElement(name = "INSDFeature")
public class INSDFeature {

    @XmlElement(name = "INSDFeature_key", required = true)
    protected String insdFeatureKey;
    @XmlElement(name = "INSDFeature_location", required = true)
    protected String insdFeatureLocation;
    @XmlElement(name = "INSDFeature_intervals")
    protected INSDFeatureIntervals insdFeatureIntervals;
    @XmlElement(name = "INSDFeature_operator")
    protected String insdFeatureOperator;
    @XmlElement(name = "INSDFeature_partial5")
    protected INSDFeaturePartial5 insdFeaturePartial5;
    @XmlElement(name = "INSDFeature_partial3")
    protected INSDFeaturePartial3 insdFeaturePartial3;
    @XmlElement(name = "INSDFeature_quals")
    protected INSDFeatureQuals insdFeatureQuals;
    @XmlElement(name = "INSDFeature_xrefs")
    protected INSDFeatureXrefs insdFeatureXrefs;

    /**
     * Gets the value of the insdFeatureKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINSDFeatureKey() {
        return insdFeatureKey;
    }

    /**
     * Sets the value of the insdFeatureKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINSDFeatureKey(String value) {
        this.insdFeatureKey = value;
    }

    /**
     * Gets the value of the insdFeatureLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINSDFeatureLocation() {
        return insdFeatureLocation;
    }

    /**
     * Sets the value of the insdFeatureLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINSDFeatureLocation(String value) {
        this.insdFeatureLocation = value;
    }

    /**
     * Gets the value of the insdFeatureIntervals property.
     * 
     * @return
     *     possible object is
     *     {@link INSDFeatureIntervals }
     *     
     */
    public INSDFeatureIntervals getINSDFeatureIntervals() {
        return insdFeatureIntervals;
    }

    /**
     * Sets the value of the insdFeatureIntervals property.
     * 
     * @param value
     *     allowed object is
     *     {@link INSDFeatureIntervals }
     *     
     */
    public void setINSDFeatureIntervals(INSDFeatureIntervals value) {
        this.insdFeatureIntervals = value;
    }

    /**
     * Gets the value of the insdFeatureOperator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINSDFeatureOperator() {
        return insdFeatureOperator;
    }

    /**
     * Sets the value of the insdFeatureOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINSDFeatureOperator(String value) {
        this.insdFeatureOperator = value;
    }

    /**
     * Gets the value of the insdFeaturePartial5 property.
     * 
     * @return
     *     possible object is
     *     {@link INSDFeaturePartial5 }
     *     
     */
    public INSDFeaturePartial5 getINSDFeaturePartial5() {
        return insdFeaturePartial5;
    }

    /**
     * Sets the value of the insdFeaturePartial5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link INSDFeaturePartial5 }
     *     
     */
    public void setINSDFeaturePartial5(INSDFeaturePartial5 value) {
        this.insdFeaturePartial5 = value;
    }

    /**
     * Gets the value of the insdFeaturePartial3 property.
     * 
     * @return
     *     possible object is
     *     {@link INSDFeaturePartial3 }
     *     
     */
    public INSDFeaturePartial3 getINSDFeaturePartial3() {
        return insdFeaturePartial3;
    }

    /**
     * Sets the value of the insdFeaturePartial3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link INSDFeaturePartial3 }
     *     
     */
    public void setINSDFeaturePartial3(INSDFeaturePartial3 value) {
        this.insdFeaturePartial3 = value;
    }

    /**
     * Gets the value of the insdFeatureQuals property.
     * 
     * @return
     *     possible object is
     *     {@link INSDFeatureQuals }
     *     
     */
    public INSDFeatureQuals getINSDFeatureQuals() {
        return insdFeatureQuals;
    }

    /**
     * Sets the value of the insdFeatureQuals property.
     * 
     * @param value
     *     allowed object is
     *     {@link INSDFeatureQuals }
     *     
     */
    public void setINSDFeatureQuals(INSDFeatureQuals value) {
        this.insdFeatureQuals = value;
    }

    /**
     * Gets the value of the insdFeatureXrefs property.
     * 
     * @return
     *     possible object is
     *     {@link INSDFeatureXrefs }
     *     
     */
    public INSDFeatureXrefs getINSDFeatureXrefs() {
        return insdFeatureXrefs;
    }

    /**
     * Sets the value of the insdFeatureXrefs property.
     * 
     * @param value
     *     allowed object is
     *     {@link INSDFeatureXrefs }
     *     
     */
    public void setINSDFeatureXrefs(INSDFeatureXrefs value) {
        this.insdFeatureXrefs = value;
    }

}
