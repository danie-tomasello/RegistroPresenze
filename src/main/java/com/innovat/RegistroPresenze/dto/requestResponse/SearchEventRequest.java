package com.innovat.RegistroPresenze.dto.requestResponse;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SearchEventRequest {

	
	private Long idUser;
	
	@NotNull(message="{NotNull.searchEventRequest.startDate}")
	@Size(min = 1, max = 50, message="{NotNull.searchEventRequest.startDate}")
	private String startDate;
	
	@NotNull(message="{NotNull.searchEventRequest.endDate}")
	@Size(min = 1, max = 50, message="{NotNull.searchEventRequest.endDate}")
	private String endDate;
}
