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
        <c:choose>
            <c:when test="${edit}">
                Anzeige bearbeiten
            </c:when>
            <c:otherwise> 
                Anzeige anlegen
            </c:otherwise>
        </c:choose>
    </jsp:attribute>

    <jsp:attribute name="head">
        <link rel="stylesheet" href="<c:url value="/css/ad_edit.css"/>" />
    </jsp:attribute>

    <jsp:attribute name="menu">
        <div class="menuitem">
            <a href="<c:url value="/app/ads/"/>">Übersicht</a>
        </div>
    </jsp:attribute>

    <jsp:attribute name="content">
        <form method="post" class="stacked">
            <div class="column">
                <%-- CSRF-Token --%>
                <input type="hidden" name="csrf_token" value="${csrf_token}">

                <%-- Eingabefelder --%>
                <label for="ad_category">Kategorie:</label>
                <div class="side-by-side">
                    <select name="ad_category" ${disable}>
                        <option value="">Keine Kategorie</option>

                        <c:forEach items="${categories}" var="category">
                            <option value="${category.id}" ${ad_form.values["ad_category"][0] == category.id ? 'selected' : ''} ${readonlii}>
                                <c:out value="${category.name}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <label for="ad_offertype">Angebotstyp:</label>
                <div class="side-by-side">
                    <select name="ad_offertype" value="${ad_form.values["ad_offertype"][0]}" ${disable}>
                        <option value="Suche">Suche</option>
                        <option value="Biete">Biete</option>
                    </select>
                </div>
                <label for="ad_title">Titel:</label> 
                <div class="side-by-side">
                    <input type="text" name="ad_title" value="${ad_form.values["ad_title"][0]}" ${readonlii}>
                </div>
                <label for="ad_description">Beschreibung:</label>
                <div class="side-by-side">
                    <input type="textarea" name="ad_description" value="${ad_form.values["ad_description"][0]}" ${readonlii}>
                </div>
                <label for="ad_price">Preis:</label>
                <div class="side-by-side">
                    <select name="ad_pricetype" value="${ad_form.values["ad_pricetype"][0]}" ${disable}>
                        <option value="Festpreis" >Festpreis</option>
                        <option value="Verhandlungbasis">Verhandlungbasis</option>
                    </select>
                    <input type="text" name="ad_price" value="${ad_form.values["ad_price"][0]}" ${readonlii}>
                </div>

                <%-- Button zum Abschicken --%>
                <div class="side-by-side">
                    <button class="icon-pencil" type="submit" name="action" value="save"${disable}>
                        Sichern
                    </button>

                    <c:if test="${edit}">
                        <button class="icon-trash" type="submit" name="action" value="delete"${disable}>
                            Löschen
                        </button>
                    </c:if>
                </div>
            </div>

            <%-- Fehlermeldungen --%>
            <c:if test="${!empty task_form.errors}">
                <ul class="errors">
                    <c:forEach items="${task_form.errors}" var="error">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </c:if>
        </form>
    </jsp:attribute>
</template:base>
