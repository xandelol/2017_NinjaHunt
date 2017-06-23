/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.scene.Node;

/**
 *
 * @author aacds
 */
public class NinjaObject {
    private Node ninja;
    private AnimChannel channel;
    private AnimControl control;
    
    public Node getNinja(){
        return ninja;
    }
    
    public void setNinja(Node ninja){
        this.ninja = ninja;
    }
    
    public AnimChannel getChannel(){
        return channel;
    }
    
    public void setChannel(AnimChannel channel){
        this.channel = channel;
    }
    
    public AnimControl getControl(){
        return control;
    }
    
    public void setControl(AnimControl control){
        this.control = control;
    }
}
