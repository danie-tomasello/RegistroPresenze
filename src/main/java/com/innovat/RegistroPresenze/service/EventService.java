package com.innovat.RegistroPresenze.service;

import java.text.ParseException;
import java.util.List;

import com.innovat.RegistroPresenze.dto.DTOEvent;
import com.innovat.RegistroPresenze.dto.JwtUser;

public interface EventService {
	
	public void save(List<DTOEvent> requestBody, JwtUser userLogged);

	public boolean isExist(Long eventId);

	public void delete(Long eventId);

	public List<DTOEvent> getByDateRange(Long idUser, String startDate, String endDate) throws ParseException;

}
