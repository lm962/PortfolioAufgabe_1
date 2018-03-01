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
import java.sql.Time;
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

        // Zu bearbeitende Aufgabe einlesen
        HttpSession session = request.getSession();

        Ad ad = this.getRequestedTask(request);
        
        if(ad.getId() != null)
            request.setAttribute("edit", ad.getId() != 0);
                                
        if (session.getAttribute("ad_form") == null) {
            // Keine Formulardaten mit fehlerhaften Daten in der Session,
            // daher Formulardaten aus dem Datenbankobjekt übernehmen
            request.setAttribute("ad_form", this.createTaskForm(ad));
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
                this.saveTask(request, response);
                break;
            case "delete":
                this.deleteTask(request, response);
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
    private void saveTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Formulareingaben prüfen
        List<String> errors = new ArrayList<>();

        String adCategory = request.getParameter("ad_category");
        String adOffertype = request.getParameter("ad_offertype");
        String adTitle = request.getParameter("ad_title");
        String adDescription = request.getParameter("ad_description");
        String adCreationDate = request.getParameter("ad_creationdate");
        String adCreationTime = request.getParameter("ad_creationtime");
        String adPrice = request.getParameter("ad_price");
        String adPricetype = request.getParameter("ad_pricetype");

        Ad ad = this.getRequestedTask(request);

        if (adCategory != null && !adCategory.trim().isEmpty()) {
            try {
                ad.setCategory(this.categoryBean.findById(Long.parseLong(adCategory)));
            } catch (NumberFormatException ex) {
                // Ungültige oder keine ID mitgegeben
            }
        }
        Date dueDate = null;
        Time dueTime = null;
        if(adCreationDate != null)
            dueDate = WebUtils.parseDate(adCreationDate);
        else
            dueDate = new Date(System.currentTimeMillis());
        if(adCreationTime != null) 
            dueTime = WebUtils.parseTime(adCreationTime);
        else
            dueTime = new Time(System.currentTimeMillis());
        
        if (dueDate != null) {
            ad.setCreationdate(dueDate);
        } else {
            errors.add("Das Datum muss dem Format dd.mm.yyyy entsprechen.");
        }

        if (dueTime != null) {
            ad.setCreationtime(dueTime);
        } else {
            errors.add("Die Uhrzeit muss dem Format hh:mm:ss entsprechen.");
        }

        try {
          ad.setOffertype(Ad.Offertype.valueOf(adOffertype));
        } catch (IllegalArgumentException ex) {
           errors.add("Die ausgewählte Art ist nicht vorhanden.");
        }
       
       try {
          ad.setPricetype(Ad.Pricetype.valueOf(adPricetype));
        } catch (IllegalArgumentException ex) {
           errors.add("Die ausgewählte Preisart ist nicht vorhanden.");
        }

        ad.setTitle(adTitle);
        ad.setDescription(adDescription);
        ad.setPrice(Double.parseDouble(adPrice));

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
    private void deleteTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Datensatz löschen
        Ad task = this.getRequestedTask(request);
        this.adBean.delete(task);

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
    private Ad getRequestedTask(HttpServletRequest request) {
        // Zunächst davon ausgehen, dass ein neuer Satz angelegt werden soll
        Ad task = new Ad();
        task.setUser(this.userBean.getCurrentUser());
        task.setCreationdate(new Date(System.currentTimeMillis()));
        task.setCreationtime(new Time(System.currentTimeMillis()));

        // ID aus der URL herausschneiden
        String taskId = request.getPathInfo();

        if (taskId == null) {
            taskId = "";
        }

        taskId = taskId.substring(1);

        if (taskId.endsWith("/")) {
            taskId = taskId.substring(0, taskId.length() - 1);
        }

        // Versuchen, den Datensatz mit der übergebenen ID zu finden
        try {
            task = this.adBean.findById(Long.parseLong(taskId));
        } catch (NumberFormatException ex) {
            // Ungültige oder keine ID in der URL enthalten
        }

        return task;
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
    private FormValues createTaskForm(Ad ad) {
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
        
        values.put("ad_creationtime", new String[]{
            WebUtils.formatTime(ad.getCreationtime())
        });
        
        values.put("ad_title", new String[]{
            ad.getTitle()
        });

        values.put("ad_description", new String[]{
            ad.getDescription()
        });
        values.put("ad_price", new String[]{
            ad.getPriceString()
        });
        
        FormValues formValues = new FormValues();
        formValues.setValues(values);
        return formValues;
    }

}
