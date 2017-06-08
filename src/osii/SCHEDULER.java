package osii;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: SCHEDULER class is responsible for
 * scheduling jobs from the memory. It maintains
 * the ready list and the blocked list and moves
 * the jobs in a round robin fashion.
 * Accuracy Analysis: This class has been developed
 * according to the requirement documents.
 * 
 */
public class SCHEDULER {
    int totalTime = 0;

    /**
     * Description: executeCPU method provides the
     * jobs to CPU to execute them. This method
     * creates a ready list and a blocked list.
     * The CPU executes an I/O operation and the
     * job is moved to the blocked queue. The job
     * waits in the blocked queue for the time
     * duration of the I/O operation and then is
     * moved back to the ready queue and it resumes
     * it's operation in the CPU.
     * After the job is terminated, it is removed from the memory.
     * Accuracy Analysis: The method has been created according
     * to the requirements.
     * @param finalError
     * @param finalWarning
     * @param lineMap
     * @param tempFile
     * @param readyList
     * @param blockedList
     * @param CLOCK
     * @param fo
     * @param endFlag
     * @throws IOException
     */
    public void executeCPU(String finalError, String finalWarning,
	    LinkedHashMap<Integer, Integer> lineMap,
	    File tempFile, ArrayList<ProcessControlBlock> readyList,
	    ArrayList<ProcessControlBlock> blockedList,
	    int CLOCK, int endFlag, int totalCorrectJobs,
	    int totalErrorJobs, FileWriter finalOutput, int totalExTime,
	    int totalIoTime, int idleTime, int totalInternalFrag,
	    int time, int errTime) throws IOException {
	int exTime = 0;
	int ioTime = 0;
	String[] infiniteJobs;
	int infiJobs = 0;
	String infinite = "";
	String errorFlag = "";
	for (int i = 0; i < readyList.size(); i++) {
	    String blocked = "";
	    if(readyList.get(i).getError()==null||readyList.get(i).getError().length()==0){
	    totalInternalFrag = totalInternalFrag + readyList.get(i).getInternalFragmentation();
	    CPU cpu = new CPU();
	     blocked = cpu.cpuModule(readyList.get(i), finalError,
		     finalWarning, lineMap, CLOCK);
	    String[] returnArray = blocked.split("!");
	    blocked = returnArray[0];
	    CLOCK = Integer.parseInt(returnArray[1]);
	    if(readyList.get(i).getError()!=null){
	    if(readyList.get(i).getError().length()>0){
		errTime = errTime + Integer.parseInt(returnArray[2]);
	    }
	    }
	    time = Integer.parseInt(returnArray[2]);
	    exTime = Integer.parseInt(returnArray[3]);
	    ioTime = Integer.parseInt(returnArray[4]);
	    if(readyList.get(i).getExecutionTime() == 0){
	    readyList.get(i).setExecutionTime(0 + exTime);
	    } else {
		readyList.get(i).setExecutionTime(readyList.get(i).
			getExecutionTime() + ioTime);
	    }
	    if(readyList.get(i).getIoTime() == 0){
		    readyList.get(i).setIoTime(0 + exTime);
	    } else {
			readyList.get(i).setIoTime(readyList.get(i).getIoTime() + ioTime);
	    }
		
		totalExTime = totalExTime + exTime;
		infinite = returnArray[6];
		if (infinite.length() > 5) {
		    infiniteJobs = infinite.split(",");
		    infiJobs = infiniteJobs.length - 1;
		}
		totalIoTime = totalIoTime + ioTime + 22;
		errorFlag = returnArray[5];
	    }
	    if(readyList.get(i).getError()!=null){
	    if (readyList.get(i).getError().length()==0) {
	    if(readyList.get(i).getErrorCheck()==0){
	    totalCorrectJobs++;
	    readyList.get(i).setErrorCheck(1);
	    }
	    } else if(readyList.get(i).getError().trim().length()!=0){
		if(readyList.get(i).getErrorCatch()==0){
	    totalErrorJobs++;
	    readyList.get(i).setErrorCatch(1);
		}
	    }
	    }
	    ProcessControlBlock blockedPcb = new ProcessControlBlock();
	    storePcb(readyList, finalOutput, i, blockedPcb);
	    finalOutput.write("For Job Id: " + SYSTEM.decHex
		    (String.valueOf(readyList.get(i).getJobId()))
		    + "(HEX): Current Degree of Multiprogramming: " +
		    (readyList.size() + blockedList.size()) + "(DEC)\n");
	    finalOutput.write("For Job Id: " + SYSTEM.decHex(String.valueOf
		    (readyList.get(i).getJobId()))
		    + "(HEX): Internal Fragmentation: " + readyList.get(i).
		    getInternalFragmentation());
	    if (readyList != null) {
		if (readyList.get(i).getFinalWarnings() != null) {
		    finalOutput.write("\nWarnings for Job Id "
			    + SYSTEM.decHex(String.valueOf(readyList.get(i).getJobId())) + ": ");
		    if (readyList.get(i).getWarningArray() != null) {
			for (int s = 0; s < readyList.get(i).getWarningArray().length; s++) {
			    finalOutput.write(readyList.get(i).getWarningArray()[s] + " ");
			}
		    }
		}
	    }
	    if(readyList.get(i).getError()!=null){
	    if (readyList.get(i).getError().length()!= 0) {
		String[] errorArray = readyList.get(i).getError().split("@");
		finalOutput.write("\nErrors for Job Id " + readyList.get(i).getJobId() + ": ");
		for (int h = 0; h < errorArray.length; h++) {
		    finalOutput.write(errorArray[h] + " ");
		}
		finalOutput.write("\nDump memory: " + readyList.get(i).getDump());
	    }
	    }
	    if(readyList.get(i).getError()==null||readyList.get(i).
		    getError().length()==0){
	    boolean val = setValuesBpcb(readyList, blockedList, 
		    errorFlag, i, blockedPcb);
	    if(val == true&&!blocked.equals("F")){
		readyList.remove(i);
		i = i-1;
	    }
	    }
	    totalTime = totalTime + time;
	    for (int j = 0; j < blockedList.size(); j++) {
		    if (time >= 10) {
			blockedList.get(j).setIoTime(blockedList.get(j).
				getIoTime() - 10);
			time = time - 10;
		    } else {
			blockedList.get(j).setIoTime(blockedList.get(j).
				getIoTime() - time);
		    }
		    if (blockedList.get(j).getIoTime() <= 0) {
			if (!errorFlag.equals("9")) {
			    readyList.add(blockedList.get(j));
			}
			blockedList.remove(j);
			j = -1;
		    }
		}
	    
	    if (blocked.equals("F")) {
		writeToFile(readyList, finalOutput, exTime, ioTime, i);
		readyList.get(i).getTraceWriter().close();
	    }
	    if (blocked.equals("F")) {
		for (int y = 0; y < blockedList.size(); y++) {
		    if (blockedList.get(y).getJobId() == readyList.get(i).getJobId()) {
			blockedList.remove(y);
			y = y - 1;
		    }

		}
		int id = readyList.get(i).getJobId();
		for (int y = 0; y < readyList.size(); y++) {
		    if (readyList.get(y).getJobId() == id) {
			readyList.remove(y);
			y = y - 1;
		    }

		}
		i = i - 1;
	    }
	    try{
	    if(readyList.get(i).getError()!=null){
	    if(readyList.get(i).getError().length()!=0){
		readyList.remove(i);
		finalError = "";
		finalWarning = "";
		i = i-1;
	    } 
	    }
	    } catch(ArrayIndexOutOfBoundsException e){
	    }
	    if(readyList.size()==0){
		for (int j = 0; j < blockedList.size(); j++) {
		blockedList.get(j).setIoTime(blockedList.get(j).getIoTime() - 1);
		    if (blockedList.get(j).getIoTime() <= 0) {
			    readyList.add(blockedList.get(j));
			blockedList.remove(j);
			j = -1;
		    }
		    
		    if(j==blockedList.size()-1&&readyList.size()==0){
			j = -1;
			idleTime++;
		    }
		}
	    }
	}
	readyList.clear();
	if (readyList.size() == 0&&blockedList.size()==0) {
	    MEMORY mem = new MEMORY();
	    mem.clearMemory();
	    LOADER loader = new LOADER();
	    if (lineMap.size() != 0) {
		Map.Entry<Integer, Integer> entry = lineMap.entrySet().iterator().next();
		loader.jobId = (entry.getKey() - 1);
		loader.newLineNumber = entry.getValue();
		loader.loadModule(tempFile, readyList, blockedList,
			CLOCK, totalCorrectJobs, totalErrorJobs,
			finalOutput, totalExTime, totalIoTime, idleTime,
			totalInternalFrag, time, errTime);
	    }
	}

	finalOutput.write("\nCLOCK: " + SYSTEM.decHex(String.valueOf(CLOCK)) + "(HEX)\n");
	DecimalFormat f = new DecimalFormat("##.##");
	if (endFlag == 0) {
	    finalOutput.write("Mean user job run time: " + 
	f.format((double) (time+totalIoTime) / totalCorrectJobs) + "(DEC)\n");
	    finalOutput.write("Mean user job execution time: " +
	f.format((double) totalExTime / totalCorrectJobs) + "(DEC)\n");
	    finalOutput.write("Mean user job I/O time: " + 
	f.format((double) totalIoTime / totalCorrectJobs) + "(DEC)\n");
	    finalOutput.write("Mean user job time in the SYSTEM: " +
	f.format((double) (CLOCK + idleTime) / totalCorrectJobs) + "(DEC)\n");
	    finalOutput.write("Total CPU Idle time: " + idleTime + "(DEC)\n");
	    if(totalErrorJobs==0){
	    finalOutput.write("Total time due to error jobs:" + "0 \n");
	    } else {
		finalOutput.write("Total time due to error jobs:" +errTime+ "\n");
	    }
	    finalOutput.write("Number of normal jobs: " + 
	    totalCorrectJobs + "(DEC)\n");
	    finalOutput.write("Total abnormal jobs: " + totalErrorJobs + "(DEC)\n");
	    finalOutput.write("Time lost for infinite jobs: " + (1000000 * infiJobs) + "(DEC)\n");
	    if (infinite.length() < 6) {
		finalOutput.write("Infinite jobs Id: none\n");
	    } else {
		finalOutput.write("Infinite jobs Id: " + infinite + "\n");
	    }
	    finalOutput.write("Mean internal fragmentation: " +
	    f.format((double) totalInternalFrag / 104) + "(DEC)\n");
	    finalOutput.close();
	    System.exit(0);
	}
    }

