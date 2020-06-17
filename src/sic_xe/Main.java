package sic_xe;


import java.io.IOException;

/**
 *
 * @author AlyBarakat
 */
public class Main {

    
    public static void main(String[] args) throws IOException {
        
        SIC_XE assembler = new SIC_XE();
        assembler.pass1();
        assembler.pass2();
        
    }
    
}