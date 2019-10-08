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

import de.coreengine.system.PlayerGameObject;
import de.coreengine.util.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**Class that manaes network stuff
 *
 * @author Darius Dinger
 */
public class NetworkManager {
    
    /**Network state of the game (one of):<br>
     * - DEDICATED_SERVER (Only server)<br>
     * - HOSTER (Hosting client)<br>
     * - CLIENT (Connected Client)<br>
     * - SINGLE (Singleplayer)<br>
     */
    public enum NetworkState {
        DEDICATED_SERVER, HOSTER, CLIENT, SINGLEPLAYER
    }
    
    //Current network state
    private static NetworkState state = NetworkState.SINGLEPLAYER;
    
    /**@return Current network state
     */
    public static NetworkState getState() {
        return state;
    }
    
    /**@param state New network state
     */
    static void setState(NetworkState state) {
        NetworkManager.state = state;
    }
    
    /**Setting up a dedicated server
     * 
     * @param port Port to bind the server to
     * @param maxPlayers Max players to join
     * @param password Password of the server
     * @param playerClass Class to instance when players join
     * @return Could the server be created
     */
    public static boolean host(int port, int maxPlayers, String password, 
            Class<? extends PlayerGameObject> playerClass){
        boolean result = TCPServer.start(port, maxPlayers, password, playerClass);
        if(result){
            state = NetworkState.DEDICATED_SERVER;
            return true;
        } else return false;
    }
    
    /**Joining a running server. 
     * 
     * @param ip Ip address of the server
     * @param port Port of the server
     * @param password Password of the server
     * @param name Player name on the server
     * @param playerClass Class to instance when players join
     * @return Joining handshake result
     */
    public static TCPClient.HandshakeResult join(String ip, int port, String password, 
            String name, Class<? extends PlayerGameObject> playerClass){
        try {
            TCPClient.HandshakeResult result =
                    TCPClient.connect(InetAddress.getByName(ip), port, 
                            password, name, playerClass);
            if(result == TCPClient.HandshakeResult.ACCEPTED){
                state = NetworkState.CLIENT;
            }
            return result;
        } catch (UnknownHostException ex) {
            Logger.warn("Error by getting host address", "The host address is "
                    + "unknown (" + ip + ")");
            return TCPClient.HandshakeResult.ERROR;
        }
    }
    
    /**Hosting and joining a server via localhost
     * 
     * @param port Port to bind the server to
     * @param maxPlayers Max players for te server
     * @param password Password of the server
     * @param playerClass Class to instance when players join
     * @param name Player name on the server
     * @return Could the server be created
     */
    public static boolean hostAndJoin(int port, int maxPlayers, String password, 
            String name, Class<? extends PlayerGameObject> playerClass){
        boolean result = TCPServer.start(port, maxPlayers, password, playerClass);
        if(result){
            state = NetworkState.HOSTER;
            try {
                TCPClient.connect(InetAddress.getByName("localhost"), port, 
                        password, name, playerClass);
            } catch (UnknownHostException ex) {
                Logger.warn("Unknown host exception", ex.getLocalizedMessage());
            }
            return true;
        } else {
            state = NetworkState.SINGLEPLAYER;
            return false;
        }
    }
    
    /**Updating the network manager
     */
    public static void sync(){
        
        //Sync
        MessageManager.reloadMsgs();
    }
    
    /**Sending a message to all other network clients
     * 
     * @param msg Message to send
     */
    static void sendToNetwork(String msg){
        switch(state){
            case SINGLEPLAYER: break;
            case HOSTER:
            case DEDICATED_SERVER:
                TCPServer.sendToAll(msg);
                break;
            case CLIENT:
                TCPClient.sendToServer(msg);
                break;
        }
    }
}
