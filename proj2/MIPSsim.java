import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * MIPSsim.java
 * @author Yiwen Liu
 * @author 2354640364@qq.com
 * On my honor, I have neither given nor received unauthorized aid on this assignment.
 */
public class MIPSsim {
    public static void main(String[] args) throws FileNotFoundException, IOException, Throwable {
        String fileName = args[0];
        File file = new File(fileName);

        // start simulating
        int startAddress = 256;
        Simulator simulator = new Simulator(startAddress);

        // start reading the file by lines
        ReaderByLines reader = new ReaderByLines(file);
        String tempLine = null;
        // translate instructions
        while((tempLine = reader.read()) != null) {
            Instruction instruction = new Instruction();
            instruction.translate(tempLine);
            simulator.addInstruction(instruction);
            if(instruction.name.equals("BREAK")){
                break;
            }
        }

        // translate data
        ArrayList<Integer> dataList = new ArrayList<Integer>();
        while((tempLine = reader.read()) != null) {
            Instruction instruction = new Instruction();
            int tempData = instruction.translateData(tempLine);
            instruction.data = tempData;
            simulator.addDataBinary(instruction);
        }
        simulator.setMemoryData();

        String dir = file.getAbsoluteFile().getParent();
        PrintStream outfileDisassembly = new PrintStream((dir+ "/disassembly.txt"));

        simulator.printInstructions(outfileDisassembly);
        outfileDisassembly.close();

        PrintStream outfileSimulation = new PrintStream((dir+"/simulation.txt"));
        simulator.run(outfileSimulation);
        outfileSimulation.close();

        reader.close();
    }
}

// read file by lines
class ReaderByLines {
    private final FileReader reader;
    private final BufferedReader bufferedReader;

    public ReaderByLines(File file) throws FileNotFoundException {
        reader = new FileReader(file);
        bufferedReader = new BufferedReader(reader);
    }

    // return a line text
    public String read() throws IOException {
        String line = bufferedReader.readLine();
        return line;
    }

    public void close() throws IOException {
        bufferedReader.close();
        reader.close();
    }
}

class Instruction {
    public String binary;
    public int address;
    public String name;
    public String format;
    public int[] parameters;
    public int[] order;
    public int[] parameterLength;
    public int[] orderedParameters;
    public boolean isData;

    public int data;

    public Instruction() {
        binary = null;
        name = null;
        format = null;
        address = 0;
        isData = false;
    }

