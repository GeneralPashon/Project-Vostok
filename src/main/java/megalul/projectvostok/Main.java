package megalul.projectvostok;

import glit.Glit;
import glit.context.ContextListener;
import glit.graphics.font.BitmapFont;
import glit.graphics.font.FontLoader;
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
    private BitmapFont font;
    private Texture texture;

    private Options options;
    private GameCamera camera;
    private ChunkProvider chunkProvider;

    public void init(){
        batch = new TextureBatch();
        font = FontLoader.getDefault();
        font.setScale(0.25F);
        texture = new Texture("textures/blocks/dirt.png");

        options = new Options();
        camera = new GameCamera(0.1, 1000, 80);
        chunkProvider = new ChunkProvider(this);
    }

    public void render(){
        Gl.clearColor(0.1, 0.06, 0.15);
        Gl.clearBufferColor();

        camera.update();
        batch.begin();
        chunkProvider.draw(batch, texture);
        font.drawText(batch, "fps: " + Glit.getFps(), 25, Glit.getHeight() - 25 - font.getLineHeight() * font.getScale());
        font.drawText(batch, "(WASD)", 25, 25);
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

    public ChunkProvider getChunkProvider(){
        return chunkProvider;
    }

}
