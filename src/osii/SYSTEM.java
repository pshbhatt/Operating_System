package osii;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Piyush Bhatt
 * CS-5323
 * Project Title: Multiprogramming batch operating system.
 * Date: 05/01/2017
 * Description: SYSTEM class is the main class which
 * controls the entire batch processing system.
 * SYSTEM is responsible for choosing the input
 * to the program and calling LOADER to process the
 * input. It contains certain external functions which
 * are used throughout the application.
 * It also fetches the user input if any to execute the application.
 * Accuracy Analysis: The SYSTEM class has been created according
 * the requirement specifications.
 * There is no discrepancy between the requirements and the
 * design of the SYSTEM class.
 *   
 */
public class SYSTEM {
	
	/**
	 * Description: This is the main function which calls all the
	 * other classes. This is the starting point of the entire
	 * program.
	 * Accuracy Analysis: The main method has been created
	 * according the requirement specifications.
	 * There is not much scope to move away from the requirement.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		LOADER loader = new LOADER();
		String fileName = args[0];
		int CLOCK = 0;
		int count = 0;
		ArrayList<ProcessControlBlock> readyList =
			new ArrayList<ProcessControlBlock>();
		    ArrayList<ProcessControlBlock> blockedList =
			    new ArrayList<ProcessControlBlock>();
		File file = new File(fileName);
		int et = 0;
		loader.newLineNumber = 1;
		FileWriter finalOutput = new FileWriter("progress_file");
		int counts = 0;
		int totEx = 0;
		int totIo = 0;
		int idt =0;
		int itf = 0;
		int time = 0;
		loader.loadModule(file, readyList, blockedList,
			CLOCK, counts, count, finalOutput,
			totEx, totIo, idt, itf, time, et);
	}
	
	/**
	 * This method is used to convert numbers from Hexadecimal
	 * format to Binary format.
	 * Accuracy Analysis: This method has been created according
	 * the requirement specifications.
	 * @param hexDigit
	 * @return
	 */
	public static String hexBin(String hexDigit) {
		try {
			return new BigInteger(hexDigit, 16).toString(2);
			} catch (Exception e) {
			return "$";
		}
	}
	/**
	 * This method fetches the input from the user. 
	 * It is called from CPU during a read instruction.
	 * Accuracy Analysis: The method is a conventional 
	 * way to fetch the value from the user.
	 * @return
	 */
	public static String fetchUserValue(){
		Scanner reader = new Scanner(System.in);  
		String n = reader.next();
		n = n.trim();
		if(n.equals(null)||n.equals("")){
			return "$";
		}
		return n;
	}
	
	/**
	 * This method is used to convert numbers from Decimal
	 * format to HexaDecimal format.
	 * Accuracy Analysis: This method uses a straightforward
	 * method to convert decimal
	 * to hexadecimal. This has no relation to the requirements.
	 * @param decimal
	 * @return
	 */
	public static String decHex(String decimal) {
		return new BigInteger(decimal, 10).toString(16);
	}

	/**
	 * This method is used to convert numbers from Binary 
	 * format to Hexadecimal format
	 * Accuracy Analysis: This method uses a straightforward
	 * method to convert binary to hexadecimal.
	 * There's no relation to the requirements.
	 * @param opCode
	 * @return
	 */
	public static String binHex(String opCode) {
		try {
			return new BigInteger(opCode, 2).toString(16);
		} catch (Exception e) {
			return "$";
		}

	}
	
	/**
	 * This method is used to convert numbers from 
	 * Hexadecimal format to Decimal format
	 * Accuracy Analysis: This method uses a straightforward
	 * method to convert hexadecimal to decimal.
	 * There's no relation to the requirements.
	 * @param opCode
	 * @return
	 */
	public static String hexDec(String hexadecimal) {
		try {
		     return String.valueOf(Integer.parseInt(hexadecimal, 16));
		} catch (Exception e) {
			return "$";
		}

	}
}