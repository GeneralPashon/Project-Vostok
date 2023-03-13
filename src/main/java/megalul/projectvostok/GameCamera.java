package megalul.projectvostok;

import glit.Glit;
import glit.graphics.camera.PerspectiveCamera;
import glit.io.glfw.Key;
import glit.math.vecmath.vector.Vec3f;

public class GameCamera extends PerspectiveCamera{

    private final Vec3f up = new Vec3f(0, 1, 0);
    private float dAngX, dAngY, prevX, prevY;
    private boolean doNotRotateThisFrame;

    public GameCamera(double near, double far, double fieldOfView){
        super(near, far, fieldOfView);

        doNotRotateThisFrame = true;
    }


    public void update(){
        if(!doNotRotateThisFrame){
            float x = Glit.mouse().getX();
            float y = Glit.mouse().getY();
            dAngX += prevX - x;
            dAngY += prevY - y;

            getRot().yaw += dAngX * 0.3;
            getRot().pitch += dAngY * 0.3;
            getRot().constrain();

            System.out.println(dAngX * 0.3 + " " + (dAngY * 0.3));

            dAngX *= 0.1;
            dAngY *= 0.1;
        }
        Glit.mouse().setPos(Glit.getWidth() / 2, Glit.getHeight() / 2);
        prevX = Glit.getWidth() / 2F;
        prevY = Glit.getHeight() / 2F;
        doNotRotateThisFrame = false;


        float speed = 0.2F;
        Vec3f dir = getRot().direction();
        Vec3f acceleration = dir.clone();
        acceleration.y = 0;
        acceleration.nor().mul(speed);

        if(Glit.isPressed(Key.W))
            getPos().add(acceleration);
        if(Glit.isPressed(Key.S))
            getPos().sub(acceleration);

        Vec3f sideMove = Vec3f.crs(up, dir).mul(speed);
        if(Glit.isPressed(Key.D))
            getPos().add(sideMove);
        if(Glit.isPressed(Key.A))
            getPos().sub(sideMove);
        if(Glit.isPressed(Key.SPACE))
            getPos().y += speed;
        if(Glit.isPressed(Key.LEFT_SHIFT))
            getPos().y -= speed;

        super.update();
    }

}
