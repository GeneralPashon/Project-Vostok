package megalul.projectvostok;

import glit.Glit;
import glit.context.ContextListener;
import glit.graphics.font.BitmapFont;
import glit.graphics.font.FontLoader;
import glit.graphics.gl.Target;
import glit.graphics.util.Gl;
import glit.graphics.util.batch.TextureBatch;
import glit.io.glfw.Key;
import glit.math.vecmath.vector.Vec3f;

public class Main implements ContextListener{

    public static void main(String[] args){
        Glit.create("Project Vostok", 1280, 720);
        Glit.init(new Main());
    }


    private TextureBatch batch;

    private TextureBatch uiBatch;
    private BitmapFont font;

    private Options options;
    private GameCamera camera;
    private World world;
    private WorldRenderer renderer;


    public void init(){
        // Gl.enable(Target.DEPTH_TEST);

        batch = new TextureBatch(10000);
        uiBatch = new TextureBatch(150);
        font = FontLoader.getDefault();

        options = new Options();
        camera = new GameCamera(0.1, 1000, 110);
        camera.getPos().y = 24;
        camera.getRot().set(0, 0, 0);
        world = new World(this);
        renderer = new WorldRenderer(this);

        Glit.mouse().show(false);
    }

    public void render(){
        controls();
        Gl.clearColor(0.2, 0.12, 0.3);
        Gl.clearBufferColor();

        camera.update();
        getWorld().getChunks().updateMeshes();
        renderer.render();
        renderUi();
    }

    private void renderUi(){
        uiBatch.begin();
        uiBatch.setColor(0.5F, 0.4F, 0.1F, 1);
        font.drawText(uiBatch, "fps: " + Glit.getFps(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight());
        font.drawText(uiBatch, "ChunkProvider Threads:", 25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 2);
        font.drawText(uiBatch, "update tps: " + world.getChunks().updateTps.get(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 3);
        font.drawText(uiBatch, "load tps: " + world.getChunks().loadTps.get(),   25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 4);
        font.drawText(uiBatch, "unload tps: " + world.getChunks().unloadTps.get(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight() * 5);
        font.drawText(uiBatch, "(WASD + (CTRL))", 25, 25);
        font.drawText(uiBatch, "(Scroll for scaling)", 25, 25 + font.getScaledLineHeight());
        font.drawText(uiBatch, "(Mouse for moving camera)", 25, 25 + font.getScaledLineHeight() * 2);
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
        batch.dispose();
        renderer.dispose();
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
