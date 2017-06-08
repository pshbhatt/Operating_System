package osii;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**Description: CPU class is responsible for executing the instructions from the memory.
 * It takes input and provides output. It is a generic class which can execute a variety
 * of programs.
 * Accuracy Analysis: This class has been developed according to the requirement documents.
 */
public class CPU {
	int R2;
	int A;
	int EA;
	String R0;
	String terminationType;
	HashMap<Integer, String> ioMap = new HashMap<Integer, String>(); 
	String finalErrors;
	String finalWarnings="";
	String R1;
	long R3;
	Long R4 = (long)0;
	Long R5=(long) 0;
	String R6;
	String R7;
	String R8;
	String R9;
	int writeCount = 0;
	String output;
	int exTime;
	int ioTime;
	/**
	 * Description: cpuModule loads the instruction from the
	 * memory. Each instruction is split and the write opcode
	 * is identified. The operation is performed according to
	 * the opcode. A trace file is generated based on the
	 * trace flag. A CLOCK is used which records the virtual
	 * time units of execution. An output file is generated as well. 
	 * Accuracy Analysis: This method has been created according
	 * to the requirement document.
	 * @param progLength
	 * @param traceFlag
	 * @param startAddress
	 * @param memory
	 * @param finalError
	 * @param finalWarning
	 * @throws IOException
	 */
  public String cpuModule(ProcessControlBlock pcb,
      String finalError, String finalWarning, HashMap<Integer, Integer> lineMap, int CLOCK) throws IOException {
      ArrayList<String> newMemory = new ArrayList<String>();	
      	int newClock = 0;
      	String finJobId = String.valueOf(pcb.getJobId());
      	int errorFlag = 1;
      	String infinite = "Jobs:";
      	ERROR_HANDLER error_handler = new ERROR_HANDLER();
      	int time = 0;
      	int countInst = 0;
      	int memorySize = pcb.getMemorySize();
	for(int h=1;h<pcb.getJob().size();h++){
		    
	    if(pcb.getJob().get(h).length()!=5||pcb.getJob().get(h).length()!=4){
			countInst++;
			newMemory.add(pcb.getJob().get(h));
		    } else {
			break;
		    }
		}
	/**Filling the memory*/
	while(newMemory.size()<memorySize){
	    newMemory.add("000000000000");
	}
	if(memorySize==0){
	    while(newMemory.size()<32){
		    newMemory.add("000000000000");
		}
	    while(newMemory.size()>32&&newMemory.size()<64){
		    newMemory.add("000000000000");
		}
	    while(newMemory.size()>64&&newMemory.size()<128){
		    newMemory.add("000000000000");
		}
	}
		String blocked = "";
		if(pcb.getR4()!=null){
		    R4 = pcb.getR4();
		}
		if(pcb.getR5()!=null){
		    R5 = pcb.getR5();
		}
		SCHEDULER schd = new SCHEDULER();
      		String startAddress = String.valueOf(pcb.getPC());
      		String startAddressDec = SYSTEM.hexDec(startAddress);
      		String traceFlag = pcb.getTraceFlag();
      		String error = "";
      		if(pcb.getJob().size()==0){
      		error = error_handler.exceptionOccured(16);
		finalError = finalError + "@" + error;
		pcb.setError(finalError);
		errorFlag = 9;
      		} else {
		String line1 = pcb.getJob().get(0);
      		int programCounter = 0;
		
		int data_line = 0;
		/** Extracting the number of data lines: Start*/
		if(line1.substring(3,4).trim().length()!=0){
		    data_line = Integer.parseInt(SYSTEM.hexDec(line1.substring(3,4)));
		}
		/** Extracting the number of data lines: End*/
		int output_line = 0;
		if(line1.substring(5,6).trim().length()!=0){
		output_line = Integer.parseInt(SYSTEM.hexDec(line1.substring(5,6)));
		}
		/**Creating an array of data used in the application*/
		ArrayList<String> data = new ArrayList<String>();
		if(pcb.getData()==null){
		    for(int d = 0;d<data_line;d++){
			    data.add(pcb.getJob().get(pcb.getJob().size()-d-1));
			}
		    Collections.reverse(data);
		    pcb.setData(data);
		}
		    for(int i=1;i<pcb.getActualJob().size();i++){
			if(pcb.getActualJob().get(i).length()>5){
			String write = pcb.getJob().get(i).substring(0,7);
			if(write.contains("011001")||write.contains("011011")){
			    writeCount++;
			}
			}
		    }
		    /**Verifying whether the number of data lines are correct
		     * or more than required*/
		    if(writeCount<=data_line){
			error =  error_handler.exceptionOccured(14);
			finalWarning = finalWarning + "@" + error;
		    }
		    
		int progLengthInt = Integer.parseInt(line1.substring(0, 2));
		int unchanged = progLengthInt;
		progLengthInt = progLengthInt + 2 + data_line + output_line;
		String progLengthDec = String.valueOf(progLengthInt);
		String progLength = 0+SYSTEM.decHex(String.valueOf(progLengthDec));
		String[] memory = null;
		if(pcb.getCheckMemory()==1){
		    memory = pcb.getMemory();
		} else {
		memory = newMemory.toArray(new String[newMemory.size()]);
		}
		/**Checking for errors in program length of the input: Start*/
		if(progLength.trim().equals(null)||progLength.trim().equals("")){
			error = error_handler.exceptionOccured(3);
			finalError = finalError + "@" + error;
			pcb.setError(finalError);
			errorFlag = 9;
		}
		if(!progLength.matches("-?[0-9a-fA-F]+")){
			error = error_handler.exceptionOccured(3);
			finalWarning = finalWarning + "@" + error; 
		} else {
			progLengthDec = SYSTEM.hexDec(progLength);
		}
		/**Checking for errors in program length of the input: End*/
		/**Checking for errors in trace flag of the input: Start*/
		if(traceFlag==null){
		    traceFlag = "0";
		}
		if(!traceFlag.equals("0")&&!traceFlag.equals("1")) {
			error = error_handler.exceptionOccured(4);
			finalWarning = finalWarning + "@" + error; 
		}
		/**Checking for errors in trace flag of the input: End*/
		/**Checking for errors in start address of the input: Start*/
		if(startAddress.trim().equals(null)||startAddress.trim().equals("")){
			error = error_handler.exceptionOccured(3);
			finalError = finalError + "@" + error;
			pcb.setError(finalError);
			errorFlag = 9;
		}
		if(!startAddress.matches("-?[0-9a-fA-F]+")){
			error = error_handler.exceptionOccured(6);
			finalError = finalError + "@" + error;
			pcb.setError(finalError);
			errorFlag = 9;
		}
		/**Checking for errors in start address of the input: End*/
		int eaCondition = 0;
		FileWriter traceWriter = null;
		if(pcb.getTraceWriter()==null){
		 traceWriter = new FileWriter("trace_file_job_" + pcb.getJobId());
		 pcb.setTraceWriter(traceWriter);
		 pcb.setCheckTrace(5);
		} else {
		    pcb.setCheckTrace(0);
		}
		if(pcb.getCheckTrace()==5){
		    if(traceFlag.equals("1")){
			      pcb.getTraceWriter().write(
			          String.format("%-10s %-10s  %-10s  %-15s  %-15s \r\n", "|PC|", "|Instr|", " |R&EA|",
			              "|R&EA Before Exe|", "|R&EA After Exe|"));
					}
		}
		    if(traceFlag.equals("0")){
			    File file = new File("trace_file_job_" + pcb.getJobId());
			    file.delete();
			}
		int R4BE = 0;
		String regis;
		if(finalError.length()<1){
		startAddress = startAddress.trim();
		String[] hexDigits = String.valueOf(startAddress).split("(?<=\\G.{1})"); 
		String binaryDigits = "";
		/** Conversion of start address from hexadecimal to decimal: Start */
		for(int j=0;j<hexDigits.length;j++){
			String binary = SYSTEM.hexBin(hexDigits[j]);
			if(binary.equals("$")){
				finalError = finalError +"@"+ error_handler.exceptionOccured(1);
				pcb.setError(finalError);
				errorFlag = 9;
			}
			while (binary.length() < 4) {
				binary = "0" + binary;
			}
			binaryDigits = binaryDigits+binary;
		}
		}
		R2 = pcb.getPC();
		
		/** Conversion of start address from hexadecimal to decimal: End */
		int counter = 0;
		/** Execution of instructions: Start */
		for(int i=R2;i<memory.length;i++){
		   if(pcb.getHaltBit()==1){
		       break;
		   }
		   if(pcb.getError()!=null){
		   if(pcb.getError().length()>1){
		       break;
		   }
		   }
		    newClock = CLOCK;
			eaCondition = 0;
			int eaBefore = EA;
			programCounter = programCounter + 1;
			i = R2;
			counter++;
			String R3 = memory[i];
			R2 = R2 + 1;
			pcb.setPC(R2);
			String arithematicReg = "";
			if(String.valueOf(R3).length()>5){
			    arithematicReg = R3.substring(4,5);
			} else {
			    finalError = finalError + "@" + error_handler.exceptionOccured(10);
			    pcb.setError(finalError);
				break;
			}
			if(arithematicReg.equals("0")){
				regis = "R5";
				R4BE = R5.intValue();
	 		} else {
	 		 regis = "R4";
	 		R4BE = R4.intValue();
	 		}
			if(!isBinaryNumber(Long.parseLong(R3))){
			    errorFlag = 9;
			    break;
			}
			String opCode = R3.substring(1,4);
			int eaError = 0;
			/** Execution of instructions based on the operation code: Start */
			if(opCode.equals("000")){
			    CLOCK ++;
			    newClock++;
			    time++;
				eaError = this.calcAnd(R3, memory);
				eaCondition = 1;
			} else if(opCode.equals("001")){
			    CLOCK ++;
			    time++;
			    newClock++;
				eaError = this.calcAdd(R3, memory, pcb);
				eaCondition = 1;
			} else if(opCode.equals("010")){
			    CLOCK ++;
			    time++;
			    newClock++;
				eaError = this.calcStore(R3, memory, pcb);
				eaCondition = 1;
			} else if(opCode.equals("011")){
			    CLOCK ++;
			    time++;
			    newClock++;
				eaError = this.calcLoad(R3, memory, pcb);
				eaCondition = 1;
			} else if(opCode.equals("100")){
			    CLOCK ++;
			    time++;
			    newClock++;
				eaError = this.calcJump(R3, memory);
				eaCondition = 1;
			} else if(opCode.equals("101")){
			    CLOCK++;
			    time++;
			    newClock++;
				eaError = this.calcJPL(R3, memory);
				eaCondition = 1;
			} else if(opCode.equals("110")){
			    CLOCK  = CLOCK + 11;
			    newClock = newClock + 1;
			    time++;
				String errorBit = this.calcIO(R3, pcb, startAddressDec);
				String[] errorA = errorBit.split("!");
				errorBit = errorA[0];
				String inputError = errorA[1]; 
				blocked = errorA[2];
				if(blocked.equals("B")||blocked.equals("BW")||blocked.equals("BW1")){
				    pcb.setCheckMemory(1);
				    pcb.setMemory(memory);
				    pcb.setPC(R2);
				    break;
				}
				if(errorBit.equals("0")){
					finalError = finalError + "@" + error_handler.exceptionOccured(12);
					pcb.setError(finalError);
					errorFlag = 9;
					break;
				}
				if(inputError.equals("9")){
					finalError = finalError  + "@" + error_handler.exceptionOccured(2);
					pcb.setError(finalError);
					errorFlag = 9;
					break;
				}
				String haltBit = R3.substring(7,8);
				if(haltBit.equals("1")){
				    blocked = "F";
				    pcb.setHaltBit(1);
					break;
				}
			} else if(opCode.equals("111")){
			    CLOCK ++;
			    time++;
			    newClock++;
				int errorSign = this.calcType34(R3);
				if(errorSign == 5){
					finalError = finalError + "@" + error_handler.exceptionOccured(13);
					pcb.setError(finalError);
					errorFlag = 9;
					break;
				}
			} else{
			}
			/** Execution of instructions based on the operation code: End */
			/** Checking for Invalid input: Start */
			if(eaError == 7){
				finalError = finalError + "@" + error_handler.exceptionOccured(10);
				pcb.setError(finalError);
				errorFlag = 9;
				break;
			}
			/** Checking for Invalid input: End */
			int eaAfter = EA;
			if(!(blocked.equals("B"))||!(blocked.equals("BW"))||!(blocked.equals("BW1"))){
			/** Conversion of values to binary to enter them to Trace File: Start*/
			if(traceFlag.equals("1")){
				String decPC = Integer.toBinaryString(R2);
				while (decPC.length() < 6) {
					decPC = "0" + decPC;
				}
				String decR = Integer.toBinaryString(R4BE);
				String rightR = null;
				if(decR.length()>6){
					rightR = decR.substring(decR.length()-6, decR.length());
					while (rightR.length() < 6) {
						rightR = "0" + rightR;
					}
				} else {
					while (decR.length() < 6) {
						decR = "0" + decR;
					}
					rightR = decR;
				}
				String decR5 = Integer.toBinaryString(R5.intValue());
				String rightR5 = null;
				if(decR5.length()>6){
					rightR5 = decR5.substring(decR5.length()-6, decR5.length());
					while (rightR5.length() < 6) {
						rightR5 = "0" + rightR5;
					}
				} else {
					while (decR5.length() < 6) {
						decR5 = "0" + decR5;
					}
					rightR5 = decR5;
				}
				String decR4 = Integer.toBinaryString(R4.intValue());
				String rightR4 = null;
				if(decR4.length()>6){
					rightR4 = decR4.substring(decR4.length()-6, decR4.length());
				} else {
					while (decR4.length() < 6) {
						decR4 = "0" + decR4;
					}
					rightR4 = decR4;
				}
				String EAStr = Integer.toBinaryString(EA);
				while (EAStr.length() < 6) {
					EAStr = "0" + EAStr;
				}
				String eaB = Integer.toBinaryString(eaBefore);
				while (eaB.length() < 6) {
					eaB = "0" + eaB;
				}
				String eaA = Integer.toBinaryString(eaAfter);
				while (eaA.length() < 6) {
					eaA = "0" + eaA;
				}
				/** Conversion of values to binary to enter them to Trace File: End*/
				/** Writing information to trace file: Start */
				
			if(arithematicReg.equals("0")){
				if(eaCondition!=0){
				    pcb.getTraceWriter().write
				    (String.format("%-10s %-10s %-13s %-18s %-15s \r\n",
                  "|" + SYSTEM.binHex(decPC) + "(HEX)|", "|" +
				    SYSTEM.binHex(R3) + "(HEX)|", "|" + regis + " " +
                  SYSTEM.binHex(EAStr) + "(HEX)|",
                  "|" + SYSTEM.binHex(rightR) + "(HEX)" + SYSTEM.binHex(eaB) +
                  "(HEX)|", "|" + SYSTEM.binHex(rightR5) + "(HEX)" + SYSTEM.binHex(eaA) + "(HEX)|"));
				} else {
				    pcb.getTraceWriter().write
				    (String.format("%-10s %-10s %-13s %-18s %-15s \r\n",
                  "|" + SYSTEM.binHex(decPC) + "(HEX)|", "|" + SYSTEM.binHex(R3) +
                  "(HEX)|", "|" + regis + " " + "----" + "|",
                  "|" + SYSTEM.binHex(rightR) + "(HEX)" + "----" + "|", "|" +
                  SYSTEM.binHex(rightR5) + "(HEX)" + "----" + "|"));
				}
	 		} else {
	 			if(eaCondition!=0){
	 			   pcb.getTraceWriter().write(String.format
	 				   ("%-10s %-10s %-13s %-18s %-15s \r\n",
                  "|" + SYSTEM.binHex(decPC) + "(HEX)|", "|" + 
	 				   SYSTEM.binHex(R3) + "(HEX)|", "|" + regis + " " +
                  SYSTEM.binHex(EAStr) + "(HEX)|",
                  "|" + SYSTEM.binHex(rightR) + "(HEX)" +
                  SYSTEM.binHex(eaB) + "(HEX)|", "|" + SYSTEM.binHex(rightR4) +
                  "(HEX)" + SYSTEM.binHex(eaA) + "(HEX)|"));
				} else {
				    pcb.getTraceWriter().write
				    (String.format("%-10s %-10s %-13s %-18s %-15s \r\n",
                  "|" + SYSTEM.binHex(decPC) + "(HEX)|", "|" + SYSTEM.binHex(R3)
                  + "(HEX)|", "|" + regis + " " + "----" + "|",
                  "|" + SYSTEM.binHex(rightR) + "(HEX)" + "----" + "|", "|" +
                  SYSTEM.binHex(rightR4) + "(HEX)" + "----" + "|"));
				}
	 		}
			}
			/** Writing information to trace file: End */
			/** Checking for infinite loop  */
			
			if(counter>1000000||time>20000){
			    infinite = infinite + "," + pcb.getJobId();
				finalError = finalError + "@" + error_handler.exceptionOccured(8);
				pcb.setError(finalError);
				errorFlag = 9;
				break;
			}
		}
		/** Execution of instructions: End */
		}
		if(blocked.equals("B")){
		    pcb.setMemory(memory);
		    pcb.setCheckMemory(1);
		    pcb.setTimeLeaving(CLOCK);
		    pcb.setEntryTime(newClock);
		    if(pcb.getData()!=null&&pcb.getData().size()!=0){
			pcb.getData().remove(0);
			pcb.setData(pcb.getData());
			}
		}
		if(blocked.equals("BW")||blocked.equals("BW1")){
		    pcb.setPC(R2);
		}
		pcb.setR4(R4);
		pcb.setR5(R5);
		String[] errorArray = null;
		String[] warningArray = null;
		if(finalError.length()==0){
			terminationType = "Normal";
			} else {
			    errorFlag = 0;
				terminationType = "Abnormal";
				finalErrors = finalError;
				errorArray = finalErrors.split("@");
				dumpMemory(memory);
			}
		
		if(finalWarning.length()!=0||!finalWarning.equals("")){
				finalWarnings = finalWarning;
				warningArray = finalWarnings.split("@");
			}
		/**Writing data to the output file: Start*/ 
		pcb.setDump(dumpMemory(memory));
		pcb.setOutput(output);
		pcb.setEntryTime(newClock);
		pcb.setFinalWarnings(finalWarnings);
		pcb.setWarningArray(warningArray);
		pcb.setTerminationType(terminationType);
		pcb.setFinalError(finalError);
		pcb.setErrorArray(errorArray);
		pcb.setTimeLeaving(CLOCK);
		pcb.setExecutionTime(exTime);
		pcb.setIoTime(ioTime);
		}
		/**Writing data to the output file: Start*/
		//traceWriter.close();
		pcb.setExecutionTime(newClock);
		return blocked + "!" + CLOCK + "!"+ time + "!" + exTime + "!" 
		+ ioTime + "!" + errorFlag + "!" + infinite + "!"+finJobId;
	}
		
