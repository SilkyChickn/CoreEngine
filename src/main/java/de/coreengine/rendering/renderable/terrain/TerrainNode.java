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
package de.coreengine.rendering.renderable.terrain;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**Class that represents one node of the terrain quadtree
 *
 * @author Darius Dinger
 */
public class TerrainNode {
    public enum Direction {BL, BR, TL, TR}
    
    //Position of the node in world space
    private final Vector2f position;
    
    //Position of the center point of this terrain node
    private final Vector2f centerPosition;
    
    //Id of the quad in the parent quad
    private final Direction dir;
    
    //Size / horizontal scale of this terrain node (squared)
    private final float size;
    
    //Lod level of this node
    private final int lod;
    
    //Parent node of this node
    private final TerrainNode parent;
    
    //Array of all children nodes of this terrain node (4 because of quad tree)
    private final TerrainNode[] childs = new TerrainNode[4];
    
    //Is the node a leaf of the tree / has it children?
    private boolean leaf = true;
    
    //Is the node in the last level of the tree
    private boolean lastLevel = false;
    
    //The main terrain, this node belongs to
    private final Terrain terrain;
    
    //Buffer variables for update method
    private final Vector2f toCam = new Vector2f(), worldPos = new Vector2f();
    
    /**Creates a new terrain node and sets its position, size and lod
     * 
     * @param parent Parent node of this node
     * @param dir Id of the quad in the parent quad
     * @param position Position of the node in world space (x,z)
     * @param size Size / horizontal scale of this terrain node (squared)
     * @param lod Lod level of this node
     * @param terrain Parent terrain instance, this node belongs to
     */
    public TerrainNode(TerrainNode parent, Direction dir, Vector2f position, float size, int lod, Terrain terrain) {
        this.parent = parent;
        this.dir = dir;
        this.position = position;
        this.size = size;
        this.lod = lod;
        this.terrain = terrain;
        
        //Size and lod of a child
        float csize = size / 2.0f; 
        int clod = lod +1;
        
        //Create childs, if max lod isnt reached
        if(lod < terrain.getConfig().getLodRanges().length){
            childs[0] = new TerrainNode(this, Direction.BL, 
                    new Vector2f(position.x, position.y), csize, clod, terrain);
            childs[1] = new TerrainNode(this, Direction.BR, 
                    new Vector2f(position.x +csize, position.y), csize, clod, terrain);
            childs[2] = new TerrainNode(this, Direction.TL, 
                    new Vector2f(position.x, position.y +csize), csize, clod, terrain);
            childs[3] = new TerrainNode(this, Direction.TR, 
                    new Vector2f(position.x +csize, position.y +csize), csize, clod, terrain);
        }else lastLevel = true;
        
        this.centerPosition = new Vector2f(position.x +csize, position.y +csize);
    }
    
    /**Updating this terrain node. This checks, if the terrain node should split
     * into childs or turn into a leaf by comparing the distance to the position 
     * pos with the lod range for its particular lod. Then it updating all childs, 
     * if it splits
     * 
     * @param pos Position to align to
     */
    void alignTo(Vector3f pos) {
        
        //Check if the node is in the last level of the quad tree (then it cant split anymore)
        if(!lastLevel){
            
            //Get Vector from terrain node center to camera
            toCam.set(pos.x, pos.z);
            worldPos.set(centerPosition);
            worldPos.scale(terrain.getScale());
            worldPos.x += terrain.getX();
            worldPos.y += terrain.getZ();
            toCam.sub(worldPos);
            
            //Calc length of this vector (camera distance to node center) squared (performance)
            float distanceSquared = (toCam.x * toCam.x) + (toCam.y * toCam.y);
            
            //check if distance squared exceed the lod limits squared
            if(distanceSquared < terrain.getConfig().getLodRanges()[lod] * 
                    terrain.getConfig().getLodRanges()[lod]){
                leaf = false;
                
                for(TerrainNode child: childs) {
                    child.alignTo(pos);
                }
            }else leaf = true;
        }
    }
    
