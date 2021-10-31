# Computer Architecture

The repository contains the homework of computer architecture : creating a simple MIPS simulator and Pipeline simulator.

## Simple MIPS Simulator

Task demand:

1. Load a specified MIPS text file1 and generate the assembly code equivalent to the input file
   (disassembler). Please see the sample input file and disassembly output.
2. Generate the instruction-by-instruction simulation of the MIPS code (simulator). It should also
   produce/print the contents of registers and data memories after execution of each instruction.
   Please see the sample simulation output file.

Compile to produce an executable:

```java
javac MIPSsim.java
```

Execute 

```java
java MIPSsim sample.txt
```

Execute to generate disassembly and simulation files and test with correct/provided ones  

```
/MIPSsim inputfilename.txt or java MIPSsim inputfilename.txt
diff–w –B disassembly.txt sample_disassembly.txt
diff–w –B simulation.txt sample_simulation.txt
```

## Pipeline Simulator

