package osii;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Description: MEMORY class stores the input
 * program into a memory. The main memory is 
 * divided into 7 slots of different capacities
 * (32, 64 and 128). The jobs are filled into 
 * these slots based on best fit algorithm.
 * Accuracy Analysis: This class has been 
 * created according to the requirements.
 */
public class MEMORY {

    ArrayList<String> memory_32_1 = new ArrayList<String>();
    ArrayList<String> memory_32_2 = new ArrayList<String>();
    ArrayList<String> memory_64_1 = new ArrayList<String>();
    ArrayList<String> memory_64_2 = new ArrayList<String>();
    ArrayList<String> memory_64_3 = new ArrayList<String>();
    ArrayList<String> memory_128_1 = new ArrayList<String>();
    ArrayList<String> memory_128_2 = new ArrayList<String>();
    ArrayList<String> memory = new ArrayList<String>();
    ArrayList<String> memoryJobs = new ArrayList<String>();
    String currentmemory;
    HashMap<Integer, StringBuffer> pcMap =
	    new HashMap<Integer, StringBuffer>();

    

    

    /**
     * Description: This method builds the memory
     * by taking the buffer from the loader and 
     * putting the data in an list.
     * Accuracy Analysis: This is just a
     * regular method which has no relation with
     * the requirements.
     * 
     * @param block
     */
    public void buildMemory(String block) {
	memory.add(block);
    }
    

    /**
     * Description: This method is responsible for storing
     * the data into the memory slots based on best
     * fit algorithm. The memory slots are segregated
     * based on their capacities. There are 3 slots of
     * memory (32, 64 and 128).
     * Accuracy Analysis: This has been created according
     * to the requirements.
     * 
     * @param pcb
     * @param jobId
     * @param schd
     * @param readyList
     * @return
     */
    public ArrayList<ArrayList<ProcessControlBlock>> 
    createMultipleMemory(ProcessControlBlock pcb, int jobId,
	    SCHEDULER schd, ArrayList<ProcessControlBlock> readyList) {
	int count = 0;
	ArrayList<ArrayList<ProcessControlBlock>> homeList
	= new ArrayList<ArrayList<ProcessControlBlock>>();
	ArrayList<ProcessControlBlock> tempList
	= new ArrayList<ProcessControlBlock>();
	ProcessControlBlock tempPcb = new ProcessControlBlock();
	if(pcb.getError().length()!=0){
	    count = 2;
	}
	if(pcb.getError().length()==0){
	/* Use the 32 bit memory slot: Start */
	if (memory.size() <= 32) {
	    count = memory32(pcb, count);
	}
	/** Use the 32 bit memory slot: End */
	/** Use the 64 bit memory slot: Start */
	if (memory.size() > 32 && memory.size() <= 64) {
	    count = memorySize64(pcb, count);
	}
	/** Use the 64 bit memory slot: End */
	/** Use the 128 bit memory slot: Start */
	if (memory.size() > 64 && memory.size() <= 128) {
	    count = memorySize128(pcb, count);
	}
	}
	if (count != 0) {
	    readyList.add(pcb);
	}
	homeList.add(readyList);
	tempPcb.setTemp(count);
	tempList.add(tempPcb);
	homeList.add(tempList);
	memory.clear();
	return homeList;
    }

    /**
     * This method has been extracted from
     * a larger method to maintain modularity.
     * @param pcb
     * @param count
     * @return
     */
    private int memorySize128(ProcessControlBlock pcb, int count) {
	if (memory_128_1.size() == 0) {
	memory_128_1 = createMemory(memory_128_1);
	count++;
	pcb.setBaseAddress("256");
	pcb.setBoundAddress("383");
	pcb.setMemorySize(128);
	pcb.setPartitionNumber(6);
	pcb.setActualJob(memory_128_1);
	pcb.setJob(memory_128_1);
	currentmemory = "memory_128_1";
	memoryJobs.add(currentmemory);
	pcb.setInternalFragmentation(128 - memory.size());
	} else if (memory_128_2.size() == 0) {
	memory_128_2 = createMemory(memory_128_2);
	count++;
	pcb.setBaseAddress("384");
	pcb.setBoundAddress("511");
	pcb.setMemorySize(128);
	pcb.setActualJob(memory_128_2);
	pcb.setPartitionNumber(7);
	pcb.setJob(memory_128_2);
	currentmemory = "memory_128_2";
	memoryJobs.add(currentmemory);
	pcb.setInternalFragmentation(128 - memory.size());
	}
	return count;
    }

