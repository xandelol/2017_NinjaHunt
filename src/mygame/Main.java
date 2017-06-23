package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
    private List<NinjaObject> ninjas;
    private List<Ninja> njs;
    private AudioNode audioSource;
    private long startTime;
    Date afterAddingTenMins;
    private BitmapText infoPontos;
    private BitmapText infoTempo;
    private BitmapText infoObjetivo;
    private BitmapText fimDeJogo;
    private int pontos = 0;
    private long tempo;
    private boolean pause;
    private final long tt = 10 * 8000;
    private int dir;

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        dir = 6;
        ninjas = new ArrayList();
        njs = new ArrayList();
        startTime = System.currentTimeMillis();
        afterAddingTenMins = new Date(startTime + tt);

        createLigth();
        createCity();
        
        initSom();    
        
        boxMatColosion = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMatColosion.setBoolean("UseMaterialColors", true);
        boxMatColosion.setColor("Ambient", ColorRGBA.Red);
        boxMatColosion.setColor("Diffuse", ColorRGBA.Red); 
        
        
        createPlayer();
        initKeys();
        
        criaNinjas();
        
        initPlacar();

        bulletAppState.setDebugEnabled(true);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        for(Spatial r : rootNode.getChildren()){
            System.out.println(r.getName());
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code    
        player.upDateKeys(tpf, up, down, left, right);
        
        for(Ninja n : njs){
            n.updateNinja(tpf);
        }
        
        infoPontos.setText("Pontos: " + pontos);
        infoObjetivo.setText("Objetivo: Capture o máximo de ninjas antes do tempo terminar !");
        if (tempo()) {
            fimDeJogo.setText("Parabéns. Você capturou "+ pontos +" ninjas!!! \n PRESSIONE R PARA REINICIAR");
            guiNode.attachChild(fimDeJogo);
            pause = true;
            tempo = 0;
            bulletAppState.setEnabled(false);
        }
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
        if (binding.equals("Reset")) {
            restart();
            pause = false;
            pontos = 0;
            fimDeJogo.setText("");
            startTime = System.currentTimeMillis();
            afterAddingTenMins = new Date(startTime + tt);

            rootNode.detachChild(player);
            for(Spatial r : rootNode.getChildren()){
                if(r.getName().equals("ninja")){
                    rootNode.detachChild(r);
                    bulletAppState.getPhysicsSpace().removeAll(r);
                }
            }
            bulletAppState.getPhysicsSpace().removeAll(player);
            bulletAppState.setEnabled(true);
            createPlayer();
            criaNinjas();
        }
        
        if (binding.equals("Stop")&&value) {
            pause = !pause;
            if(pause)
                bulletAppState.setEnabled(false);
            else if(!pause)
                bulletAppState.setEnabled(true);
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
    
    private void createNinja(float x, float y, float z) {
        NinjaObject nObj = new NinjaObject();
        Ninja ninja = new Ninja("ninja", assetManager, bulletAppState, x, y, z);
        ninja.setDir(dir);
        nObj.setNinja(ninja);
        nObj.setChannel(ninja.getAnimationChannel());
        nObj.setControl(ninja.getAnimationControl());
        ninjas.add(nObj);
        njs.add(ninja);
        rootNode.attachChild(ninja);
    }
    
    private void createCity() {
        assetManager.registerLocator("town.zip", ZipLocator.class);
        Spatial scene = assetManager.loadModel("main.scene");
        scene.setLocalTranslation(0, -5.2f, 0);
        scene.setName("city");
        rootNode.attachChild(scene);
        System.out.println("Cidade: "+scene.getName());

        Box boxMesh = new Box(100f,0.5f,100f); 
        Geometry boxGeo = new Geometry("Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);       
        boxGeo.setMaterial(boxMat); 
        boxGeo.setLocalTranslation(0, -5.2f, 0);
        
        RigidBodyControl boxPhysicsNode = new RigidBodyControl(CollisionShapeFactory.createMeshShape(boxGeo), 0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);
        
        RigidBodyControl cityPhysicsNode = new RigidBodyControl(CollisionShapeFactory.createMeshShape(scene), 0);
        scene.addControl(cityPhysicsNode);
        bulletAppState.getPhysicsSpace().add(cityPhysicsNode);
    }
    
    private void initKeys() {
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharBackward", new KeyTrigger(KeyInput.KEY_S));        
        inputManager.addMapping("Stop", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));

        inputManager.addListener(this, "CharLeft", "CharRight");
        inputManager.addListener(this, "CharForward", "CharBackward");
        inputManager.addListener(this, "Stop");
        inputManager.addListener(this, "Reset");
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {

        if(event.getNodeA().getName().equals("player") || event.getNodeB().getName().equals("player")){
        
            if(event.getNodeA().getName().equals("ninja")){
                Spatial s = event.getNodeA();             
                rootNode.detachChild(s);
                bulletAppState.getPhysicsSpace().removeAll(s);
                njs.remove(s);
                pontos++;
                if (rootNode.getChild("ninja")==null) {
                    criaNinjas();
                }
            }
            else if(event.getNodeB().getName().equals("ninja")){
                Spatial s = event.getNodeB();
                rootNode.detachChild(s);
                bulletAppState.getPhysicsSpace().removeAll(s);
                njs.remove(s);
                pontos++;
                if (rootNode.getChild("ninja")==null) {
                    criaNinjas();
                }
            }           
        }        
    }
        
    public void criaNinjas(){
        Random r = new Random();
        for(int i=0; i < 10; i++){
            createNinja(r.nextInt(32), 3, r.nextInt(32));
        }
    }

    private void initSom() {
        audioSource = new AudioNode(assetManager, "Sounds/som.wav", false);
        audioSource.setLooping(false);
        audioSource.setPositional(false);
        audioSource.setVolume(0.5f);
        audioSource.playInstance();
    }
    
    private void initPlacar() {

        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        infoPontos = new BitmapText(guiFont, false);
        infoPontos.setSize(guiFont.getCharSet().getRenderedSize());
        infoPontos.setLocalTranslation(0, settings.getHeight() - 60, 0);
        guiNode.attachChild(infoPontos);

        infoTempo = new BitmapText(guiFont, false);
        infoTempo.setSize(guiFont.getCharSet().getRenderedSize());
        infoTempo.setLocalTranslation(0, settings.getHeight() - 80, 0);
        guiNode.attachChild(infoTempo);

        infoObjetivo = new BitmapText(guiFont, false);
        infoObjetivo.setSize(guiFont.getCharSet().getRenderedSize());
        infoObjetivo.setLocalTranslation(0, settings.getHeight() - 20, 0);
        guiNode.attachChild(infoObjetivo);

        fimDeJogo = new BitmapText(guiFont, false);
        fimDeJogo.setSize(guiFont.getCharSet().getRenderedSize());

        fimDeJogo.setLocalTranslation( // center
                (settings.getWidth() / 2) - (guiFont.getCharSet().getRenderedSize() * (fimDeJogo.getText().length() / 3)),
                settings.getHeight() / 2 + fimDeJogo.getLineHeight() / 2 - 100, 0);

    }
    
    public boolean tempo() {
        
        tempo = System.currentTimeMillis();
        long differenceTime = afterAddingTenMins.getTime() - tempo;
        if (differenceTime >= 0) {
            infoTempo.setText("Tempo restante: " + TimeUnit.MILLISECONDS.toSeconds(differenceTime) + " sec");
        }

        if (TimeUnit.MILLISECONDS.toSeconds(differenceTime) == 0) {
            return true;

        } else {
            return false;
        }
    }
}
