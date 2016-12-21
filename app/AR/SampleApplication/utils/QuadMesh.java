package com.google.samples.apps.topeka.AR.SampleApplication.utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Nguyen Hai on 1/3/2016.
 */
public class QuadMesh {
    private final float[] mVertices = new float[] {
            -0.5f, -0.5f, 0.0f, //bottom-left corner
            0.5f, -0.5f, 0.0f, //bottom-right corner
            0.5f, 0.5f, 0.0f, //top-right corner
            -0.5f, 0.5f, 0.0f //top-left corner
    };
    private final float[] mNormals = new float[] {
            0.0f, 0.0f, 1.0f, //normal at bottom-left corner
            0.0f, 0.0f, 1.0f, //normal at bottom-right corner
            0.0f, 0.0f, 1.0f, //normal at top-right corner
            0.0f, 0.0f, 1.0f //normal at top-left corner
    };
    private final float[] mTexCoords = new float[] {
            0.0f, 0.0f, //tex-coords at bottom-left corner
            1.0f, 0.0f, //tex-coords at bottom-right corner
            1.0f, 1.0f, //tex-coords at top-right corner
            0.0f, 1.0f //tex-coords at top-left corner
    };
    private final short[] mIndices = new short[] {
            0,1,2, //triangle 1
            2,3,0 // triangle 2
    };
    public final Buffer vertices;
    public final Buffer normals;
    public final Buffer texCoords;
    public final Buffer indices;
    public final int numVertices = mVertices.length/3; //3 coords per vertex
    public final int numIndices = mIndices.length;
    public QuadMesh() {
// init vertex buffers
        vertices = fillBuffer(mVertices);
        normals = fillBuffer(mNormals);
        texCoords = fillBuffer(mTexCoords);
        indices = fillBuffer(mIndices);
    }
    private Buffer fillBuffer(float[] array) {
        final int sizeOfFloat = 4;//1 float = 4 bytes
        ByteBuffer bb = ByteBuffer.allocateDirect(sizeOfFloat * array.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (float f : array) {
            bb.putFloat(f);
        }
        bb.rewind();
        return bb;
    }
    private Buffer fillBuffer(short[] array) {
        final int sizeOfShort = 2;//1 short = 2 bytes
        ByteBuffer bb = ByteBuffer.allocateDirect(sizeOfShort * array.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (short s : array) {
            bb.putShort(s);
        }
        bb.rewind();
        return bb;
    }
}