    /**
     * This method has been extracted from a
     * larger method to maintain modularity.
     * @param pcb
     * @param count
     * @return
     */
    private int memorySize64(ProcessControlBlock pcb, int count) {
	if (memory_64_1.size() == 0) {
	memory_64_1 = createMemory(memory_64_1);
	count++;
	pcb.setBaseAddress("64");
	pcb.setBoundAddress("127");
	pcb.setMemorySize(64);
	pcb.setPartitionNumber(3);
	pcb.setActualJob(memory_64_1);
	pcb.setJob(memory_64_1);
	currentmemory = "memory_64_1";
	memoryJobs.add(currentmemory);
	pcb.setInternalFragmentation(64 - memory.size());
	} else {
	if (memory_64_2.size() == 0) {
	    memory_64_2 = createMemory(memory_64_2);
	    count++;
	    pcb.setMemorySize(64);
	    pcb.setPartitionNumber(4);
	    pcb.setBaseAddress("128");
	    pcb.setBoundAddress("191");
	    pcb.setActualJob(memory_64_2);
	    pcb.setJob(memory_64_2);
	    currentmemory = "memory_64_2";
	    memoryJobs.add(currentmemory);
	    pcb.setInternalFragmentation(64 - memory.size());
	} else if (memory_64_1.size() != 0 && memory_64_2.size()
		!= 0 && memory_64_3.size() == 0) {
	    memory_64_3 = createMemory(memory_64_3);
	    count++;
	    pcb.setBaseAddress("192");
	    pcb.setMemorySize(64);
	    pcb.setActualJob(memory_64_3);
	    pcb.setPartitionNumber(5);
	    pcb.setBoundAddress("255");
	    currentmemory = "memory_64_3";
	    pcb.setJob(memory_64_3);
	    memoryJobs.add(currentmemory);
	    pcb.setInternalFragmentation(64 - memory.size());
	}
	}
	return count;
    }

    /**
     * This method has been extracted from a larger
     * method to maintain modularity.
     * @param pcb
     * @param count
     * @return
     */
    private int memory32(ProcessControlBlock pcb, int count) {
	if (memory_32_1.size() == 0) {
	memory_32_1 = createMemory(memory_32_1);
	count++;
	pcb.setBaseAddress("0");
	pcb.setBoundAddress("31");
	pcb.setJob(memory_32_1);
	pcb.setMemorySize(32);
	pcb.setPartitionNumber(1);
	pcb.setActualJob(memory_32_1);
	currentmemory = "memory_32_1";
	memoryJobs.add(currentmemory);
	pcb.setInternalFragmentation(32 - memory.size());
	} else if (memory_32_2.size() == 0) {
	memory_32_2 = createMemory(memory_32_2);
	count++;
	pcb.setBaseAddress("32");
	pcb.setBoundAddress("63");
	pcb.setJob(memory_32_2);
	pcb.setMemorySize(32);
	pcb.setPartitionNumber(2);
	pcb.setActualJob(memory_32_2);
	currentmemory = "memory_32_2";
	memoryJobs.add(currentmemory);
	pcb.setInternalFragmentation(32 - memory.size());
	}
	return count;
    }

    public ArrayList<String> createMemory(ArrayList<String> toClone) {
	for (int i = 0; i < memory.size(); i++) {
	    toClone.add(memory.get(i));
	}
	return toClone;
    }

    /**
     * Description: This method creates an array using
     * the list called memory created before. Each 
     * elements of the memory array is a word which
     * contains 12 bits. The memory array has 4096 
     * words. This method calls the method in the 
     * CPU class and passes the memory to the CPU class.
     * Accuracy Analysis: This method has been created
     * in accordance with the requirement
     * document.
     * 
     * @param progLength
     * @param traceFlag
     * @param startAddress
     * @param finalError
     * @param finalWarning
     * @throws IOException
     */
    public void memoryManager(String finalError,
	    String finalWarning, LinkedHashMap<Integer, Integer> lineMap,
	    SCHEDULER schd, File tempFile,
	    ArrayList<ProcessControlBlock> readyList,
	    ArrayList<ProcessControlBlock> blockedList,
	    int CLOCK, int endFlag, int totalCorJobs, int totalErrJobs,
	    FileWriter fo, int tet, int tio, int idt, int itf, int time,
	    int errTime) throws IOException {
	schd.executeCPU(finalError, finalWarning, lineMap, tempFile,
		readyList, blockedList, CLOCK, endFlag,
		totalCorJobs, totalErrJobs, fo, tet, tio, idt, itf, time, errTime);
    }
    
    /**
     * This method clears the entire memory whenever needed.
     */
    public void clearMemory(){
	memory_128_1.clear();
	memory_128_2.clear();
	memory_32_1.clear();
	memory_32_2.clear();
	memory_64_1.clear();
	memory_64_2.clear();
	memory_64_3.clear();
    }
}