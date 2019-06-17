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

import java.util.HashMap;
import java.util.LinkedList;

/**Class that handles syncronizing methods
 *
 * @author Darius Dinger
 */
class MessageManager {
    
    //All new syncs
    private static final LinkedList<String> MSG_LIST = new LinkedList<>();
    private static final HashMap<String, String> TAGGED_MAP = new HashMap<>();
    
    /**Reloading syncrozed messanges from the server
     */
    static void reloadMsgs(){
        MSG_LIST.clear();
        TAGGED_MAP.clear();
        
        //Getting all updates
        switch (NetworkManager.getState()){
            case SINGLEPLAYER: break;
            case HOSTER:
                TCPClient.getMsgList().get(MSG_LIST);
                TCPClient.getMsgList().clear();
            case DEDICATED_SERVER:
                for(TCPServerClient t: TCPServer.getClients()){
                    if(t != null){
                        t.getMsgList().get(MSG_LIST);
                        t.getMsgList().clear();
                    }
                }
                break;
            case CLIENT:
                TCPClient.getMsgList().get(MSG_LIST);
                TCPClient.getMsgList().clear();
                break;
        }
        
        //Iterate syncs of ACTUAL_SYNCS and adding to syncronizer
        MSG_LIST.forEach((msg) -> {
            String[] args = msg.split(NetworkMessenger.SEPERATOR);
            if(args.length > 2 && args[0].equals(NetworkMessenger.TAGGED_BANNER)){
                
                //Get tag and data of sync
                String tag = args[1];
                StringBuilder data = new StringBuilder();
                
                //Adding sync to syncs
                for (int i = 2; i < args.length; i++) data.append(args[i]);
                TAGGED_MAP.put(tag, data.toString());
            }
        });
    }
    
    /**Send tagged data to the network
     * 
     * @param tag Tag of the data
     * @param data Data to tag
     */
    static void sendTaggedData(String tag, String data){
        String msg = NetworkMessenger.TAGGED_BANNER + NetworkMessenger.SEPERATOR + 
                tag + NetworkMessenger.SEPERATOR + data;
        NetworkManager.sendToNetwork(msg);
    }
    
    /**Syncronize data with network
     * 
     * @param tag Tag of the syncronized data
     * @return Syncronized data or null, if no update exist
     */
    static String getTaggedData(String tag){
        return TAGGED_MAP.get(tag);
    }
}