    public void selectOP(String category, int opCode) {
        if(category.equals("01")) {
            switch (opCode) {
                case 0:
                    this.name = "J";
                    this.format = "#%d";
                    this.order = new int[] {0};
                    this.parameterLength = new int[] {26};
                    break;
                case 1:
                    this.name = "JR";
                    this.format = "R%d";
                    this.order = new int[] {0};
                    this.parameterLength = new int[] {5, 21};
                    break;
                case 2:
                    this.name = "BEQ";
                    this.format = "R%d, R%d, #%d";
                    this.order = new int[] {0, 1, 2};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                case 3:
                    this.name = "BLTZ";
                    this.format = "R%d, #%d";
                    this.order = new int[] {0, 2};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                case 4:
                    this.name = "BGTZ";
                    this.format = "R%d, #%d";
                    this.order = new int[] {0, 2};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                case 5:
                    this.name = "BREAK";
                    this.format = "";
                    this.order = new int[] {};
                    this.parameterLength = new int[] {20,6};
                    break;
                case 6:
                    this.name = "SW";
                    this.format = "R%d, %d(R%d)";
                    this.order = new int[] {1, 2, 0};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                case 7:
                    this.name = "LW";
                    this.format = "R%d, %d(R%d)";
                    this.order = new int[] {1, 2, 0};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                case 8:
                    this.name = "SLL";
                    this.format = "R%d, R%d, #%d";
                    this.order = new int[] {2, 1, 3};
                    this.parameterLength = new int[] {5, 5, 5, 5, 6};
                    break;
                case 9:
                    this.name = "SRL";
                    this.format = "R%d, R%d, #%d";
                    this.order = new int[] {2, 1, 3};
                    this.parameterLength = new int[] {5, 5, 5, 5, 6};
                    break;
                case 10:
                    this.name = "SRA";
                    this.format = "R%d, R%d, #%d";
                    this.order = new int[] {2, 1, 3};
                    this.parameterLength = new int[] {5, 5, 5, 5, 6};
                    break;
                case 11:
                    this.name = "NOP";
                    this.format = "";
                    this.order = new int[] {};
                    this.parameterLength = new int[] {0};
                    break;
                default:
                    System.out.print("Can't find this operator!");
                    return;
            }
        }
        else if(category.equals("11")) {
            switch (opCode) {
                case 0:
                    this.name = "ADD";
                    this.format = "R%d, R%d, R%d";
                    this.order = new int[] {2, 0, 1};
                    this.parameterLength = new int[] {5, 5, 5, 11};
                    break;
                case 1:
                    this.name = "SUB";
                    this.format = "R%d, R%d, R%d";
                    this.order = new int[] {2, 0, 1};
                    this.parameterLength = new int[] {5, 5, 5, 11};
                    break;
                case 2:
                    this.name = "MUL";
                    this.format = "R%d, R%d, R%d";
                    this.order = new int[] {2, 0, 1};
                    this.parameterLength = new int[] {5, 5, 5, 11};
                    break;
                case 3:
                    this.name = "AND";
                    this.format = "R%d, R%d, R%d";
                    this.order = new int[] {2, 0, 1};
                    this.parameterLength = new int[] {5, 5, 5, 11};
                    break;
                case 4:
                    this.name = "OR";
                    this.format = "R%d, R%d, R%d";
                    this.order = new int[] {2, 0, 1};
                    this.parameterLength = new int[] {5, 5, 5, 11};
                    break;
                case 5:
                    this.name = "XOR";
                    this.format = "R%d, R%d, R%d";
                    this.order = new int[] {2, 0, 1};
                    this.parameterLength = new int[] {5, 5, 5, 11};
                    break;
                case 6:
                    this.name = "NOR";
                    this.format = "R%d, R%d, R%d";
                    this.order = new int[] {2, 0, 1};
                    this.parameterLength = new int[] {5, 5, 5, 11};
                    break;
                case 7:
                    this.name = "SLT";
                    this.format = "R%d, R%d, R%d";
                    this.order = new int[] {2, 0, 1};
                    this.parameterLength = new int[] {5, 5, 5, 11};
                    break;
                case 8:
                    this.name = "ADDI";
                    this.format = "R%d, R%d, #%d";
                    this.order = new int[] {1, 0, 2};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                case 9:
                    this.name = "ANDI";
                    this.format = "R%d, R%d, #%d";
                    this.order = new int[] {1, 0, 2};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                case 10:
                    this.name = "ORI";
                    this.format = "R%d, R%d, #%d";
                    this.order = new int[] {1, 0, 2};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                case 11:
                    this.name = "XORI";
                    this.format = "R%d, R%d, #%d";
                    this.order = new int[] {1, 0, 2};
                    this.parameterLength = new int[] {5, 5, 16};
                    break;
                default:
                    System.out.print("Can't find this operator!");
                    return;
            }
        }
    }

    public void translate(String line) {
        this.binary = line;
        String category = line.substring(0,2);
        int opCode = binaryToDecimal(line.substring(2,6),false);
        selectOP(category, opCode);
        String lineTmp = line.substring(6);

        parameters = new int[parameterLength.length];
        for(int i=0; i<parameterLength.length; i++) {
            parameters[i] = binaryToDecimal(lineTmp.substring(0, parameterLength[i]), false);
            lineTmp = lineTmp.substring(parameterLength[i]);
        }

        orderedParameters = new int[order.length];
        for(int i=0; i<order.length; i++) {
            orderedParameters[i] = parameters[order[i]];
        }

        //expand the parameters of J BEQ BGTZ BLTZ
        if(this.name.equals("J")) {
            this.orderedParameters[0] = this.orderedParameters[0] << 2;
        }
        else if(this.name.equals("BEQ")) {
            this.orderedParameters[2] = this.orderedParameters[2] << 2;
        }
        else if(this.name.equals("BGTZ") || this.name.equals("BLTZ")) {
            this.orderedParameters[1] = this.orderedParameters[1] << 2;
        }
    }



    // translate data with symbolicBit
    public int translateData(String line) {
        this.binary = line;
        data = binaryToDecimal(line, true);
        return data;
    }

    // translate binary into decimal
    public static int binaryToDecimal(String binary, boolean hasSymbolBit) {
        int symbolFlag = 0;
        if (hasSymbolBit) {
            symbolFlag = binary.charAt(0) - '0';
            binary = binary.substring(1);
        }
        char[] array = binary.toCharArray();
        int num = 0;
        for (int i = 0; i != array.length; i++) {
            int bit = (array[i] - '0') << (array.length - 1 - i);
            num = num + bit;
        }
        return (symbolFlag << 31) + num;
    }