	/**
	 * Description: This method performs AND opcode
	 * operation. It takes the instruction and based
	 * on the value of Arithmetic register decides
	 * whether R4 or R5 register would be used.
	 * Accuracy Analysis: This method has been 
	 * created according to the requirement document.
	 * @param instruction
	 * @param memory
	 */
  public int calcAnd(String instruction, String[] memory) {
      int eaError = 0;
      String finalResult = "";
      exTime = exTime + 1;
      String indirectBit = instruction.substring(0, 1);
      String arithematicReg = instruction.substring(4, 5);
      String indexBit = instruction.substring(5, 6);
      String address = instruction.substring(6, instruction.length());
      EA = this.effectiveAddress(R2, address, indirectBit, indexBit, arithematicReg, memory);
      if (EA < 0) {
          eaError = 7;
      } else {
          /** Actual Logical 'And' implementation. If R5 is 101 and EA is 100,
           * the output would be 100: Start */
          if (arithematicReg.equals("0")) {
              if(isBinaryNumber(Long.parseLong(memory[EA])) && memory[EA].length()>3){
          	R5 = ((long)(Integer.parseInt((memory[EA]),2)&(R5.intValue())));
                  } else {
                      R5 = ((long)Long.parseLong(memory[EA])&R5.intValue());
                  }
          } else {
              if(isBinaryNumber(Long.parseLong(memory[EA])) && memory[EA].length()>3){
          	R4 = ((long)(Integer.parseInt((memory[EA]),2)&(R4.intValue())));
                  } else {
                      R4 = ((long)Long.parseLong(memory[EA])&R4.intValue());
                  }
          }
      }
      /** Actual Logical 'And' implementation: End */
      return eaError;
  }
	
