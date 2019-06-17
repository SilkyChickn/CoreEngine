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

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**Linked list that is securedby a semaphore
 *
 * @author Darius Dinger
 * @param <T> Type of the list data
 */
public class BlockedList<T> {
    
    //List of data in this list
    private LinkedList<T> data = new LinkedList<>();
    private Semaphore semaphore = new Semaphore(1);
    
    /**@param str Adding message to list
     */
    public void add(T str){
        try {
            semaphore.acquire();
            data.add(str);
            semaphore.release();
        } catch (InterruptedException ex) {
            Logger.warn("Interrupted Exception", ex.getLocalizedMessage());
        }
    }
    
    /**Getting message from list and storing into out
     * 
     * @param out List to store messages
     */
    public void get(LinkedList<T> out){
        try {
            semaphore.acquire();
            out.addAll(data);
            semaphore.release();
        } catch (InterruptedException ex) {
            Logger.warn("Interrupted Exception", ex.getLocalizedMessage());
        }
    }
    
    /**Clearing list
     */
    public void clear(){
        try {
            semaphore.acquire();
            data.clear();
            semaphore.release();
        } catch (InterruptedException ex) {
            Logger.warn("Interrupted Exception", ex.getLocalizedMessage());
        }
    }
}
