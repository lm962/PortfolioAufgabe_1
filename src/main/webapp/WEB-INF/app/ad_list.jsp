<%-- 
    Copyright © 2018 Dennis Schulmeister-Zimolong

    E-Mail: dhbw@windows3.de
    Webseite: https://www.wpvs.de/

    Dieser Quellcode ist lizenziert unter einer
    Creative Commons Namensnennung 4.0 International Lizenz.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib tagdir="/WEB-INF/tags/templates" prefix="template"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<template:base>
    <jsp:attribute name="title">
        Übersicht
    </jsp:attribute>

    <jsp:attribute name="head">
        <link rel="stylesheet" href="<c:url value="/css/ad_list.css"/>" />
    </jsp:attribute>

    <jsp:attribute name="menu">
        <div class="menuitem">
            <a href="<c:url value="/app/ads/edit/"/>">Inserat hinzufügen</a>
        </div>
        <div class="menuitem">
            <a href="<c:url value="/app/categories/"/>">Kategorien bearbeiten</a>
        </div>
        <div class ="menuitem">
            <a href="<c:url value="/app/users/"/>">Benutzer bearbeiten</a>
        </div>
    </jsp:attribute>

    <jsp:attribute name="content">
        <%-- Suchfilter --%>
        <form method="GET" class="horizontal" id="search">
            <input type="text" name="search_title" value="${param.search_title}" placeholder="Titel"/>
            <input type="text" name="search_description" value="${param.search_description}" placeholder="Beschreibung"/>
            <select name="search_category">
                <option value="">Alle Kategorien</option>

                <c:forEach items="${categories}" var="category">
                    <option value="${category.id}" ${param.search_category == category.id ? 'selected' : ''}>
                        <c:out value="${category.name}" />
                    </option>
                </c:forEach>
            </select>

            <button class="icon-search" type="submit">
                Suchen
            </button>
        </form>

        <%-- Gefundene Anzeigen --%>
        <c:choose>
            <c:when test="${empty ads}">
                <p>
                    Es wurden keine Anzeigen gefunden. 🐈
                </p>
            </c:when>
            <c:otherwise>
                <jsp:useBean id="utils" class="com.dhbw.wwi16.b2.portfolioaufgabe1.web.WebUtils"/>
                
                <table>
                    <thead>
                        <tr>
                            <th>Titel</th>
                            <th>Beschreibung</th>
                            <th>Kategorie</th>
                            <th>Typ</th>
                            <th>Erstelldatum</th>
                            <th>Preis</th>
                            <th>Preisart</th>
                            <th>Ersteller</th>
                        </tr>
                    </thead>
                    <c:forEach items="${ads}" var="ad">
                        <tr>
                            <td>
                                <a href="<c:url value="/app/ads/${ad.id}/"/>">
                                    <c:out value="${ad.title}"/>
                                </a>
                            </td>
                            <td>
                                <c:out value="${ad.description}"/>
                            </td>
                            <td>
                                <c:out value="${ad.category.name}"/>
                            </td>
                            <td>
                                <c:out value="${ad.offertype}"/>
                            </td>
                            <td>
                                <c:out value="${utils.formatDate(ad.creationdate)}"/>
                                <c:out value="${utils.formatTime(ad.creationtime)}"/>
                            </td>
                            <td>
                                <c:out value="${ad.price}"/>
                            </td>
                            <td>
                                <c:out value="${ad.pricetype}"/>
                            </td>             
                            <td>
                                <c:out value="${ad.user.username}"/>
                            </td>                     
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </jsp:attribute>
</template:base>