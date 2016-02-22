//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.12.16 at 08:54:18 AM CET 
//


package iristk.xml.srgs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the iristk.xml.srgs package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Grammar_QNAME = new QName("http://www.w3.org/2001/06/grammar", "grammar");
    private final static QName _RuleToken_QNAME = new QName("http://www.w3.org/2001/06/grammar", "token");
    private final static QName _RuleItem_QNAME = new QName("http://www.w3.org/2001/06/grammar", "item");
    private final static QName _RuleTag_QNAME = new QName("http://www.w3.org/2001/06/grammar", "tag");
    private final static QName _RuleOneOf_QNAME = new QName("http://www.w3.org/2001/06/grammar", "one-of");
    private final static QName _RuleRuleref_QNAME = new QName("http://www.w3.org/2001/06/grammar", "ruleref");
    private final static QName _RuleExample_QNAME = new QName("http://www.w3.org/2001/06/grammar", "example");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: iristk.xml.srgs
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Grammar }
     * 
     */
    public Grammar createGrammar() {
        return new Grammar();
    }

    /**
     * Create an instance of {@link Lexicon }
     * 
     */
    public Lexicon createLexicon() {
        return new Lexicon();
    }

    /**
     * Create an instance of {@link OneOf }
     * 
     */
    public OneOf createOneOf() {
        return new OneOf();
    }

    /**
     * Create an instance of {@link Ruleref }
     * 
     */
    public Ruleref createRuleref() {
        return new Ruleref();
    }

    /**
     * Create an instance of {@link Meta }
     * 
     */
    public Meta createMeta() {
        return new Meta();
    }

    /**
     * Create an instance of {@link Token }
     * 
     */
    public Token createToken() {
        return new Token();
    }

    /**
     * Create an instance of {@link Rule }
     * 
     */
    public Rule createRule() {
        return new Rule();
    }

    /**
     * Create an instance of {@link Item }
     * 
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link Metadata }
     * 
     */
    public Metadata createMetadata() {
        return new Metadata();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Grammar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "grammar")
    public JAXBElement<Grammar> createGrammar(Grammar value) {
        return new JAXBElement<Grammar>(_Grammar_QNAME, Grammar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Token }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "token", scope = Rule.class)
    public JAXBElement<Token> createRuleToken(Token value) {
        return new JAXBElement<Token>(_RuleToken_QNAME, Token.class, Rule.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Item }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "item", scope = Rule.class)
    public JAXBElement<Item> createRuleItem(Item value) {
        return new JAXBElement<Item>(_RuleItem_QNAME, Item.class, Rule.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "tag", scope = Rule.class)
    public JAXBElement<String> createRuleTag(String value) {
        return new JAXBElement<String>(_RuleTag_QNAME, String.class, Rule.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OneOf }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "one-of", scope = Rule.class)
    public JAXBElement<OneOf> createRuleOneOf(OneOf value) {
        return new JAXBElement<OneOf>(_RuleOneOf_QNAME, OneOf.class, Rule.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ruleref }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "ruleref", scope = Rule.class)
    public JAXBElement<Ruleref> createRuleRuleref(Ruleref value) {
        return new JAXBElement<Ruleref>(_RuleRuleref_QNAME, Ruleref.class, Rule.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "example", scope = Rule.class)
    public JAXBElement<String> createRuleExample(String value) {
        return new JAXBElement<String>(_RuleExample_QNAME, String.class, Rule.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Token }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "token", scope = Item.class)
    public JAXBElement<Token> createItemToken(Token value) {
        return new JAXBElement<Token>(_RuleToken_QNAME, Token.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Item }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "item", scope = Item.class)
    public JAXBElement<Item> createItemItem(Item value) {
        return new JAXBElement<Item>(_RuleItem_QNAME, Item.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "tag", scope = Item.class)
    public JAXBElement<String> createItemTag(String value) {
        return new JAXBElement<String>(_RuleTag_QNAME, String.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OneOf }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "one-of", scope = Item.class)
    public JAXBElement<OneOf> createItemOneOf(OneOf value) {
        return new JAXBElement<OneOf>(_RuleOneOf_QNAME, OneOf.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ruleref }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2001/06/grammar", name = "ruleref", scope = Item.class)
    public JAXBElement<Ruleref> createItemRuleref(Ruleref value) {
        return new JAXBElement<Ruleref>(_RuleRuleref_QNAME, Ruleref.class, Item.class, value);
    }

}
