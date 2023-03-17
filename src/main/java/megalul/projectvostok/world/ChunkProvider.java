package megalul.projectvostok.world;

import glit.math.Maths;
import glit.math.vecmath.vector.Vec2f;
import glit.math.vecmath.vector.Vec3f;
import glit.util.time.FpsCounter;
import glit.util.time.Sync;
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
    private static final int MAX_LOADS_PER_TICK = 64;

    private final Main session;

    private final ConcurrentMap<ChunkPos, Chunk> loadedChunkList;
    private final CopyOnWriteArrayList<ChunkPos> chunkLoadQueue;
    private final Set<ChunkPos> chunksToLoadQueue;

    private final CopyOnWriteArrayList<Chunk> chunksToBuildQueue;
    private final ConcurrentMap<Chunk, float[]> builtChunksList;
    private final ConcurrentMap<ChunkPos, ChunkMesh> meshList;

    public final FpsCounter findTps, loadTps, unloadTps, buildTps, updateTps;
    private final Sync findSync, loadSync, unloadSync, buildSync, updateSync;


    public ChunkProvider(Main session){
        this.session = session;

        loadedChunkList = new ConcurrentHashMap<>();

        chunksToBuildQueue = new CopyOnWriteArrayList<>();
        builtChunksList = new ConcurrentHashMap<>();
        meshList = new ConcurrentHashMap<>();

        chunksToLoadQueue = new HashSet<>();
        chunkLoadQueue = new CopyOnWriteArrayList<>();

        findTps = new FpsCounter();
        loadTps = new FpsCounter();
        unloadTps = new FpsCounter();
        buildTps = new FpsCounter();
        updateTps = new FpsCounter();

        findSync = new Sync(20);
        loadSync = new Sync(20);
        unloadSync = new Sync(20);
        buildSync = new Sync(20);
        updateSync = new Sync(20);

        Thread findThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                findTps.update();
                findSync.sync();
                findChunks();
            }
        }, "Find Chunks Thread");
        findThread.setDaemon(true);
        findThread.start();

        Thread loadThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                loadTps.update();
                loadSync.sync();
                loadChunks();
            }
        }, "Load Chunks Thread");
        loadThread.setDaemon(true);
        loadThread.start();

        Thread unloadThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                unloadTps.update();
                unloadSync.sync();
                unloadChunks();
            }
        }, "Unload Chunks Thread");
        unloadThread.setDaemon(true);
        unloadThread.start();

        Thread buildThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                buildTps.update();
                buildSync.sync();
                buildChunks();
            }
        }, "Build Chunks Thread");
        buildThread.setDaemon(true);
        buildThread.start();

        Thread updateThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                updateTps.update();
                updateSync.sync();
                updateChunks();
            }
        }, "Update Chunks Thread");
        updateThread.setDaemon(true);
        updateThread.start();
    }


    private void findChunks(){
        Vec3f camPos = getCamPos();

        if(loadedChunkList.size() == 0)
            ensureInChunkLoad(new ChunkPos(
                getChunkPos(camPos.xf()),
                getChunkPos(camPos.zf())
            ));

        for(ChunkPos chunkPos: loadedChunkList.keySet()){
            ensureInChunkLoad(chunkPos.getNeighbor(-1, 0));
            ensureInChunkLoad(chunkPos.getNeighbor(1, 0));
            ensureInChunkLoad(chunkPos.getNeighbor(0, -1));
            ensureInChunkLoad(chunkPos.getNeighbor(0, 1));
        }

        if(chunksToLoadQueue.size() != 0){
            chunkLoadQueue.addAll(chunksToLoadQueue);
            chunkLoadQueue.sort((pos1, pos2)->Maths.round(distToChunk(pos1.x, pos1.z, camPos) - Maths.round(distToChunk(pos2.x, pos2.z, camPos))));
            chunksToLoadQueue.clear();
        }
    }

    private void loadChunks(){
        int chunkLoadedNum = 0;
        for(ChunkPos chunkPos: chunkLoadQueue){
            if(chunkLoadedNum >= MAX_LOADS_PER_TICK)
                break;

            chunkLoadQueue.remove(chunkPos);
            if(chunkPos == null || isOffTheGrid(chunkPos))
                continue;

            // while(lock);
            // lock = true;
            loadChunk(chunkPos);
            chunkLoadedNum++;
        }
    }

    public void unloadChunks(){
        chunkLoadQueue.removeIf(this::isOffTheGrid);

        for(Chunk chunk: loadedChunkList.values())
            if(isOffTheGrid(chunk.getPos()))
                unloadChunk(chunk);
    }

    private void buildChunks(){
        for(Chunk chunk: chunksToBuildQueue){
            float[] vertices = ChunkBuilder.build(chunk);
            builtChunksList.put(chunk, vertices);

            chunksToBuildQueue.remove(chunk);
        }
    }

    public void updateChunks(){
        for(Chunk chunk: loadedChunkList.values())
            if(chunk.isDirty())
                rebuildChunk(chunk);
    }

    public void updateMeshes(){
        for(Map.Entry<Chunk, float[]> entry: builtChunksList.entrySet()){
            updateMesh(entry.getKey(), entry.getValue());
            builtChunksList.remove(entry.getKey());
        }

        for(ChunkPos chunkPos: meshList.keySet())
            if(loadedChunkList.get(chunkPos) == null){
                meshList.get(chunkPos).dispose();
                meshList.remove(chunkPos);
            }
    }


    public void loadChunk(ChunkPos chunkPos){
        Chunk chunk = new Chunk(this, chunkPos);
        loadedChunkList.put(chunkPos, chunk);
        DefaultGenerator.getInstance().generate(chunk);

        updateNeighborChunksEdgesAndSelf(chunk, true);
        chunksToBuildQueue.add(chunk);
    }

    public void unloadChunk(Chunk chunk){
        loadedChunkList.remove(chunk.getPos());
        updateNeighborChunksEdgesAndSelf(chunk, false);
    }

    public void updateMesh(Chunk chunk, float[] vertices){
        ChunkMesh mesh = meshList.get(chunk.getPos());
        if(mesh == null)
            mesh = new ChunkMesh();

        mesh.setVertices(vertices);
        meshList.put(chunk.getPos(), mesh);
        chunk.onMeshUpdate();
    }

    public void rebuildChunk(Chunk chunk){
        if(!chunksToBuildQueue.contains(chunk) && !builtChunksList.containsKey(chunk))
            chunksToBuildQueue.add(chunk);
    }


    private void ensureInChunkLoad(ChunkPos chunkPos){
        if(isOffTheGrid(chunkPos))
            return;

        Chunk chunk = getChunk(chunkPos);
        if(chunk != null)
            return;

        if(!chunkPos.isInFrustum(session.getCamera()) || chunkLoadQueue.contains(chunkPos) || loadedChunkList.containsKey(chunkPos))
            return;

        chunksToLoadQueue.add(chunkPos);
    }

    private void updateNeighborChunksEdgesAndSelf(Chunk chunk, boolean loaded){
        Chunk neighbor = loadedChunkList.get(chunk.getPos().getNeighbor(-1, 0));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.setBlock(SIZE, y, i, loaded ? chunk.getBlock(0, y, i) : BlockState.AIR);
                    if(loaded)
                        chunk.setBlock(-1, y, i, neighbor.getBlock(SIZE_IDX, y, i));
                }
        neighbor = loadedChunkList.get(chunk.getPos().getNeighbor(1, 0));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.setBlock(-1, y, i, loaded ? chunk.getBlock(SIZE_IDX, y, i) : BlockState.AIR);
                    if(loaded)
                        chunk.setBlock(SIZE, y, i, neighbor.getBlock(0, y, i));
                }
        neighbor = loadedChunkList.get(chunk.getPos().getNeighbor(0, -1));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.setBlock(i, y, SIZE, loaded ? chunk.getBlock(i, y, 0) : BlockState.AIR);
                    if(loaded)
                        chunk.setBlock(i, y, -1, neighbor.getBlock(i, y, SIZE_IDX));
                }
        neighbor = loadedChunkList.get(chunk.getPos().getNeighbor(0, 1));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.setBlock(i, y, -1, loaded ? chunk.getBlock(i, y, SIZE_IDX) : BlockState.AIR);
                    if(loaded)
                        chunk.setBlock(i, y, SIZE, neighbor.getBlock(i, y, 0));
                }
    }


    public Chunk getChunk(ChunkPos chunkPos){
        return loadedChunkList.getOrDefault(chunkPos, EMPTY_CHUNK);
    }

    public Chunk getChunk(int chunkX, int chunkZ){
        return getChunk(new ChunkPos(chunkX, chunkZ));
    }

    public Collection<Chunk> getChunks(){
        return loadedChunkList.values();
    }

    public Collection<Map.Entry<ChunkPos, ChunkMesh>> getMeshes(){
        return meshList.entrySet();
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
