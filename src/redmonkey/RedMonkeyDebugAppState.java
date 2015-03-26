/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redmonkey;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class RedMonkeyDebugAppState extends BaseAppState {

    protected ViewPort viewPort;
    protected RenderManager rm;
    RMSpace space;
    Node redmonkeyDebugRootNode = new Node("RM Debug Root Node");
    BitmapFont font;
    protected HashMap<RMItem, BitmapText> labels = new HashMap<RMItem, BitmapText>();

    public RedMonkeyDebugAppState(RMSpace space, BitmapFont font) {
        this.space = space;
        this.font = font;
    }

    @Override
    protected void initialize(Application app) {
        this.rm = app.getRenderManager();
        redmonkeyDebugRootNode.setCullHint(Spatial.CullHint.Never);
        viewPort = rm.createMainView("Physics Debug Overlay", app.getCamera());
        viewPort.setClearFlags(false, true, false);
        viewPort.attachScene(redmonkeyDebugRootNode);    
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        updateRMItems();
        redmonkeyDebugRootNode.updateLogicalState(tpf);
        redmonkeyDebugRootNode.updateGeometricState();
    }

    private void updateRMItems() {
        HashMap<RMItem, BitmapText> oldObjects = labels;
        labels = new HashMap<RMItem, BitmapText>();
        Collection<RMItem> current = space.items;
        //create new map
        for (Iterator<RMItem> it = current.iterator(); it.hasNext();) {
            RMItem physicsObject = it.next();
            //copy existing spatials
            if (oldObjects.containsKey(physicsObject)) {
                BitmapText spat = oldObjects.get(physicsObject);
                spat.setText(physicsObject.toString());
                spat.setLocalTranslation(physicsObject.position);
                labels.put(physicsObject, spat);
                oldObjects.remove(physicsObject);
            } else {
                //if (filter == null || filter.displayObject(physicsObject))
                {
                    //logger.log(Level.FINE, "Create new debug RigidBody");
                    //create new spatial
                    BitmapText hudText = new BitmapText(font, false);
                    hudText.scale(0.01f);
//hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
//hudText.setColor(ColorRGBA.Blue);                             // font color
                    hudText.setText(physicsObject.toString());             // the text
                    hudText.setLocalTranslation(physicsObject.position); // position
                    hudText.addControl(new BillboardControl());
                    labels.put(physicsObject, hudText);
                    redmonkeyDebugRootNode.attachChild(hudText);
                }
            }
        }
        //remove leftover spatials
        for (Map.Entry<RMItem, BitmapText> entry : oldObjects.entrySet()) {
            RMItem object = entry.getKey();
            BitmapText spatial = entry.getValue();
            spatial.removeFromParent();
        }
    }
    @Override
    public void render(RenderManager rm) {
        super.render(rm);
        if (viewPort != null) {
            rm.renderScene(redmonkeyDebugRootNode, viewPort);
        }
    }
    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
