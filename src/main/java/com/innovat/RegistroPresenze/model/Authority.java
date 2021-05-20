package com.innovat.RegistroPresenze.model;
import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "AUTHORITIES")
@Data
@ToString
public class Authority implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5815946603279238962L;

	@Id
    @Column(name = "ID",length = 50, unique = true)
    @GeneratedValue
    private Long id;

    @Column(name = "NAME", length = 50, unique = true)
    @NotNull
    private String name;

    @ManyToMany(mappedBy = "authorities", fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude 
    private List<User> users;

}