    /**Gets the greater or equal top neighbour node<br>
     * returns null if its root node or neighbour is smaller
     * 
     * @return Top neighbour node or null
     */
    public TerrainNode getNeighboursGeTop(){
        if(getParent() == null) return null;
        
        if(getDirection() == TerrainNode.Direction.BL) 
            return getParent().getChilds()[2];
        if(getDirection() == TerrainNode.Direction.BR) 
            return getParent().getChilds()[3];
        
        TerrainNode neighbour = getParent().getNeighboursGeTop();
        if(neighbour == null || neighbour.isLeaf()) return neighbour;
        
        if(getDirection() == TerrainNode.Direction.TL)
            return neighbour.getChilds()[0];
        else return neighbour.getChilds()[1];
    }
    
    /**Gets the greater or equal bottom neighbour node<br>
     * returns null if its root node or neighbour is smaller
     * 
     * @return Bottom neighbour node or null
     */
    public TerrainNode getNeighboursGeBottom(){
        if(getParent() == null) return null;
        
        if(getDirection() == TerrainNode.Direction.TL) 
            return getParent().getChilds()[0];
        if(getDirection() == TerrainNode.Direction.TR)
            return getParent().getChilds()[1];
        
        TerrainNode neighbour = getParent().getNeighboursGeBottom();
        if(neighbour == null || neighbour.isLeaf()) return neighbour;
        
        if(getDirection() == TerrainNode.Direction.BL)
            return neighbour.getChilds()[2];
        else return neighbour.getChilds()[3];
    }
    
    /**Gets the greater or equal left neighbour node<br>
     * returns null if its root node or neighbour is smaller
     * 
     * @return Left neighbour node or null
     */
    public TerrainNode getNeighboursGeLeft(){
        if(getParent() == null) return null;
        
        if(getDirection() == TerrainNode.Direction.TR) 
            return getParent().getChilds()[2];
        if(getDirection() == TerrainNode.Direction.BR) 
            return getParent().getChilds()[0];
        
        TerrainNode neighbour = getParent().getNeighboursGeLeft();
        if(neighbour == null || neighbour.isLeaf()) return neighbour;
        
        if(getDirection() == TerrainNode.Direction.BL)
            return neighbour.getChilds()[1];
        else return neighbour.getChilds()[3];
    }
    
    /**Gets the greater or equal right neighbour node<br>
     * returns null if its root node or neighbour is smaller
     * 
     * @return Right neighbour node or null
     */
    public TerrainNode getNeighboursGeRight(){
        if(getParent() == null) return null;
        
        if(getDirection() == TerrainNode.Direction.TL) 
            return getParent().getChilds()[3];
        if(getDirection() == TerrainNode.Direction.BL) 
            return getParent().getChilds()[1];
        
        TerrainNode neighbour = getParent().getNeighboursGeRight();
        if(neighbour == null || neighbour.isLeaf()) return neighbour;
        
        if(getDirection() == TerrainNode.Direction.BR)
            return neighbour.getChilds()[0];
        else return neighbour.getChilds()[2];
    }
    
    /**Is the node a leaf of the tree / has it children?
     * 
     * @return Wether the node is a leaf
     */
    public boolean isLeaf() {
        return leaf;
    }
    
    /**Gets all child nodes of this node.<br>
     * If the node is a leaf, the childs will be returned anyway. 
     * 
     * @return Child nodes of this node
     */
    public TerrainNode[] getChilds() {
        return childs;
    }
    
    /**Getting the horizontal scaling/size of this terrain node
     * (squared)
     * 
     * @return Size of this node
     */
    public float getSize() {
        return size;
    }
    
    /**Getting the position/offset of this chunk relative to the terrain (0, 0)
     * coordinate
     * 
     * @return This terrain nodes position
     */
    public Vector2f getPosition() {
        return position;
    }
    
    /**Getting the lod level of this specific node
     * 
     * @return Lod level of this node
     */
    public int getLod() {
        return lod;
    }
    
    /**@return Dir of the quad in the parent quad
     */
    public Direction getDirection() {
        return dir;
    }
    
    /**@return Parent node
     */
    public TerrainNode getParent() {
        return parent;
    }
}
