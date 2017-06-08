package osii;

/**
 * Description: ERROR_HANDLER class is to captures the errors which can occur due to multiple reasons.
 * Accuracy Analysis: This class has been created according to the requirement document. 
 *
 */
public class ERROR_HANDLER {
	/**
	 * Description: This method takes the error code as an argument and provides the 
	 * appropriate error message.
	 * Accuracy Analysis: This method has been created according to the requirement document.
	 * @param error_code
	 * @return
	 */
	public String exceptionOccured(int error_code){
		String error = null;
		if(error_code == 1){
			error = "Invalid hex digits in input";
		} else if(error_code == 2){
			error = "Invalid input.";
		} else if(error_code == 3){
			error = "Invalid loader format: Invalid Program Length";
		} else if(error_code == 4){
			error = "Bad trace flag";
		} else if(error_code == 5){
			error = "Invalid hexadecimal character in Program Length";
		} else if(error_code == 6){
			error = "Invalid hexadecimal character in Starting address";
		} else if(error_code == 7){
			error = "Invalid input.";
		} else if(error_code == 8){
			error = "Infinite loop suspected.";
		} else if(error_code == 9){
			error = "Program Size too large as compared to the program length";
		} else if(error_code == 10){
			error = "The address is out of range.";
		} else if(error_code == 11){
			error = "Memory overflow error.";
		} else if(error_code == 12){
			error = "Input output instruction error. Read, write and halt function"
			    + " are being executed at the same time.";
		} else if(error_code == 13){
			error = "Multiple bits set for type 3 instructions.";
		} else if(error_code == 14){
			error = "Data lines exceed then required";
		} else if(error_code == 15){
		    error = "** JOB record missing";
		} else if(error_code==16){
		    error = "Loader format job missing";
		} else if(error_code==17){
		    error = "** DATA part missing";
		} else if(error_code==18){
		    error = "** END part missing";
		}
		return error;
	}
	
}