    /**
     * This method has been extracted from a 
     * larger method to maintain modularity.
     * @param readyList
     * @param finalOutput
     * @param i
     * @param blockedPcb
     * @throws IOException
     */
    private void storePcb(ArrayList<ProcessControlBlock> readyList,
	    FileWriter finalOutput, int i,
	    ProcessControlBlock blockedPcb) throws IOException {
	blockedPcb.setJobId(readyList.get(i).getJobId());
	finalOutput.write("Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId())) + "(HEX)\n");
	blockedPcb.setPC(readyList.get(i).getPC());
	blockedPcb.setDump(readyList.get(i).getDump());
	blockedPcb.setData(readyList.get(i).getData());
	blockedPcb.setCheckMemory(readyList.get(i).getCheckMemory());
	blockedPcb.setHaltBit(readyList.get(i).getHaltBit());
	blockedPcb.setExecutionTime(readyList.get(i).getExecutionTime());
	blockedPcb.setIoTime(readyList.get(i).getIoTime());
	blockedPcb.setMemory(readyList.get(i).getMemory());
	blockedPcb.setCheckTrace(readyList.get(i).getCheckTrace());
	blockedPcb.setPartitionNumber(readyList.get(i).getPartitionNumber());
	blockedPcb.setErrorCheck(readyList.get(i).getErrorCheck());
	blockedPcb.setErrorCatch(readyList.get(i).getErrorCatch());
	blockedPcb.setMemorySize(readyList.get(i).getMemorySize());
	blockedPcb.setTraceFlag(readyList.get(i).getTraceFlag());
	if(readyList.get(i).getError()==null||readyList.get(i).getError().length()==0){
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
	    + "(HEX): Program Counter: " + SYSTEM.decHex
	    (String.valueOf(readyList.get(i).getPC())) + "(HEX)\n");
	if(readyList.get(i).getR5()!=null){
	String binaryR5 = Integer.toBinaryString(readyList.get(i).getR5().intValue());
	while(binaryR5.length()<12){
	    binaryR5 = "0" + binaryR5;
	}
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
	    + "(HEX): Register R5: " + binaryR5 + "(BIN)\n");
	}
	if(readyList.get(i).getR4().toString().trim()!=null||
		!readyList.get(i).getR4().toString().trim().equals("")){
	String binaryR4 = Integer.toBinaryString
		(readyList.get(i).getR4().intValue());
	while(binaryR4.length()<12){
	    binaryR4 = "0" + binaryR4;
	}
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
	    + "(HEX): Register R4: " + binaryR4 + "(BIN)\n");
	}
	if (readyList.get(i).getIoRequest() != null) {
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): I/O Request: " + readyList.get(i).getIoRequest() + "\n");
	}
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
	    + "(HEX): Job Initiation: " + readyList.get(i)
	    .getEntryTime() + "(DEC)\n");
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
	    + "(HEX): Job left the CPU: " + readyList.
	    get(i).getTimeLeaving() + "(DEC)\n");
	}
    }

    /**
     * This method has been extracted from the
     * larger code to maintain modularity.
     * @param readyList
     * @param blockedList
     * @param errorFlag
     * @param i
     * @param blockedPcb
     */
    private boolean setValuesBpcb(ArrayList<ProcessControlBlock>
    readyList, ArrayList<ProcessControlBlock> blockedList,
	    String errorFlag, int i, ProcessControlBlock blockedPcb) {
	blockedPcb.setQuantumLeft(35 - readyList.get(i).getExecutionTime());
	blockedPcb.setIoTime(10);
	blockedPcb.setBaseAddress(readyList.get(i).getBaseAddress());
	blockedPcb.setBoundAddress(readyList.get(i).getBoundAddress());
	blockedPcb.setInput_line(readyList.get(i).getInput_line());
	blockedPcb.setJob(readyList.get(i).getJob());
	blockedPcb.setErrorCheck(readyList.get(i).getErrorCheck());
	blockedPcb.setErrorCatch(readyList.get(i).getErrorCatch());
	blockedPcb.setTraceWriter(readyList.get(i).getTraceWriter());
	blockedPcb.setOutput_line(readyList.get(i).getOutput_line());
	blockedPcb.setTotalOutput(readyList.get(i).getTotalOutput());
	blockedPcb.setR4(readyList.get(i).getR4());
	blockedPcb.setR5(readyList.get(i).getR5());
	blockedPcb.setActualJob(readyList.get(i).getActualJob());
	blockedPcb.setInternalFragmentation(readyList.get(i).getInternalFragmentation());
	if (!errorFlag.equals("9")) {
	blockedList.add(blockedPcb);
	return true;
	} else {
	return false;
	}
    }

    /**
     * This method has been extracted from a larger
     * method to maintain modularity of the code.
     * @param readyList
     * @param finalOutput
     * @param exTime
     * @param ioTime
     * @param i
     * @throws IOException
     */
    private void writeToFile(ArrayList<ProcessControlBlock>
    readyList, FileWriter finalOutput, int exTime, int ioTime,
	    int i) throws IOException {
	finalOutput.write("\nJob Id:" + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId())) + "(HEX)\n");
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): Part of partition occupied: \n"
		+ readyList.get(i).getDump() + "(BIN)");
	finalOutput.write("\nFor Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): Input lines: " + readyList.get(i).getData() + "(HEX)\n");
	if(readyList.get(i).getTotalOutput()!=null){
	String[] outputArr = readyList.get(i).getTotalOutput().toString().split(",");
	String output = "";
	for(int j=0;j<outputArr.length;j++){
	    if(outputArr.length==1){
		output = outputArr[j];
	    } else {
		output = output + " , " + outputArr[j];
	    }
	}
	finalOutput.write("For Job Id: " + SYSTEM.decHex(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): Output lines: " + output + "(HEX)\n");
	}
	finalOutput.write("For Job Id: " + SYSTEM.decHex(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): Partition number: " + readyList.get(i).
		getPartitionNumber() + "(DEC) && Memory Size:"
		+ readyList.get(i).getMemorySize() + "(DEC)\n");
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): Time of entry: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getEntryTime()))
		+ "(HEX)\n");
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): Time leaving : " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getTimeLeaving()))
		+ " (HEX)\n");
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): EXECUTION TIME: " + readyList.get(i).
		getExecutionTime() + " (DEC)\n");
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): INPUT/OUTPUT TIME: " + readyList.get(i).getIoTime() + " (DEC)\n");
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): Total time: " + (readyList.get(i).
			getExecutionTime() + readyList.get(i).getIoTime()) + "(DEC)\n");
	finalOutput.write("For Job Id: " + SYSTEM.decHex
		(String.valueOf(readyList.get(i).getJobId()))
		+ "(HEX): Termination: " + readyList.get(i).getTerminationType() + "\n");
    }

}
