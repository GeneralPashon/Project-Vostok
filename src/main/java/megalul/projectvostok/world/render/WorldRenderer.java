package megalul.projectvostok.world.render;

import glit.context.Disposable;
import glit.files.FileHandle;
import glit.graphics.texture.Pixmap;
import glit.graphics.texture.Texture;
import glit.graphics.util.Shader;
import glit.math.vecmath.matrix.Matrix4f;
import megalul.projectvostok.Main;
import megalul.projectvostok.chunk.Chunk;
import megalul.projectvostok.chunk.data.ChunkPos;
import megalul.projectvostok.chunk.render.ChunkMesh;

import java.util.Map;
import java.util.Set;

public class WorldRenderer implements Disposable{

    private final Main session;
    private final Shader chunkShader;
    private final Texture atlasTexture;
    private final Matrix4f chunkMatrix;


    public WorldRenderer(Main session){
        this.session = session;
        
        chunkShader = new Shader(new FileHandle("shader/chunk.vert"), new FileHandle("shader/chunk.frag"));
        chunkMatrix = new Matrix4f();
        
        Pixmap pixmap = new Pixmap(16 * 3, 16 * 3);
        pixmap.clear(0, 0, 0, 0F);
        pixmap.fill(0, 0, 15, 15, 0, 0, 0, 1F);
        pixmap.fill(1, 1, 14, 14, 1, 0.75F, 1, 1F);
        
        atlasTexture = new Texture(pixmap);
    }

    public void render(){
        chunkShader.bind();
        chunkShader.setUniform("u_projection", session.getCamera().getProjection());
        chunkShader.setUniform("u_view", session.getCamera().getView());
        chunkShader.setUniform("u_atlas", atlasTexture);

        Map<Chunk, ChunkMesh> meshes = session.getWorld().getChunks().getMeshes();
    
        for(Chunk chunk: meshes.keySet()){
            if(!session.getCamera().isChunkSeen(chunk))
                continue;
            
            ChunkMesh mesh = meshes.get(chunk);
            if(mesh == null)
                continue;
            
            ChunkPos chunkPos = chunk.getPos();
            chunkShader.setUniform("u_model", chunkMatrix.toTranslated(chunkPos.globalX(), 0, chunkPos.globalZ()));
            mesh.render();
        }
    }

    @Override
    public void dispose(){
        chunkShader.dispose();
        atlasTexture.dispose();
    }

}
