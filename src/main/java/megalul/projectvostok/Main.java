package megalul.projectvostok;

import glit.Glit;
import glit.context.ContextListener;
import glit.files.FileHandle;
import glit.graphics.font.BitmapFont;
import glit.graphics.font.FontLoader;
import glit.graphics.util.Gl;
import glit.graphics.util.batch.TextureBatch;
import glit.io.glfw.Key;
import glit.util.time.Sync;
import megalul.projectvostok.chunk.Chunk;
import megalul.projectvostok.options.Options;
import megalul.projectvostok.world.World;
import megalul.projectvostok.world.WorldRenderer;

public class Main implements ContextListener{

    public static String GAME_DIR_PATH = "./";


    public static void main(String[] args){
        Glit.create("Project Vostok", 1280, 720);
        Glit.init(new Main());
    }

    private TextureBatch uiBatch;
    private BitmapFont font;

    private Options options;
    private GameCamera camera;
    private World world;
    private WorldRenderer renderer;
    private Sync fpsSync;

    public void init(){
        new FileHandle(GAME_DIR_PATH).mkdirs();
        uiBatch = new TextureBatch(150);
        font = FontLoader.getDefault();

        fpsSync = new Sync(0);
        options = new Options(this, GAME_DIR_PATH);

        camera = new GameCamera(this, 0.1, 1000, 110);
        camera.getPos().y = Chunk.HEIGHT;
        camera.getRot().set(0, 0, 0);
        renderer = new WorldRenderer(this);
        world = new World(this);
    }

    public void render(){
        controls();
        Gl.clearColor(0.4, 0.7, 0.9);
        Gl.clearBufferColor();

        camera.update();
        getWorld().getChunks().updateMeshes();
        renderer.render();
        renderUi();
    }

    private void renderUi(){
        if(!options.isShowFPS())
            return;

        uiBatch.begin();
        uiBatch.setColor(0.5F, 0.4F, 0.1F, 1);
        font.drawText(uiBatch, "fps: " + Glit.getFps(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight());
        font.drawText(uiBatch, "ChunkProvider Threads:", 25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 2);
        font.drawText(uiBatch, "update tps: " + world.getChunks().updateTps.get(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 3);
        font.drawText(uiBatch, "load tps: " + world.getChunks().loadTps.get(),   25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 4);
        font.drawText(uiBatch, "unload tps: " + world.getChunks().unloadTps.get(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 5);
        font.drawText(uiBatch, "build tps: " + world.getChunks().buildTps.get(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 6);
        uiBatch.end();
    }

    private void controls(){
        if(Glit.isDown(Key.ESCAPE))
            Glit.exit();
        if(Glit.isDown(Key.F11))
            Glit.window().toggleFullscreen();
    }


    public void resize(int width, int height){
        camera.resize(width, height);
    }

    public void dispose(){
        uiBatch.dispose();
        font.dispose();
        renderer.dispose();

        options.save();
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

    public Sync getFpsSync(){
        return fpsSync;
    }

}
