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
package de.coreengine.network;

/**Class that handles network messenges
 *
 * @author Darius Dinger
 */
public class NetworkMessenger {
    
    static final String HANDSHAKE_BANNER = "handshake";
    static final String SEPERATOR = ";";
    
    static final String HANDSHAKE_ACCEPTED = "accepted";
    static final String HANDSHAKE_FULL = "full";
    static final String HANDSHAKE_WRONG_PASSWORD = "password";
    static final String HANDSHAKE_BANNED = "banned";
    static final String HANDSHAKE_NAME_NOT_AVAILABLE = "name";
    
    static final String KICKED_BANNER = "kicked";
    static final String BANNED_BANNER = "banned";
    
    static final String TIMEOUT_EXPIRED = KICKED_BANNER + SEPERATOR + "timeout";
    static final String STREAM_ENDED = KICKED_BANNER + SEPERATOR + "stream_ended";
    static final String HOSTER_CLOSED = KICKED_BANNER + SEPERATOR + "hoster_closed";
    static final String SERVER_CLOSED = KICKED_BANNER + SEPERATOR + "server_closed";
    
    static final String TAGGED_BANNER = "tagged";
    
    static final String JOINED_BANNER = "joined";
    static final String LEFT_BANNER = "left";
    
    /**Checking, if a handshake message of a client is acceptable
     * 
     * @param msg Handshake message from client
     * @param password Password of the server
     * @return Playername if password is correct else null
     */
    static String checkHandShakeMessage(String msg, String password){
        String[] args = msg.split(SEPERATOR);
        boolean accept = args[0].equals(HANDSHAKE_BANNER) && ((args.length > 2 &&  
                args[2].equals(password)) || password.equals(""));
        return accept ? args[1] : null;
    }
}
