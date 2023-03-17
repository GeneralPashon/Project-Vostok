package megalul.projectvostok.chunk;

import glit.math.Maths;
import glit.math.function.FastNoiseLite;
import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.block.Block;

public class DefaultGenerator implements ChunkGenerator{

    private final FastNoiseLite noise = new FastNoiseLite();

    private DefaultGenerator(){
        noise.setFrequency(0.007F);
        noise.setSeed((int) Maths.randomSeed(8));
    }

    @Override
    public void generate(Chunk chunk){
        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++){
                int y = Maths.round(noise.getNoise(i + 16 * chunk.getPos().x, j + 16 * chunk.getPos().z) * 16 + 128);
                chunk.getField().set(i, y, j, new BlockState(Block.DIRT));
            }
    }


    private static DefaultGenerator instance;

    public static DefaultGenerator getInstance(){
        if(instance == null)
            instance = new DefaultGenerator();

        return instance;
    }

}
