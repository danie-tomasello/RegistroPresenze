package com.innovat.RegistroPresenze.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.innovat.RegistroPresenze.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	
	@Query("select e from Event e where e.user.id = ?1 and (e.input1 between ?2 AND ?3)")
	List<Event> findByUserId(Long userId, Date start, Date end);

}