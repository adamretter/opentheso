/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.ws.restnew;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.ws.rest.ConnexionRest;
import mom.trd.opentheso.ws.rest.RestRDFHelper;

/**
 * REST Web Service
 *
 * @author miled.rousset
 */

//general path = /api

@Path("/")
public class Rest_new {
    /**
     * Creates a new instance of resources La connexion est faite à chaque
     * question
     *
     */
    public Rest_new() {
    }
    
    private HikariDataSource connect() {
        ConnexionRest connexionRest = new ConnexionRest();
        return connexionRest.getConnexion();
    }

  

    /**
     * Permet de lire les préférences d'un thésaurus pour savoir si le
     * webservices est activé ou non
     *
     * @param idTheso
     */
    private boolean getStatusOfWebservices(HikariDataSource ds , String idTheso) {
        return new PreferencesHelper().isWebservicesOn(ds, idTheso);
    }

/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * recherche par Id Ark
     * Partie pour la négociation de contenu 
     * concernant les URI de type ARK avec header 
     * curl -L --header "Accept: application/rdf+xml »
     * curl -L --header "Accept: text/turtle »
     * curl -L --header "Accept: application/json »
     * curl -L --header "Accept: application/ld+json »
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////    

    /**
     *  pour produire du RDF-SKOS
     * @param naan
     * @param arkId
     * @return 
     * #MR
     */
    @Path("/{naan}/{idArk}")
    @GET
    @Produces("application/rdf+xml;charset=UTF-8")
    public Response getSkosFromArk__(@PathParam("naan") String naan,
            @PathParam("idArk") String arkId) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(naan == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(naan.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();        
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConcept(ds,
                naan + "/" + arkId,
                "application/rdf+xml");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build();
    }    
    
    
    /**
     *  pour produire du RDF-SKOS
     * @param naan
     * @param arkId
     * @return 
     * #MR
     */
    @Path("/{naan}/{idArk}.rdf")
    @GET
    @Produces("application/rdf+xml;charset=UTF-8")
    public Response getSkosFromArk(@PathParam("naan") String naan,
            @PathParam("idArk") String arkId) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(naan == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(naan.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();        
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConcept(ds,
                naan + "/" + arkId,
                "application/rdf+xml");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build();
    }
    
    /**
     * pour produire du Json
     * @param naan
     * @param arkId
     * @return 
     * #MR
     */
    @Path("/{naan}/{idArk}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getJsonFromArk__(@PathParam("naan") String naan,
            @PathParam("idArk") String arkId) {
        HikariDataSource ds = connect();        
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(naan == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(naan.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();        
        }       

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConcept(ds,
                naan + "/" + arkId,
                "application/json");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();
    }    
    
    /**
     * pour produire du Json
     * @param naan
     * @param arkId
     * @return 
     * #MR
     */
    @Path("/{naan}/{idArk}.json")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getJsonFromArk(@PathParam("naan") String naan,
            @PathParam("idArk") String arkId) {
        HikariDataSource ds = connect();        
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(naan == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(naan.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();        
        }       

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConcept(ds,
                naan + "/" + arkId,
                "application/json");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * pour produire du JsonLd
     * @param naan
     * @param arkId
     * @return 
     * #MR
     */
    @Path("/{naan}/{idArk}")
    @GET
    @Produces("application/ld+json;charset=UTF-8")
    public Response getJsonldFromArk__(@PathParam("naan") String naan,
            @PathParam("idArk") String arkId) {
        HikariDataSource ds = connect();        
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(naan == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(naan.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();        
        }      

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConcept(ds,
                naan + "/" + arkId,
                "application/ld+json");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * pour produire du JsonLd
     * @param naan
     * @param arkId
     * @return 
     * #MR
     */
    @Path("/{naan}/{idArk}.jsonld")
    @GET
    @Produces("application/ld+json;charset=UTF-8")
    public Response getJsonldFromArk(@PathParam("naan") String naan,
            @PathParam("idArk") String arkId) {
        HikariDataSource ds = connect();        
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(naan == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(naan.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();        
        }      

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConcept(ds,
                naan + "/" + arkId,
                "application/ld+json");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();
    }    
    
    /**
     * pour produire du Turtle
     * @param naan
     * @param arkId
     * @return 
     * #MR
     */
    @Path("/{naan}/{idArk}")
    @GET
    @Produces("text/turtle;charset=UTF-8")
    public Response getTurtleFromArk__(@PathParam("naan") String naan,
            @PathParam("idArk") String arkId) {
        HikariDataSource ds = connect();        
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(naan == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(naan.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();        
        }      

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConcept(ds,
                naan + "/" + arkId,
                "text/turtle");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.TEXT_PLAIN).build();
    }    
    
    /**
     * pour produire du Turtle
     * @param naan
     * @param arkId
     * @return 
     * #MR
     */
    @Path("/{naan}/{idArk}.ttl")
    @GET
    @Produces("text/turtle;charset=UTF-8")
    public Response getTurtleFromArk(@PathParam("naan") String naan,
            @PathParam("idArk") String arkId) {
        HikariDataSource ds = connect();        
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(naan == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(naan.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(arkId.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();        
        }      

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConcept(ds,
                naan + "/" + arkId,
                "text/turtle");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.TEXT_PLAIN).build();
    }

/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * fin de la recherche par idArk
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////       

    
    
    
    
/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * Recherche par Id du concept
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////  

    /**
     *  pour produire du RDF-SKOS
     * @param idTheso
     * @param idConcept
     * @return 
     * #MR
     */
    @Path("/{idTheso}.{idConcept}")
    @GET
    @Produces("application/rdf+xml;charset=UTF-8")
    public Response getSkosFromIdConcept__(
            @PathParam("idTheso") String idTheso,
            @PathParam("idConcept") String idConcept) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(idConcept == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(idConcept.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConceptFromId(ds,
                idConcept, idTheso, 
                "application/rdf+xml");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build();
    } 
    
    /**
     *  pour produire du RDF-SKOS
     * @param idTheso
     * @param idConcept
     * @return 
     * #MR
     */
    @Path("/{idTheso}.{idConcept}.rdf")
    @GET
    @Produces("application/rdf+xml;charset=UTF-8")
    public Response getSkosFromIdConcept(
            @PathParam("idTheso") String idTheso,
            @PathParam("idConcept") String idConcept) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(idConcept == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(idConcept.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConceptFromId(ds,
                idConcept, idTheso, 
                "application/rdf+xml");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build();
    }  

    /**
     *  pour produire du Json
     * @param idTheso
     * @param idConcept
     * @return 
     * #MR
     */
    @Path("/{idTheso}.{idConcept}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getJsonFromIdConcept__(
            @PathParam("idTheso") String idTheso,
            @PathParam("idConcept") String idConcept) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(idConcept == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(idConcept.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConceptFromId(ds,
                idConcept, idTheso, 
                "application/json");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();
    }    
    
    /**
     *  pour produire du Json
     * @param idTheso
     * @param idConcept
     * @return 
     * #MR
     */
    @Path("/{idTheso}.{idConcept}.json")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getJsonFromIdConcept(
            @PathParam("idTheso") String idTheso,
            @PathParam("idConcept") String idConcept) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(idConcept == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(idConcept.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConceptFromId(ds,
                idConcept, idTheso, 
                "application/json");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();
    }

    /**
     *  pour produire du JsonLd
     * @param idTheso
     * @param idConcept
     * @return 
     * #MR
     */
    @Path("/{idTheso}.{idConcept}")
    @GET
    @Produces("application/ld+json;charset=UTF-8")
    public Response getJsonLdFromIdConcept__(
            @PathParam("idTheso") String idTheso,
            @PathParam("idConcept") String idConcept) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(idConcept == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(idConcept.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConceptFromId(ds,
                idConcept, idTheso, 
                "application/ld+json");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();
    }    
    
    /**
     *  pour produire du JsonLd
     * @param idTheso
     * @param idConcept
     * @return 
     * #MR
     */
    @Path("/{idTheso}.{idConcept}.jsonld")
    @GET
    @Produces("application/ld+json;charset=UTF-8")
    public Response getJsonLdFromIdConcept(
            @PathParam("idTheso") String idTheso,
            @PathParam("idConcept") String idConcept) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(idConcept == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(idConcept.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConceptFromId(ds,
                idConcept, idTheso, 
                "application/ld+json");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();
    }
    
    /**
     *  pour produire du Turtle
     * @param idTheso
     * @param idConcept
     * @return 
     * #MR
     */
    @Path("/{idTheso}.{idConcept}")
    @GET
    @Produces("text/turtle;charset=UTF-8")
    public Response getTurtleFromIdConcept__(
            @PathParam("idTheso") String idTheso,
            @PathParam("idConcept") String idConcept) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(idConcept == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(idConcept.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConceptFromId(ds,
                idConcept, idTheso, 
                "text/turtle");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.TEXT_PLAIN).build();
    } 
    
    /**
     *  pour produire du Turtle
     * @param idTheso
     * @param idConcept
     * @return 
     * #MR
     */
    @Path("/{idTheso}.{idConcept}.ttl")
    @GET
    @Produces("text/turtle;charset=UTF-8")
    public Response getTurtleFromIdConcept(
            @PathParam("idTheso") String idTheso,
            @PathParam("idConcept") String idConcept) {
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        if(idConcept == null) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(idConcept.isEmpty()){
            ds.close();            
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }

        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.exportConceptFromId(ds,
                idConcept, idTheso, 
                "text/turtle");
        ds.close();
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.TEXT_PLAIN).build();
    } 
/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * Fin de la recherche par Id du concept
     */
///////////////////////////////////////////////////// 
////////////////////////////////////////////////////
            
            
            
            
            
            

/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * Recherche par valeurs avec négociation de contenu
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////  

    /**
     *  Permet de rechercher une valeur en filtrant par theso et par langue avec négociation de contenu
     * //exp
     * curl -L --header "Accept: application/rdf+xml" http://localhost:8083/opentheso/api/search?q="vase&lang=fr&theso=2"  
     * curl http://localhost:8083/opentheso/api/search?q=notation:nota1&theso=1&format=json
     * curl -L --header "Accept: application/rdf+xml" http://localhost:8083/opentheso/api/search?q=notation:nota1&theso=1
     * 
     * @param uri
     * RDF+XML
     * @return 
     */
    @Path("/search")
    @GET
    @Produces("application/rdf+xml;charset=UTF-8")
    public Response searchRdf(@Context UriInfo uri) {
        String value = null;
        String idLang = "";
        String idTheso = null;
        String format = null;
        String group = "";
        
        String filter = null;
        
        String datas;

        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("q")) 
                    value = valeur;
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
                if(e.getKey().equalsIgnoreCase("group")) 
                    group = valeur;                
                if(e.getKey().equalsIgnoreCase("format")) 
                    format = valeur;
            }
        }
        
        if(idTheso == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(value == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }  
        
        // vérification du filtre pour savoir si la recherche concerne des champs spécifiques 
        if(value.contains("notation:")) {
            /// rercherche par notation
            filter = "notation:";
        }
        if(value.contains("prefLabel:")) {
            /// rercherche par notation
        }        
        
        
        if(format == null)
            format = "rdf";
        switch (format) {
            case "rdf":
            {
                format = "application/rdf+xml";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build(); 
            }
            case "jsonld":
                format = "application/ld+json";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();                     
            case "turtle":
                format = "text/turtle";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.TEXT_PLAIN).build();                 
            case "json":
                format = "application/json";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();                 
        }
        return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
    }
    
    
    /**
     *  Permet de rechercher une valeur en filtrant par theso et par langue avec négociation de contenu
     * //exp
     * curl -L --header "Accept: application/json" "http://193.48.140.131:8083/opentheso/api/search?q=or&lang=fr&theso=TH_1"  
     * @param uri
     * JSON
     * @return 
     */
    @Path("/search")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response searchJson(@Context UriInfo uri) {
        String value = null;
        String idLang = "";
        String idTheso = null;
        String group = "";
        String format = null;
        String filter = null;
        
        String datas;
        
        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("q")) 
                    value = valeur;
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
                if(e.getKey().equalsIgnoreCase("group")) 
                    group = valeur;                
                if(e.getKey().equalsIgnoreCase("format")) 
                    format = valeur;
            }
        }        
        
        if(idTheso == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(value == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        
        // vérification du filtre pour savoir si la recherche concerne des champs spécifiques 
        if(value.contains("notation:")) {
            /// rercherche par notation
            filter = "notation:";
        }
        if(value.contains("prefLabel:")) {
            /// rercherche par notation
        }          
        
        if(format == null)
            format = "json";
        switch (format) {
            case "rdf":
            {
                format = "application/rdf+xml";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build(); 
            }
            case "jsonld":
                format = "application/ld+json";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();                     
            case "turtle":
                format = "text/turtle";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.TEXT_PLAIN).build();                 
            case "json":
                format = "application/json";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();                 
        }
        return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
    }    
    
    /**
     *  Permet de rechercher une valeur en filtrant par theso et par langue avec négociation de contenu
     * //exp
     * curl -L --header "Accept: application/ld+json" "http://193.48.140.131:8083/opentheso/api/search?q=or&lang=fr&theso=TH_1"  
     * @param uri
     * JSON+Ld
     * @return 
     */
    @Path("/search")
    @GET
    @Produces("application/ld+json;charset=UTF-8")
    public Response searchJsonLd(@Context UriInfo uri) {
        String value = null;
        String idLang = "";
        String idTheso = null;
        String group = "";
        String format = null;
        String filter = null;        
        String datas;        

        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("q")) 
                    value = valeur;
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
                if(e.getKey().equalsIgnoreCase("group")) 
                    group = valeur;                
                if(e.getKey().equalsIgnoreCase("format")) 
                    format = valeur;
            }
        }        
        if(idTheso == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(value == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }        
        // vérification du filtre pour savoir si la recherche concerne des champs spécifiques 
        if(value.contains("notation:")) {
            /// rercherche par notation
            filter = "notation:";
        }
        if(value.contains("prefLabel:")) {
            /// rercherche par notation
        }          
        
        if(format == null)
            format = "jsonld";
        switch (format) {
            case "rdf":
            {
                format = "application/rdf+xml";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build(); 
            }
            case "jsonld":
                format = "application/ld+json";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();                     
            case "turtle":
                format = "text/turtle";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.TEXT_PLAIN).build();                 
            case "json":
                format = "application/json";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();                 
        }
        return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();    
    } 
    
    /**
     *  Permet de rechercher une valeur en filtrant par theso et par langue avec négociation de contenu
     * //exp
     * curl -L --header "Accept: text/turtle" "http://193.48.140.131:8083/opentheso/api/search?q=or&lang=fr&theso=TH_1"  
     * @param uri
     * JSON+Ld
     * @return 
     */
    @Path("/search")
    @GET
    @Produces("text/turtle;charset=UTF-8")
    public Response searchTurtle(@Context UriInfo uri) {
        String value = null;
        String idLang = "";
        String idTheso = null;
        String group = "";
        String format = null;
        String filter = null;        
        String datas;        

        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("q")) 
                    value = valeur;
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
                if(e.getKey().equalsIgnoreCase("group")) 
                    group = valeur;                
                if(e.getKey().equalsIgnoreCase("format")) 
                    format = valeur;
            }
        }        
        
        if(idTheso == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(value == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        
        // vérification du filtre pour savoir si la recherche concerne des champs spécifiques 
        if(value.contains("notation:")) {
            /// rercherche par notation
            filter = "notation:";
        }
        if(value.contains("prefLabel:")) {
            /// rercherche par notation
        }          
        
        if(format == null)
            format = "turtle";
        switch (format) {
            case "rdf":
            {
                format = "application/rdf+xml";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build(); 
            }
            case "jsonld":
                format = "application/ld+json";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();                     
            case "turtle":
                format = "text/turtle";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.TEXT_PLAIN).build();                 
            case "json":
                format = "application/json";
                datas = getDatas(idTheso, idLang, group, value, format, filter);
                if(datas == null) {
                    return Response.status(Status.OK).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();                 
        }
        return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();      
    }

    private String getDatas(
            String idTheso, String idLang, String group, 
            String value,
            String format, String filter){
        HikariDataSource ds = connect();
        if(ds == null) 
            return null;
        String datas = null;
        RestRDFHelper restRDFHelper = new RestRDFHelper();
        
        if(filter !=null) {
            switch (filter) {
                case "notation:":
                    value = value.substring(value.indexOf(":") + 1);
                    datas = restRDFHelper.findNotation(ds, idTheso, value, format);
                    return datas;
            }
        }
                
        datas = restRDFHelper.findConcepts(ds,
                idTheso, idLang, group, value, format);
        ds.close();
        if(datas == null) {
            return null;
        }
        return datas;
    }
    

    

/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * fin de la recherche par valeur
     */
///////////////////////////////////////////////////// 
///////////////////////////////////////////////////// 


    
/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * Recherche par valeurs pour autocomplétion 
     * on revoie que le prefLable et l'URI en Json
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////  
    
    
    /**
     * Permet de rechercher une valeur en filtrant par theso et par langue
     * retourne une liste des valeurs (prefLabel + Uri) pour les programmes qui font de l'autocompletion  
     * exp :
     * http://193.48.140.131:8083/opentheso/api/autocomplete/or?theso=TH_1&lang=fr&group=6
     * @param value
     * @param uri
     * JSON+Ld
     * @return 
     */
    @Path("/autocomplete/{value}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response searchAutocomplete(@PathParam("value") String value, @Context UriInfo uri) {
        String idLang = "";
        String idTheso = null;
        String group = "";
        
        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
                if(e.getKey().equalsIgnoreCase("group")) 
                    group = valeur;                              
            }
        }
        if(idTheso == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        if(value == null || value.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
      
        String datas = getAutocompleteDatas(idTheso, idLang, group, value);
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();       
    }
    
    
    /**
     * Une autre forme d'appel pour l'autocomplétion adaptée aux plugins type Omeka-S
     * Permet de rechercher une valeur en filtrant par theso et par langue
     * retourne une liste des valeurs (prefLabel + Uri) pour les programmes qui font de l'autocompletion  
     * exp :
     * http://193.48.140.131:8083/opentheso/api/autocomplete?theso=TH_1&value=vase&lang=fr&group=6
     * @param uri
     * JSON+Ld
     * @return 
     */
    @Path("/autocomplete")
    @GET
    @Produces("application/ld+json;charset=UTF-8")
    public Response searchAutocomplete2(@Context UriInfo uri) {
        String idLang = "";
        String value = null;        
        String idTheso = null;
        String group = "";
        
        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("value")) 
                    value = valeur;                 
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
                if(e.getKey().equalsIgnoreCase("group")) 
                    group = valeur;                              
            }
        }
        if(value == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
        }        
        if(idTheso == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
        }
      
        String datas = getAutocompleteDatas(idTheso, idLang, group, value);
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();       
    }    

    private String getAutocompleteDatas(String idTheso,
            String idLang, String group,
            String value){
        HikariDataSource ds = connect();
        if(ds == null) 
            return null;
        RestRDFHelper restRDFHelper = new RestRDFHelper();
        String datas = restRDFHelper.findAutocompleteConcepts(ds,
                idTheso, idLang, group, value);
        ds.close();
        if(datas == null) {
            return null;
        }
        return datas;
    } 

/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * Fin de la recherche par valeurs pour autocomplétion 
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////  

    
    
/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * Fonctions avancées pour retourner une branche complète
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////      


    /**
     * Pour retourner une branche complète à partir d'un concept en SKOS mais en
     * remontant la branche par les BT (termes génériques)
     *
     * @param uri
     * @return
     */
    @Path("expansion/concept")
    @GET
    @Produces("application/rdf+xml;charset=UTF-8")
    public Response getBrancheOfConcepts(@Context UriInfo uri) {
        String idConcept = null;
        String idTheso = null;
        String way = null;

        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("id")) 
                    idConcept = valeur;
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
                if(e.getKey().equalsIgnoreCase("way")) 
                    way = valeur;
            }
        }
        if(idTheso == null || idConcept == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        String datas = getBranchOfConcepts(idConcept, idTheso, way);

        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build();     
    }

