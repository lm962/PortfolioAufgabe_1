<%-- 
    Document   : user_edit
    Created on : 28.02.2018, 11:01:34
    Author     : geizzo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib tagdir="/WEB-INF/tags/templates" prefix="template"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 

<template:base>
    <jsp:attribute name="title">
        <c:choose>
            <c:when test="${edit}">
                Aufgabe bearbeiten
            </c:when>
            <c:otherwise>
                Aufgabe anlegen
            </c:otherwise>
        </c:choose>
    </jsp:attribute>

    <jsp:attribute name="head">
        <link rel="stylesheet" href="<c:url value="/css/user_edit.css"/>" />
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
                <label for="user_username">Username:</label> 
                <div class="side-by-side">
                    <input type="text" name="user_username" value="${user_form.values["user_username"][0]}" readonly="readonly">
                </div>
                <label for="user_name">Name:</label>
                <div class="side-by-side">
                    <input type="text" name="user_name" value="${user_form.values["user_name"][0]}">
                </div>
                <label for="user_anschrift">Anschrift:</label>
                <div class="side-by-side">
                    <input type="text" name="user_anschrift" value="${user_form.values["user_anschrift"][0]}">
                </div>
                <label for="user_ort">Ort:</label>
                <div class="side-by-side">
                    <input type="text" name="user_ort" value="${user_form.values["user_ort"][0]}">
                </div>
                <label for="user_plz">Postleitzahl:</label>
                <div class="side-by-side">
                    <input type="text" name="user_plz" value="${user_form.values["user_plz"][0]}">
                </div>
                <label for="user_email">E-mail:</label>
                <div class="side-by-side">
                    <input type="text" name="user_email" value="${user_form.values["user_email"][0]}">
                </div>
                <label for="user_tel">Telefon:</label>
                <div class="side-by-side">
                    <input type="text" name="user_tel" value="${user_form.values["user_tel"][0]}">
                </div>
                <%-- Button zum Abschicken --%>
                <div class="side-by-side">
                    <button class="icon-pencil" type="submit" name="action" value="save">
                        Sichern
                    </button>

                    <c:if test="${edit}">
                        <button class="icon-trash" type="submit" name="action" value="delete">
                            Löschen
                        </button>
                    </c:if>
                </div>
            </div>

            <%-- Fehlermeldungen --%>
            <c:if test="${!empty user_form.errors}">
                <ul class="errors">
                    <c:forEach items="${user_form.errors}" var="error">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </c:if>
        </form>
    </jsp:attribute>
</template:base>
