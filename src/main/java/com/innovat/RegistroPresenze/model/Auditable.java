package com.innovat.RegistroPresenze.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable<U> implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4292484566886615232L;

	@CreatedDate
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;
    
    @Column(name = "last_modified_by")
    private U lastModifiedBy;
    
    public void setLastModifiedBy(U username) {
    	this.lastModifiedBy=username;
    }
    
}
