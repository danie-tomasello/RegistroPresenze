package com.innovat.RegistroPresenze.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "EVENT")
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class Event extends Auditable<String> implements Serializable{
	   /**
		 * 
		 */
		private static final long serialVersionUID = -3518018508482697232L;
		
		
		@Id
	    @Column(name = "EVENTID",length = 50, unique = true)
	    @GeneratedValue
		private Long id;
		
		@Column(name = "INPUT1")
		private Date input1;
		
		@Column(name = "OUTPUT1")
		private Date output1;
		
		@Column(name = "INPUT2")
		private Date input2;
		
		@Column(name = "OUTPUT2")
		private Date output2;
		
		@ManyToOne(fetch=FetchType.EAGER)
		@JoinColumn(name="USER_ID",nullable=false)
		@JsonBackReference
		@ToString.Exclude 
	    private User user;

		
		@ManyToOne(fetch=FetchType.EAGER,cascade= {CascadeType.ALL})
		@JoinColumn(name="PERMISSION_ID",nullable=true)
		@JsonBackReference
		@ToString.Exclude
	    private Permission permission;

		public Event() {}

		public Event(Long id,Long userId, Date input1, Date output1,Date input2, Date output2) {
			super();
			this.id = id;
			this.user = new User(userId);
			this.input1 = input1;
			this.output1 = output1;
			this.input2 = input2;
			this.output2 = output2;
		}
		
		
		
		
		
}
