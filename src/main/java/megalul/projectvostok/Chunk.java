package megalul.projectvostok;

public class Chunk{

    public static final int CHUNK_SIZE = 16;
    public static final int SECTIONS = 16;


    private final ChunkPos position;
    private final ChunkHeightMap heightMap;
    private final ChunkSection[] sections;

    public Chunk(ChunkPos position){
        this.position = position;
        heightMap = new ChunkHeightMap();
        sections = new ChunkSection[SECTIONS];

        for(int i = 0; i < sections.length; i++)
            sections[i] = new ChunkSection();
    }




    public ChunkPos getPos(){
        return position;
    }

    public ChunkHeightMap getHeightMap(){
        return heightMap;
    }

    public ChunkSection[] getSections(){
        return sections;
    }

    public ChunkSection getSection(int index){
        if(index >= SECTIONS || index < 0)
            return null;

        return sections[index];
    }

}
