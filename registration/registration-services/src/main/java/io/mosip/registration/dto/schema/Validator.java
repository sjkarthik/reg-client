package io.mosip.registration.dto.schema;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Validator class will be having information of what type of validator it is,
 * and validator value, and required arguments also
 * 
 * @author YASWANTH S
 *
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Validator {

	/**
	 * Type of the validator
	 */
	private String type;
	/**
	 * Validator value
	 */
	private String validator;
	/**
	 * Arguments if required
	 */
	private List<String> arguments;
	
	/**
	 * Langcode to opt validator 
	 */
	private String langCode;
	
	/**
	 * errorCode to display specific validation 
	 */
	private String errorCode;
}
