package megalul.projectvostok;

import glit.context.Disposable;
import glit.files.FileHandle;
import glit.graphics.texture.Texture;
import glit.graphics.util.Shader;
import glit.math.vecmath.matrix.Matrix4f;
import glit.math.vecmath.vector.Vec3f;

import java.util.Map;

public class WorldRenderer implements Disposable{

    private final Main session;
    private final Shader chunkShader;
    private final Texture atlasTexture;


    public WorldRenderer(Main session){
        this.session = session;

        chunkShader = new Shader(new FileHandle("shader/chunk.vert"), new FileHandle("shader/chunk.frag"));
        atlasTexture = new Texture("texture/grass_block.png");
    }


    public void render(){
        chunkShader.bind();
        chunkShader.setUniform("u_projection", session.getCamera().getProjection());
        chunkShader.setUniform("u_view", session.getCamera().getView());
        chunkShader.setUniform("u_atlas", atlasTexture);

        for(Map.Entry<ChunkPos, ChunkMesh> entry: session.getWorld().getChunks().getMeshes()){
            ChunkPos chunkPos = entry.getKey();
            chunkShader.setUniform("u_model", new Matrix4f().toTranslated(new Vec3f(chunkPos.x, 0, chunkPos.z).mul(ChunkUtils.SIZE_XZ)));
            entry.getValue().render();
        }
    }

    @Override
    public void dispose(){
        chunkShader.dispose();
        atlasTexture.dispose();
    }

}