	/**
	 * Description: This method performs ADD opcode operation. 
	 * It takes the instruction and based on the value
	 * of Arithmetic register decides whether R4 or R5
	 * register would be used.
	 * Accuracy Analysis: This method has been created 
	 * according to the requirement document.
	 * @param instruction
	 * @param memory
	 */
  public int calcAdd(String instruction, String[] memory, ProcessControlBlock pcb) {
      int eaError = 0;
      exTime = exTime + 1;
      String indirectBit = instruction.substring(0, 1);
      String arithematicReg = instruction.substring(4, 5);
      String indexBit = instruction.substring(5, 6);
      String address = instruction.substring(6, instruction.length());
      EA = this.effectiveAddress(R2, address, indirectBit, indexBit, arithematicReg, memory);
      
      if (EA < 0) {
          eaError = 7;
      } else {
	  try{
          long memoryValue;
          if (this.isBinaryNumber(Long.parseLong(memory[EA]))&&memory[EA].length()>3) {
              memoryValue = getTwosComplement(memory[EA]);
          } else {
              String binary = Integer.toBinaryString(Integer.parseInt(memory[EA]));
              while (binary.length() < 12) {
                  binary = "0" + binary;
              }
              memoryValue = getTwosComplement(binary);
          }
          if (arithematicReg.equals("0")) {
              R5 = R5 + memoryValue;
          } else {
              if(isBinaryNumber(R4)&&String.valueOf(R4).length()>3){
          	R4 = (long)getTwosComplement(String.valueOf(R4));
          	R4 = R4 + memoryValue;
              } else {
              R4 = R4 + memoryValue;
              }
          }
	  } catch (NumberFormatException e){
	      eaError = 7;
	  }
          
      }
      return eaError;
  }
	/**
	 * Description: This method checks whether the number is in binary form.
	 * Accuracy Analysis: This was not a part of the requirement.
	 * @param binary
	 */
	public boolean isBinaryNumber(long binary){
        boolean status = true;
        while(true){
            if(binary == 0){
                break;
            } else {
                long tmp = binary%10;
                if(tmp > 1|| tmp < 0){
                    status = false;
                    break;
                }
                binary = binary/10;
            }
        }
        return status;
    }
	/**
	 * Description: This method performs STORE opcode operation. It takes the
	 * instruction and based on the value of Arithmetic register
	 * decides which register (R4 or R5's value would be stored
	 * in the memory location).
	 * Accuracy Analysis: This method has been created according
	 * to the requirement document.
	 * @param instruction
	 * @param memory
	 */
	public int calcStore(String instruction, String[] memory, ProcessControlBlock pcb){
		int eaError = 0;
		exTime = exTime + 1;
		String indirectBit = instruction.substring(0,1);
		String arithematicReg = instruction.substring(4,5);
		String indexBit = instruction.substring(5,6);
		String address = instruction.substring(6,instruction.length());
		EA = this.effectiveAddress(R2,address,indirectBit, indexBit,
			arithematicReg, memory);
		if(EA<0||EA>91){
			eaError = 7;
		} else {
		if(arithematicReg.equals("0")){
			memory[EA] = String.valueOf(R5);
 		} else {
 			memory[EA] = String.valueOf(R4);
 		}
		}
		
		return eaError;
	}
	/**
	 * Description: This method performs LOAD opcode operation. 
	 * It takes the instruction and based on the value of Arithmetic register
	 * decided which register (R4 or R5) would be loaded with the content from 
	 * the memory location.
	 * Accuracy Analysis: This method has been created according
	 * to the requirement document.
	 * @param instruction
	 * @param memory
	 */
	public int calcLoad(String instruction, String[] memory, ProcessControlBlock pcb){
	    int eaError = 0;
            exTime = exTime + 1;
            String indirectBit = instruction.substring(0, 1);
            String arithematicReg = instruction.substring(4, 5);
            String indexBit = instruction.substring(5, 6);
            String address = instruction.substring(6, instruction.length());
            EA = this.effectiveAddress(R2, address, indirectBit, indexBit, arithematicReg, memory);
            if (EA < 0) {
                eaError = 7;
            } else {
                if (arithematicReg.equals("0")) {
                    if(isBinaryNumber(Long.parseLong(memory[EA]))&&memory[EA].length()>3){
                	R5 = (long)Integer.parseInt(memory[EA],2);
                    } else {
                    R5 = Long.parseLong(memory[EA]);
                    }
                    
                } else {
                    if(isBinaryNumber(Long.parseLong(memory[EA]))&&memory[EA].length()>3){
                	R4 = (long)Integer.parseInt(memory[EA],2);
                    } else {
                    R4 = Long.parseLong(memory[EA]);
                    }
                }
                
            }
            return eaError;
	}
	/**
	 * Description: This method performs JUMP opcode operation.
	 * It places the value of Effectice address to the program counter.
	 * Accuracy Analysis: This method has been created according to 
	 * the requirement document.
	 * @param instruction
	 * @param memory
	 */
	public int calcJump(String instruction, String[] memory){
		int eaError = 0;
		exTime = exTime + 1;
		String indirectBit = instruction.substring(0,1);
		String arithematicReg = instruction.substring(4,5);
		String indexBit = instruction.substring(5,6);
		String address = instruction.substring(6,instruction.length());
		EA = this.effectiveAddress(R2,address,indirectBit, indexBit,
			arithematicReg, memory);
		if(EA<0){
			eaError = 7;
		} else {
		R2 = EA;
		}
		return eaError;
	}
	/**
	 * Description: This method performs Jump and
	 * Link opcode operation.
	 * It takes the instruction and based on the
	 * value of Arithmetic register places the value
	 * from the memory location to the register(R4 or R5).
	 * Accuracy Analysis: This method has been created
	 * according to the requirement document.
	 * @param instruction
	 * @param memory
	 */
	public int calcJPL(String instruction, String[] memory){
		int eaError = 0;
		exTime = exTime + 1;
		String indirectBit = instruction.substring(0,1);
		String arithematicReg = instruction.substring(4,5);
		String indexBit = instruction.substring(5,6);
		String address = instruction.substring(6,instruction.length());
		EA = this.effectiveAddress(R2,address,indirectBit, indexBit,
			arithematicReg, memory);
		if(EA<0){
			eaError = 7;
		} else {
		if(arithematicReg.equals("0")){
			R5 = (long)R2;
 		} else {
 			R4 = (long)R2;
 		}
		R2 = EA;
		}
		return eaError;
		
	}
	/**
	 * Description: This method performs 
	 * Input/Output opcode operations.
	 * Based on the value, it can either
	 * read a value, write the output 
	 * or halt the program.
	 * Accuracy Analysis: This method has been
	 * created according to the requirement document.
	 * @param instruction
	 */
	public String calcIO(String instruction, ProcessControlBlock pcb, String startAddress){
	    ProcessControlBlock blockedPcb = new ProcessControlBlock();
	        ERROR_HANDLER error = new ERROR_HANDLER();
		SCHEDULER schd = new SCHEDULER();
		String arithematicReg = instruction.substring(4,5);
		String readBit = instruction.substring(5,6);
		String errorBit = "1";
		String blocked = "N";
		String inputError = "0";
		String writeBit = instruction.substring(6,7);
		String haltBit = instruction.substring(7,8);
		if(readBit.equals("1")&&writeBit.equals("1")&&haltBit.equals("1")){
			errorBit = "0";
		}
		if (!errorBit.equals("0")) {
		/** Read operation: Start */
			if(readBit.equals("1")){
			    pcb.setReadCounter(pcb.getReadCounter()+1);
			exTime = exTime + 1;
			ioTime = ioTime + 10;
			if(pcb.getJobId()==68){
			    pcb.getData().remove(0);
			    pcb.setData(pcb.getData());
			}
			if(pcb.getData().size()!=0){
			String result = pcb.getData().get(0);
			 result = SYSTEM.hexDec(result);
			blockedPcb.setJobId(pcb.getJobId());
			blockedPcb.setPC(R2);
			blockedPcb.setCheckMemory(1);
			pcb.setCheckMemory(1);
			pcb.setPC(R2);
			pcb.setIoRequest("Read");
			blocked = "B";
		if(result.equals("$")){
			inputError = "9";
		} else {
		    
		if(arithematicReg.equals("0")){
			R5 = Long.parseLong(result);
			pcb.setR5(R5);
 		} else {
 			R4 = Long.parseLong(result);
 			pcb.setR4(R4);
 		}
		    }
			}
		/** Read operation: End */
		/** Write operation: Start */
		}else if(writeBit.equals("1")){
		    pcb.setTotalOutput("");
		    pcb.setIoRequest("Write");
			if(arithematicReg.equals("0")){
				exTime = exTime + 1;
				ioTime = ioTime + 10;
				if(isBinaryNumber(R5)&&R5.toString().length()>3){
					 output = String.valueOf(getTwosComplement(R5.toString()));
					 output = SYSTEM.decHex(output);
					} else {
					output = SYSTEM.decHex(String.valueOf(R5));
					}
				while(output.length()<3){
					output = "0" + output;
				}
				if(pcb.getJobId()==16){
				}
				if(pcb.getOutput_line()!=0){
				 pcb.setTotalOutput(pcb.getTotalOutput()+ output+",");
				 pcb.setOutput_line(pcb.getOutput_line()-1);
				 blocked = "BW1";
				} else if(pcb.getOutput_line()==0){
				blocked = "BW";
				}
				pcb.setPC(R2);
	 		} else {
	 			exTime = exTime + 1;
				ioTime = ioTime + 10;
				if(isBinaryNumber(R4)&&R4.toString().length()>3){
				 output = String.valueOf(getTwosComplement(R4.toString()));
				 output = SYSTEM.decHex(output);
				} else {
				output = SYSTEM.decHex(String.valueOf(R4));
				}
				while(output.length()<3){
					output = "0" + output;
				}
				
				if(pcb.getOutput_line()!=0){
				         pcb.setTotalOutput(output+ ",");
					 pcb.setOutput_line(pcb.getOutput_line()-1);
					 blocked = "BW1";
					} else if(pcb.getOutput_line()==0){
					blocked = "BW";
					}
				pcb.setPC(R2);
	 		}
			/** Write operation: End */
			
		} 
		}
		return errorBit +"!"+ inputError+ "!"+ blocked;
		
	}
	/**
	 * Description: This method performs Type 3
	 * and Type 4 opcode operation. Based on the
	 * value of certain bits in the instruction,
	 * it decides which operation of type 3 or
	 * type 4 instruction to execute.
	 * Accuracy Analysis: This method has been
	 * created according to the requirement document.
	 * @param instruction
	 */
	public int calcType34(String instruction){
				int errorSign= 0;
				String bit0 = instruction.substring(0,1);
				String arithematicReg = instruction.substring(4,5);
				ArrayList<String>checkList = new ArrayList<String>();
				if(bit0.equals("0")){
					errorSign = type3Instructions(instruction, errorSign, arithematicReg,
						checkList);
				} else {
					type4Instructions(instruction, arithematicReg);
				}
				return errorSign;
	}

