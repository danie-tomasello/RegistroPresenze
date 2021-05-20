package com.innovat.RegistroPresenze.model;
import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;




@Entity
@Table(name = "USERS")
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
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
    @NotNull(message="{NotNull.User.username.Validation}")
    @Size(min = 4, max = 50, message="{Size.User.username.Validation}")
    private String username;

    @Column(name = "PASSWORD", length = 100)
    @NotNull(message="{NotNull.User.password.Validation}")
    @Size(min = 4, max = 100, message="{Size.User.password.Validation}")
    private String password;

    @Column(name = "ENABLED")
    @NotNull
    private Boolean enabled;
    
    @Column(name = "EMAIL", length = 50, unique = true)
    @NotNull(message="{NotNull.User.email.Validation}")
    @Size(min = 4, max = 50 ,message="{Size.User.email.Validation}")
    private String email;
    
    @Column(name = "PHONE_NUMBER", length = 15, unique = true)
    @NotNull(message="{NotNull.User.phoneNumber.Validation}")
    @Size(min = 4, max = 15, message="{Size.User.phoneNumber.Validation}")
    private String phoneNumber;
      

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USERS_AUTHORITIES",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "USERID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    @ToString.Exclude 
    private List<Authority> authorities;



}