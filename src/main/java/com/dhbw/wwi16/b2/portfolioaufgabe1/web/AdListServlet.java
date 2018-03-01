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
import com.dhbw.wwi16.b2.portfolioaufgabe1.jpa.User;
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
        String searchCategory = request.getParameter("search_category");
        String searchOffertype = request.getParameter("search_offertype");
        String searchPricetype = request.getParameter("search_pricetype");
        String ownerId = request.getParameter("user_id");
        
        // Anzuzeigende Aufgaben suchen
        Category category = null;
        Ad.Offertype offertype = null;
        Ad.Pricetype pricetype = null;
        User owner = null;

        if (searchCategory != null) {
            try {
                category = this.categoryBean.findById(Long.parseLong(searchCategory));
            } catch (NumberFormatException ex) {
                category = null;
            }
        }

        if (searchOffertype != null) {
            try {
                offertype = Ad.Offertype.valueOf(searchOffertype);
            } catch (IllegalArgumentException ex) {
                offertype = null;
            }

        }
        
        if (searchPricetype != null) {
            try {
                pricetype = Ad.Pricetype.valueOf(searchOffertype);
            } catch (IllegalArgumentException ex) {
                pricetype = null;
            }

        }
        
        if (ownerId != null){
             try {
                owner = new User();
                owner.setUsername(ownerId);
            } catch (Exception ex) {
                ownerId = null;
            }
        }

        List<Ad> ads = this.adBean.search(searchTitle, category);
        request.setAttribute("ads", ads);

        // Anfrage an die JSP weiterleiten
        request.getRequestDispatcher("/WEB-INF/app/ad_list.jsp").forward(request, response);
    }
    
}