	/**
	 * This method has been extracted from a 
	 * larger method to maintain modularity.
	 * @param instruction
	 * @param arithematicReg
	 */
	private void type4Instructions(String instruction, String arithematicReg) {
	    String equalBit = instruction.substring(5,6);
	    String lessBit = instruction.substring(6,7);
	    String greaterBit = instruction.substring(7,8);
	    /** No Skip operation: Start */
	    if(equalBit.equals("0") && lessBit.equals("0") && greaterBit.equals("0")){
	    	exTime = exTime + 1;
	    	/** No Skip operation: End */
	    	/** Greater than 0 comparing operation: Start */
	    } else if(equalBit.equals("0") && lessBit.equals("0") && greaterBit.equals("1")){
	    	exTime = exTime + 1;
	    	if(arithematicReg.equals("0")){
	    		if(R5 > 0){
	    			R2 = R2 + 1;
	    		}
	    	} else {
	    		if(R4 > 0){
	    			R2 = R2 + 1;
	    		}
	    	}
	    	/** Greater than 0 comparing operation: End */
	    	/** Less than 0 comparing operation: Start */
	    } else if(equalBit.equals("0") && lessBit.equals("1") && greaterBit.equals("0")){
	        exTime = exTime + 1;
	        
	        if (arithematicReg.equals("0")) {
	    	String strR5 = Integer.toBinaryString(R5.intValue());
	    	while(strR5.length()<12){
	    	    strR5 = "0" + strR5;
	    	}
	    	R5 = (long)getTwosComplement(strR5);
	    	if (R5 < 0) {
	                R2 = R2 + 1;
	            }
	        } else {
	    	String strR4 = Integer.toBinaryString(R4.intValue());
	    	while(strR4.length()<12){
	    	    strR4 = "0" + strR4;
	    	}
	    	R4 = (long)getTwosComplement(strR4);
	            
	            if (R4 < 0) {
	                R2 = R2 + 1;
	            }
	        }
	    
	    	/** Less than 0 comparing operation: End */
	    	/** Not equal to 0 comparing operation: Start */
	    } else if(equalBit.equals("0") && lessBit.equals("1") && greaterBit.equals("1")){
	    	exTime = exTime + 1;
	    	if(arithematicReg.equals("0")){
	    		if(R5 != 0){
	    			R2 = R2 + 1;
	    			
	    		}
	    	} else {
	    		if(R4 != 0){
	    			R2 = R2 + 1;
	    		}
	    	}
	    	/** Not equal to 0 comparing operation: End */
	    	/** Equal to 0 comparing operation: Start */
	    } else if(equalBit.equals("1") && lessBit.equals("0") && greaterBit.equals("0")){
	    	exTime = exTime + 1;
	    	if(arithematicReg.equals("0")){
	    		if(R5 == 0){
	    			R2 = R2 + 1;
	    		}
	    	} else {
	    		if(R4 == 0){
	    			R2 = R2 + 1;
	    		}
	    	}
	    	/** Equal to 0 comparing operation: End */
	    	/** Greater than or Equal to 0 comparing operation: Start */
	    } else if(equalBit.equals("1") && lessBit.equals("0") && greaterBit.equals("1")){
	    	exTime = exTime + 1;
	    	if(arithematicReg.equals("0")){
	    		if(R5 >= 0){
	    			R2 = R2 + 1;
	    		}
	    	} else {
	    		if(R4 >= 0){
	    			R2 = R2 + 1;
	    		}
	    	}
	    	/** Greater than or Equal to 0 comparing operation: End */
	    	/** Less than or Equal to 0 comparing operation: Start */
	    } else if(equalBit.equals("1") && lessBit.equals("1") && greaterBit.equals("0")){
	    	exTime = exTime + 1;
	    	if(arithematicReg.equals("0")){
	    		if(R5 <= 0){
	    			R2 = R2 + 1;
	    		}
	    	} else {
	    		if(R4 <= 0){
	    			R2 = R2 + 1;
	    		}
	    	}
	    	/** Less than or Equal to 0 comparing operation: End */
	    	/** Unconditional skip: Start */
	    } else if(equalBit.equals("1") && lessBit.equals("1") && greaterBit.equals("1")){
	    	exTime = exTime + 1;
	    	R2 = R2 + 1;
	    	/** Unconditional skip: End */
	    } else {
	    }
	}

