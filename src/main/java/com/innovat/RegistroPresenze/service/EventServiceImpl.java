package com.innovat.RegistroPresenze.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.innovat.RegistroPresenze.dto.DTOEvent;
import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.model.Event;
import com.innovat.RegistroPresenze.repository.EventRepository;
import com.innovat.RegistroPresenze.utility.EventFactory;

import lombok.extern.java.Log;



@Service
@Log
public class EventServiceImpl implements EventService {
	
	
	@Autowired
	private EventRepository repo;
	
	
	@Override
	@CacheEvict(cacheNames = "event", allEntries = true)
	public void save(List<DTOEvent> requestBody, JwtUser userLogged) {
		List<Event> event = EventFactory.makeEvent(requestBody, userLogged);
		repo.saveAll(event);
		
	}


	@Override
	public boolean isExist(Long eventId) {
		return repo.existsById(eventId);
	}


	@Override
	@CacheEvict(cacheNames = "event", allEntries = true)
	public void delete(Long eventId) {
		// TODO Auto-generated method stub
		repo.deleteById(eventId);
	}


	@Override
	@Cacheable(value="event")
	public List<DTOEvent> getByDateRange(Long idUser, String startDate, String endDate) throws ParseException {
		// TODO Auto-generated method stub
		log.info("getByDateRange richiesta non cachata");
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date start = formatter.parse(startDate+" 00:00:01");
		Date end = formatter.parse(endDate+" 23:59:59");
		
		List<DTOEvent> res = new ArrayList<>();
		List<Event> list = repo.findByUserId(idUser,start,end);
		
		for(Event e : list) {
			res.add(EventFactory.makeDtoEvent(e));
		}
		return res;
	}

}
