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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;




@Entity
@Table(name = "USERS")
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User extends Auditable<String> implements Serializable{
   /**
	 * 
	 */
	private static final long serialVersionUID = -3518018508482697232L;

	@Id 
	@GeneratedValue
	@Column(name = "USERID", length = 50, unique = true)
	private Long id;
	
	@Column(name = "USERNAME", length = 50, unique = true)
    @NotNull(message="{NotNull.User.username}")
    @Size(min = 4, max = 50, message="{Size.User.username}")
    private String username;
	
	@Column(name = "NAME", length = 50)
    @NotNull(message="{NotNull.User.name}")
    @Size(min = 2, max = 50, message="{Size.User.name}")
    private String name;
	
	@Column(name = "SURNAME", length = 50)
    @NotNull(message="{NotNull.User.surname}")
    @Size(min = 2, max = 50, message="{Size.User.surname}")
    private String surname;

    @Column(name = "PASSWORD", length = 100)
    @NotNull(message="{NotNull.User.password}")
    @Size(min = 4, max = 100, message="{Size.User.password}")
    private String password;
    
    @Column(name = "EMAIL", length = 50, unique = true)
    @NotNull(message="{NotNull.User.email}")
    @Size(min = 4, max = 50 ,message="{Size.User.email}")
    private String email;
    
    @Column(name = "PHONE_NUMBER", length = 15, unique = true)
    @Size(min = 4, max = 15, message="{Size.User.phoneNumber}")
    private String phoneNumber;
      

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USERS_AUTHORITIES",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USERID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")}) 
    private List<Authority> authorities;
    
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade= {CascadeType.ALL})
    private List<Event> events;
    
    public User() {}
    
    public User(Long userId) {
    	this.id = userId;
    }



}