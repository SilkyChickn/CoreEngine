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

import de.coreengine.system.Game;
import de.coreengine.system.PlayerGameObject;
import de.coreengine.util.BlockedList;
import de.coreengine.util.Configuration;
import de.coreengine.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

/**Client class for a tcp connection
 *
 * @author Darius Dinger
 */
public class TCPClient implements Runnable{
    private static final int TIMEOUT = 
            Configuration.getValuei("CLIENT_TIMEOUT");
    private static final int HANDSHAKE_TIMEOUT = 
            Configuration.getValuei("HANDSHAKE_TIMEOUT");
    
    //Class to create player instance from
    private static Class<? extends PlayerGameObject> playerClass;
    
    //Map of all players
    private static HashMap<String, PlayerGameObject> players = new HashMap<>();
    
    //Own player
    private static PlayerGameObject player;
    private static String playerName;
    
    /**Result values for a tcp handshake
     */
    public enum HandshakeResult {
        ERROR, ACCEPTED, WRONG_PASSWORD, FULL, BANNED, NAME_TAKEN
    }
    
    //Instance of running client
    private static TCPClient instance;
    
    //Runnig client data
    private static Socket socket;
    private static BufferedReader reader;
    private static PrintWriter writer;
    
    //Clients message queues
    private static BlockedList<String> msgList;
    
    /**Connecting to a server by handle out a tcp handshake.
     * 
     * @param address Address of the server to connect
     * @param port Port of the server to connect
     * @param password Password of the server to connect or "" for no password
     * @param playerName Player name on the server
     * @return Handshake result
     */
    static HandshakeResult connect(InetAddress address, int port, String password, 
            String playerName, Class<? extends PlayerGameObject> playerClass){
        
        TCPClient.msgList = new BlockedList<>();
        TCPClient.players = new HashMap<>();
        TCPClient.playerClass = playerClass;
        TCPClient.playerName = playerName;
        
        try {
            
            //Creating socket
            socket = new Socket(address, port);
            
            //Setting socket timeout
            socket.setSoTimeout(HANDSHAKE_TIMEOUT);
            
            //Bind reader and writer
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
            
            //Request handshake
            String handshakeMsg = Protocol.HANDSHAKE_BANNER +
                    Protocol.SEPERATOR + playerName +
                    Protocol.SEPERATOR + password;
            writer.println(handshakeMsg);
            writer.flush();
            
            //Await response
            try{
                String answer = reader.readLine();
                String[] args = answer.split(Protocol.SEPERATOR);
                
                switch (args[0]) {
                    case Protocol.HANDSHAKE_ACCEPTED:
                        
                        //Setting timeout
                        socket.setSoTimeout(TIMEOUT);
                        
                        return HandshakeResult.ACCEPTED;
                    case Protocol.HANDSHAKE_FULL:
                        return HandshakeResult.FULL;
                    case Protocol.HANDSHAKE_WRONG_PASSWORD:
                        return HandshakeResult.WRONG_PASSWORD;
                    case Protocol.HANDSHAKE_BANNED:
                        return HandshakeResult.BANNED;
                    case Protocol.HANDSHAKE_NAME_NOT_AVAILABLE:
                        return HandshakeResult.NAME_TAKEN;
                    default:
                        Logger.warn("Response not readable", "The response of the "
                                + "server doenst match any expected!");
                        return HandshakeResult.ERROR;
                }
            }catch (IOException ex){
                Logger.warn("Error by tcp handshake", "The timeout of " + 
                        TIMEOUT + " expired without response!");
                return HandshakeResult.ERROR;
            }
            
        } catch (NullPointerException ex) {
            Logger.warn("Error by creating tcp socket", "The server address " + 
                    address + " is null!");
            return HandshakeResult.ERROR;
        } catch (IllegalArgumentException ex) {
            Logger.warn("Error by creating tcp socket", "The server port " + 
                    port + " is outside the specified range!");
            return HandshakeResult.ERROR;
        } catch (SecurityException ex) {
            Logger.warn("Error by creating tcp socket", "The security manager " + 
                    " does not allow the connection!");
            return HandshakeResult.ERROR;
        } catch (IOException ex) {
            Logger.warn("Error by creating tcp socket", "An IO Error occurs!");
            return HandshakeResult.ERROR;
        }
    }
    
