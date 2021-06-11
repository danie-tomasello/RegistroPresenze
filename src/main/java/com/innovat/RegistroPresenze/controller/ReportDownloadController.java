package com.innovat.RegistroPresenze.controller;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.innovat.RegistroPresenze.dto.DTOEvent;
import com.innovat.RegistroPresenze.exception.NotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.java.Log;


@RestController
@RequestMapping(value = "${reportDownload.uri}")
@Api(value="search", tags="Controller operazioni di ricerca dati utenti")
@Log
public class ReportDownloadController {
	


	@ApiOperation(
		      value = "Download report in formato csv", 
		      notes = "Download report in formato csv",
		      produces = "text/csv")
	@ApiResponses(value =
	{   @ApiResponse(code = 201, message = "Il file è stato scaricato con successo"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
	@RequestMapping(value = "${csv.download}", method = RequestMethod.POST)
	public void doGetCsvDownload(@ApiParam("Dati richiesta download csv") @RequestBody List<DTOEvent> requestBody, HttpServletRequest request, HttpServletResponse response) throws NotFoundException {
		
		log.info("START DOWNLOAD REPORT CSV");
		//set file name and content type
        String filename = "report.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");
        
        List<DTOEvent> eventList= requestBody;

        try
        {
            OutputStream outputStream = response.getOutputStream();
            
            SimpleDateFormat formDate = new SimpleDateFormat("d/M/yyyy");
            SimpleDateFormat formTime = new SimpleDateFormat("HH:mm");
            Collections.sort(eventList, new Comparator<DTOEvent>() {
            	  public int compare(DTOEvent o1, DTOEvent o2) {
            	      if (o1.getData() == null || o2.getData() == null)
            	        return 0;
            	      try {
						return formDate.parse(o1.getData()).compareTo(formDate.parse(o2.getData()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return 0;
					}
            	  }
            	});
            String outputResult = "Codice;Username;Data;Ingresso1;Uscita1;Ingresso2;Uscita2;TOT\n";
            double Etot = 0;
            for(int i=0;i<eventList.size();i++) {
            	DTOEvent e = eventList.get(i);
            	double tot = tot(e,formTime);
            	Etot+=tot;
            	outputResult += e.getIdUser();
            	outputResult += ";";
            	outputResult += e.getUsername();
            	outputResult += ";";
            	outputResult += e.getData();
            	outputResult += ";";
            	outputResult += e.getInput1();
            	outputResult += ";";
            	outputResult += e.getOutput1();
            	outputResult += ";";
            	outputResult += e.getInput2();
            	outputResult += ";";
            	outputResult += e.getOutput2();
            	outputResult += ";";
            	outputResult += tot;
            	outputResult += "\n";
            }
            outputResult +="-;-;-;-;-;-;-;"+Etot+"\n";
            
            
            outputStream.write(outputResult.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}

	private double tot(DTOEvent e, SimpleDateFormat formatter) throws ParseException {
		// TODO Auto-generated method stub
		Date input1 = formatter.parse(e.getInput1());
		Date output1 = formatter.parse(e.getOutput1());
		Date input2 = formatter.parse(e.getInput2());
		Date output2 = formatter.parse(e.getOutput2());
		
		long  timediff1 = output1.getTime () - input1.getTime ();
		long  timediff2 = output2.getTime () - input2.getTime (); 
		long timediff = timediff1 + timediff2;
		
		
		return (double)((timediff/1000)/60)/60;
	}
}