    private String getBranchOfConcepts(String idConcept,
            String idTheso, String way){
        HikariDataSource ds = connect();
        String datas = null;
        if(ds == null) 
            return null;
        RestRDFHelper restRDFHelper = new RestRDFHelper();
        
        // sens de récupération des concepts vers le haut
        if(way.equalsIgnoreCase("top")) {
            datas = restRDFHelper.brancheOfConceptsTop(ds,
                    idConcept, idTheso, "application/rdf+xml");
        }
        
        // sens de récupération des concepts vers le bas        
        if(way.equalsIgnoreCase("down")) {
            datas = restRDFHelper.brancheOfConceptsDown(ds,
                    idConcept, idTheso, "application/rdf+xml");
        }        
        ds.close();
        if(datas == null) {
            return null;
        }
        return datas;
    } 

    
  
    /**
     * Pour retourner une branche complète à partir d'un identifiant d'un groupe
     *
     * @param uri
     * @return
     */
    @Path("all/group")
    @GET
    @Produces("application/rdf+xml;charset=UTF-8")
    public Response getAllBrancheOfGroup(@Context UriInfo uri) {
        String idGroup = null;
        String idTheso = null;

        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("id")) 
                    idGroup = valeur;
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
            }
        }
        if(idTheso == null || idGroup == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        String datas = getAllBrancheOfGroup__(idTheso, idGroup);

        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptySkos()).type(MediaType.APPLICATION_XML).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_XML).build();     
    }

    private String getAllBrancheOfGroup__(String idtheso,
            String idGroup){
        HikariDataSource ds = connect();
        String datas;
        if(ds == null) 
            return null;
        RestRDFHelper restRDFHelper = new RestRDFHelper();
        datas = restRDFHelper.brancheOfGroup(ds, idGroup, idtheso, "application/rdf+xml");

        ds.close();
        if(datas == null) {
            return null;
        }
        return datas;
    }     
    
    
    
    
    
    
