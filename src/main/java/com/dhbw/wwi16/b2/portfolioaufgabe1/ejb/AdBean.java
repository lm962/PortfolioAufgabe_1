/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhbw.wwi16.b2.portfolioaufgabe1.ejb;

import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.Ad;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.Category;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.Task;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.TaskStatus;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author x7
 */
@Stateless
@RolesAllowed("portfolioaufgabe_1-app-user")
public class AdBean extends EntityBean<Ad, Long> {
    
    public AdBean (){
        super(Ad.class);
    }
    
    /**
     * Auslesen aller Anzeigen, alphabetisch sortiert.
     *
     * @return Liste mit allen Kategorien
     */
    public List<Ad> findAllSorted() {
        return this.em.createQuery("SELECT a FROM ad a ORDER BY a.titel").getResultList();
    }
    
    /**
     * Alle Aufgaben eines Benutzers, nach Fälligkeit sortiert zurückliefern.
     * @param username Benutzername
     * @return Alle Aufgaben des Benutzers
     */
    public List<Task> findByUsername(String username) {
        return em.createQuery("SELECT t FROM Task t WHERE t.owner.username = :username ORDER BY t.dueDate, t.dueTime")
                 .setParameter("username", username)
                 .getResultList();
    }
    
    /**
     * Suche nach Aufgaben anhand ihrer Bezeichnung, Kategorie und Status.
     * 
     * Anders als in der Vorlesung behandelt, wird die SELECT-Anfrage hier
     * mit der CriteriaBuilder-API vollkommen dynamisch erzeugt.
     * 
     * @param search In der Kurzbeschreibung enthaltener Text (optional)
     * @param category Kategorie (optional)
     * @param status Status (optional)
     * @return Liste mit den gefundenen Aufgaben
     */
    public List<Ad> search(String search, Category category, Ad.Offertype status) {
        // Hilfsobjekt zum Bauen des Query
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        
        // SELECT t FROM Task t
        CriteriaQuery<Ad> query = cb.createQuery(Ad.class);
        Root<Ad> from = query.from(Ad.class);
        query.select(from);

        // ORDER BY dueDate, dueTime
        query.orderBy(cb.asc(from.get("creationdate")), cb.asc(from.get("creationdate")));
        
        // WHERE t.shortText LIKE :search
        if (search != null && !search.trim().isEmpty()) {
            query.where(cb.like(from.get("title"), "%" + search + "%"));
        }
        
        // WHERE t.category = :category
        if (category != null) {
            query.where(cb.equal(from.get("category"), category));
        }
        
        return em.createQuery(query).getResultList();
    }
}
