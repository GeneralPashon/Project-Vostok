package megalul.projectvostok;

import glit.Glit;
import glit.context.ContextListener;
import glit.graphics.font.BitmapFont;
import glit.graphics.font.FontLoader;
import glit.graphics.texture.Texture;
import glit.graphics.util.Gl;
import glit.graphics.util.batch.TextureBatch;
import glit.io.glfw.Key;

public class Main implements ContextListener{

    public static void main(String[] args){
        Glit.create("Project Vostok", 1280, 720);
        Glit.init(new Main());
    }


    private TextureBatch batch;
    private BitmapFont font;
    private Texture texture;

    private Options options;
    private GameCamera camera;
    private World world;

    public void init(){
        batch = new TextureBatch(10000);
        font = FontLoader.getDefault();
        texture = new Texture("textures/blocks/dirt.png");

        options = new Options();
        camera = new GameCamera(0.1, 1000, 80);
        world = new World(this);
    }

    public void render(){
        Gl.clearColor(0.1, 0.06, 0.15);
        Gl.clearBufferColor();

        camera.update();
        batch.begin();
        world.getChunks().draw(batch, texture);
        font.drawText(batch, "fps: " + Glit.getFps(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight());
        font.drawText(batch, "(WASD + (CTRL))", 25, 25);
        batch.end();

        if(Glit.isDown(Key.ESCAPE))
            Glit.exit();
    }

    public void resize(int width, int height){
        camera.resize(width, height);
    }

    public void dispose(){
        batch.dispose();
        texture.dispose();
    }


    public Options getOptions(){
        return options;
    }

    public GameCamera getCamera(){
        return camera;
    }

    public World getWorld(){
        return world;
    }

}