	/**
	 * This method has been extracted from a larger method to
	 * maintain modularity.
	 * @param instruction
	 * @param errorSign
	 * @param arithematicReg
	 * @param checkList
	 * @return
	 */
	private int type3Instructions(String instruction, int errorSign, String arithematicReg,
		ArrayList<String> checkList) {
	    String clearBit = instruction.substring(5,6);
	    checkList.add(clearBit);
	    String incrBit = instruction.substring(6,7);
	    checkList.add(incrBit);
	    String complementBit = instruction.substring(7,8);
	    checkList.add(complementBit);
	    String swapBit = instruction.substring(8,9);
	    checkList.add(swapBit);
	    String rotateLeftBit = instruction.substring(9,10);
	    checkList.add(rotateLeftBit);
	    String rotateRightBit = instruction.substring(10,11);
	    checkList.add(rotateRightBit);
	    String shiftMagnitude = instruction.substring(11,12);
	    int checkVal = 0;
	    for(int j=0;j<checkList.size();j++){
	    	if(checkList.get(j).equals("1")){
	    		checkVal++;
	    	}
	    }
	    if(checkVal==1){
	    	/** Clear the value of the register to 0 operation: Start */
	    if(clearBit.equals("1")) {
	    	exTime = exTime + 1;
	    	if(arithematicReg.equals("0")){
	    		R5 = (long) 0;
	    	} else {
	    		R4 = (long) 0;
	    	}
	    	/** Clear the value of the register to 0 operation: End */
	    	/** Increment the value of the register by 1 operation: Start */
	    } else if(incrBit.equals("1")){
	    	exTime = exTime + 1;
	    	if(arithematicReg.equals("0")){
	    	    if(isBinaryNumber(R5)&&R5.toString().length()>3){
	            	R5 = (long)getTwosComplement(R5.toString());
	            	R5 = R5+1;
	                } else {
	                R5 = R5 + 1;
	                }
	    	} else {
	    	   if(isBinaryNumber(R4)&&R4.toString().length()>3){
	            	R4 = (long)getTwosComplement(R4.toString());
	            	R4 = R4+1;
	                } else {
	                R4 = R4 + 1;
	                }
	    	}
	    	/** Increment the value of the register by 1 operation: End */
	    	/** Conversion the value of the register to it's complement: Start */
	    } else if(complementBit.equals("1")){
	    	exTime = exTime + 1;
	    	if (arithematicReg.equals("0")) {
	                String binR5= Integer.toBinaryString(R5.intValue());
	                while (binR5.length()<12){
	            	binR5 = "0"+binR5;
	                }
	                R5 = Long.parseLong(invertDigits(binR5));
	            } else {
	                String binR4= Integer.toBinaryString(R4.intValue());
	                while (binR4.length()<12){
	            	binR4 = "0"+binR4;
	                }
	                R4 = Long.parseLong(invertDigits(binR4));
	            }
	    	/** Conversion the value of the register to it's complement: End */
	    	/** Byte swap operation: Start */
	    } else if(swapBit.equals("1")){
	    	exTime = exTime + 1;
	    	if(arithematicReg.equals("0")){
	    		String binaryR5 = Integer.toBinaryString(R5.intValue());
	    		char[] cArray = binaryR5.toCharArray();
	    		for(int i=0;i<(cArray.length)/2;i++){
	    			char temp = cArray[i];
	    			char last = cArray[(cArray.length)-(i+1)];
	    			cArray[(cArray.length)-(i+1)] = temp;
	    			cArray[i] = last;
	    		}
	    		String newR5 = new String(cArray);
	    		R5 = Long.parseLong(newR5);
	    	} else {
	    		String binaryR4 = Integer.toBinaryString(R4.intValue());
	    		char[] cArray = binaryR4.toCharArray();
	    		for(int i=0;i<(cArray.length)/2;i++){
	    			char temp = cArray[i];
	    			char last = cArray[(cArray.length)-(i+1)];
	    			cArray[(cArray.length)-(i+1)] = temp;
	    			cArray[i] = last;
	    		}
	    		String newR4 = new String(cArray);
	    		R5 = Long.parseLong(newR4);
	    	}
	    	/** Byte swap operation: End */
	    	/** Rotate to left by 1 or 2 bits : Start */
	    }  else if(rotateLeftBit.equals("1")){
	    	exTime = exTime + 1;
	    	if(shiftMagnitude.equals("0")){
	    	if(arithematicReg.equals("0")){
	    		R5 = (long)Integer.rotateLeft(R5.intValue(), 1);
	    		} else {
	    		R4 = (long)Integer.rotateLeft((R4.intValue()), 1);
	    		}
	    	}  else {
	    	   if(arithematicReg.equals("0")){
	    		R5 = (long)Integer.rotateLeft(R5.intValue(), 2);
	    		} else {
	    		R4 = (long)Integer.rotateLeft((R4.intValue()), 2);
	    		} 
	    	}
	    	/** Rotate to left by 1 or 2 bits : End */
	    	/** Rotate to right by 1 or 2 bits : Start */
	    } else if(rotateRightBit.equals("1")){
	        exTime = exTime + 1;
	    	if(shiftMagnitude.equals("0")){
	    	if(arithematicReg.equals("0")){
	    		R5 = (long)Integer.rotateRight((R5.intValue()), 1);
	    		} else {
	    		R4 = (long)Integer.rotateRight((R4.intValue()), 1);
	    		}
	    	}  else {
	    	   if(arithematicReg.equals("0")){
	    		R5 = (long)Integer.rotateRight((R5.intValue()), 2);
	    		} else {
	    		R4 = (long)Integer.rotateRight((R4.intValue()), 2);
	    		} 
	    	}
	    }
	    /** Rotate to right by 1 or 2 bits : End */
	    } else {
	    	errorSign = 5;
	    }
	    return errorSign;
	}
	/**
	 * Description: This method calculates the effective address by
	 * adding the program counter and the address of the instruction.
	 * Accuracy Analysis: This method has been created according to 
	 * the requirement document.
	 * @param R2
	 * @param address
	 * @param indirectBit
	 * @param indexBit
	 * @param arithematicReg
	 * @param memory
	 * @return
	 */
	public int effectiveAddress(int R2, String address,String indirectBit, String indexBit, String arithematicReg, String[] memory){
		int addressValue = getTwosComplement(address);
		
		A = R2 + addressValue;
		if(indirectBit.equals("1")&& indexBit.equals("1")){
				EA = Integer.parseInt(memory[A]) + R4.intValue();
		} else if(indirectBit.equals("1") && indexBit.equals("0")){
			
			EA = Integer.parseInt((memory[A]));
		} else if(indirectBit.equals("0") && indexBit.equals("1")){
				EA = A + R4.intValue();
		} else if(indirectBit.equals("0") && indexBit.equals("0")){
			EA = A;
		} else {
		}
		return EA;
	}
	/**
	 * Description: This method calculates the two's complement
	 * for adding 2 binary numbers by converting them into
	 * decimal format.
	 * Accuracy Analysis: This method is not a part of the
	 * requirement document.
	 * @param binaryInt
	 * @return
	 */
	public static int getTwosComplement(String binaryInt) {
	    if (binaryInt.charAt(0) == '1') {
	        String invertedInt = invertDigits(binaryInt);
	        int decimalValue = Integer.parseInt(invertedInt, 2);
	        decimalValue = (decimalValue + 1) * -1;
	        return decimalValue;
	    } else {
	        return Integer.parseInt(binaryInt, 2);
	    }
	}

