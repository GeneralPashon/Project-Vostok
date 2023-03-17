package megalul.projectvostok.chunk;

import glit.Glit;
import glit.io.glfw.Key;
import glit.math.Maths;
import glit.math.vecmath.vector.Vec2f;
import glit.math.vecmath.vector.Vec3f;
import glit.util.time.FpsCounter;
import glit.util.time.Sync;
import megalul.projectvostok.Main;
import megalul.projectvostok.block.BlockState;

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
        int renderDist = session.getOptions().getRenderDistance();

        int beginX = Maths.floor(camPos.x) - renderDist;
        int beginZ = Maths.floor(camPos.z) - renderDist;
        int endX = beginX + 1 + renderDist * 2;
        int endZ = beginZ + 1 + renderDist * 2;

        for(int x = beginX; x < endX; x++){
            for(int z = beginZ; z < endZ; z++){
                if(isOffTheGrid(x, z))
                    continue;

                Chunk chunk = getChunk(x, z);
                if(chunk != null)
                    continue;

                ChunkPos chunkPos = new ChunkPos(x, z);
                if(!chunkPos.isInFrustum(session.getCamera()) || chunkLoadQueue.contains(chunkPos) || loadedChunkList.containsKey(chunkPos))
                    continue;

                chunksToLoadQueue.add(chunkPos);
            }
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

    private void updateNeighborChunksEdgesAndSelf(Chunk chunk, boolean loaded){
        Chunk neighbor = loadedChunkList.get(chunk.getPos().neighbor(-1, 0));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.getField().set(SIZE, y, i, loaded ? chunk.getField().get(0, y, i) : BlockState.AIR);
                    if(loaded)
                        chunk.getField().set(-1, y, i, neighbor.getField().get(SIZE_IDX, y, i));
                }
        neighbor = loadedChunkList.get(chunk.getPos().neighbor(1, 0));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.getField().set(-1, y, i, loaded ? chunk.getField().get(SIZE_IDX, y, i) : BlockState.AIR);
                    if(loaded)
                        chunk.getField().set(SIZE, y, i, neighbor.getField().get(0, y, i));
                }
        neighbor = loadedChunkList.get(chunk.getPos().neighbor(0, -1));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.getField().set(i, y, SIZE, loaded ? chunk.getField().get(i, y, 0) : BlockState.AIR);
                    if(loaded)
                        chunk.getField().set(i, y, -1, neighbor.getField().get(i, y, SIZE_IDX));
                }
        neighbor = loadedChunkList.get(chunk.getPos().neighbor(0, 1));
        if(neighbor != null)
            for(int i = 0; i < SIZE; i++)
                for(int y = 0; y < HEIGHT; y++){
                    neighbor.getField().set(i, y, -1, loaded ? chunk.getField().get(i, y, SIZE_IDX) : BlockState.AIR);
                    if(loaded)
                        chunk.getField().set(i, y, SIZE, neighbor.getField().get(i, y, 0));
                }
    }


    public void updateChunks(){
        for(Chunk chunk: loadedChunkList.values())
            if(chunk.getField().isDirty())
                chunksToBuildQueue.add(chunk);
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

    public void updateMesh(Chunk chunk, float[] vertices){
        ChunkMesh mesh = meshList.get(chunk.getPos());
        if(mesh == null)
            mesh = new ChunkMesh();
        else
            System.out.println("UPDATED MESH");

        mesh.setVertices(vertices);
        meshList.put(chunk.getPos(), mesh);
        chunk.getField().built();
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
