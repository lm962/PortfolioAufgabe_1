/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhbw.wwi16.b2.portfolioaufgabe1.jpa;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author geizzo
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ad implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ad_ids")
    @TableGenerator(name = "ad_ids", initialValue = 0, allocationSize = 50)
    private Long id;   
    
    @ManyToOne
    @NotNull(message = "Die Anzeige muss einem Benutzer geordnet werden.")
    private User user;
    
    @ManyToOne
    private Category category;
    
    @Enumerated(EnumType.STRING)
    private Offertype offertype;
    
    @Column(length = 50)
    @NotNull(message = "Der Titel darf nicht leer sein.")
    @Size(min = 1, max = 50, message = "Der Titel muss zwischen ein und 50 Zeichen lang sein.")
    private String title;
    
    @Lob
    private String description;
    
    private Date creationdate;
    
    private Time creationtime;
    
    private double price;
    
    @Enumerated(EnumType.STRING)
    private Pricetype pricetype; 
    
    public String getPriceString(){
        return ""+ getPrice();
    }
    
    public enum Offertype{
        Biete, Suche 
    }
    public enum Pricetype{
        Verhandlungsbasis, Festpreis
    }
    
    
}