    /**@return List of all msgs from the server
     */
    static BlockedList<String> getMsgList() {
        return msgList;
    }
    
    /**Sending a message to the server
     * 
     * @param msg Message to send to the server
     */
    static void sendToServer(String msg){
        writer.println(msg);
        writer.flush();
    }
    
    /**@return Is the client still connected to the server
     */
    public static boolean isRunning(){
        return !socket.isClosed();
    }
    
    /**@return Player name on the connected server
     */
    public static String getPlayerName() {
        return playerName;
    }
    
    /**Stopping connection to the server
     * 
     * @param message Message to send to server before close
     */
    public void stop(String message){
        
        //Sending exit message to server
        writer.println(message);
        writer.flush();
        
        if(player != null) player.onDisconnect();
        
        try {
            
            //Closing connection and reset state
            if(NetworkManager.getState() == NetworkManager.NetworkState.HOSTER
                    && TCPServer.isRunning()) TCPServer.stop(Protocol.HOSTER_CLOSED);
            socket.close();
            NetworkManager.setState(NetworkManager.NetworkState.SINGLEPLAYER);
        } catch (IOException ex) {
            Logger.warn("Error by closing connection", 
                    "The socket could not be closed clean!");
        }
    }
    
    /**@return List of all other players
     */
    public static HashMap<String, PlayerGameObject> getPlayers() {
        return players;
    }
    
    /**Joining client on the current scene
     */
    public static void join(){
        
        //Creating and start client instance
        instance = new TCPClient();
        new Thread(instance).start();
    }
    
    @Override
    public void run() {
        
        //Wait til network manager changes state
        while (NetworkManager.getState() == NetworkManager.NetworkState.SINGLEPLAYER) {
        }
        
        //Spawn player
        try {
            player = playerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.warn("Error by creating player", "Error by "
                    + "creating player game object!");
            stop(Protocol.LEFT_BANNER);
            return;
        }
        
        //Creating player in scene
        player.setup(playerName, true);
        Objects.requireNonNull(Game.getCurrentScene()).addGameObject(player);
        player.onJoin();

        String line;
        try {
            
            //Read from clients stream while alive
            while((line = reader.readLine()) != null){
                if(NetworkManager.getState() == NetworkManager.NetworkState.CLIENT &&
                        line.startsWith(Protocol.JOINED_BANNER)){
                    
                    //Player connecting to server
                    String[] args = line.split(Protocol.SEPERATOR);
                    
                    try {
                        PlayerGameObject newPlayer = playerClass.newInstance();
                        newPlayer.setup(args[1], false);
                        players.put(args[1], newPlayer);
                        Game.getCurrentScene().addGameObject(newPlayer);
                        newPlayer.onJoin();
                    } catch (IllegalAccessException | InstantiationException ex) {
                        Logger.warn("Error by creating player", 
                                "The game object for a joined player could not "
                                        + "be created!");
                    }

                }else if(NetworkManager.getState() == NetworkManager.NetworkState.CLIENT &&
                        line.startsWith(Protocol.LEFT_BANNER)){
                    
                    //Player disconnects from server
                    String[] args = line.split(Protocol.SEPERATOR);
                    players.get(args[1]).onDisconnect();
                    players.remove(args[1]);
                    
                }else msgList.add(line);
            }
            
            //Clients stream has ended
            Logger.warn("Stream ended", "The stream to the server has been "
                    + "ended!");
            stop(Protocol.LEFT_BANNER);
        } catch (IOException ex) {
            if(isRunning()){
                
                //Client timeout expired
                Logger.warn("Server timeout", "The servers timeout expired!");
                stop(Protocol.LEFT_BANNER);
            }
        }
    }
}
