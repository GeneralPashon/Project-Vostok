package megalul.projectvostok.world.render;

import glit.context.Disposable;
import glit.files.FileHandle;
import glit.graphics.texture.Pixmap;
import glit.graphics.texture.Texture;
import glit.graphics.util.Shader;
import glit.math.vecmath.matrix.Matrix4f;
import glit.math.vecmath.vector.Vec3f;
import megalul.projectvostok.Main;
import megalul.projectvostok.chunk.render.ChunkMesh;
import megalul.projectvostok.chunk.data.ChunkPos;
import megalul.projectvostok.chunk.ChunkUtils;

import java.util.Map;

public class WorldRenderer implements Disposable{

    private final Main session;
    private final Shader chunkShader;
    private final Texture atlasTexture;


    public WorldRenderer(Main session){
        this.session = session;

        chunkShader = new Shader(new FileHandle("shader/chunk.vert"), new FileHandle("shader/chunk.frag"));

        Pixmap pixmap = new Pixmap(16, 16);
        pixmap.clear(0, 0, 0, 1F);
        pixmap.fill(1, 1, 14, 14, 1, 0.75F, 1, 1F);

        atlasTexture = new Texture(pixmap);
    }

    public void render(){
        chunkShader.bind();
        chunkShader.setUniform("u_projection", session.getCamera().getProjection());
        chunkShader.setUniform("u_view", session.getCamera().getView());
        chunkShader.setUniform("u_atlas", atlasTexture);

        for(Map.Entry<ChunkPos, ChunkMesh> entry: session.getWorld().getChunks().getMeshes()){
            ChunkPos chunkPos = entry.getKey();
            if(!chunkPos.isInFrustum(session.getCamera()))
                continue;
            chunkShader.setUniform("u_model", new Matrix4f().toTranslated(new Vec3f(chunkPos.x, 0, chunkPos.z).mul(ChunkUtils.SIZE)));
            entry.getValue().render();
        }
    }

    @Override
    public void dispose(){
        chunkShader.dispose();
        atlasTexture.dispose();
    }

}
