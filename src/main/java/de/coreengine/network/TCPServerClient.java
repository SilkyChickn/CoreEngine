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

import de.coreengine.system.PlayerGameObject;
import de.coreengine.util.BlockedList;
import de.coreengine.util.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**A client of a tcp server
 *
 * @author Darius Dinger
 */
public class TCPServerClient implements Runnable{
    
    //Client data
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private boolean running = true;
    private final String prefix;
    private final PlayerGameObject player;
    
    //Clients message queues
    private static BlockedList<String> msgList = new BlockedList<>();
    
    /**Creating new client for a tcp server.
     * 
     * @param reader Reader to listen from client messages
     * @param writer Writer to write to client
     * @param socket Socket of the client or null if no socket exist
     * @param prefix Prefix to identify the player in the network
     * @param player Player game object in the scene
     */
    public TCPServerClient(BufferedReader reader, PrintWriter writer, 
            Socket socket, String prefix, PlayerGameObject player) {
        this.reader = reader;
        this.writer = writer;
        this.socket = socket;
        this.prefix = prefix;
        this.player = player;
    }
    
    /**@return Host address of the client
     */
    public InetAddress getAddress(){
        if(socket != null) return socket.getInetAddress();
        else return null;
    }
    
    /**@return Is the client still connected
     */
    public boolean isAlive(){
        if(running && socket != null) running = !socket.isClosed();
        return running;
    }
    
    /**Sending a message to the client
     * 
     * @param message Message to send to the client
     */
    public void sendMessage(String message){
        writer.println(message);
        writer.flush();
    }
    
    /**@return List of all msgs from the client
     */
    public BlockedList<String> getMsgList() {
        return msgList;
    }
    
    /**@return Get player prefix/id to identify the player in the network
     */
    public String getPrefix() {
        return prefix;
    }
    
    /**Stopping clients connection to the server
     * 
     * @param message Message to send to client before close
     */
    public void stop(String message){
        TCPServer.removeClient(this);
        
        //Sending exit message to client
        writer.println(message);
        writer.flush();
        
        if(player != null) player.onDisconnect();
        TCPServer.sendToAll(NetworkMessenger.LEFT_BANNER + 
                NetworkMessenger.SEPERATOR + getPrefix());
        
        try {
            
            //Closing clients connection
            socket.close();
        } catch (IOException ex) {
            Logger.warn("Error by closing client", 
                    "The client could not be closed clean!");
        }
        
        //Exit clients thread
        running = false;
    }
    
    @Override
    public void run() {
        if(player != null) player.onJoin();
        
        String line = "";
        try {
            
            //Read from clients stream while alive
            while((line = reader.readLine()) != null){
                msgList.add(line);
                TCPServer.sendToAll(line);
            }
            
            //Clients stream has ended
            Logger.warn("Client stream ended", "The stream of a client has been "
                    + "ended!");
            stop(NetworkMessenger.STREAM_ENDED);
        } catch (IOException ex) {
            if(running){
                
                //Client timeout expired
                Logger.warn("Client timeout", "A clients timeout expired!");
                stop(NetworkMessenger.TIMEOUT_EXPIRED);
            }
        }
    }
}
