package megalul.projectvostok;

import glit.Glit;
import glit.context.ContextListener;
import glit.graphics.camera.CenteredOrthographicCamera;
import glit.graphics.font.BitmapFont;
import glit.graphics.font.FontLoader;
import glit.graphics.util.Gl;
import glit.graphics.util.batch.TextureBatch;
import glit.io.glfw.Key;
import glit.math.Maths;
import glit.math.function.FastNoiseLite;

public class Main implements ContextListener{

    public static void main(String[] args){
        Glit.create("Project Vostok", 1280, 720);
        Glit.init(new Main());
    }


    private TextureBatch batch;
    private TextureBatch uiBatch;
    private CenteredOrthographicCamera camera2d;
    private BitmapFont font;

    private Options options;
    private GameCamera camera;
    private World world;

    public void init(){
        batch = new TextureBatch(1000);
        uiBatch = new TextureBatch(1000);
        camera2d = new CenteredOrthographicCamera();
        font = FontLoader.getDefault();

        options = new Options();
        camera = new GameCamera(0.1, 1000, 80);
        world = new World(this);
    }

    public void render(){
        Gl.clearColor(0.1, 0.06, 0.15);
        Gl.clearBufferColor();

        camera.update();
        camera2d.update();
        batch.begin(camera2d);
        world.getChunks().draw(batch);
        batch.end();

        if(Glit.isDown(Key.ESCAPE))
            Glit.exit();

        FastNoiseLite noise = new FastNoiseLite();
        noise.setFrequency(0.007F);

        for(Chunk chunk: world.getChunks().getChunks())
            if(chunk.texture == null){
                for(int i = 0; i < 16; i++)
                    for(int j = 0; j < 16; j++){
                        int y = Maths.round(noise.getNoise(i + 16 * chunk.getPos().x, j + 16 * chunk.getPos().z) * Chunk.HEIGHT / 2 + Chunk.HEIGHT / 2F);
                        chunk.setBlock(i, y, j, new BlockState(BlockType.DIRT));
                    }
                chunk.buildTexture();
            }

        final float scale = 1.2F;
        int scroll = Glit.mouse().getScroll();
        if(scroll > 0)
            camera2d.scale(scale);
        else if(scroll < 0)
            camera2d.scale(1 / scale);

        uiBatch.begin();
        font.drawText(uiBatch, "fps: " + Glit.getFps(), 25, Glit.getHeight() - 25 - font.getScaledLineHeight());
        font.drawText(uiBatch, "(WASD + (CTRL))", 25, 25);
        font.drawText(uiBatch, "(Scroll for scaling)", 25, 25 + font.getScaledLineHeight());
        uiBatch.end();
    }

    public void resize(int width, int height){
        camera.resize(width, height);
        camera2d.resize(width, height);
    }

    public void dispose(){
        batch.dispose();
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