    @Override
    public String toString() {
        switch (orderedParameters.length) {
            case 0:
                return name;
            case 1:
                return String.format(name + " " + format, orderedParameters[0]);
            case 2:
                return String.format(name + " " + format, orderedParameters[0], orderedParameters[1]);
            case 3:
                return String.format(name + " " + format, orderedParameters[0], orderedParameters[1], orderedParameters[2]);
            case 4:
                return String.format(name + " " + format, orderedParameters[0], orderedParameters[1], orderedParameters[2], orderedParameters[3]);
            default:
                return "";
        }
    }

}

class Simulator {

    private ArrayList<Instruction> WaitingInstruction = new ArrayList<Instruction>(1);
    private ArrayList<Instruction> ExecutedInstruction = new ArrayList<Instruction>(1);

    private ArrayList<Instruction> PreIssueUnready = new ArrayList<Instruction>(4);
    private ArrayList<Instruction> PreIssueReady = new ArrayList<Instruction>(4);

    private ArrayList<Instruction> PreALU1Unready = new ArrayList<Instruction>(2);
    private ArrayList<Instruction> PreALU1Ready = new ArrayList<Instruction>(2);

    private ArrayList<Instruction> PreMemUnready = new ArrayList<Instruction>(1);
    private ArrayList<Instruction> PreMemReady = new ArrayList<Instruction>(1);
    private ArrayList<Instruction> PostMemUnready = new ArrayList<Instruction>(1);
    private ArrayList<Instruction> PostMemReady = new ArrayList<Instruction>(1);

    private ArrayList<Instruction> PreALU2Unready = new ArrayList<Instruction>(2);
    private ArrayList<Instruction> PreALU2Ready = new ArrayList<Instruction>(2);
    private ArrayList<Instruction> PostALU2Unready = new ArrayList<Instruction>(1);
    private ArrayList<Instruction> PostALU2Ready = new ArrayList<Instruction>(2);

    private ArrayList<Instruction> instructions;
    private ArrayList<Instruction> dataBinary;

    private int startAddress;
    private int pc;
    private int cycle;
    private boolean isRunning;


    private int[] Reg;
    private int[] memory;

    // 1:ready,0:unready
    int[] registerStatus = new int[32];

    // issue: ~[des]=0
    int[] checkRegister = new int[32];

    // preissue: behaviors like checkRegister(if flag>0 )
    int[] checkRegister1 = new int[32];

    // avoid WAR\WAW\RAW
    int[] checkRegister2 = new int[32];

    // isStalled=false:fetch instructions
    boolean isStalled;
    boolean isEnough;

    private int memoryStartAddress;

    // obtain execution function by Reflection
    private Class<?> operateFunction = Simulator.class;


