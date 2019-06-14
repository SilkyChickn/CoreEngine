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

import de.coreengine.system.Game;
import de.coreengine.system.PlayerGameObject;
import de.coreengine.util.Configuration;
import de.coreengine.util.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.LinkedList;
import java.util.List;

/**Server class for a tcp connection
 *
 * @author Darius Dinger
 */
public class TCPServer implements Runnable{
    private static final int TIMEOUT = 
            Configuration.getValuei("SERVER_TIMEOUT");
    private static final int HANDSHAKE_TIMEOUT = 
            Configuration.getValuei("HANDSHAKE_TIMEOUT");
    
    //Server instance
    private static TCPServer instance;
    private static TCPServerClient[] clients;
    
    //Server data
    private static String password;
    private static int maxPlayers;
    private static ServerSocket socket;
    
    //List of all banned clients
    private static List<InetAddress> bannedAddresses = new LinkedList<>();
    
    //Class to create player instance from
    private static Class<? extends PlayerGameObject> playerClass;
    
    /**Starting a new TCP Server
     * 
     * @param port Port to listen for clients
     * @param maxPlayers Max players for the server
     * @param password Password for the server or "" for no password
     * @return Could the server be successfull started
     */
    static boolean start(int port, int maxPlayers, String password, 
            Class<? extends PlayerGameObject> playerClass){
        try {
            
            TCPServer.password = password;
            TCPServer.maxPlayers = maxPlayers;
            TCPServer.clients = new TCPServerClient[maxPlayers];
            TCPServer.playerClass = playerClass;
            
            //Create server socket
            socket = new ServerSocket(port);
            socket.setSoTimeout(0);
            
            //Creating server instance
            instance = new TCPServer();
            new Thread(instance).start();
            
            //Print info
            Logger.info("Server started", "Server started and listening to " +
                    socket.getInetAddress().getHostName() + ":" + 
                    socket.getLocalPort() + "...");
            
            return true;
        } catch (IllegalArgumentException ex) {
            Logger.warn("Error by creating tcp socket", "The port " + port +
                    " is out of the specified range!");
            return false;
        } catch (SecurityException ex) {
            Logger.warn("Error by creating tcp socket", "The security manager " + 
                    " does not allow the connection!");
            return false;
        } catch (IOException ex) {
            Logger.warn("Error by creating tcp socket", "An IO Exception occurs!");
            return false;
        }
    }
    
    /**@return Is the server full
     */
    public static boolean isFull(){
        for(TCPServerClient c: clients) if(c == null) return false;
        return true;
    }
    
    /**Adding new server client to the clients and starting his thread
     * 
     * @param client Client to add to the server
     */
    private static void addClient(BufferedReader reader, PrintWriter writer, 
            Socket socket, String name){
        for(int i = 0; i < maxPlayers; i++){
            if(clients[i] == null){
                writer.println(NetworkMessenger.HANDSHAKE_ACCEPTED);
                writer.flush();
                
                try {
                    socket.setSoTimeout(TIMEOUT);
                } catch (SocketException ex) {
                    Logger.warn("Error by setting timeout", "The timeout for a "
                            + "client could not be setted!");
                    writer.println(NetworkMessenger.KICKED_BANNER + 
                    NetworkMessenger.SEPERATOR + "Server error");
                    writer.flush();
                }
                
                PlayerGameObject player = null;
                if(NetworkManager.getState() != NetworkManager.
                        NetworkState.HOSTER || i != 0){
                    try {
                        player = playerClass.newInstance();
                        player.setup(name, false);
                        Game.getCurrentScene().addGameObject(player);
                    } catch (IllegalAccessException | InstantiationException ex) {
                        Logger.warn("Error adding player", "Error by instancing "
                                + "players object in the server!");
                        writer.println(NetworkMessenger.KICKED_BANNER + 
                        NetworkMessenger.SEPERATOR + "Server error");
                        writer.flush();
                        return;
                    }
                }
                
                TCPServerClient client = new TCPServerClient(reader, writer, 
                        socket, name, player);
                
                sendToAll(NetworkMessenger.JOINED_BANNER + 
                        NetworkMessenger.SEPERATOR + client.getPrefix());
                
                for(TCPServerClient t: clients){
                    if(t != null){
                        writer.println(NetworkMessenger.JOINED_BANNER + 
                                NetworkMessenger.SEPERATOR + t.getPrefix());
                        writer.flush();
                    }
                }
                
                clients[i] = client;
                new Thread(client).start();
                break;
            }
        }
    }
    
    /**Removing client from list
     * 
     * @param client Client to remove
     */
    static void removeClient(TCPServerClient client){
        for(int i = 0; i < clients.length; i++){
            if(clients[i] == client){
                clients[i] = null;
            }
        }
    }
    
