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
            simulator.addDataBinary(tempLine);
            dataList.add(tempData);
        }
        simulator.setData(dataList.toArray());

        String dir = file.getAbsoluteFile().getParent();
        PrintStream outfileDisassembly = new PrintStream((dir+"/disassembly.txt"));
        PrintStream outfileSimulation = new PrintStream((dir+"/simulation.txt"));

        simulator.printInstructions(outfileDisassembly);
        simulator.run(outfileSimulation);

        outfileDisassembly.close();
        outfileSimulation.close();

        reader.close();
    }
}

//read file by lines
class ReaderByLines {
    private final FileReader reader;
    private final BufferedReader bufferedReader;

    public ReaderByLines(File file) throws FileNotFoundException {
        reader = new FileReader(file);
        bufferedReader = new BufferedReader(reader);
    }

    //return a line text
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

    public int data;

    public Instruction() {
        binary = null;
        name = null;
        format = null;
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
//                    System.out.print("Can't find this operator!");
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
//                    System.out.print("Can't find this operator!");
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



    //translate data with symbolicBit
    public int translateData(String line) {
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
    private int[] Reg;
    private int[] memory;
    private int startAddress;
    private int memoryStartAddress;

    // program count:store the address of the next instruction
    private int pc;
    private int cycle;
    private boolean isRunning;

    // obtain execution function by Reflection
    private Class<?> operateFunction = Simulator.class;

    public ArrayList<String> dataBinary;


    private ArrayList<Instruction> instructions;

    public Simulator(int startAddress) {
        this.startAddress = startAddress;
        this.cycle = 0;

        instructions = new ArrayList<Instruction>();
        dataBinary = new ArrayList<String>();
        isRunning = false;

        this.Reg = new int[32];
        for(int i=0;i<32;i++) {
            Reg[i] = 0;
        }
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addDataBinary(String line) {
        dataBinary.add(line);
    }

    public void setData(Object[] dataList){
        memory = new int[dataList.length];
        for(int i=0; i<dataList.length; i++) {
            memory[i] = (int)dataList[i];
        }
    }

    public void printInstructions(PrintStream out) {
        Iterator<Instruction> iter = instructions.iterator();
        int address = startAddress;
        while(iter.hasNext()) {
            Instruction instruction = iter.next();
            out.print(instruction.binary+"\t"+address+"\t"+instruction);
            out.println();
            address += 4;
        }

        for(int i=0; i<memory.length; i++) {
            out.print(dataBinary.get(i)+"\t"+address+"\t"+memory[i]);
            out.println();
            address += 4;
        }
    }

    public void run(PrintStream out) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        cycle = 1;
        pc = startAddress;
        isRunning = true;
        memoryStartAddress = startAddress + (instructions.size() << 2);

        //execute until break, break symbol: isRunning is false
        while(isRunning) {
            int index = (pc - startAddress) >> 2;
            Instruction currentInstruction = instructions.get(index);

            try {
                Method method = operateFunction.getDeclaredMethod(currentInstruction.name, int[].class);
                method.invoke(this, currentInstruction.orderedParameters);
            } catch (Exception e) {
                e.printStackTrace();
            }

            out.println("--------------------");
            out.printf("Cycle:%d \t %d \t", cycle, pc);
            out.println(currentInstruction);
            out.println();

            out.println("Registers");
            for(int i=0; i<Reg.length; i++) {
                if (i % 8 == 0) {
                    out.printf("\nR%02d:", i);
                }
                out.print("\t" + Reg[i]);
            }
            out.println();
            out.println();

            out.println("Data");
            for(int i=0; i<memory.length; i++) {
                if (i % 8 == 0) {
                    out.printf("\n%3d:", memoryStartAddress + (i << 2));
                }
                out.print("\t" + memory[i]);
            }
            out.println();
            out.println();
            cycle += 1;
            pc += 4;

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
