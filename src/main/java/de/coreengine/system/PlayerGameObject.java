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
package de.coreengine.system;

/**Gameobject that can be spawned by a client
 *
 * @author Darius Dinger
 */
public abstract class PlayerGameObject extends GameObject{
    private String playerName;
    private boolean controlled;
    
    /**Setup player controlled game object
     * 
     * @param playerName Name of the player who controls this object
     * @param controlled Is the game object controlled from this player
     */
    public void setup(String playerName, boolean controlled){
        this.playerName = playerName;
        this.controlled = controlled;
    }
    
    /**Getting the name of this player on the server. Use this as
     * prefix for all syncronized objects and events in this gameobject!
     * 
     * @return Name of this player
     */
    protected String getPlayerName() {
        return playerName;
    }
    
    /**Getting called when player disconnects
     */
    public void onDisconnect(){}
    
    /**Getting called, when player joines
     */
    public void onJoin(){}
    
    /**@return Is the game object controlled by this player
     */
    public boolean isControlled() {
        return controlled;
    }
}
