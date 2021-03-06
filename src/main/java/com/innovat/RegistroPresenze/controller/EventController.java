package com.innovat.RegistroPresenze.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.innovat.RegistroPresenze.dto.DTOEvent;
import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.dto.requestResponse.MessageResponse;
import com.innovat.RegistroPresenze.dto.requestResponse.SearchEventRequest;
import com.innovat.RegistroPresenze.exception.BindingException;
import com.innovat.RegistroPresenze.exception.DateFormatException;
import com.innovat.RegistroPresenze.exception.DuplicateException;
import com.innovat.RegistroPresenze.exception.NotFoundException;
import com.innovat.RegistroPresenze.service.EventService;
import com.innovat.RegistroPresenze.utility.JwtTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.java.Log;

@RestController
@RequestMapping(value = "${gestioneEventi.uri}")
@Api(value="eventService", tags="Controller operazioni di gestione timbrature")
@Log
public class EventController {
	
	@Autowired
    private EventService eventService;
	
//	@Autowired
//    private TypeService typeService;
	
	@Autowired
	private ResourceBundleMessageSource msg;
	
	@Value("${jwt.header}")
    private String tokenHeader;
	
	@Autowired
    private JwtTokenUtil jwtTokenUtil;
	
	
    
    
	@ApiOperation(
		      value = "Aggiungi timbrature", 
		      notes = "Salvataggio di una o pi?? tibrature nel db",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "La timbrature ?? stato creata con successo"),
	    @ApiResponse(code = 400, message = "Questo utente esiste gi??"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	@RequestMapping(value = "${event.save}", method = RequestMethod.POST)
	public ResponseEntity<?> saveEvent(@ApiParam("Dati richiesta salvataggio timbratura") @RequestBody List<DTOEvent> requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws  BindingException, DuplicateException {
		log.info("===========================Start event/eventService/save/=="+requestBody.toString()+"=============================");
		
		JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));		
		MessageResponse res = new MessageResponse();
		//Input validation
		if(bindingResult.hasErrors()) {
			String error = msg.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());			
			log.warning(error);
			throw new BindingException(error);
		}
		
		log.info("start salvataggio timbrature");

		eventService.save(requestBody,userlogged);
		
		
		res.setCod(HttpStatus.CREATED.value());
		res.setMsg(msg.getMessage("event.success.save", null, LocaleContextHolder.getLocale()));
    	
