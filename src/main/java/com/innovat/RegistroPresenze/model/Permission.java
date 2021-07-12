package com.innovat.RegistroPresenze.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;


@Entity
@Table(name = "PERMISSION")
@Data
@ToString
public class Permission implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5815946603279238962L;

	@Id
    @Column(name = "ID",length = 50, unique = true)
    @GeneratedValue
    private Long id;

    @Column(name = "NAME", length = 50)
    @NotNull(message="{NotNull.permission.name}")
    @Size(min = 1, max = 100, message="{Size.perission.name}")
    private String name;
    
    @Column(name = "ACCEPTED")
    @NotNull(message="{NotNull.permission.accepted}")
    private boolean accepted;

    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="USER_ID",nullable=false)
	@JsonBackReference
	@ToString.Exclude 
    private User user;
    
    @OneToMany(mappedBy = "permission",fetch = FetchType.LAZY,cascade= {CascadeType.ALL})
    private List<Event> events;
    
    public Permission() {}
    
}
