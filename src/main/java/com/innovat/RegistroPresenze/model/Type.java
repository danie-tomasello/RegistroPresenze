//package com.innovat.RegistroPresenze.model;
//
//import java.io.Serializable;
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//
//import lombok.Data;
//import lombok.ToString;
//
//
//@Entity
//@Table(name = "TYPE")
//@Data
//@ToString
////@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//public class Type implements Serializable {
//
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 5815946603279238962L;
//
//	@Id
//    @Column(name = "ID",length = 50, unique = true)
//    @GeneratedValue
//    private Long id;
//
//    @Column(name = "NAME", length = 50, unique = true)
//    @NotNull(message="{NotNull.type.name}")
//    @Size(min = 1, max = 100, message="{Size.type.name}")
//    private String name;
//
//    @OneToMany(mappedBy = "type",cascade= {CascadeType.ALL}, fetch=FetchType.LAZY)
//	@JsonBackReference
//    @ToString.Exclude 
//    private List<Event> events;
//    
//    public Type() {}
//    
//    public Type(Long typeId) {
//		// TODO Auto-generated constructor stub
//    	this.id = typeId;
//    	this.name = null;
//	}
//
//	public Type(String name) {
//		// TODO Auto-generated constructor stub
//		this.name = name;
//	}
//}