    public Simulator(int startAddress) {
        this.startAddress = startAddress;
        this.cycle = 0;

        instructions = new ArrayList<Instruction>();
        dataBinary = new ArrayList<Instruction>();
        isRunning = false;

        this.Reg = new int[32];
        for(int i=0;i<32;i++) {
            Reg[i] = 0;
        }
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addDataBinary(Instruction instruction) {
        dataBinary.add(instruction);
    }

    public void setMemoryData() {
        memory = new int[dataBinary.size()];
        int cnt = 0;
        Iterator<Instruction> iter = dataBinary.iterator();
        while(iter.hasNext()) {
            Instruction dataInstruction = iter.next();
            memory[cnt] = dataInstruction.data;
            cnt++;
        }
    }

    public void printInstructions(PrintStream out) {
        Iterator<Instruction> iter = instructions.iterator();
        int address = startAddress;
        while(iter.hasNext()) {
            Instruction instruction = iter.next();
            out.print(instruction.binary+"\t"+address+"\t"+instruction);
            instruction.address = address;
            out.println();
            address += 4;
        }

        for (Instruction data : dataBinary) {
            out.print(data.binary + "\t" + address + "\t" + data.data);
            data.address = address;
            out.println();
            address += 4;
        }
    }

    public void prepare() {
        for (int i =0; i<32; i++)
        {
            registerStatus[i] = 1;
            checkRegister[i] = 1;
            checkRegister1[i] = 1;
            checkRegister2[i] = 1;
        }
    }

    public void setDependency(Instruction instruction) {
        String name = instruction.name;
        if(name.equals("SW")) {
        }
        else {
            checkRegister[instruction.orderedParameters[0]] = 0;
        }
    }

    public void setDependency1(Instruction instruction) {
        String name = instruction.name;
        if(name.equals("SW")) {
        }
        else {
            checkRegister1[instruction.orderedParameters[0]] = 0;
        }
    }

    // WAR WAW RAW
    public void setDependency2(Instruction instruction) {
        String name = instruction.name;
        if(name.equals("SW")) {
            checkRegister2[instruction.orderedParameters[0]] = 0;
            checkRegister2[instruction.orderedParameters[2]] = 0;
        }
        else if(name.equals("LW")) {
            checkRegister2[instruction.orderedParameters[2]] = 0;
        }
        else if(name.equals("ADD") || name.equals("SUB") || name.equals("MUL") || name.equals("OR") || name.equals("XOR") || name.equals("NOR")) {
            checkRegister2[instruction.orderedParameters[1]] = 0;
            checkRegister2[instruction.orderedParameters[2]] = 0;
        }
        else if(name.equals("ADDI") || name.equals("ANDI") || name.equals("ORI") || name.equals("XORI") || name.equals("SLL") || name.equals("SRL") || name.equals("SRA")) {
            checkRegister2[instruction.orderedParameters[1]] = 0;
        }
        else {

        }
    }

    public boolean checkDependency(Instruction instruction) {
        String name = instruction.name;

        if(name.equals("SW")) {
            if(checkRegister[instruction.orderedParameters[0]] == 1 && checkRegister[instruction.orderedParameters[2]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(name.equals("LW")) {
            if(checkRegister[instruction.orderedParameters[2]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(name.equals("ADD") || name.equals("SUB") || name.equals("MUL") || name.equals("OR") || name.equals("XOR") || name.equals("NOR")) {
            if(checkRegister[instruction.orderedParameters[1]] == 1 && checkRegister[instruction.orderedParameters[2]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(name.equals("ADDI") || name.equals("ANDI") || name.equals("ORI") || name.equals("XORI") || name.equals("SLL") || name.equals("SRL") || name.equals("SRA")) {
            if(checkRegister[instruction.orderedParameters[1]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public boolean checkDependency1(Instruction instruction) {
        String name = instruction.name;

        if(name.equals("SW")) {
            if(checkRegister1[instruction.orderedParameters[0]] == 1 && checkRegister1[instruction.orderedParameters[2]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(name.equals("LW")) {
            if(checkRegister1[instruction.orderedParameters[2]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(name.equals("ADD") || name.equals("SUB") || name.equals("MUL") || name.equals("OR") || name.equals("XOR") || name.equals("NOR")) {
            if(checkRegister1[instruction.orderedParameters[1]] == 1 && checkRegister1[instruction.orderedParameters[2]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(name.equals("ADDI") || name.equals("ANDI") || name.equals("ORI") || name.equals("XORI") || name.equals("SLL") || name.equals("SRL") || name.equals("SRA")) {
            if(checkRegister1[instruction.orderedParameters[1]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public boolean checkDependency2(Instruction instruction) {
        String name = instruction.name;

        if(name.equals("SW")) {
                return true;
        }
        else {
            if(checkRegister2[instruction.orderedParameters[0]] == 1) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public void IF() {
        if (ExecutedInstruction.size() == 1) {
            ExecutedInstruction.remove(0);
        }

        if (WaitingInstruction.size() == 1) {
            Instruction instr1 = WaitingInstruction.get(0);
            if (instr1.name.equals("BEQ")) {
                if (registerStatus[instr1.orderedParameters[0]] == 1 && registerStatus[instr1.orderedParameters[1]] == 1) {
                    BEQ(instr1.orderedParameters);
                    ExecutedInstruction.add(instr1);
                    WaitingInstruction.remove(0);
                    isStalled = false;
                }
            }
            else if(instr1.name.equals("BGTZ")) {
                if (registerStatus[instr1.orderedParameters[0]] == 1) {
                    BGTZ(instr1.orderedParameters);
                    ExecutedInstruction.add(instr1);
                    WaitingInstruction.remove(0);
                    isStalled = false;
                }
            }
            else if(instr1.name.equals("BLTZ")) {
                if (registerStatus[instr1.orderedParameters[0]] == 1) {
                    BGTZ(instr1.orderedParameters);
                    ExecutedInstruction.add(instr1);
                    WaitingInstruction.remove(0);
                    isStalled = false;
                }
            }
        }
        else {
            int index = (pc-startAddress)>>2;

            Instruction instruction1 = instructions.get(index);
            isEnough = false;
            Instruction instruction2 = new Instruction();
            if(index + 1 < instructions.size()) {
                isEnough = true;
                instruction2 = instructions.get(index + 1);
            }

            // no stall at the ed of last cycle and PreIssue<4
            if(!isStalled && PreIssueReady.size()<4) {

                // the first is branch, fetch one
                if(instruction1.name.equals("J") || instruction1.name.equals("JR") || instruction1.name.equals("BEQ") || instruction1.name.equals("BLTZ") || instruction1.name.equals("BGTZ")) {
                    isEnough = false;
                    if(instruction1.name.equals("BEQ")) {
                        //源寄存器准备好了
                        if(registerStatus[instruction1.orderedParameters[0]] == 1 && registerStatus[instruction1.orderedParameters[1]] == 1) {
                            ExecutedInstruction.add(instruction1);
                            BEQ(instruction1.orderedParameters);
                        }
                        else {
                            //源寄存器没准备好
                            WaitingInstruction.add(instruction1);
                            isStalled = true;

                        }
                    }
                    else if(instruction1.name.equals("BLTZ")) {
                        if(registerStatus[instruction1.orderedParameters[0]] == 1) {
                            ExecutedInstruction.add(instruction1);
                            BLTZ(instruction1.orderedParameters);
                        }
                        else {
                            WaitingInstruction.add(instruction1);
                            isStalled = true;
                        }

                    }
                    else if(instruction1.name.equals("BGTZ")) {
                        if(registerStatus[instruction1.orderedParameters[0]] == 1) {
                            ExecutedInstruction.add(instruction1);
                            BGTZ(instruction1.orderedParameters);
                        }
                        else {
                            WaitingInstruction.add(instruction1);
                            isStalled = true;
                        }
                    }
                    else if(instruction1.name.equals("J")) {
                        ExecutedInstruction.add(instruction1);
                        J(instruction1.orderedParameters);
//                        isWaiting = false;
//                        isStalled = false;
                    }
                    else {
                        //JR
                        ExecutedInstruction.add(instruction1);
                        JR(instruction1.orderedParameters);
//                        isWaiting = false;
//                        isStalled = false;
                    }
                }
                else if(instruction1.name.equals("BREAK") || instruction1.name.equals("NOP")) {
                    BREAK(instruction1.orderedParameters);
                    ExecutedInstruction.add(instruction1);
                    isEnough = false;
                }
                else {
                    // the first isn;t branch, put one, and then judge the second
                    if(!instruction1.name.equals("SW")) {
                        registerStatus[instruction1.orderedParameters[0]] = 0;
                    }
                    PreIssueUnready.add(instruction1);

                    // can fetch the second
                    if(isEnough && PreIssueReady.size()<3) {
                        // the second is branch
                        if(instruction2.name.equals("J") || instruction2.name.equals("JR") || instruction2.name.equals("BEQ") || instruction2.name.equals("BLTZ") || instruction2.name.equals("BGTZ")) {
                            if(instruction2.name.equals("BEQ")) {
                                //源寄存器准备好了
                                if(registerStatus[instruction2.orderedParameters[0]] == 1 && registerStatus[instruction2.orderedParameters[1]] == 1) {
                                    ExecutedInstruction.add(instruction2);
                                    BEQ(instruction2.orderedParameters);
                                }
                                else {
                                    //源寄存器没准备好
                                    WaitingInstruction.add(instruction2);
                                    isStalled = true;

                                }
                            }
                            else if(instruction2.name.equals("BLTZ")) {
                                if(registerStatus[instruction2.orderedParameters[0]] == 1) {
                                    ExecutedInstruction.add(instruction2);
                                    BLTZ(instruction2.orderedParameters);
                                }
                                else {
                                    WaitingInstruction.add(instruction2);
                                    isStalled = true;
                                }

                            }
                            else if(instruction2.name.equals("BGTZ")) {
                                if(registerStatus[instruction2.orderedParameters[0]] == 1) {
                                    ExecutedInstruction.add(instruction2);
                                    BGTZ(instruction2.orderedParameters);
                                }
                                else {
                                    WaitingInstruction.add(instruction2);
                                    isStalled = true;
                                }
                            }
                            else if(instruction2.name.equals("J")) {
                                ExecutedInstruction.add(instruction2);
                                J(instruction2.orderedParameters);
                            }
                            else {
                                //JR
                                ExecutedInstruction.add(instruction2);
                                JR(instruction2.orderedParameters);
                            }
                        }
                        else if(instruction2.name.equals("BREAK") || instruction2.name.equals("NOP")) {
                            BREAK(instruction2.orderedParameters);
                            ExecutedInstruction.add(instruction2);
                        }
                        else {
                            //the second isn't branch
                            if(!instruction1.name.equals("SW")) {
                                registerStatus[instruction2.orderedParameters[0]] = 0;
                            }
                            PreIssueUnready.add(instruction2);
                        }
                    }

                }

                pc = pc + 4;
                if(!isStalled && PreIssueReady.size()<3) {
                    pc = pc + 4;
                }

            }
        }
    }

    public void Issue() {
        int flag1 = PreALU1Ready.size();
        int flag2 = PreALU2Ready.size();
        int flag = 0;


        while(PreIssueReady.size() > 0 && flag < PreIssueReady.size()) {
            Instruction instruction = PreIssueReady.get(flag);

            if(instruction.name.equals("SW") || instruction.name.equals("LW")) {
                if(flag1<2 && checkDependency(instruction)) {
                    if(flag>0) {
                        if(checkDependency1(instruction)) {
                            setDependency(instruction);
                            PreALU1Unready.add(instruction);
                            PreIssueReady.remove(flag);
                            break;
                        }
                        else {
                            flag++;
                            setDependency(instruction);
                            setDependency1(instruction);
                            setDependency2(instruction);
                        }
                    }
                    else {
                        setDependency(instruction);
                        PreALU1Unready.add(instruction);
                        PreIssueReady.remove(flag);
                        break;
                    }
                }
                else {
                    flag++;
                    setDependency(instruction);
                    setDependency1(instruction);
                    if(flag>0) {
                        setDependency2(instruction);
                    }
                }
            }
            else {
                if(flag2<2 && checkDependency(instruction)) {
                    if(flag>0){
                        if(checkDependency2(instruction)) {
                            setDependency(instruction);
                            PreALU2Unready.add(instruction);
                            PreIssueReady.remove(flag);
                            break;
                        }
                        else {
                            flag++;
                            setDependency1(instruction);
                            setDependency2(instruction);
                        }
                    }
                    else {
                        setDependency(instruction);
                        PreALU2Unready.add(instruction);
                        PreIssueReady.remove(flag);
                        break;
                    }
                }
                else {
                    flag++;
                    setDependency1(instruction);
                    if(flag>0) {
                        setDependency2(instruction);
                    }
                }
            }
//            setDependency(instruction);
        }

    }

    public void ALU() {
        if(PreALU1Ready.size()>0) {
            Instruction currentInstruction = PreALU1Ready.get(0);
            PreMemUnready.add(currentInstruction);
            PreALU1Ready.remove(0);
        }

        if(PreALU2Ready.size()>0) {
            Instruction currentInstruction = PreALU2Ready.get(0);
            PostALU2Unready.add(currentInstruction);
            PreALU2Ready.remove(0);
        }

    }

    public void MEM() {
        if(PreMemReady.size()>0) {
            Instruction instruction = PreMemReady.get(0);
            if(instruction.name.equals("LW")) {
                PreMemReady.remove(0);
                PostMemUnready.add(instruction);
            }
            else if(instruction.name.equals("SW")) {
                PreMemReady.remove(0);
                SW(instruction.orderedParameters);
                if(instruction.orderedParameters[0] != PostALU2Unready.get(0).orderedParameters[0]) {
                    checkRegister[instruction.orderedParameters[0]] = 1;
                }
                if(instruction.orderedParameters[2] != PostALU2Unready.get(0).orderedParameters[0]) {
                    checkRegister[instruction.orderedParameters[2]] = 1;
                }
            }
        }
    }

    public void WriteBack() {
        if(PostALU2Ready.size()>0) {
            Instruction instruction = PostALU2Ready.get(0);

            try {
                Method method = operateFunction.getDeclaredMethod(instruction.name, int[].class);
                method.invoke(this, instruction.orderedParameters);
            } catch (Exception e) {
                e.printStackTrace();
            }

            registerStatus[instruction.orderedParameters[0]] = 1;
            checkRegister[instruction.orderedParameters[0]] = 1;
            checkRegister1[instruction.orderedParameters[0]] = 1;
            PostALU2Ready.remove(0);

            String name = instruction.name;
            if (name.equals("ADD") || name.equals("SUB") || name.equals("MUL") || name.equals("OR") || name.equals("XOR") || name.equals("NOR")){
                checkRegister2[instruction.orderedParameters[1]] = 1;
                checkRegister2[instruction.orderedParameters[2]] = 1;
            }
            else if(name.equals("ADDI") || name.equals("ANDI") || name.equals("ORI") || name.equals("XORI") || name.equals("SLL") || name.equals("SRL") || name.equals("SRA")) {
                checkRegister2[instruction.orderedParameters[1]] = 1;
            }
        }

        if(PostMemReady.size()>0) {
            Instruction instruction = PostMemReady.get(0);
            LW(instruction.orderedParameters);
            registerStatus[instruction.orderedParameters[0]] = 1;
            checkRegister[instruction.orderedParameters[0]] = 1;
            checkRegister1[instruction.orderedParameters[0]] = 1;
            PostMemReady.remove(0);
        }

    }

    public void updateBuffer() {
        while(PreIssueUnready.size()>0) {
            PreIssueReady.add(PreIssueUnready.get(0));
            PreIssueUnready.remove(0);
        }

        while(PreALU1Unready.size()>0) {
            PreALU1Ready.add(PreALU1Unready.get(0));
            PreALU1Unready.remove(0);
        }

        while(PreMemUnready.size()>0) {
            PreMemReady.add(PreMemUnready.get(0));
            PreMemUnready.remove(0);
        }

        while(PostMemUnready.size()>0) {
            PostMemReady.add(PostMemUnready.get(0));
            PostMemUnready.remove(0);
        }

        while(PreALU2Unready.size()>0) {
            PreALU2Ready.add(PreALU2Unready.get(0));
            PreALU2Unready.remove(0);
        }

        while(PostALU2Unready.size()>0) {
            PostALU2Ready.add(PostALU2Unready.get(0));
            PostALU2Unready.remove(0);
        }
    }

    public void run (PrintStream out) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        cycle = 1;
        isRunning = true;
        pc = startAddress;
        memoryStartAddress = startAddress + (instructions.size() << 2);
        isStalled = false;
        isEnough = false;
        prepare();

        while(isRunning) {
            IF();
            Issue();
            ALU();
            MEM();
            WriteBack();
            updateBuffer();

            out.println("--------------------");
            out.printf("Cycle:%d", cycle);
            out.println();
            out.println();

            out.println("IF Unit:");
            out.print("\t"+"Waiting Instruction: ");
            for(int i=0; i<WaitingInstruction.size(); i++) {
                out.print("["+WaitingInstruction.get(i)+"]");
            }
            out.println();
            out.print("\t"+"Executed Instruction: ");
            for(int i=0;i<ExecutedInstruction.size();i++) {
                out.print("["+ExecutedInstruction.get(i)+"]");
            }
            out.println();

            out.println("Pre-Issue Queue:");
            for(int i=0; i<4; i++) {
                if(i<PreIssueReady.size()) {
                    out.println("\t"+"Entry "+i+": ["+PreIssueReady.get(i)+"]");
                }
                else {
                    out.println("\t"+"Entry "+i+":");
                }
            }

            out.println("Pre-ALU1 Queue:");
            for(int i=0; i<2; i++) {
                if(i<PreALU1Ready.size()) {
                    out.println("\t"+"Entry "+i+": ["+PreALU1Ready.get(i)+"]");
                }
                else {
                    out.println("\t"+"Entry "+i+":");
                }
            }

            out.print("Pre-MEM Queue:");
            if(PreMemReady.size()>0) {
                out.println(" ["+PreMemReady.get(0)+"]");
            }
            else {
                out.println();
            }

            out.print("Post-MEM Queue:");
            if(PostMemReady.size()>0) {
                out.println(" ["+PostMemReady.get(0)+"]");
            }
            else {
                out.println();
            }

            out.println("Pre-ALU2 Queue:");
            for(int i=0; i<2; i++) {
                if(i<PreALU2Ready.size()) {
                    out.println("\t"+"Entry "+i+": ["+PreALU2Ready.get(i)+"]");
                }
                else {
                    out.println("\t"+"Entry "+i+":");
                }
            }

            out.print("Post-ALU2 Queue:");
            if(PostALU2Ready.size()>0) {
                out.println(" ["+PostALU2Ready.get(0)+"]");
            }
            else {
                out.println();
            }
            out.println();

            out.println("Registers");
            for(int i=0; i<Reg.length; i++) {
                if (i % 8 == 0) {
                    out.printf("R%02d:", i);
                }
                out.print("\t" + Reg[i]);
                if((i+1) % 8 == 0) {
                    out.println();
                }
            }
            out.println();

            out.println("Data");
            for(int i=0; i<memory.length; i++) {
                if (i % 8 == 0) {
                    out.printf("%3d:", memoryStartAddress + (i << 2));
                }
                out.print("\t" + memory[i]);
                if((i+1) % 8 == 0) {
                    out.println();
                }
            }
            out.println();

            cycle++;
        }
    }

    //category1
    private void J(int[] params) {
        int instrIndex = params[0];
        pc = instrIndex - 4;
    }

    private void JR(int[] params) {
        int rs = params[0];
        pc = Reg[rs] - 4;
    }

    private void BEQ(int[] params) {
        int rs = params[0];
        int rt = params[1];
        int offset = params[2];
        if(Reg[rs] == Reg[rt]) {
            pc = pc + offset;
        }
    }

    private void BLTZ(int[] params) {
        int rs = params[0];
        int offset = params[1];
        if (Reg[rs] < 0) {
            pc = pc + offset;
        }
    }

    private void BGTZ(int[] params) {
        int rs = params[0];
        int offset = params[1];
        if (Reg[rs] > 0) {
            pc = pc + offset;
        }
    }

    private void BREAK(int[] params) {
        isRunning = false;
    }

    private void SW(int[] params) {
        int rt = params[0];
        int offset = params[1];
        int base = params[2];
        memory[(Reg[base]+offset-memoryStartAddress)>>2] = Reg[rt];
    }

    private void LW(int[] params) {
        int rt = params[0];
        int offset = params[1];
        int base = params[2];
        Reg[rt] = memory[(Reg[base]+offset-memoryStartAddress)>>2];
    }

    private void SLL(int[] params) {
        int rd = params[0];
        int rt = params[1];
        int sa = params[2];
        Reg[rd] = Reg[rt] << sa;
    }

    private void SRL(int[] params) {
        int rd = params[0];
        int rt = params[1];
        int sa = params[2];
        Reg[rd] = Reg[rt] >>> sa;
    }

    private void SRA(int[] params) {
        int rd = params[0];
        int rt = params[1];
        int sa = params[2];
        Reg[rd] = Reg[rt] >>> sa;
    }

    private void NOP(int[] params) {}


    //category2
    private void ADD(int[] params) {
        int rd = params[0];
        int rs = params[1];
        int rt = params[2];
        Reg[rd] = Reg[rs] + Reg[rt];
    }

    private void SUB(int[] params) {
        int rd = params[0];
        int rs = params[1];
        int rt = params[2];
        Reg[rd] = Reg[rs] - Reg[rt];
    }

    private void MUL(int[] params) {
        int rd = params[0];
        int rs = params[1];
        int rt = params[2];
        Reg[rd] = Reg[rs] * Reg[rt];
    }

    private void AND(int[] params) {
        int rd = params[0];
        int rs = params[1];
        int rt = params[2];
        Reg[rd] = Reg[rs] & Reg[rt];
    }

    private void OR(int[] params) {
        int rd = params[0];
        int rs = params[1];
        int rt = params[2];
        Reg[rd] = Reg[rs] | Reg[rt];
    }

    private void XOR(int[] params) {
        int rd = params[0];
        int rs = params[1];
        int rt = params[2];
        Reg[rd] = Reg[rs] ^ Reg[rt];
    }

    private void NOR(int[] params) {
        int rd = params[0];
        int rs = params[1];
        int rt = params[2];
        Reg[rd] = ~(Reg[rs] | Reg[rt]);
    }

    private void SLT(int[] params) {
        int rd = params[0];
        int rs = params[1];
        int rt = params[2];
        Reg[rd] = (Reg[rs] < Reg[rt]) ? 1 : 0;
    }

    private void ADDI(int[] params) {
        int rt = params[0];
        int rs = params[1];
        int immediate = params[2];
        Reg[rt] = Reg[rs] + immediate;
    }

    private void ANDI(int[] params) {
        int rt = params[0];
        int rs = params[1];
        int immediate = params[2];
        Reg[rt] = Reg[rs] & immediate;
    }

    private void ORI(int[] params) {
        int rt = params[0];
        int rs = params[1];
        int immediate = params[2];
        Reg[rt] = Reg[rs] | immediate;
    }

    private void XORI(int[] params) {
        int rt = params[0];
        int rs = params[1];
        int immediate = params[2];
        Reg[rt] = ~(Reg[rs] | immediate);
    }
}