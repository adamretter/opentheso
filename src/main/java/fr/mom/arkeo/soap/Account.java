
package fr.mom.arkeo.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour account complex type.
 * 
 * <p>Le fragment de sch\u00e9ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="account"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="baseId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="groups" type="{http://soap.arkeo.mom.fr/}group" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="user" type="{http://soap.arkeo.mom.fr/}user" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "account", propOrder = {
    "baseId",
    "groups",
    "user"
})
public class Account {

    protected String baseId;
    @XmlElement(nillable = true)
    protected List<Group> groups;
    protected User user;

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 baseId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseId() {
        return baseId;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 baseId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseId(String value) {
        this.baseId = value;
    }

    /**
     * Gets the value of the groups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Group }
     * 
     * 
     */
    public List<Group> getGroups() {
        if (groups == null) {
            groups = new ArrayList<Group>();
        }
        return this.groups;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 user.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getUser() {
        return user;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 user.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setUser(User value) {
        this.user = value;
    }

}
