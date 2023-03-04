package megalul.projectvostok;

import glit.Glit;
import glit.graphics.camera.PerspectiveCamera;
import glit.io.glfw.Key;

public class GameCamera extends PerspectiveCamera{

    public GameCamera(double near, double far, double fieldOfView){
        super(near, far, fieldOfView);
    }

    public void update(){
        float speed = 0.07F;

        if(Glit.isPressed(Key.W))
            position.z += speed;
        if(Glit.isPressed(Key.A))
            position.x -= speed;
        if(Glit.isPressed(Key.S))
            position.z -= speed;
        if(Glit.isPressed(Key.D))
            position.x += speed;

        super.update();
    }

}
