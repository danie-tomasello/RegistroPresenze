package com.innovat.RegistroPresenze.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.innovat.RegistroPresenze.dto.DTOEvent;
import com.innovat.RegistroPresenze.dto.JwtUser;
import com.innovat.RegistroPresenze.model.Event;

public class EventFactory {

	public static List<Event> makeEvent(List<DTOEvent> requestBody, JwtUser userLogged) {
		// TODO Auto-generated method stub
		SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy HH:mm");
			
		return requestBody.stream()
                .map(dtoevent -> {
					try {
						Event e = new Event(
								(dtoevent.getId().isEmpty()||dtoevent.getId()==null)?null:Long.valueOf(dtoevent.getId()),
								userLogged.getId(),
								formatter.parse(dtoevent.getData()+" "+dtoevent.getInput1()),
								formatter.parse(dtoevent.getData()+" "+dtoevent.getOutput1()),
								formatter.parse(dtoevent.getData()+" "+dtoevent.getInput2()),
								formatter.parse(dtoevent.getData()+" "+dtoevent.getOutput2())
								);
						e.setLastModifiedBy(userLogged.getUsername());
						return e;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
						
					}
				})
                .collect(Collectors.toList());
		
	}

	public static DTOEvent makeDtoEvent(Event e) {
		// TODO Auto-generated method stub
		
		return new DTOEvent(
					String.valueOf(e.getId()),
					String.valueOf(e.getUser().getId()),
					e.getUser().getUsername(),
					new DateTime(e.getInput1()).toString("d/M/yyyy"),
					new DateTime(e.getInput1()).toString("HH:mm"),
					new DateTime(e.getOutput1()).toString("HH:mm"),
					new DateTime(e.getInput2()).toString("HH:mm"),
					new DateTime(e.getOutput2()).toString("HH:mm")
				);
	}
	
}
	
	


