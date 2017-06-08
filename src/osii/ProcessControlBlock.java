package osii;

import java.io.FileWriter;
import java.util.ArrayList;
/**
 * Description: This class is The Process Control Block(PCB).
 * This class contains fields which are associated with jobs
 * and each job has an instance of PCB. It contains all the
 * information a process needs to execute.
 *
 */
public class ProcessControlBlock {
int jobId;
int PC;
int input_line;
String error;
int output_line;
int time;
int executionTime;
int ioTime;
int quantumLeft;
String baseAddress;
String boundAddress;
ArrayList<String> job = new ArrayList<String>();
Long R4;
Long R5;
int temp;
int memorySize;
int partitionNumber;
String dump;
ArrayList<String> data;
String output;
int entryTime;
String[] errorArray;
String[] warningArray;
String finalWarnings;
String finalError;
String terminationType;
int timeLeaving;
ArrayList<String>actualJob = new ArrayList<String>();
int readCounter;
String traceFlag;
int checkMemory;
String[] memory;
String ioRequest;
int haltBit;
String totalOutput;
int internalFragmentation;
int totalInternalFrag;
FileWriter traceWriter;
int checkTrace;
int errorCheck;
int errorCatch;

public int getErrorCatch() {
    return errorCatch;
}
public void setErrorCatch(int errorCatch) {
    this.errorCatch = errorCatch;
}
public int getErrorCheck() {
    return errorCheck;
}
public void setErrorCheck(int errorCheck) {
    this.errorCheck = errorCheck;
}
public int getCheckTrace() {
    return checkTrace;
}
public void setCheckTrace(int checkTrace) {
    this.checkTrace = checkTrace;
}
public FileWriter getTraceWriter() {
    return traceWriter;
}
public void setTraceWriter(FileWriter traceWriter) {
    this.traceWriter = traceWriter;
}
public String getError() {
    return error;
}
public void setError(String error) {
    this.error = error;
}
public int getTotalInternalFrag() {
    return totalInternalFrag;
}
public void setTotalInternalFrag(int totalInternalFrag) {
    this.totalInternalFrag = totalInternalFrag;
}
public int getInternalFragmentation() {
    return internalFragmentation;
}
public void setInternalFragmentation(int internalFragmentation) {
    this.internalFragmentation = internalFragmentation;
}
public String getTotalOutput() {
    return totalOutput;
}
public void setTotalOutput(String totalOutput) {
    this.totalOutput = totalOutput;
}
public int getHaltBit() {
    return haltBit;
}
public void setHaltBit(int haltBit) {
    this.haltBit = haltBit;
}
public String getIoRequest() {
    return ioRequest;
}
public void setIoRequest(String ioRequest) {
    this.ioRequest = ioRequest;
}
public String[] getMemory() {
    return memory;
}
public void setMemory(String[] memory) {
    this.memory = memory;
}
public int getCheckMemory() {
    return checkMemory;
}
public void setCheckMemory(int checkMemory) {
    this.checkMemory = checkMemory;
}
public String getTraceFlag() {
    return traceFlag;
}
public void setTraceFlag(String traceFlag) {
    this.traceFlag = traceFlag;
}
public int getReadCounter() {
    return readCounter;
}
public void setReadCounter(int readCounter) {
    this.readCounter = readCounter;
}
public ArrayList<String> getActualJob() {
    return actualJob;
}
public void setActualJob(ArrayList<String> actualJob) {
    this.actualJob = actualJob;
}
public int getTimeLeaving() {
    return timeLeaving;
}
public void setTimeLeaving(int timeLeaving) {
    this.timeLeaving = timeLeaving;
}
public String getTerminationType() {
    return terminationType;
}
public void setTerminationType(String terminationType) {
    this.terminationType = terminationType;
}
public String getFinalWarnings() {
    return finalWarnings;
}
public void setFinalWarnings(String finalWarnings) {
    this.finalWarnings = finalWarnings;
}
public String getFinalError() {
    return finalError;
}
public void setFinalError(String finalError) {
    this.finalError = finalError;
}
public String[] getErrorArray() {
    return errorArray;
}
public void setErrorArray(String[] errorArray) {
    this.errorArray = errorArray;
}
public String[] getWarningArray() {
    return warningArray;
}
public void setWarningArray(String[] warningArray) {
    this.warningArray = warningArray;
}
public int getEntryTime() {
    return entryTime;
}
public void setEntryTime(int entryTime) {
    this.entryTime = entryTime;
}
public String getOutput() {
    return output;
}
public void setOutput(String output) {
    this.output = output;
}
public ArrayList<String> getData() {
    return data;
}
public void setData(ArrayList<String> data) {
    this.data = data;
}
public String getDump() {
    return dump;
}
public void setDump(String dump) {
    this.dump = dump;
}
public int getPartitionNumber() {
    return partitionNumber;
}
public void setPartitionNumber(int partitionNumber) {
    this.partitionNumber = partitionNumber;
}
public int getMemorySize() {
    return memorySize;
}
public void setMemorySize(int memorySize) {
    this.memorySize = memorySize;
}
public int getTemp() {
    return temp;
}
public void setTemp(int temp) {
    this.temp = temp;
}
public Long getR4() {
    return R4;
}
public void setR4(Long r4) {
    R4 = r4;
}
public Long getR5() {
    return R5;
}
public void setR5(Long r5) {
    R5 = r5;
}
public int getIoTime() {
    return ioTime;
}
public void setIoTime(int ioTime) {
    this.ioTime = ioTime;
}
public int getExecutionTime() {
    return executionTime;
}
public void setExecutionTime(int executionTime) {
    this.executionTime = executionTime;
}
public int getInput_line() {
    return input_line;
}
public void setInput_line(int input_line) {
    this.input_line = input_line;
}
public int getOutput_line() {
    return output_line;
}
public void setOutput_line(int output_line) {
    this.output_line = output_line;
}
public String getBaseAddress() {
    return baseAddress;
}
public void setBaseAddress(String baseAddress) {
    this.baseAddress = baseAddress;
}
public String getBoundAddress() {
    return boundAddress;
}
public void setBoundAddress(String boundAddress) {
    this.boundAddress = boundAddress;
}
public ArrayList<String> getJob() {
    return job;
}
public void setJob(ArrayList<String> job) {
    this.job = job;
}

public int getJobId() {
    return jobId;
}
public void setJobId(int jobId) {
    this.jobId = jobId;
}
public int getPC() {
    return PC;
}
public void setPC(int pC) {
    PC = pC;
}
public int getTime() {
    return time;
}
public void setTime(int time) {
    this.time = time;
}
public int getQuantumLeft() {
    return quantumLeft;
}
public void setQuantumLeft(int quantumLeft) {
    this.quantumLeft = quantumLeft;
}

}
