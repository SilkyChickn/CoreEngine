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
package de.coreengine.network;

/**Abstract class for network events
 *
 * @author Darius Dinger
 */
public abstract class Event {
    
    //Tag of the event in the network
    private final String tag;
    
    //Has the event state changed since last sync
    private boolean hasEvent = false;
    
    /**@param tag Tag of the event in the network
     */
    public Event(String tag) {
        this.tag = tag;
    }
    
    /**Syncronize event with the network<br>
     * (Call every sync)
     */
    public void syncronize(){
        
        //If event has occured send event to server
        if(hasEvent){
            hasEvent = false;
            MessageManager.sendTaggedData(tag, event());
        }
        
        event(MessageManager.getTaggedData(tag));
    }
    
    /**Has an event occured
     */
    protected void eventOccured(){
        hasEvent = true;
    }
    
    /**Resetting the event<br>
     * (Call every frame, AFTER event gets processed)
     */
    public abstract void reset();
    
    //Event methods
    protected abstract void event(String event);
    protected abstract String event();
}
