//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-146 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.11 at 02:26:06 PM UTC 
//


package com.quakearts.workflowapp.jbpm.xml.assignment;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.quakearts.workflow_assignement package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.quakearts.workflow_assignement
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Assignment.Role }
     * 
     */
    public Assignment.Role createAssignmentRole() {
        return new Assignment.Role();
    }

    /**
     * Create an instance of {@link Assignment.Role.Actor }
     * 
     */
    public Assignment.Role.Actor createAssignmentRoleActor() {
        return new Assignment.Role.Actor();
    }

    /**
     * Create an instance of {@link Assignment }
     * 
     */
    public Assignment createAssignment() {
        return new Assignment();
    }

    /**
     * Create an instance of {@link Assignment.Role.Query }
     * 
     */
    public Assignment.Role.Query createAssignmentRoleQuery() {
        return new Assignment.Role.Query();
    }

}
