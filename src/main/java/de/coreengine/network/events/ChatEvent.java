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
package de.coreengine.network.events;

import de.coreengine.network.Event;

/**Event class for chats
 *
 * @author Darius Dinger
 */
public class ChatEvent extends Event{

    private String msgToSend = null;
    private String msgGet = null;
    
    /**Creating chat event handler
     * 
     * @param tag Tag of the chat object in the network
     */
    public ChatEvent(String tag) {
        super(tag);
    }
    
    /**Sending a message to the network
     * 
     * @param msg Message to send
     */
    public void sendMessage(String msg){
        eventOccured();
        msgToSend = msg;
    }
    
    /**Getting last message from network or null, if no messsage came
     * 
     * @return Last message or null
     */
    public String getMessage(){
        return msgGet;
    }
    
    @Override
    protected void event(String event) {
        msgGet = event;
    }
    
    @Override
    protected String event() {
        return msgToSend;
    }

    @Override
    public void reset() {
        msgGet = null;
    }
}