/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * inforamtions sur le thésaurus
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////      
    

    /**
     * Pour retourner une branche complète à partir d'un identifiant d'un groupe
     *
     * @param uri
     * @return
     */
    @Path("info/lastupdate")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getInfoLastUpdate(@Context UriInfo uri) {
        String idTheso = null;
        for (Map.Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("theso")) 
                    idTheso = valeur;
            }
        }
        if(idTheso == null) {
            return Response.status(Status.BAD_REQUEST).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
        }
        HikariDataSource ds = connect();
        if(ds == null)
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
        
        String datas = getInfoLastUpdate__(ds, idTheso);
        ds.close();
        
        if(datas == null) {
            return Response.status(Status.NO_CONTENT).entity(messageEmptyJson()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(datas).type(MediaType.APPLICATION_JSON).build();     
    }
    
    private String getInfoLastUpdate__(HikariDataSource ds, String idTheso) {
        ConceptHelper conceptHelper = new ConceptHelper();
        Date date = conceptHelper.getLastModifcation(ds, idTheso);
        return "{\"lastApdate\": \"" + date.toString() + "\"}";
    }
    
   
    private String messageEmptySkos() {
        String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<rdf:RDF\n"
                + "	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
                + "</rdf:RDF>";

        return message;
    }

    private String messageEmptyJson() {
        String message = "{\n"
                + "}";

        return message;
    }
   
}
