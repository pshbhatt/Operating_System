package osii;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * Description: LOADER class loads the input from the 
 * input file into the memory while separating the
 * components into trace flag, starting address, program
 * length and the main input program.
 * Accuracy Analysis: The LOADER class has
 * been created according to the requirement document.
 * There's no discrepancy between the requirements 
 * and the functionality.
 */
public class LOADER {
    int newLineNumber;
    int jobId;

    /**
     * Description: Load module is responsible for 
     * reading the test batch jobs line by line and
     * separating one job from other. It picks out
     * the program length, starting address, the 
     * trace flag and the actual instructions and
     * sends them to the memory.
     * Accuracy Analysis: This method is developed
     * according to the requirement documentation.
     * @throws IOException
     */

    public void loadModule(File tempFile, ArrayList<ProcessControlBlock> readyList,
	    ArrayList<ProcessControlBlock> blockedList,
	    int CLOCK, int totalCorrectJobs, int totalErrorJobs,
	    FileWriter fo, int tet, int tio, int idt, int itf,
	    int time, int errTime) throws IOException {
	String finalError = "";
	String finalWarning = "";
	ERROR_HANDLER error_handler = new ERROR_HANDLER();
	MEMORY memory = new MEMORY();
	Scanner scanner = new Scanner(tempFile);
	int number_data_lines;
	int number_output_lines;
	String prog_length;
	String instructions = "";
	String startAddress;
	String traceFlag;
	ArrayList<String> data = new ArrayList<String>();
	int count;
	int checkCt = 0;
	int oldLineNumber;
	SCHEDULER schd = new SCHEDULER();
	FileReader fileReader = new FileReader(tempFile);
	LineNumberReader bufferedReader = new LineNumberReader(fileReader);
	StringBuffer stringBuffer = new StringBuffer();
	String line;
	int endFlag = 0;
	int lineNumber = 0;
	ArrayList<ArrayList<ProcessControlBlock>> homeList 
	= new ArrayList<ArrayList<ProcessControlBlock>>();
	ArrayList<ProcessControlBlock> tempList
	= new ArrayList<ProcessControlBlock>();
	LinkedHashMap<Integer, Integer> lineMap
	= new LinkedHashMap<Integer, Integer>();
	while ((line = bufferedReader.readLine()) != null) {
	    endFlag = 0;
	    lineNumber = bufferedReader.getLineNumber();
	    if (line.trim().equals("")) {
		line = bufferedReader.readLine();
		lineNumber++;
	    }
	    oldLineNumber = lineNumber;
	    /**Fetching of information based on the recorded
	     * previous line number*/
	    if (lineNumber == newLineNumber) {
		if (!line.contains("** JOB")) {
		    finalError = finalError + "@" + error_handler.exceptionOccured(15);
		}
		    ProcessControlBlock pcb = new ProcessControlBlock();
		    if (line.contains("** JOB")) {
			endFlag = 1;
			jobId++;
			lineNumber++;
			pcb.setJobId(jobId);
			number_data_lines = Integer.parseInt
				(SYSTEM.hexDec(line.substring(7, 8)));
			pcb.setInput_line(number_data_lines);
			number_output_lines = Integer.parseInt
				(SYSTEM.hexDec(line.substring(9, 10)));
			pcb.setOutput_line(Integer.parseInt
				(SYSTEM.hexDec(String.valueOf(number_output_lines))));
			line = line.replace(String.valueOf(number_data_lines), "");
			line = line.replace(String.valueOf(number_output_lines), "");
			line = "";
			prog_length = bufferedReader.readLine();
			/**Validation of program length: Start*/
			if (prog_length.length() == 0) {
			    finalError = finalError +
				    "@" + error_handler.exceptionOccured(3);
			} else if (prog_length == null) {
			    finalError = finalError +
				    "@" + error_handler.exceptionOccured(3);
			}
			lineNumber++;
			prog_length = SYSTEM.hexDec(prog_length);
			if (prog_length.contains("$")) {
			    finalError = finalError +
				    "@" + error_handler.exceptionOccured(3);
			}
			if (prog_length.contains("**")) {
			    finalError = finalError + 
				    "@" + error_handler.exceptionOccured(16);
			}
			/**Validation of program length: End*/
			    while (!(line = bufferedReader.readLine()).contains("DATA")) {
				checkCt++;
				lineNumber++;
				stringBuffer.append(line);
				stringBuffer.append("\n");
				if (checkCt > 100) {
				    finalError = finalError + "@"
				+ error_handler.exceptionOccured(17);
				    break;
				}
			    }
				instructions = stringBuffer.toString();
				traceFlag = instructions.substring(instructions.length() - 2,
					instructions.length() - 1);
				if (traceFlag.equals("1")) {
				    instructions = instructions.replace(" 1", "");
				} else if (traceFlag.equals("0")) {
				    instructions = instructions.replace(" 0", "");
				} else {
				    instructions = instructions.replace(instructions
					    .substring(instructions.length()
						    - 2, instructions.length() - 1), "");
				}
				pcb.setTraceFlag(traceFlag);
				startAddress = instructions.substring(instructions.length() - 4,
					instructions.length() - 1);
				startAddress = startAddress.trim();
				pcb.setPC(Integer.parseInt(SYSTEM.hexDec(startAddress)));
				instructions = instructions.substring
					(0, instructions.lastIndexOf('\n') - 3);
				instructions = instructions.substring(0, instructions.length() - 1);
				for (int y = 0; y < number_data_lines; y++) {
				    data.add(bufferedReader.readLine());
				    lineNumber++;
				}
				lineNumber++;
				lineNumber++;
				memory.buildMemory(prog_length + " " +
				number_data_lines + " " + number_output_lines);
				instructions = instructions.replace("\n", "");
				/**Conversion of Hex data to binary format:Start*/
				String[] hexDigits = instructions.split("(?<=\\G.{1})");
				String binaryDigits = "";
				for (int i = 0; i < hexDigits.length; i++) {
				    String binary = SYSTEM.hexBin(hexDigits[i]);
				    if (binary.contains("$")) {
					finalError = finalError + "@"
				    + error_handler.exceptionOccured(1);
				    }

				    while (binary.length() < 4) {
					binary = "0" + binary;
				    }
				    binaryDigits = binaryDigits + binary;
				}
				/**Conversion of Hex data to Binary format: End*/
				String[] word = binaryDigits.split("(?<=\\G.{12})");
				for (int j = 0; j < word.length; j++) {
				    memory.buildMemory(word[j]);
				}
				memory.buildMemory(startAddress + " " + traceFlag);
				for (int t = 0; t < data.size(); t++) {
				    memory.buildMemory(data.get(t));
				}
				newLineNumber = lineNumber + 1;
				if(bufferedReader.readLine().equals("")||bufferedReader.readLine()==null){
				    finalError = finalError + "@" + error_handler.exceptionOccured(18);
				    newLineNumber = newLineNumber -1;
				}
				pcb.setError(finalError);
				homeList = memory.createMultipleMemory(pcb, jobId, schd, readyList);
				readyList = homeList.get(0);
				tempList = homeList.get(1);
				count = tempList.get(0).getTemp();
				if (count == 0) {
				    lineMap.put(jobId, oldLineNumber);
				    break;
				} else if(count == 2){
				    lineMap.put(jobId+1, newLineNumber);
				    break;
				}
				data.clear();
				stringBuffer.delete(0, stringBuffer.length());
		    }
	    }

	}
	if (newLineNumber > 1854) {
	    endFlag = 0;
	}
	fileReader.close();
	memory.memoryManager(finalError, finalWarning, lineMap,
		schd, tempFile, readyList, blockedList, CLOCK, endFlag,
		totalCorrectJobs, totalErrorJobs, fo, tet, tio,
		idt, itf, time, errTime);
    }
}
