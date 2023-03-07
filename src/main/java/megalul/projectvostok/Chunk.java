package megalul.projectvostok;

import glit.graphics.texture.Pixmap;
import glit.graphics.texture.Texture;

public class Chunk extends ChunkUtils{

    public static final int SIZE_XZ = 16;
    public static final int SECTIONS_NUM = 16;
    public static final int HEIGHT = SECTIONS_NUM * ChunkSection.SIZE;


    private final ChunkPos position;
    private final ChunkHeightMap heightMap;
    private final ChunkSection[] sections;

    public Chunk(ChunkPos position){
        this.position = position;
        heightMap = new ChunkHeightMap();
        sections = new ChunkSection[SECTIONS_NUM];

        for(int i = 0; i < sections.length; i++)
            sections[i] = new ChunkSection();
    }


    public BlockState getBlock(int x, int y, int z){
        int index = getIndex(y);
        if(isOutOfBounds(index))
            return AIR;

        return getSection(index).getBlocks().get(x, getLocalY(y), z);
    }

    public int getBlockIDNow(int x, int y, int z){
        int index = getIndex(y);
        return getSection(index).getBlocks().getIDNow(x, getLocalY(y), z);
    }

    public void setBlock(int x, int y, int z, BlockState block){
        int index = getIndex(y);
        if(isOutOfBounds(index))
            return;

        boolean isChanged = getSection(index).getBlocks().set(x, getLocalY(y), z, block);
        if(isChanged){
            int height = heightMap.getHeight(x, z);

            if(y == height && block.type == BlockType.AIR)
                for(height--; getBlockIDNow(x, height, z) == BlockType.AIR.id && height > 0; )
                    height--;
            else if(y > height && block.type != BlockType.AIR)
                height = y;

            heightMap.setHeight(x, z, height);
        }
    }


    public Texture texture;

    public void buildTexture(){
        Pixmap pixmap = new Pixmap(16, 16);
        for(int i = 0; i < SIZE_XZ; i++)
            for(int j = 0; j < SIZE_XZ; j++){
                float grayScale = (float) heightMap.getHeight(i, j) / HEIGHT;
                pixmap.setPixel(i, 15 - j, grayScale, grayScale, grayScale, 1);
            }

        texture = new Texture(pixmap);
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
        if(index >= SECTIONS_NUM || index < 0)
            return null;

        return sections[index];
    }

}