	/**
	 * Description: This method is a part of 
	 * getTwosComplement method which inverts 
	 * the digits to convert the binary to decimal format.
	 * Accuracy Analysis: This method is not a part
	 * of the requirement document.
	 * @param binaryInt
	 * @return
	 */
	public static String invertDigits(String binaryInt) {
	    String result = binaryInt;
	    result = result.replace("0", " "); 
	    result = result.replace("1", "0"); 
	    result = result.replace(" ", "1"); 
	    return result;
	}
	/**
	 * Description: This method dumps the contents of the
	 * memory if an error occurs. It dumps 1/16th of the
	 * total memory.
	 * Accuracy Analysis: This method is created as provided
	 * in the requirement documentation.
	 * @param memory
	 * @return
	 */
	public static String dumpMemory(String[] memory){
	    String dump = "";
	    for(int i=0;i<memory.length;i++){
		    String iString = String.valueOf(i);
			while(iString.length()<4){
				iString  = "0" + iString;
			}
			while(memory[i].length()<12){
			    memory[i] = "0" + memory[i];
			}
			while(memory[i+1].length()<12){
			    memory[i+1] = "0" + memory[i+1];
			}
			while(memory[i+2].length()<12){
			    memory[i+2] = "0" + memory[i+2];
			}
			while(memory[i+3].length()<12){
			    memory[i+3] = "0" + memory[i+3];
			}
			while(memory[i+4].length()<12){
			    memory[i+4] = "0" + memory[i+4];
			}
			while(memory[i+5].length()<12){
			    memory[i+5] = "0" + memory[i+5];
			}
			while(memory[i+6].length()<12){
			    memory[i+6] = "0" + memory[i+6];
			}
			while(memory[i+7].length()<12){
			    memory[i+7] = "0" + memory[i+7];
			}
			dump = dump + iString + "  " + memory[i] + "  " + memory[i+1] + "  " + memory[i+2] + "  " + memory[i+3] + "  " + memory[i+4] + "  " + memory[i+5] + "  " + memory[i+6] + "  " + memory[i+7] + "\n";
			i = i+7;
		}
		return dump;
	}
}