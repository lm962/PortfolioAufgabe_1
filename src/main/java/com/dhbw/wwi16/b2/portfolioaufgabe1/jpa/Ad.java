/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dhbw.wwi16.b2.portfolioaufgabe1.jpa;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.*;
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
    @Id
    @GeneratedValue(generator = "ad_ids")
    @TableGenerator(name = "ad_ids", initialValue = 0, allocationSize = 50)
    private Long id;   
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Category category;
    
    @Enumerated(EnumType.STRING)
    private Offertype offertype;
    
    private String title;
    
    @Lob
    private String description;
    
    private Date creationdate;
    
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
