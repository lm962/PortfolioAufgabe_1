/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhbw.wwi16.b2.portfolioaufgabe1.web;

import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.CategoryBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.ejb.AdBean;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.Category;
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.Ad;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author x7
 */
@WebServlet(urlPatterns = {"/app/ads/"})
public class AdListServlet extends HttpServlet{
    
    
    @EJB
    private CategoryBean categoryBean;
    
    @EJB
    private AdBean adBean;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verfügbare Kategorien und Stati für die Suchfelder ermitteln
        request.setAttribute("categories", this.categoryBean.findAllSorted());
        request.setAttribute("statuses", Ad.Offertype.values());

        // Suchparameter aus der URL auslesen
        String searchTitle = request.getParameter("search_title");
        String searchDescription = request.getParameter("search_description");
        String searchCategory = request.getParameter("search_category");

        // Anzuzeigende Aufgaben suchen
        Category category = null;
        Ad.Offertype status = null;

        if (searchCategory != null) {
            try {
                category = this.categoryBean.findById(Long.parseLong(searchCategory));
            } catch (NumberFormatException ex) {
                category = null;
            }
        }

        List<Ad> ads = this.adBean.search(searchTitle, category, searchDescription);
        request.setAttribute("ads", ads);

        // Anfrage an die JSP weiterleiten
        request.getRequestDispatcher("/WEB-INF/app/ad_list.jsp").forward(request, response);
    }
    
}
