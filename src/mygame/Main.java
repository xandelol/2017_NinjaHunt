package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.List;
import java.util.Random;
/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener{

    private static Main app = null;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.showSettings = false;
        app.start();
    }
    
    private BulletAppState bulletAppState;
    private PlayerCameraNode player;
    private boolean up = false, down = false, left = false, right = false;
    private Material boxMatColosion;
    private List<Geometry> cubos;

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        

        createLigth();
        createCity();
        
        
        criaCubos();
        
        boxMatColosion = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMatColosion.setBoolean("UseMaterialColors", true);
        boxMatColosion.setColor("Ambient", ColorRGBA.Red);
        boxMatColosion.setColor("Diffuse", ColorRGBA.Red); 
        
        
        createPlayer();
        initKeys();

        bulletAppState.setDebugEnabled(true);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        player.upDateKeys(tpf, up, down, left, right);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void createPlayer() {

        player = new PlayerCameraNode("player", assetManager, bulletAppState, cam);
        rootNode.attachChild(player);
        flyCam.setEnabled(false);

    }
    
    @Override
    public void onAction(String binding, boolean value, float tpf) {
        switch (binding) {
            case "CharLeft":
                if (value) {
                    left = true;
                } else {
                    left = false;
                }
                break;
            case "CharRight":
                if (value) {
                    right = true;
                } else {
                    right = false;
                }
                break;
        }
        switch (binding) {
            case "CharForward":
                if (value) {
                    up = true;
                } else {
                    up = false;
                }
                break;
            case "CharBackward":
                if (value) {
                    down = true;
                } else {
                    down = false;
                }
                break;
        }
    }
    
    private void createLigth() {

        DirectionalLight l1 = new DirectionalLight();
        l1.setDirection(new Vector3f(1, -0.7f, 0));
        rootNode.addLight(l1);

        DirectionalLight l2 = new DirectionalLight();
        l2.setDirection(new Vector3f(-1, 0, 0));
        rootNode.addLight(l2);

        DirectionalLight l3 = new DirectionalLight();
        l3.setDirection(new Vector3f(0, 0, -1.0f));
        rootNode.addLight(l3);

        DirectionalLight l4 = new DirectionalLight();
        l4.setDirection(new Vector3f(0, 0, 1.0f));
        rootNode.addLight(l4);


        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
    }
    
    private void createCubo(float x, float y, float z, float t) {
        /* A colored lit cube. Needs light source! */
        Box boxMesh = new Box(t*1f, t*1f, t*1f);
        Geometry boxGeo = new Geometry("Box", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        boxMat.setBoolean("UseMaterialColors", true);
        boxMat.setColor("Ambient", ColorRGBA.Green);
        boxMat.setColor("Diffuse", ColorRGBA.Green);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(x,y,z);
        rootNode.attachChild(boxGeo);
        


        RigidBodyControl boxPhysicsNode = new RigidBodyControl(1);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);

    }
    
    private void createCity() {
        assetManager.registerLocator("town.zip", ZipLocator.class);
        Spatial scene = assetManager.loadModel("main.scene");
        scene.setLocalTranslation(0, -5.2f, 0);
        rootNode.attachChild(scene);

        RigidBodyControl cityPhysicsNode = new RigidBodyControl(CollisionShapeFactory.createMeshShape(scene), 0);
        scene.addControl(cityPhysicsNode);
        bulletAppState.getPhysicsSpace().add(cityPhysicsNode);
    }
    
    private void initKeys() {
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharBackward", new KeyTrigger(KeyInput.KEY_S));

        inputManager.addListener(this, "CharLeft", "CharRight");
        inputManager.addListener(this, "CharForward", "CharBackward");

    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {

        if(event.getNodeA().getName().equals("player") || event.getNodeA().getName().equals("player")){
        
            if(event.getNodeA().getName().equals("Box")){
                  Spatial s = event.getNodeA();             
                  menorCubo(s);
            }
            else
            if(event.getNodeB().getName().equals("Box")){
                  Spatial s = event.getNodeB();
                  menorCubo(s);
            }
            
        }
        
    }
    
    private void menorCubo(Spatial cb){
        
        Vector3f menor = null;
        Geometry geo = null;
        BoundingBox box = null;
        float xb = 0.0f;
        float tcb = 0.0f;
        boolean menorCubo = true;
        
        geo = (Geometry) cb;
        box = (BoundingBox) geo.getModelBound();
        tcb = box.getExtent(menor).x;
        
        
        for (Spatial r : rootNode.getChildren()) {
            if (r.getName().equals("Box")) {
                geo = (Geometry) r;
                box = (BoundingBox) geo.getModelBound();
                xb = box.getExtent(menor).x;
                
                if (xb < tcb) {
                    menorCubo = false;
                }

            }
        }
        
        if (menorCubo) {
            rootNode.detachChild(cb);
            bulletAppState.getPhysicsSpace().removeAll(cb);
            if (rootNode.getChild("Box") == null) {
                criaCubos();
            }
        }
        else{
            cb.setMaterial(boxMatColosion);
        }
        
    }
    
    public void criaCubos(){
        Random r = new Random();
        for(int i=0; i < 10; i++){
            createCubo(r.nextInt(32), 3, r.nextInt(32), (i+1)*0.25f);
        }
    }
}
