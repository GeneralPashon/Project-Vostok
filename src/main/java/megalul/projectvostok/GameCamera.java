package megalul.projectvostok;

import glit.Glit;
import glit.graphics.camera.PerspectiveCamera;
import glit.io.glfw.Key;
import glit.math.vecmath.vector.Vec3f;

public class GameCamera extends PerspectiveCamera{

    public GameCamera(double near, double far, double fieldOfView){
        super(near, far, fieldOfView);
    }

    private Vec3f vel = new Vec3f(1, 0, 0);

    public void update(){
        float speed = 1F * Glit.getDeltaTime() * 75;
        if(Glit.isPressed(Key.LEFT_CONTROL))
            speed *= 4;

        Vec3f velocity = new Vec3f();
        if(Glit.isPressed(Key.W))
            velocity.z += speed;
        if(Glit.isPressed(Key.A))
            velocity.x -= speed;
        if(Glit.isPressed(Key.S))
            velocity.z -= speed;
        if(Glit.isPressed(Key.D))
            velocity.x += speed;


        position.add(velocity);
        if(!velocity.isZero())
            vel = velocity.clone();
        rotation.setDirection(rotation.direction().add(vel.div(2)));

        super.update();
    }

}
