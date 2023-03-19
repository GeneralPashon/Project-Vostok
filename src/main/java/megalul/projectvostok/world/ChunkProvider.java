package megalul.projectvostok.world;

import glit.math.Maths;
import glit.math.vecmath.vector.Vec2f;
import glit.math.vecmath.vector.Vec3f;
import glit.util.time.FpsCounter;
import megalul.projectvostok.Main;
import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.chunk.Chunk;
import megalul.projectvostok.chunk.data.ChunkPos;
import megalul.projectvostok.chunk.gen.DefaultGenerator;
import megalul.projectvostok.chunk.render.ChunkBuilder;
import megalul.projectvostok.chunk.render.ChunkMesh;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static megalul.projectvostok.chunk.ChunkUtils.*;

public class ChunkProvider{

    public static final Chunk EMPTY_CHUNK = null;
    private static final int MAX_LOAD_TASKS = 1;

    private final Main session;

    private final ConcurrentMap<ChunkPos, Chunk> chunks;

    private final List<ChunkPos> frontiers;
    private final CopyOnWriteArrayList<ChunkPos> loadQueue;
    private final Set<ChunkPos> toLoadQueue;

    private final CopyOnWriteArrayList<Chunk> toBuildQueue;
    private final ConcurrentMap<Chunk, float[]> built;
    private final ConcurrentMap<ChunkPos, ChunkMesh> meshes;

    public final FpsCounter findTps, loadTps, buildTps, checkTps;


