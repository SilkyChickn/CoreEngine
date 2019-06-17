/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Suuirad
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.coreengine.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**Class to manage logging
 *
 * @author Darius Dinger
 */
public class Logger {
    
    //Time stamp helper variables
    private static final String TIME_STAMP_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static final Date TIME_STAMP_DATE = new Date();
    private static final SimpleDateFormat TIME_STAMP_DATE_FORMATTER = 
            new SimpleDateFormat(TIME_STAMP_FORMAT);
    
    /**Prints and logging error message to console
     * 
     * @param header Error header
     * @param msg Error message
     */
    public static void err(String header, String msg){
        //System.err.println("|" + getTimeStamp() + "| E | [" + header + "] " + msg);
        
        try {
            throw new CoreEngineException(header + "\n" + msg);
        } catch (CoreEngineException ex) {
            ex.printStackTrace();
        }
    }
    
    /**Prints and logging warning message to console
     * 
     * @param header Warning header
     * @param msg Warning message
     */
    public static void warn(String header, String msg){
        System.out.println("|" + getTimeStamp() + "| W | [" + header + "] " + msg);
    }
    
    /**Prints and logging info message to console
     * 
     * @param header Info header
     * @param msg Info message
     */
    public static void info(String header, String msg){
        System.out.println("|" + getTimeStamp() + "| I | [" + header + "] " + msg);
    }
    
    /**@return Stamp with actual time and date
     */
    private static String getTimeStamp(){
        TIME_STAMP_DATE.setTime(System.currentTimeMillis());
        return TIME_STAMP_DATE_FORMATTER.format(TIME_STAMP_DATE);
    }
}
