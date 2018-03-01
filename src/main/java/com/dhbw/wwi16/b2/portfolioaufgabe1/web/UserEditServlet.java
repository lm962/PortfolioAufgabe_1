/*
 * Copyright © 2018 Dennis Schulmeister-Zimolong
 * 
 * E-Mail: dhbw@windows3.de
 * Webseite: https://www.wpvs.de/
 * 
 * Dieser Quellcode ist lizenziert unter einer
 * Creative Commons Namensnennung 4.0 International Lizenz.
 */
package com.dhbw.wwi16.b2.portfolioaufgabe1.web;

import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.CategoryBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.UserBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.ValidationBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.User;
import java.io.IOException;
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
 * Seite zum Anlegen oder Bearbeiten einer Aufgabe.
 */
@WebServlet(urlPatterns = "/app/users/")
public class UserEditServlet extends HttpServlet {

    @EJB
    CategoryBean categoryBean;

    @EJB
    UserBean userBean;

    @EJB
    ValidationBean validationBean;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Zu bearbeitende Aufgabe einlesen
        HttpSession session = request.getSession();

        User user = this.userBean.getCurrentUser();

                                
         if (session.getAttribute("user_form") == null) {
            request.setAttribute("user_form", this.createUserForm(user));
        }

        // Anfrage an die JSP weiterleiten
        request.getRequestDispatcher("/WEB-INF/app/user_edit.jsp").forward(request, response);

        session.removeAttribute("user_form");
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
                this.saveUser(request, response);
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
    private void saveUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Formulareingaben prüfen
        List<String> errors = new ArrayList<>();

        String userName = request.getParameter("user_name");
        String userAnschrift = request.getParameter("user_anschrift");
        String userOrt = request.getParameter("user_ort");
        String userPlz = request.getParameter("user_plz");
        String userEmail = request.getParameter("user_email");
        String userTel = request.getParameter("user_tel");

        User user = this.userBean.getCurrentUser();

        user.setName(userName);
        user.setAnschrift(userAnschrift);
        user.setOrt(userOrt);
        user.setPlz(userPlz);
        user.setEmail(userEmail);
        user.setTel(userTel);
                
        this.validationBean.validate(user, errors);

       if (errors.isEmpty()) {
            this.userBean.update(user);
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
            session.setAttribute("user_form", formValues);

            response.sendRedirect(request.getRequestURI());
        }
    }

    private FormValues createUserForm(User user) {
        Map<String, String[]> values = new HashMap<>();
        
        values.put("user_username", new String[]{
            user.getUsername()
        });
        
        values.put("user_name", new String[]{
            user.getName()
        });

        values.put("user_anschrift", new String[]{
            user.getAnschrift()
        });

        values.put("user_ort", new String[]{
            user.getOrt()
        });
        
        values.put("user_plz", new String[]{
            user.getPlz()
        });

        values.put("user_email", new String[]{
            user.getEmail()
        });
        
        values.put("user_tel", new String[]{
            user.getTel()
        });

        FormValues formValues = new FormValues();
        formValues.setValues(values);
        return formValues;
    }

}
