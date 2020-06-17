/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sic_xe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

/**
 *
 * @author AlyBarakat
 */
public class SIC_XE {

        //Files declarations
        static File code = new File("srcCode.txt");
        static File intermdiate = new File("intermdiate.txt");

        //SrcLine variables
        static ArrayList label = new ArrayList();
        static ArrayList opCode = new ArrayList();
        static ArrayList operand = new ArrayList();
        static ArrayList address = new ArrayList();
        
        static ArrayList firstFormat = new ArrayList();
        static ArrayList secondFormat = new ArrayList();
        static ArrayList thirdFormat = new ArrayList();
        static ArrayList fourthFormat = new ArrayList();
        
        
        //SymbolTable and OpCodeTable 
        static Hashtable<String,String> symTab = new Hashtable<>();
        
        //Lietral Table
        static Hashtable<String,String> litTab = new Hashtable<>();
        
        static int LTORG = 0;
        
        static String base = new String();
        
        

        //record parsers
        static ArrayList record = new ArrayList();
        static ArrayList recordAddress = new ArrayList();

        //counters
        static int startingAdd;
        static int EndingAdd;
        static int locCtr;
        static int progLength;

        //flags
        static int duplicateFlag = 0;
        static int InvalidOpCodeError = 0;
    
    SIC_XE(){
        
        firstFormat.add("FIX");
        firstFormat.add("FLOAT");
        firstFormat.add("HIO");
        firstFormat.add("SIO");
        firstFormat.add("TIO");
        
        secondFormat.add("ADDR");
        secondFormat.add("CLEAR");
        secondFormat.add("COMPR");
        secondFormat.add("DIVR");
        secondFormat.add("MULR");
        secondFormat.add("RMO");
        secondFormat.add("SHIFTL");
        secondFormat.add("SHIFTR");
        secondFormat.add("SUBR");
        secondFormat.add("SVC");
        secondFormat.add("TIXR");
        
        thirdFormat.add("ADD");
        thirdFormat.add("ADDF");
        thirdFormat.add("AND");
        thirdFormat.add("COMP");
        thirdFormat.add("COMPF");
        thirdFormat.add("DIV");
        thirdFormat.add("DIVF");
        thirdFormat.add("J");
        thirdFormat.add("JEQ");
        thirdFormat.add("JGT");
        thirdFormat.add("JLT");
        thirdFormat.add("JSUB");
        thirdFormat.add("LDA");
        thirdFormat.add("LDB");
        thirdFormat.add("LDCH");
        thirdFormat.add("LDF");
        thirdFormat.add("LDL");
        thirdFormat.add("LDS");
        thirdFormat.add("LDT");
        thirdFormat.add("LDX");
        thirdFormat.add("LPS");
        thirdFormat.add("MUL");
        thirdFormat.add("MULF");
        thirdFormat.add("OR");
        thirdFormat.add("RD");
        thirdFormat.add("RSUB");
        thirdFormat.add("SSK");
        thirdFormat.add("STA");
        thirdFormat.add("STB");
        thirdFormat.add("STCH");
        thirdFormat.add("STF");
        thirdFormat.add("STI");
        thirdFormat.add("STL");
        thirdFormat.add("STS");
        thirdFormat.add("STSW");
        thirdFormat.add("STT");
        thirdFormat.add("STX");
        thirdFormat.add("SUB");
        thirdFormat.add("SUBF");
        thirdFormat.add("TD");
        thirdFormat.add("TIX");       
        thirdFormat.add("WD"); 

    }
    
