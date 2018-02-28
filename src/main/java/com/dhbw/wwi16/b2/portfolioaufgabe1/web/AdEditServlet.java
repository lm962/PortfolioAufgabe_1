/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhbw.wwi16.b2.portfolioaufgabe1.web;

import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.AdBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.CategoryBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.UserBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.ValidationBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.Ad;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.Category;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.TaskStatus;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.User;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author x7
 */
@WebServlet(urlPatterns = {"/app/ads/*"})
public class AdEditServlet extends HttpServlet{
    
    @EJB
    AdBean adBean;

    @EJB
    CategoryBean categoryBean;
    
    @EJB
    UserBean userBean;

    @EJB
    ValidationBean validationBean;
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verfügbare Kategorien und Stati für die Suchfelder ermitteln
        request.setAttribute("categories", this.categoryBean.findAllSorted());
        request.setAttribute("statuses", TaskStatus.values());

        // Zu bearbeitende Aufgabe einlesen
        HttpSession session = request.getSession();

        Ad ad = this.getRequestedAd(request);
        request.setAttribute("edit", ad.getId() != 0);
                                
        if (session.getAttribute("ad_form") == null) {
            // Keine Formulardaten mit fehlerhaften Daten in der Session,
            // daher Formulardaten aus dem Datenbankobjekt übernehmen
            request.setAttribute("ad_form", this.createAdForm(ad));
        }

        // Anfrage an die JSP weiterleiten
        request.getRequestDispatcher("/WEB-INF/app/ad_edit.jsp").forward(request, response);

        session.removeAttribute("ad_form");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Angeforderte Aktion ausführen
        request.setCharacterEncoding("utf-8");

        String action = request.getParameter("action");

        if (action == null) {
            action = "";
        }

        switch (action) {
            case "save":
                this.saveAd(request, response);
                break;
            case "delete":
                this.deleteAd(request, response);
                break;
        }
    }

    /**
     * Aufgerufen in doPost(): Neue oder vorhandene Aufgabe speichern
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void saveAd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Formulareingaben prüfen
        List<String> errors = new ArrayList<>();

        String adCategory = request.getParameter("ad_category");
        String adOffertype = request.getParameter("ad_offertype");
        String adTitle = request.getParameter("ad_title");
        String adDescription = request.getParameter("ad_description");
        String adCreationDate = request.getParameter("ad_creationdate");
        String adPrice = request.getParameter("ad_price");
        String adPricetype = request.getParameter("ad_pricetype");

        Ad ad = this.getRequestedAd(request);

        if (adCategory != null && !adCategory.trim().isEmpty()) {
            try {
                ad.setCategory(this.categoryBean.findById(Long.parseLong(adCategory)));
            } catch (NumberFormatException ex) {
                // Ungültige oder keine ID mitgegeben
            }
        }

        Date dueDate = null;
        
        if(adCreationDate == null)
            dueDate = new Date(System.currentTimeMillis());
        else
            dueDate = WebUtils.parseDate(adCreationDate);

        if (dueDate != null) {
            ad.setCreationdate(dueDate);
        } else {
            errors.add("Das Datum muss dem Format dd.mm.yyyy entsprechen.");
        }
            
        ad.setCategory(new Category(adCategory));
        ad.setTitle(adTitle);
        ad.setUser(this.userBean.getCurrentUser());
        ad.setDescription(adDescription);
        ad.setTitle(adTitle);
        ad.setCreationdate(dueDate);
        ad.setOffertype(Ad.Offertype.valueOf(adOffertype));
        ad.setPrice(Double.parseDouble(adPrice));
        ad.setPricetype(Ad.Pricetype.valueOf(adPricetype));
               
        this.validationBean.validate(ad, errors);

        // Datensatz speichern
        if (errors.isEmpty()) {
            this.adBean.update(ad);
        }

        // Weiter zur nächsten Seite
        if (errors.isEmpty()) {
            // Keine Fehler: Startseite aufrufen
            response.sendRedirect(WebUtils.appUrl(request, "/app/ads/"));
        } else {
            // Fehler: Formuler erneut anzeigen
            FormValues formValues = new FormValues();
            formValues.setValues(request.getParameterMap());
            formValues.setErrors(errors);

            HttpSession session = request.getSession();
            session.setAttribute("ad_form", formValues);

            response.sendRedirect(request.getRequestURI());
        }
    }

    /**
     * Aufgerufen in doPost: Vorhandene Aufgabe löschen
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void deleteAd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Datensatz löschen
        Ad ad = this.getRequestedAd(request);
        this.adBean.delete(ad);

        // Zurück zur Übersicht
        response.sendRedirect(WebUtils.appUrl(request, "/app/ads/"));
    }

    /**
     * Zu bearbeitende Aufgabe aus der URL ermitteln und zurückgeben. Gibt
     * entweder einen vorhandenen Datensatz oder ein neues, leeres Objekt
     * zurück.
     *
     * @param request HTTP-Anfrage
     * @return Zu bearbeitende Aufgabe
     */
    private Ad getRequestedAd(HttpServletRequest request) {
        // Zunächst davon ausgehen, dass ein neuer Satz angelegt werden soll
        Ad ad = new Ad();
        ad.setUser(this.userBean.getCurrentUser());
        ad.setCreationdate(new Date(System.currentTimeMillis()));

        // ID aus der URL herausschneiden
        String adId = request.getPathInfo();

        if (adId == null) {
            adId = "";
        }

        adId = adId.substring(1);

        if (adId.endsWith("/")) {
            adId = adId.substring(0, adId.length() - 1);
        }
        
        if(ad.getId() == null)
            ad.setId(0L);
        // Versuchen, den Datensatz mit der übergebenen ID zu finden
        try {
            ad = this.adBean.findById(Long.parseLong(adId));
        } catch (NumberFormatException ex) {
            // Ungültige oder keine ID in der URL enthalten
        }

        return ad;
    }

    /**
     * Neues FormValues-Objekt erzeugen und mit den Daten eines aus der
     * Datenbank eingelesenen Datensatzes füllen. Dadurch müssen in der JSP
     * keine hässlichen Fallunterscheidungen gemacht werden, ob die Werte im
     * Formular aus der Entity oder aus einer vorherigen Formulareingabe
     * stammen.
     *
     * @param task Die zu bearbeitende Aufgabe
     * @return Neues, gefülltes FormValues-Objekt
     */
    private FormValues createAdForm(Ad ad) {
        Map<String, String[]> values = new HashMap<>();

        values.put("ad_user", new String[]{
            ad.getUser().getName()
        });

        if (ad.getCategory() != null) {
            values.put("ad_category", new String[]{
                ad.getCategory().toString()
            });
        }

        values.put("ad_creationdate", new String[]{
            WebUtils.formatDate(ad.getCreationdate())
        });

        values.put("ad_title", new String[]{
            ad.getTitle()
        });

        values.put("ad_description", new String[]{
            ad.getDescription()
        });

        FormValues formValues = new FormValues();
        formValues.setValues(values);
        return formValues;
    }
}
