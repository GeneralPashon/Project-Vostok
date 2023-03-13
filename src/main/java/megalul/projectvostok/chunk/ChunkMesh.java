package megalul.projectvostok.chunk;

import glit.context.Disposable;
import glit.graphics.gl.BufferUsage;
import glit.graphics.gl.Primitive;
import glit.graphics.gl.Type;
import glit.graphics.vertex.VertexArray;
import glit.graphics.vertex.VertexAttr;
import glit.graphics.vertex.VertexBuffer;

public class ChunkMesh implements Disposable{

    private Primitive mode;
    private final VertexArray vao;
    private final VertexBuffer vbo;

    public ChunkMesh(){
        mode = Primitive.TRIANGLES;

        vao = new VertexArray();
        vbo = new VertexBuffer();
        vbo.enableAttributes(new VertexAttr(3, Type.FLOAT), new VertexAttr(4, Type.FLOAT), new VertexAttr(2, Type.FLOAT)); // pos3, col4, tex2
    }

    public void render(){
        vao.drawArrays(vbo.getVertexCount(), mode);
    }

    public void setRenderMode(Primitive mode){
        this.mode = mode;
    }


    public void setVertices(float[] vertices){
        vbo.setData(vertices, BufferUsage.DYNAMIC_DRAW);
    }


    @Override
    public void dispose(){
        vbo.dispose();
        vao.dispose();
    }



}
