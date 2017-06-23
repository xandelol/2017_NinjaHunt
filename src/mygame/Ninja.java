/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author aacds
 */
public class Ninja extends Node implements AnimEventListener {
    private final BetterCharacterControl physicsCharacter;
    private AnimControl animationControl;
    private AnimChannel animationChannel;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private float airTime;
    private float rot = 0;
    
    public Ninja (String name,AssetManager assetManager, BulletAppState bulletAppState, float x, float y, float z){
        super(name);
        rot = y*x*z;
        Node ninja = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.setLocalTranslation(x, y, z);
        rotate(0.0f, -rot, 0.0f);
        scale(0.012f);
        setLocalTranslation(x, y, z);
        
        attachChild(ninja);
        
        physicsCharacter = new BetterCharacterControl(0f, 2.5f, 16f);
        addControl(physicsCharacter);
        
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        
        animationControl = ninja.getControl(AnimControl.class);
        animationControl.addListener(this);
        animationChannel = animationControl.createChannel();
        
        for(String anim : animationControl.getAnimationNames()){
            System.out.println();
        }
        
        animationChannel.setAnim("Walk", 0.005f);
        
    }
    
    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }
    
//    public void simpleUpdate(float tpf) {
//        //TODO: add update code
//        Vector3f position = getLocalTranslation();
//        position.setZ(position.getZ() + 10/10000);
//        setLocalTranslation(position);
//    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        //
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        //
    }
    
    public AnimChannel getAnimationChannel(){
        return animationChannel;
    }
    
    public void setAnimationChannel(AnimChannel animationChannel){
        this.animationChannel = animationChannel;
    }
    
    public AnimControl getAnimationControl(){
        return animationControl;
    }
    
    public void setAnimationControl(AnimControl animationControl){
        this.animationControl = animationControl;
    }
}
