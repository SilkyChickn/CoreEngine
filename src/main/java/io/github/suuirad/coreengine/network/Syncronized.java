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
package io.github.suuirad.coreengine.network;

/**Class that represents an object that can be syncronized in a network
 *
 * @author Darius Dinger
 */
public abstract class Syncronized {
    
    //Tag of the syncronized object in thenetwork
    private final String tag;
    
    //Has the object state changed since last sync
    private boolean changed = false;
    
    /**@param tag Tag of the syncronized object in thenetwork
     */
    public Syncronized(String tag) {
        this.tag = tag;
    }
    
    /**Syncronizing object with the network
     */
    public void syncronize(){
        
        //If value has changed send sync to server
        if(changed){
            changed = false;
            MessageManager.sendTaggedData(tag, sync());
        }else{
            String sync = MessageManager.getTaggedData(tag);
            if(sync != null) sync(sync);
        }
    }
    
    /**Sync object at next sync
     */
    protected void change(){
        changed = true;
    }
    
    //Syncronize methods
    protected abstract void sync(String sync);
    protected abstract String sync();
}