    public void pass1() throws FileNotFoundException, IOException
    {
        //i: file line pointer
        int i = 0;
        Scanner s = new Scanner(code);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("intermdiate.txt"))) {
            String x = s.nextLine();
            String[] line = x.split("\t",3);
            label.add(line[0]);
            opCode.add(line[1]);
            operand.add(line[2]);
            
            //Start line check  
            if("START".equals((String)opCode.get(i)))
            {
                startingAdd = Integer.parseInt((String)operand.get(i),16);
                locCtr = startingAdd;
                writer.write((String)label.get(i) + "\t" +(String) opCode.get(i) + "\t" +(String) operand.get(i) + "\t" + Integer.toHexString(locCtr) + "\n");
                address.add(Integer.toHexString(locCtr));
                //DEBUG
                System.out.println((String) label.get(i) + "\t" + (String) opCode.get(i) + "\t" + (String) operand.get(i) + "\t" + Integer.toHexString(locCtr) );
                //locCtr -= 3;
            }
            else
            {
                locCtr = 0;
            }
            i++;
            
            //while file not empty
            while(s.hasNextLine())
            {    
                int locCtrP1 = locCtr;
                
                x = s.nextLine();
                line = x.split("\t",3);
                label.add(line[0]);
                opCode.add(line[1]);
                operand.add(line[2]);
                
                //store literals as keys in literal table
                if(((String) operand.get(i)).contains("="))
                {
                    String lit = "";
                    lit = ((String) operand.get(i)).substring(1, ((String) operand.get(i)).length());
                    LTORG++;
                    litTab.put(Integer.toString(LTORG) ,lit );       
                }
                
                //check for not last line of code
                if(!"END".equals((String)opCode.get(i)))
                {
                    //temp for alpahbet check
                    String temp = (String)label.get(i);
                    //check for a valid symbol in label field
                    if(temp.matches("^[a-zA-Z]*$"))
                    {
                        //check for symbol table reocc
                        if(symTab.containsKey((String) label.get(i)))
                        {
                            duplicateFlag = 1;
                            System.out.println("ERROR: Duplicate symbol " + (String) label.get(i));
                            break;
                        }
                        else
                        {
                            //add value to symTable [label,LocationCounter]
                            symTab.put((String) label.get(i), Integer.toHexString(locCtrP1));
                        }
                    }
                    //search opTable for opCode, WORD, BYTE, RESW, RESB, format1,2,3,4
                    if("WORD".equals((String)opCode.get(i) ) || thirdFormat.contains((String) opCode.get(i)))
                    {
                        //word or 3rd format
                        locCtr += 3;   
                    }
                    else if(secondFormat.contains((String) opCode.get(i)))
                    {
                        //2nd format
                        locCtr += 2;
                    }
                    else if( ((String) opCode.get(i)).charAt(0) == '+')
                    {
                        //format 4
                        locCtr += 4;                    
                    }
                    else if("RESW".equals((String)opCode.get(i)))
                    {   
                        int op = Integer.parseInt((String)operand.get(i));
                        locCtr += 3 * op;
                    }
                    else if("RESB".equals((String)opCode.get(i)))
                    {
                        int op = Integer.parseInt((String)operand.get(i));
                        locCtr += op;
                    }
                    else if("BYTE".equals((String)opCode.get(i)))
                    {
                        String op = (String)operand.get(i);
                        if(op.contains("C'"))
                        {
                            locCtr += op.length()-3;
                        }
                        else if(op.contains("X'"))
                        {
                            String hex = "";
                            hex = op.substring(2, op.length()-2); 
                            
                            locCtr += (hex.length() + 1)/2;
                        }
                        else
                        {
                            locCtr += Integer.parseInt((String)operand.get(i));
                        }
                    }
                    else if("BASE".equals((String) opCode.get(i)))
                    {
                        //save base label
                        base = (String) operand.get(i);
                    }
                    else if("LTORG".equals((String) opCode.get(i)))
                    {
                        for(int j = 1; j <=LTORG; j++)
                        {
                            String holder  = litTab.get(Integer.toString(j));
                            if(holder.contains("C'"))
                            {
                                locCtr += holder.length()-3;
                            }
                            else if(holder.contains("X'"))
                            {
                                String hex = "";
                                hex = holder.substring(2, holder.length()-2); 
                                locCtr += (hex.length() + 1)/2;
                            }
                            
                            litTab.put(litTab.get(Integer.toString(j)),decimalToHex(locCtr,4));
                            litTab.remove(Integer.toString(j));
                        }
                        LTORG = 0;                
                    }
                    else if("EQU".equals((String) opCode.get(i)))
                    {
                        String[] oper = ((String)operand.get(i)).split(" ");
                        int value = 0;
                        int signP = 1;
                        for(int m = 0; m<oper.length ; m++)
                        {
                            if(symTab.containsKey(oper[m]))
                            {
                                if (signP ==  1)
                                    value += Integer.parseInt(symTab.get(oper[m]) , 16);
                                else
                                    value -= Integer.parseInt(symTab.get(oper[m]) , 16);
                                             
                            }
                            else if(oper[m].matches("^[0-9]*$"))
                            {
                                if (signP == 1)
                                    value += Integer.parseInt(oper[m]);
                                else
                                    value -= Integer.parseInt(oper[m]);
                            }
                            else if(oper[m].equals("+"))
                            {
                                signP = 1;
                            }
                            else if(oper[m].equals("-"))
                            {
                                signP = 0;
                            }
                            else if(oper[m].equals("*"))
                            {
                                value = locCtrP1;
                                break;
                            }     
                        }
                    //DEBUG
                    System.out.println((String) label.get(i) + "\t" + (String) opCode.get(i) + "\t" + (String) operand.get(i) + "\t" + decimalToHex(value,4));
                    address.add(decimalToHex(value,4));
                    //Write in file(intermediate)
                    writer.write(label.get(i) + "\t" + opCode.get(i) + "\t" + operand.get(i) + "\t" + decimalToHex(value,4) + "\n");
                    i++;
                    continue;
                    }
                    else
                    {
                        InvalidOpCodeError =  1;            
                        System.out.println("ERROR: Invalid operation code" + (String)opCode.get(i));
                        break;
                    }//End if opcode search

                    //DEBUG
                    System.out.println((String) label.get(i) + "\t" + (String) opCode.get(i) + "\t" + (String) operand.get(i) + "\t" + Integer.toHexString(locCtrP1));
                    address.add(Integer.toHexString(locCtrP1));
                    //Write in file(intermediate)
                    writer.write(label.get(i) + "\t" + opCode.get(i) + "\t" + operand.get(i) + "\t" + Integer.toHexString(locCtrP1) + "\n");
                    i++;
                }//End if not "END"
                else
                {
                    //Write in file(intermediate)
                    writer.write(label.get(i) + "\t" + opCode.get(i) + "\t" + operand.get(i) + "\t" + Integer.toHexString(locCtr) + "\n");
                    address.add(Integer.toHexString(locCtr));
                    //DEBUG
                    System.out.println((String) label.get(i) + "\t" + (String) opCode.get(i) + "\t" + (String) operand.get(i) + "\t" + Integer.toHexString(locCtr));
                    
                    if(LTORG == 1)
                    {
                        for(int j = 1; j <=LTORG; j++)
                        {
                            String holder  = litTab.get(Integer.toString(j));
                            if(holder.contains("C'"))
                            {
                                locCtr += holder.length()-3;
                            }
                            else if(holder.contains("X'"))
                            {
                                String hex = "";
                                hex = holder.substring(2, holder.length()-2); 
                                locCtr += (hex.length() + 1)/2;
                            }
                                 
                            litTab.put(litTab.get(Integer.toString(j)),decimalToHex(locCtrP1,4));
                            litTab.remove(Integer.toString(j));
                        }
                    }

                    progLength = locCtrP1 - startingAdd;
                    System.out.println("Program length opa7 = " + Integer.toHexString(progLength));      
                }   //End if "END"             
            }   //End While has next line 
        }   //End try Buffer writer
        System.out.println(symTab.toString());
        System.out.println(litTab.toString());
        for(int k = 0; k < address.size() ; k++)
        {
            System.out.println(address.get(k));
        }
    }//End pass1
    
    
    public void pass2() throws FileNotFoundException, IOException
    { 
        //i: file line pointer
        int i = 0;
        //t: record pointer
        int t = 0;
        //objCode holder
        String holder = "";
        //Modification record holder
        String modifications = "";
        
        Scanner s = new Scanner(intermdiate);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("objectProgram.txt"))) {
            String x = s.nextLine();
            String[] line = x.split("\t",4);
            /*
            label.add(line[0]);
            opCode.add(line[1]);
            operand.add(line[2]);
            address.add(line[3]);
            */
            label.remove(i);
            opCode.remove(i);
            operand.remove(i);
            address.remove(i);
            
            label.add(i, line[0]);
            opCode.add(i, line[1]);
            operand.add(i, line[2]);
            address.add(i, line[3]);
            
   
            //Right START record to object program
            if("START".equals((String)opCode.get(i)))
            {
                writer.write("H" + "\t");
                writer.write((String)label.get(i) + "\t");
                writer.write(decimalToHex(startingAdd, 6)+ "\t");
                writer.write(decimalToHex(progLength, 6));
                i++;
            }        
            
            while(s.hasNextLine())
            {


                x = s.nextLine();
                line = x.split("\t",4);
                
                //label.remove(i);
                //opCode.remove(i);
                //operand.remove(i);
                //address.remove(i);

                //label.add(i, line[0]);
                //opCode.add(i, line[1]);
                //operand.add(i, line[2]);
                //address.add(i, line[3]);
                
                
                
                //check for not last line of code
                if(!"END".equals((String)opCode.get(i)))
                {
                    System.out.println("i = " + i + "   t = " + t +"    address = " + (String) address.get(i) +  "  address+1 = " + (String)address.get(i + 1));

                    //search opTable for opCode, WORD, BYTE, RESW, RESB, format1,2,3,4
                    if(firstFormat.contains((String) opCode.get(i)))
                    {             
                        holder = decimalToHex(opCodeGenerate((String) opCode.get(i)) , 2);
                        record.add(holder);
                        recordAddress.add(hexToHex((String)address.get(i), 6));
                        t++;
                        System.out.println(holder + "   1st Format");

                    }
                    else if(secondFormat.contains((String) opCode.get(i)))
                    {
                        String [] registers = ((String)operand.get(i)).split(",");
                        holder = decimalToHex(opCodeGenerate((String) opCode.get(i)) , 2);
                        holder = holder.concat(decimalToHex(regGenerate(registers[0]), 2));
                        if(registers.length > 1)
                        {
                            holder = holder.concat(decimalToHex(regGenerate(registers[1]), 2));
                            record.add(holder);
                            recordAddress.add(hexToHex((String)address.get(i), 6));
                            t++;
                        }
                        System.out.println(holder + "   2nd Format");
                    }
                    else if(thirdFormat.contains((String) opCode.get(i)))
                    {
                        holder = decimalToHex(opCodeGenerate((String) opCode.get(i)) , 2);
                       
                        //second digit
                        int n = Integer.parseInt(holder.substring(1), 16);
                        //third digit
                        int xbpe = 0;
                        int disp = 0;                    
                        String operandHolder = (String)operand.get(i);
                        

                        if(((String) operand.get(i)).contains("#"))
                        {
                            //Immediate
                            holder = holder.replace(holder.substring(1), decimalToHex(n+2, 1)); 
                            operandHolder = operandHolder.replace("#", "");
                        }
                        else if(((String) operand.get(i)).contains("@"))
                        {
                            //Indirect Addressing
                            holder = holder.replace(holder.substring(1), decimalToHex(n+1, 1));
                            operandHolder = operandHolder.replace("@", "");

                        }
                        else
                        {
                            //Simple
                            holder = holder.replace(holder.substring(1), decimalToHex(n+3, 1));
                            if(((String) operand.get(i)).contains("="))
                            {
                                operandHolder = ((String) operand.get(i)).substring(1, ((String) operand.get(i)).length());
                            }
                            
                        }

                        if(((String) operand.get(i)).contains(",X"))
                        {
                            //x = 1
                            xbpe += 8;
                            operandHolder = ((String) operand.get(i)).substring(0,((String) operand.get(i)).length()-2);
                        }
                        
                        
                        
                        //check for symbol reoccurance
                        if(symTab.containsKey(operandHolder))
                        {
                            //disp = operand address - pc+1 address
                            disp = Integer.parseInt(symTab.get(operandHolder),16) - Integer.parseInt(((String)address.get(i+1)) , 16);
                            
                            //System.out.println(Integer.parseInt(symTab.get(operandHolder), 16));
                            
                            //Base relative addressing
                            if(disp > 0xfff )
                            {
                                xbpe += 4;
                                //disp = operand address - base address
                                disp = Integer.parseInt(symTab.get(operandHolder),16) - Integer.parseInt(symTab.get(base),16);
                                System.out.println("base");
                            }
                            //PC relative addressing
                            else if(disp < 0xfff)
                            {
                                xbpe += 2;
                                System.out.println("PC");
                            }
                        }
                        else if(litTab.containsKey(operandHolder))
                        {
                            disp = Integer.parseInt(litTab.get(operandHolder),16) - Integer.parseInt(((String)address.get(i+1)) , 16);

                            //Base relative addressing (literal)
                            if(disp > 0xfff )
                            {
                                xbpe += 4;
                                //disp = operand address - base address
                                disp = Integer.parseInt(litTab.get(operandHolder),16) - Integer.parseInt(symTab.get(base),16);
                                System.out.println("base");
                            }
                            //PC relative addressing (literal)
                            else if(disp < 0xfff)
                            {
                                xbpe += 2;
                                //disp = operand address - pc+1 address
                                System.out.println("PC");
                            }
                        }
                        else if((operandHolder).matches("\\d+"))
                        {
                            disp = Integer.parseInt((operandHolder), 10); 
                            if(((String) operand.get(i)).matches("\\d+") )
                            {
                                int modAddress = Integer.parseInt(hexToHex((String)address.get(i), 6), 16) + 1;
                                modifications = modifications + "M\t" + hexToHex(Integer.toHexString(modAddress), 6) + "\t03\n";
                                System.out.println("LASTEDITS: " + modifications);
                            }
                        }
                        else
                        {
                            
                            System.out.println("check value format 3 !!     " + operandHolder);
                        }
                        holder = holder.concat(decimalToHex(xbpe, 1));
                        holder = holder.concat(decimalToHex(disp,3));
                        
                        if(i == 42)
                        {
                            System.out.println("xbpe = " + decimalToHex(xbpe, 1) + "    disp =" + decimalToHex(disp,3));
                        }
                        record.add(holder);
                        recordAddress.add(hexToHex((String)address.get(i), 6));
                        
                        System.out.println(holder + "   3rd format");
                        t++;
                    }

                    //fourth format
                    else if( ((String) opCode.get(i)).charAt(0) == '+')
                    {
                        /* Add to modification record */
                        int modAddress = Integer.parseInt(hexToHex((String)address.get(i), 6), 16) + 1;
                        modifications = modifications + "M\t" + hexToHex(Integer.toHexString(modAddress), 6) + "  05\n";
                        //Remove + from opCode
                        String InstructionType4opCode = (String) opCode.get(i);
                        InstructionType4opCode = InstructionType4opCode.substring(1,InstructionType4opCode.length());
                        holder = decimalToHex(opCodeGenerate(InstructionType4opCode) , 2);
                        //second digit
                        int n = Integer.parseInt(holder.substring(1), 16);
                        holder = holder.replace(holder.substring(1), decimalToHex(n+3, 1));

                        holder = holder.concat("1");
                        
                        System.out.println("operand: " + operand.get(i));

                        //check for symbol reoccurance
                        if(symTab.containsKey((String) operand.get(i)))
                        {
                            holder = holder.concat(hexToHex(symTab.get((String) operand.get(i)), 5));
                        }
                        else //Not a symbol
                        {
                            //Immediate Handling
                            if(((String) operand.get(i)).contains("#")){
                                //The Operand
                                String op = (String) operand.get(i);
                                //Decimal value after #
                                int dec = Integer.parseInt(op.substring(1, op.length()));
                                //Convert to 5 letter Hexadecimal and append to holder
                                holder += hexToHex(Integer.toHexString(dec), 5);
                            }else{
                                holder = "00000000";
                                System.out.println("Error format 4");
                            }
                        }
                        record.add(holder);
                        recordAddress.add(hexToHex((String)address.get(i), 6));
                        System.out.println(holder + "   4th format");
                        t++;
                    }
                    else if("WORD".equals((String)opCode.get(i) ) )
                    {
                        int n = Integer.parseInt((String)operand.get(i));
                        holder = decimalToHex(n, 6);
                        record.add(holder);
                        recordAddress.add(hexToHex((String)address.get(i), 6));
                        t++;
                    }
                    else if("BYTE".equals((String)opCode.get(i)))
                    {
                        //check for BYTE array
                        String op = (String)operand.get(i);
                        if(op.contains("C'"))
                        {
                            holder = decimalToHex(op.length()-3, 6);
                        }
                        else if(op.contains("X'"))
                        {
                            String hex = "";
                            hex = op.substring(2, op.length()-2);  
                            holder = decimalToHex(Integer.parseInt(hex,16), 6);
                        }
                        else
                        {
                            holder = decimalToHex(Integer.parseInt((String)operand.get(i)),6);
                        }
                        record.add(holder);
                        recordAddress.add(hexToHex((String)address.get(i), 6));
                        t++;
                    }
                    else if("BASE".equals((String) opCode.get(i)))
                    {
                        
                    }
                    else if("LTORG".equals((String) opCode.get(i)))
                    {
                        
                    }
                    else if("EQU".equals((String) opCode.get(i)))
                    {
                        
                    }
                    

                    //Right text record to object program
                    if("RESW".equals((String)opCode.get(i)) || "RESB".equals((String)opCode.get(i)) || t >= 10)
                    {
                        System.out.println("Writing");
                            try{
                                int start = Integer.parseInt((String)recordAddress.get(0) , 16);
                                int end =   Integer.parseInt((String)recordAddress.get(t-1) , 16);
                                int length = end - start;
                                writer.write("\nT" + "\t");
                                writer.write((String)recordAddress.get(0) + "\t");
                                writer.write(Integer.toHexString(length)+ "\t");
                                for(int j = 0; j < t; j++)
                                {
                                    writer.write((String)record.get(j) + "\t");
                                }
                                //Write Modification records
                            }catch(Exception e)
                            {
                              System.out.println("RESW jumped !!");
                            }

                            //DEBUG
                            //System.out.println("t =   " + t +"\ti = " + i );
                            //System.out.println("record size =   " + record.size() +"\trecordAddress size = " + recordAddress.size());
                            //System.out.println(record.toString());
                            //System.out.println(recordAddress.toString());

                            t = 0;
                            record = new ArrayList();
                            recordAddress = new ArrayList();
                    }
                    
                    //System.out.println(record.get(t-1) + "   " + recordAddress.get(t-1));
                    i++;
                }
                else
                {
                    InvalidOpCodeError =  1;
                    System.out.println("ERROR: Invalid operation code" + (String)opCode.get(i));
                    break;
                }//End
            }
            //Write Modification Record
            writer.write("\n" + modifications);
            //The End of the HTE record
            writer.write("E\t"+ hexToHex((String)address.get(0), 6));
        }
    }



    int opCodeGenerate(String instruction)
    {
        switch (instruction) {
            case "LDA":     return 0x00;
            case "STA":     return 0x0C;
            case "LDX":     return 0x04;
            case "ADD":     return 0x18;
            case "COMP":    return 0x28;
            case "TIX":     return 0x2C;
            case "JLT":     return 0x38;
            case "AND":     return 0x40;
            case "DIV":     return 0x24;
            case "J":       return 0x02;
            case "JEQ":     return 0x30;
            case "JGT":     return 0x34;
            case "JSUB":    return 0x48;
            case "LDCH":    return 0x50;
            case "LDL":     return 0x08;
            case "MUL":     return 0x20;
            case "OR":      return 0x44;
            case "RD":      return 0x0D8;
            case "RSUB":    return 0x4C;
            case "STCH":    return 0x54;
            case "STL":     return 0x14;
            case "STSW":    return 0xE8;
            case "STX":     return 0x10;
            case "SUB":     return 0x1C;
            case "TD":      return 0xE0;
            case "WD":      return 0xDC;
            case "LDT":     return 0x74;            
            default:        return 0;
        }
    }
    
    int regGenerate(String reg)
    {
        switch (reg){
            case "A": return 0;
            case "X": return 1;
            case "L": return 2;
            case "B": return 3;
            case "S": return 4;
            case "T": return 5;
            case "F": return 6;
            case "PC": return 8;
            case "SW": return 9;
        }
        return 50;
    }
 
    //Convert decimal to HEX string n bits
    public String decimalToHex(int x,int  n)
    {
        String hex = Integer.toHexString(x);
        String val = "";
        if(x < 0)
        {
            val = hex.substring(8-n, hex.length());
        }
        else
        {
           for(int i = 0; i < n-hex.length();i++)
            {
                val = val.concat("0");
            }
            val = val.concat(hex); 
        }
        
        return val.toUpperCase() ;
    }

    public String hexToHex(String hex, int n)
    {
        String val = "";
        for(int i = 0; i < n-hex.length(); i++)
        {
            val = val.concat("0");
        }
        val = val.concat(hex);
        return val.toUpperCase();
    }

    private void replace(String string, String string0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}