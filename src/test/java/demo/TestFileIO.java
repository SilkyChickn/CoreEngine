package demo;

import io.github.suuirad.coreengine.asset.FileLoader;
import io.github.suuirad.coreengine.rendering.model.Color;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (c) 2019, Darius Dinger
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 *
 * @author Darius Dinger
 */
public class TestFileIO {
    
    private static final String BIN_FILE = "test.bin";
    private static final String TEXT_FILE = "test.txt";
    
    public static void main(String[] args){
        
        String[] data = FileLoader.getResource("res/TestFile", true);
        for(String d: data) System.out.println(d);
        if(true) return;
        
        //Schreiben testen
        try {
            String[] test = {"abc", "defg", "hi", "jklm", "nop", "qrst", "u", "vw", "xyz"};
            FileLoader.writeFile(TEXT_FILE, test);
            
            Color c = new Color();
            FileLoader.writeBinaryFile(BIN_FILE, c);
            
        } catch (IOException ex) {
            Logger.getLogger(TestFileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Lesen testen
        try {
            String[] test = FileLoader.readFile(TEXT_FILE, false);
            for(String line: test) System.out.println(line);
            
            Color c = FileLoader.readBinaryFile(BIN_FILE);
            System.out.println(c);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(TestFileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