    /**@return How many clients are currently connected to the server
     */
    public static int clientCount(){
        int c = 0;
        for(TCPServerClient t: clients) if(t != null) c++;
        return c;
    }
    
    /**Banning client from the server
     * 
     * @param id Client id to ban
     * @param message Message to send by banning
     */
    public static void banClient(int id, String message){
        if(clients[id] != null){
            bannedAddresses.add(clients[id].getAddress());
            clients[id].stop(NetworkMessenger.BANNED_BANNER + 
                    NetworkMessenger.SEPERATOR + message);
            clients[id] = null;
        }
    }
    
    /**Kicking client from the server
     * 
     * @param id Client id to kick
     * @param message Message to send by kicking
     */
    public static void kickClient(int id, String message){
        if(clients[id] != null){
            clients[id].stop(NetworkMessenger.KICKED_BANNER + 
                    NetworkMessenger.SEPERATOR + message);
            clients[id] = null;
        }
    }
    
    /**Sending message to all clients
     * 
     * @param msg Message to send
     */
    static void sendToAll(String msg){
        for(TCPServerClient t: clients){
            if(t != null){
                t.sendMessage(msg);
            }
        }
    }
    
    /**@param addr Address to ban
     */
    public static void addBannedAddress(InetAddress addr){
        bannedAddresses.add(addr);
    }
    
    /**@return Max player count of the server
     */
    public static int getMaxPlayers() {
        return maxPlayers;
    }
    
    /**Stopping server
     * 
     * @param message 
     */
    static void stop(String message){
        
        //Stop all clients
        for(TCPServerClient c: clients){
            if(c != null) c.stop(message);
        }
        
        try {
            
            //Closing connection and reset state
            socket.close();
            NetworkManager.setState(NetworkManager.NetworkState.SINGLEPLAYER);
        } catch (IOException ex) {
            Logger.warn("Error by closing connection", 
                    "The socket could not be closed clean!");
        }
    }
    
    /**Getting an array of all clients that are connected to the server. The id
     * if the array is the id of the client. Ifa client at id x is equal to null, 
     * thn the id x has still no client!
     * 
     * @return Array of the connected clients
     */
    public static TCPServerClient[] getClients() {
        return clients;
    }
    
    /**@return Is the tcp server still running
     */
    static boolean isRunning(){
        return !socket.isClosed();
    }
    
    /**Checks if the name is already taken on the server
     * 
     * @param name Name to ckeck
     * @return Is the name available
     */
    private static boolean nameAvailable(String name){
        for(TCPServerClient c: getClients()){
            if(c != null && c.getPrefix().equals(name)) return false;
        }
        return true;
    }
    
    @Override
    public void run() {
        
        //While server socket is alive, waiting for clients
        while(isRunning()){
            
            try {
                
                //Accepting client and setting timeout
                Socket client = socket.accept();
                client.setSoTimeout(HANDSHAKE_TIMEOUT);
                
                //Create and bind stream reader/writer
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                PrintWriter writer = new PrintWriter(client.getOutputStream());
                
                //Await handshake from client
                String handshake = reader.readLine();
                
               String name = NetworkMessenger.
                        checkHandShakeMessage(handshake, password);
                
                if(bannedAddresses.contains(client.getInetAddress())){
                    writer.println(NetworkMessenger.HANDSHAKE_BANNED);
                    writer.flush();
                }else if(isFull()){
                    writer.println(NetworkMessenger.HANDSHAKE_FULL);
                    writer.flush();
                }else if(name == null){
                    writer.println(NetworkMessenger.HANDSHAKE_WRONG_PASSWORD);
                    writer.flush();
                }else if(!nameAvailable(name)){
                    writer.println(NetworkMessenger.HANDSHAKE_NAME_NOT_AVAILABLE);
                    writer.flush();
                }else{
                    addClient(reader, writer, client, name);
                }
                
            } catch (IllegalBlockingModeException ex) {
                Logger.warn("Error by accepting client", "A client could not be "
                        + "accepted with an illegal blocking mode ecxeption!");
            } catch (SecurityException ex) {
                Logger.warn("Error by accepting client", "The security manager "
                        + "does not allow accepting a client!");
            } catch (SocketTimeoutException ex) {
                Logger.warn("Error by accepting client", "The timeout expires by "
                        + "accepting a client!");
            } catch (IOException ex) {
                Logger.warn("Error by accepting client", "A IO Exception occurs "
                        + "by accepting a client!");
            }
        }
        stop(NetworkMessenger.SERVER_CLOSED);
    }
}
