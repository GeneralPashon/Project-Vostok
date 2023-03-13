package megalul.projectvostok;

import glit.Glit;
import glit.graphics.camera.PerspectiveCamera;
import glit.math.vecmath.vector.Vec3f;
import megalul.projectvostok.options.KeyMapping;

public class GameCamera extends PerspectiveCamera{

    private final Main session;

    private final Vec3f up = new Vec3f(0, 1, 0);
    private float dAngX, dAngY, prevX, prevY;
    private boolean doNotRotateThisFrame;

    public GameCamera(Main session, double near, double far, double fieldOfView){
        super(near, far, fieldOfView);

        this.session = session;

        doNotRotateThisFrame = true;
        Glit.mouse().show(false);
    }


    public void update(){
        if(Glit.window().isFocused()){
            if(!doNotRotateThisFrame){
                float x = Glit.mouse().getX();
                float y = Glit.mouse().getY();
                dAngX += prevX - x;
                dAngY += prevY - y;

                float sensitivity = session.getOptions().getMouseSensitivity();
                getRot().yaw += dAngX * 0.1 * sensitivity;
                getRot().pitch += dAngY * 0.1 * sensitivity;
                getRot().constrain();

                dAngX *= 0.1;
                dAngY *= 0.1;
            }
            Glit.mouse().setPos(Glit.getWidth() / 2, Glit.getHeight() / 2);
            prevX = Glit.getWidth() / 2F;
            prevY = Glit.getHeight() / 2F;
            doNotRotateThisFrame = false;
        }


        float speed = Glit.getDeltaTime() * 75;
        if(isPressed(KeyMapping.SPRINT))
            speed *= 3;

        Vec3f dir = getRot().direction();
        Vec3f acceleration = dir.clone();
        acceleration.y = 0;
        acceleration.nor().mul(speed);

        if(isPressed(KeyMapping.FORWARD))
            getPos().add(acceleration);
        if(isPressed(KeyMapping.BACK))
            getPos().sub(acceleration);

        Vec3f sideMove = Vec3f.crs(up, dir).mul(speed);
        if(isPressed(KeyMapping.RIGHT))
            getPos().add(sideMove);
        if(isPressed(KeyMapping.LEFT))
            getPos().sub(sideMove);
        if(isPressed(KeyMapping.JUMP))
            getPos().y += speed;
        if(isPressed(KeyMapping.SNEAK))
            getPos().y -= speed;

        super.update();
    }

    private boolean isPressed(KeyMapping key){
        return Glit.isPressed(session.getOptions().getKey(key));
    }

}
