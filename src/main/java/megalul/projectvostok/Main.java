package megalul.projectvostok;

import glit.Glit;
import glit.context.ContextListener;
import glit.graphics.texture.Texture;
import glit.graphics.util.Gl;
import glit.graphics.util.TextureBatch;
import glit.io.glfw.Key;

public class Main implements ContextListener{

    public static void main(String[] args){
        Glit.create("Project Vostok", 1280, 720);
        Glit.init(new Main());
    }


    private TextureBatch batch;
    private Texture texture;

    public void init(){
        batch = new TextureBatch();
        texture = new Texture("textures/blocks/dirt.png");
    }

    public void render(){
        Gl.clearColor(0.2, 0.15, 0.3);
        Gl.clearBufferColor();

        batch.begin();
        batch.draw(texture, 100, 100, 600, 600);
        batch.end();

        if(Glit.isDown(Key.ESCAPE))
            Glit.exit();
    }

    public void resize(int i, int i1){ }

    public void dispose(){
        texture.dispose();
    }

}
