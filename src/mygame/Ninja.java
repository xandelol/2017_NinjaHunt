/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author aacds
 */
public class Ninja extends Node {
    private final BetterCharacterControl physicsCharacter;
    private final AnimControl animationControl;
    private final AnimChannel animationChannel;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 0);
    private float airTime;
    
    public Ninja (String name,AssetManager assetManager, BulletAppState bulletAppState, float x, float y, float z){
        super(name);
        
        Node ninja = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.setLocalTranslation(x, y, z);
        scale(0.012f);
        setLocalTranslation(x, y, z);
        attachChild(ninja);
        
        physicsCharacter = new BetterCharacterControl(0.4f, 2.5f, 16f);
        addControl(physicsCharacter);
        
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        
        animationControl = ninja.getControl(AnimControl.class);
        animationChannel = animationControl.createChannel();
    }
    
    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(Vector3f viewDirection) {
        this.viewDirection = viewDirection;
    }
    

    
    void upDateAnimationPlayer() {
   
//        if (walkDirection.length() == 0) {
//            if (!"Kick".equals(animationChannel.getAnimationName())) {
//                animationChannel.setAnim("Kick", 1f);
//            }
//        } else {
//            if (airTime > .3f) {
//                if (!"Kick".equals(animationChannel.getAnimationName())) {
//                    animationChannel.setAnim("Kick");
//                }
//            } else if (!"Walk".equals(animationChannel.getAnimationName())) {
//                animationChannel.setAnim("Walk", 0.7f);
//            }
//        }
        
        animationChannel.setAnim("Walk",0.7f);

    }
}
