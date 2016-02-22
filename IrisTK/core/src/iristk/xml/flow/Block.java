//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.29 at 11:17:13 AM CEST 
//


package iristk.xml.flow;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.sun.xml.internal.bind.Locatable;
import com.sun.xml.internal.bind.annotation.XmlLocation;
import org.w3c.dom.Element;
import org.xml.sax.Locator;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{iristk.flow}actionSequence"/>
 *       &lt;attribute name="weight" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="prio" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *       &lt;attribute name="cond" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "actionGroup"
})
@XmlRootElement(name = "block")
public class Block implements Locatable
{

    @XmlElementRefs({
        @XmlElementRef(name = "block", namespace = "iristk.flow", type = Block.class, required = false),
        @XmlElementRef(name = "raise", namespace = "iristk.flow", type = Raise.class, required = false),
        @XmlElementRef(name = "propagate", namespace = "iristk.flow", type = Propagate.class, required = false),
        @XmlElementRef(name = "exec", namespace = "iristk.flow", type = Exec.class, required = false),
        @XmlElementRef(name = "if", namespace = "iristk.flow", type = If.class, required = false),
        @XmlElementRef(name = "reentry", namespace = "iristk.flow", type = Reentry.class, required = false),
        @XmlElementRef(name = "log", namespace = "iristk.flow", type = Log.class, required = false),
        @XmlElementRef(name = "elseif", namespace = "iristk.flow", type = Elseif.class, required = false),
        @XmlElementRef(name = "var", namespace = "iristk.flow", type = Var.class, required = false),
        @XmlElementRef(name = "goto", namespace = "iristk.flow", type = Goto.class, required = false),
        @XmlElementRef(name = "return", namespace = "iristk.flow", type = Return.class, required = false),
        @XmlElementRef(name = "wait", namespace = "iristk.flow", type = Wait.class, required = false),
        @XmlElementRef(name = "else", namespace = "iristk.flow", type = Else.class, required = false),
        @XmlElementRef(name = "send", namespace = "iristk.flow", type = Send.class, required = false),
        @XmlElementRef(name = "random", namespace = "iristk.flow", type = Random.class, required = false),
        @XmlElementRef(name = "call", namespace = "iristk.flow", type = Call.class, required = false),
        @XmlElementRef(name = "run", namespace = "iristk.flow", type = Run.class, required = false)
    })
    @XmlAnyElement(lax = true)
    protected List<Object> actionGroup;
    @XmlAttribute(name = "weight")
    protected Integer weight;
    @XmlAttribute(name = "prio")
    protected Integer prio;
    @XmlAttribute(name = "cond")
    protected String cond;
    @XmlLocation
    @XmlTransient
    protected Locator locator;

    /**
     * Gets the value of the actionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Block }
     * {@link Object }
     * {@link Raise }
     * {@link If }
     * {@link Exec }
     * {@link Propagate }
     * {@link Reentry }
     * {@link Elseif }
     * {@link Log }
     * {@link Var }
     * {@link Element }
     * {@link Wait }
     * {@link Return }
     * {@link Goto }
     * {@link Else }
     * {@link Send }
     * {@link Random }
     * {@link Call }
     * {@link Run }
     * 
     * 
     */
    public List<Object> getActionGroup() {
        if (actionGroup == null) {
            actionGroup = new ArrayList<Object>();
        }
        return this.actionGroup;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWeight(Integer value) {
        this.weight = value;
    }

    /**
     * Gets the value of the prio property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getPrio() {
        if (prio == null) {
            return  0;
        } else {
            return prio;
        }
    }

    /**
     * Sets the value of the prio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrio(Integer value) {
        this.prio = value;
    }

    /**
     * Gets the value of the cond property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCond() {
        return cond;
    }

    /**
     * Sets the value of the cond property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCond(String value) {
        this.cond = value;
    }

    @Override
	public Locator sourceLocation() {
        return locator;
    }

    public void setSourceLocation(Locator newLocator) {
        locator = newLocator;
    }

}