		return ResponseEntity.ok(res);
	}
	
	@ApiOperation(
		      value = "Elimina timbratura", 
		      notes = "Elimina le informazioni di una timbratura",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "La timbratura ?? stato eliminato con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @RequestMapping(value = "${event.delete}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEvent(@ApiParam("Id timbratura univoco") @PathVariable(value="eventId") Long eventId, HttpServletRequest request, HttpServletResponse response) throws NotFoundException {

		log.info("===========================Start event/service/delete/=="+eventId+"=============================");
		MessageResponse res = new MessageResponse();
		if(!eventService.isExist(eventId)) {
			String errMsg = msg.getMessage("exc.notFound.event", null, LocaleContextHolder.getLocale());
			log.warning(errMsg);
			throw new NotFoundException(errMsg);
		}
		
		eventService.delete(eventId);  
		res.setCod(HttpStatus.OK.value());
		res.setMsg(msg.getMessage("event.success.delete", null, LocaleContextHolder.getLocale()));
		return ResponseEntity.ok(res);
    }
	
	
	@ApiOperation(
		      value = "Cerca timbrature", 
		      notes = "Cerca timbrature",
		      response = MessageResponse.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "Ricerca effettuata con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	@RequestMapping(value = "${event.search}", method = RequestMethod.POST)
	public ResponseEntity<?> searchEvent(@ApiParam("Richiesta ricerca timbrature") @RequestBody SearchEventRequest requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws NotFoundException, DateFormatException {
		
		log.info("===========================Start event/search/=="+requestBody.toString()+"=============================");
	  	
	  	List<DTOEvent> eventList = new ArrayList<>();
		try {
			eventList = eventService.getByDateRange(requestBody.getIdUser(),requestBody.getStartDate(),requestBody.getEndDate());
		} catch (ParseException e) {
			String errMsg = msg.getMessage("exc.dateFormat", null, LocaleContextHolder.getLocale());
	  		log.warning(errMsg);
			throw new DateFormatException();
		}
	  	
	  	
	  	if(eventList.isEmpty()) {
	  		String errMsg = msg.getMessage("exc.notFound.search.event", null, LocaleContextHolder.getLocale());
	  		log.warning(errMsg);
	  		throw new NotFoundException(errMsg);
	  	}
	  	
	  	
	  	
	  	return ResponseEntity.ok(eventList);
	}
	
//	@ApiOperation(
//		      value = "Aggiungi tipo evento", 
//		      notes = "Aggiunge uno o pi?? tipologie di timbratura",
//		      response = MessageResponse.class, 
//		      produces = "application/json")
//	@ApiResponses(value =
//	{   @ApiResponse(code = 201, message = "Le tipologie di timbri sono stati creati con successo"),
//	    @ApiResponse(code = 400, message = "Questo tipo esiste gi??"),
//	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
//	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
//	})
//	@RequestMapping(value = "${type.save}", method = RequestMethod.POST)
//	public ResponseEntity<?> saveType(@ApiParam("Dati richiesta salvataggio timbratura") @RequestBody List<Type> requestBody,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws  BindingException, DuplicateException {
//		log.info("===========================Start event/service/type/save=="+requestBody.toString()+"=============================");
//		
//		JwtUser userlogged = jwtTokenUtil.getUserDetails(request.getHeader(tokenHeader));		
//		MessageResponse res = new MessageResponse();
//		//Input validation
//		if(bindingResult.hasErrors()) {
//			String error = msg.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());			
//			log.warning(error);
//			throw new BindingException(error);
//		}
//		
//		log.info("start salvataggio timbrature");
//
//		typeService.save(requestBody,userlogged);
//		
//		
//		res.setCod(HttpStatus.CREATED.value());
//		res.setMsg(msg.getMessage("type.success.save", null, LocaleContextHolder.getLocale()));
//  	
//		return ResponseEntity.ok(res);
//	}
	
//	@ApiOperation(
//		      value = "Elimina tipologia timbratura", 
//		      notes = "Elimina una tipologia di timbratura",
//		      response = MessageResponse.class, 
//		      produces = "application/json")
//	@ApiResponses(value =
//	{   @ApiResponse(code = 201, message = "La tipologia di timbratura ?? stata eliminata con successo"),
//	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
//	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
//	})
//	@RequestMapping(value = "${type.delete}", method = RequestMethod.DELETE)
//	public ResponseEntity<?> deleteType(@ApiParam("Id tipo timbratura univoco") @PathVariable(value="typeId") Long typeId, HttpServletRequest request, HttpServletResponse response) throws NotFoundException {
//		
//		log.info("===========================Start event/service/type/delete/=="+typeId+"=============================");
//		MessageResponse res = new MessageResponse();
//		if(!typeService.isExist(typeId)) {
//			String errMsg = msg.getMessage("exc.notFound.type", null, LocaleContextHolder.getLocale());
//			log.warning(errMsg);
//			throw new NotFoundException(errMsg);
//		}
//		
//		typeService.delete(typeId);  
//		res.setCod(HttpStatus.OK.value());
//		res.setMsg(msg.getMessage("type.success.delete", null, LocaleContextHolder.getLocale()));
//		return ResponseEntity.ok(res);
//	}
	
//	@ApiOperation(
//		      value = "Get type", 
//		      notes = "Restituisce tutti i tipi di timbratura",
//		      response = MessageResponse.class, 
//		      produces = "application/json")
//	@ApiResponses(value =
//	{   @ApiResponse(code = 201, message = "Richiesta effettuata con successo"),
//	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
//	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
//	})
//	@RequestMapping(value = "${type.get}", method = RequestMethod.GET)
//	public ResponseEntity<?> getAllType(HttpServletRequest request, HttpServletResponse response) throws NotFoundException {
//		
//		log.info("===========================Start event/search/type/=============================");
//		
//		List<Type> resList = typeService.getAll();
//		
//		if(resList.isEmpty()) {
//	  		String errMsg = msg.getMessage("exc.notFound.search.type", null, LocaleContextHolder.getLocale());
//	  		log.warning(errMsg);
//	  		throw new NotFoundException(errMsg);
//	  	}
//	  	
//	  	return ResponseEntity.ok(resList);
//	}
	
	
}