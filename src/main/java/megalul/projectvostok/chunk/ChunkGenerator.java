package megalul.projectvostok.chunk;

import glit.math.Maths;
import glit.math.function.FastNoiseLite;
import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.block.BlockType;

public class ChunkGenerator{

    private static final FastNoiseLite noise = new FastNoiseLite();
    static{
        noise.setFrequency(0.007F);
    }

    public static void generate(Chunk chunk){
        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++){
                int y = Maths.round(noise.getNoise(i + 16 * chunk.getPos().x, j + 16 * chunk.getPos().z) * 24 + 64);
                chunk.getBlocks().set(i, y, j, new BlockState(BlockType.DIRT));
            }
    }

}
