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
package de.coreengine.asset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**Class that can read and write files onto the drive
 *
 * @author Darius Dinger
 */
public class FileLoader {
    
    /**Reading a file from drive into a string array
     * 
     * @param path Path to the file relative to the application
     * @param lineBreak If true, the strings in the result array will have an \n at the end
     * @return String that contains the file content
     * @throws FileNotFoundException Throws if file could not be found
     * @throws IOException Throws if an I/O error occurs
     */
    public static String[] readFile(String path, boolean lineBreak) throws FileNotFoundException, IOException{
        String lb = lineBreak ? "\n" : "";
        
        //File source string list
        ArrayList<String> fileData = new ArrayList<>();
        
        //Create streams for reading file
        try (FileReader freader = new FileReader(path); 
                BufferedReader reader = new BufferedReader(freader)) {
            
            //Read lines from buffered reader
            String line = "";
            while((line = reader.readLine()) != null){
                fileData.add(line + lb);
            }
        }
        return fileData.toArray(new String[fileData.size()]);
    }
    
    /**Writes an string array ascii decoded into a file.
     * Adding line breaks ('\n') at the end of every line
     * 
     * @param path Path of the output file relative to the application
     * @param data Data to write (lines, strings)
     * @throws IOException Throws if lines cant be written
     */
    public static void writeFile(String path, String[] data) throws IOException{
        
        //Create streams for writing file
        try (FileWriter fwriter = new FileWriter(path);
                BufferedWriter writer = new BufferedWriter(fwriter)){
            
            //Write lines into streams
            for(String line: data){
                writer.write(line + '\n');
                writer.flush();
            }
        }
    }
    
    /**Reading a binary file from drive into an object
     * 
     * @param <T> Type of the object to read
     * @param path Path of the binary file to read
     * @return Readed object as T
     * @throws FileNotFoundException Throws if file could no be found
     * @throws IOException Throws if file could not be loaded
     * @throws ClassNotFoundException Throws if the objects class from the file could not be found
     * @throws ClassCastException Throws if object from file couldnt parsed into T
     */
    @SuppressWarnings("unchecked")
    public static <T> T readBinaryFile(String path) throws FileNotFoundException, IOException, 
            ClassNotFoundException, ClassCastException{
        
        //Define object to read in
        T object = null;
        
        //Create streams for reading bianry file
        try(FileInputStream fis = new FileInputStream(path); 
                ObjectInputStream ois = new ObjectInputStream(fis)){
            
            //Read objects from object input stream
            object = (T) ois.readObject();
        }
        
        return object;
    }
    
    /**Writes an object binary decoded into a file. 
     * The object class has to implement the serializable interface!
     * 
     * @param <T> Type of the object to write
     * @param path Path of the output file relative to application
     * @param data Data to write (object of type T)
     * @throws IOException Throws if object cant be written
     */
    public static <T> void writeBinaryFile(String path, T data) throws IOException{
        
        //Declare file writer and output streams
        try (FileOutputStream fos = new FileOutputStream(path); 
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            //Write objects into streams
            oos.writeObject(data);
            oos.flush();
        }
    }
    
    /**Reading a ascii resource from resources into a string array
     * 
     * @param path Path to the resource
     * @param lineBreak If true, the strings in the result array will have an \n at the end
     * @return String that contains the resource content (ascii)
     */
    public static String[] getResource(String path, boolean lineBreak){
        String lb = lineBreak ? "\n" : "";
        
        LinkedList<String> data = new LinkedList<>();
        
        Scanner sc = new Scanner(FileLoader.class.getClassLoader().
                getResourceAsStream(path), "UTF-8");
        sc.useDelimiter("\n");
        
        while(sc.hasNext()){
            data.add(sc.next().replace("\r", "") + lb);
        }
        
        return data.toArray(new String[data.size()]);
    }
}