    public ChunkProvider(Main session){
        this.session = session;

        chunks = new ConcurrentHashMap<>();
        frontiers = new ArrayList<>();
        toBuildQueue = new CopyOnWriteArrayList<>();
        built = new ConcurrentHashMap<>();
        meshes = new ConcurrentHashMap<>();

        toLoadQueue = new HashSet<>();
        loadQueue = new CopyOnWriteArrayList<>();

        findTps = new FpsCounter();
        loadTps = new FpsCounter();
        buildTps = new FpsCounter();
        checkTps = new FpsCounter();

        Thread findThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                findTps.update();
                findChunks();
                Thread.yield();
            }
        }, "Find Chunks Thread");
        findThread.setDaemon(true);
        findThread.start();

        Thread loadThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                loadTps.update();
                loadChunks();
                Thread.yield();
            }
        }, "Load Chunks Thread");
        loadThread.setDaemon(true);
        loadThread.start();

        Thread buildThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                buildTps.update();
                buildChunks();
                Thread.yield();
            }
        }, "Build Chunks Thread");
        buildThread.setDaemon(true);
        buildThread.start();

        Thread checkThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                checkTps.update();
                checkChunks();
                Thread.yield();
            }
        }, "Check Chunks Thread");
        checkThread.setDaemon(true);
        checkThread.start();
    }


    private void findChunks(){
        Vec3f camPos = getCamPos();

        if(toLoadQueue.size() == 0)
            ensureInChunkLoad(new ChunkPos(
                getChunkPos(camPos.xf()),
                getChunkPos(camPos.zf())
            ));

        for(ChunkPos chunkPos: chunks.keySet()){
            ensureInChunkLoad(chunkPos.getNeighbor(-1, 0));
            ensureInChunkLoad(chunkPos.getNeighbor(1, 0));
            ensureInChunkLoad(chunkPos.getNeighbor(0, -1));
            ensureInChunkLoad(chunkPos.getNeighbor(0, 1));
        }

        if(toLoadQueue.size() != 0){
            loadQueue.addAll(toLoadQueue);
            loadQueue.sort((pos1, pos2)->Maths.round(distToChunk(pos1.x, pos1.z, camPos) - Maths.round(distToChunk(pos2.x, pos2.z, camPos))));
            toLoadQueue.clear();
        }
    }

    private void loadChunks(){
        for(ChunkPos chunkPos: loadQueue){
            loadQueue.remove(chunkPos);
            if(chunkPos == null || isOffTheGrid(chunkPos))
                continue;

            loadChunk(chunkPos);
        }
    }

    private void buildChunks(){
        for(Chunk chunk: toBuildQueue){
            float[] vertices = ChunkBuilder.build(chunk);
            built.put(chunk, vertices);

            toBuildQueue.remove(chunk);
        }
    }

    public void checkChunks(){
        for(Chunk chunk: chunks.values())
            if(chunk.isDirty())
                rebuildChunk(chunk);

        loadQueue.removeIf(this::isOffTheGrid);

        for(Chunk chunk: chunks.values())
            if(isOffTheGrid(chunk.getPos()))
                unloadChunk(chunk);
    }

    public void updateMeshes(){
        for(Map.Entry<Chunk, float[]> entry: built.entrySet()){
            updateMesh(entry.getKey(), entry.getValue());
            built.remove(entry.getKey());
        }

        for(ChunkPos chunkPos: meshes.keySet())
            if(chunks.get(chunkPos) == null){
                meshes.get(chunkPos).dispose();
                meshes.remove(chunkPos);
            }
    }


    public void loadChunk(ChunkPos chunkPos){
        Chunk chunk = new Chunk(this, chunkPos);
        chunks.put(chunkPos, chunk);
        DefaultGenerator.getInstance().generate(chunk);

        updateNeighborChunksEdgesAndSelf(chunk, true);
        toBuildQueue.add(chunk);
    }

    public void unloadChunk(Chunk chunk){
        chunks.remove(chunk.getPos());
        updateNeighborChunksEdgesAndSelf(chunk, false);
    }

    public void updateMesh(Chunk chunk, float[] vertices){
        ChunkMesh mesh = meshes.get(chunk.getPos());
        if(mesh == null)
            mesh = new ChunkMesh();

        mesh.setVertices(vertices);
        meshes.put(chunk.getPos(), mesh);
        chunk.onMeshUpdate();
    }

    public void rebuildChunk(Chunk chunk){
        if(!toBuildQueue.contains(chunk) && !built.containsKey(chunk))
            toBuildQueue.add(chunk);
    }


    private void ensureInChunkLoad(ChunkPos chunkPos){
        if(toLoadQueue.size() >= MAX_LOAD_TASKS)
            return;

        if(isOffTheGrid(chunkPos))
            return;

        Chunk chunk = getChunk(chunkPos);
        if(chunk != null)
            return;

        if(!chunkPos.isInFrustum(session.getCamera()) || loadQueue.contains(chunkPos) || chunks.containsKey(chunkPos))
            return;

        toLoadQueue.add(chunkPos);
    }

    private void updateNeighborChunksEdgesAndSelf(Chunk chunk, boolean loaded){
        Chunk neighbor = chunks.get(chunk.getPos().getNeighbor(-1, 0));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.setBlock(SIZE, y, i, loaded ? chunk.getBlock(0, y, i) : BlockState.AIR);
                    if(loaded)
                        chunk.setBlock(-1, y, i, neighbor.getBlock(SIZE_IDX, y, i));
                }
        neighbor = chunks.get(chunk.getPos().getNeighbor(1, 0));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.setBlock(-1, y, i, loaded ? chunk.getBlock(SIZE_IDX, y, i) : BlockState.AIR);
                    if(loaded)
                        chunk.setBlock(SIZE, y, i, neighbor.getBlock(0, y, i));
                }
        neighbor = chunks.get(chunk.getPos().getNeighbor(0, -1));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.setBlock(i, y, SIZE, loaded ? chunk.getBlock(i, y, 0) : BlockState.AIR);
                    if(loaded)
                        chunk.setBlock(i, y, -1, neighbor.getBlock(i, y, SIZE_IDX));
                }
        neighbor = chunks.get(chunk.getPos().getNeighbor(0, 1));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.setBlock(i, y, -1, loaded ? chunk.getBlock(i, y, SIZE_IDX) : BlockState.AIR);
                    if(loaded)
                        chunk.setBlock(i, y, SIZE, neighbor.getBlock(i, y, 0));
                }
    }


    public Chunk getChunk(ChunkPos chunkPos){
        return chunks.getOrDefault(chunkPos, EMPTY_CHUNK);
    }

    public Chunk getChunk(int chunkX, int chunkZ){
        return getChunk(new ChunkPos(chunkX, chunkZ));
    }

    public Collection<Chunk> getChunks(){
        return chunks.values();
    }

    public Collection<Map.Entry<ChunkPos, ChunkMesh>> getMeshes(){
        return meshes.entrySet();
    }


    private boolean isOffTheGrid(int x, int z){
        return distToChunk(x, z, getCamPos()) > session.getOptions().getRenderDistance();
    }

    private boolean isOffTheGrid(ChunkPos chunkPos){
        return isOffTheGrid(chunkPos.x, chunkPos.z);
    }


    private float distToChunk(int x, int z, Vec3f camPos){
        return Vec2f.len(x - camPos.x + 0.5F, z - camPos.z + 0.5F);
    }

    private Vec3f getCamPos(){
        return session.getCamera().getPos().clone().div(SIZE);
    }

}
