package com.google.samples.apps.topeka.trail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.google.samples.apps.topeka.R;
import com.jiahuan.svgmapview.SVGMapView;
import com.jiahuan.svgmapview.overlay.SVGMapBaseOverlay;

/**
 * Created by jiahuan on 2015/7/12.
 */
public class BitmapOverlay extends SVGMapBaseOverlay
{
    private SVGMapView mMapView;
    private int x1 = 500;
    private int y1 = 300;
    private int x2 = 300;
    private int y2 = 500;
    private int x3 = 500;
    private int y3 = 500;
    private Bitmap mBitmap;
    Activity temp;
    public BitmapOverlay(SVGMapView svgMapView, Activity temp, int redNum)
    {
        this.temp =temp;
        initLayer(svgMapView);
        int RED =  Color.RED ;int GREEN1 =  Color.GREEN ;int GREEN2 =  Color.GREEN ;
        if (redNum==1) {
            mMapView.getController().sparkAtPoint(new PointF(x1, y1), 50, RED, 1000);

            mMapView.getController().sparkAtPoint(new PointF(x2, y2), 50, GREEN1, 1000);

            mMapView.getController().sparkAtPoint(new PointF(x3, y3), 50, GREEN2, 1000);
        }else if (redNum==2) {
            mMapView.getController().sparkAtPoint(new PointF(x1, y1), 50, GREEN1, 1000);

            mMapView.getController().sparkAtPoint(new PointF(x2, y2), 50, RED, 1000);

            mMapView.getController().sparkAtPoint(new PointF(x3, y3), 50, GREEN2, 1000);
        }
        else if (redNum==3) {
            mMapView.getController().sparkAtPoint(new PointF(x1, y1), 50, GREEN2, 1000);

            mMapView.getController().sparkAtPoint(new PointF(x2, y2), 50, GREEN1, 1000);

            mMapView.getController().sparkAtPoint(new PointF(x3, y3), 50, RED, 1000);
        }
    }

    private void initLayer(SVGMapView svgMapView)
    {
        mMapView = svgMapView;
        mBitmap = BitmapFactory.decodeResource(svgMapView.getResources(), R.mipmap.ic_launcher);
    }


    @Override
    public void onDestroy()
    {

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onTap(MotionEvent event)
    {

        float[] mapCoordinate = mMapView.getMapCoordinateWithScreenCoordinate(event.getX(), event.getY());
        Log.i("test","mapcoordinate:  "+ mapCoordinate);
        if (mapCoordinate[0] >= x1 && mapCoordinate[0] <= x1 + mBitmap.getWidth() && mapCoordinate[1] >= y1 && mapCoordinate[1] <= y1 + mBitmap.getHeight())
        {
            /*Toast.makeText(mMapView.getContext(), "Clicked on bitmap11111",Toast.LENGTH_LONG).show();*/
            Intent intent = new Intent(temp, InforTab.class);
            intent.putExtra("id", 1);
            temp.startActivity(intent);
            //startActivity(intent);
             Log.i("test","Clicked on bitmap11111" );
        }
        if (mapCoordinate[0] >= x2 && mapCoordinate[0] <= x2 + mBitmap.getWidth() && mapCoordinate[1] >= y2 && mapCoordinate[1] <= y2 + mBitmap.getHeight())
        {
            Intent intent = new Intent(temp, InforTab.class);
            intent.putExtra("id", 2);
            temp.startActivity(intent);
            Log.i("test","Clicked on bitmap2222" );
            /*Toast.makeText(mMapView.getContext(), "Clicked on bitmap2222",Toast.LENGTH_LONG).show();*/
        }
        if (mapCoordinate[0] >= x3 && mapCoordinate[0] <= x3 + mBitmap.getWidth() && mapCoordinate[1] >= y3 && mapCoordinate[1] <= y3 + mBitmap.getHeight())
        {
            Intent intent = new Intent(temp, InforTab.class);
            intent.putExtra("id", 3);
            temp.startActivity(intent);
            Log.i("test","Clicked on bitmap2222" );
            /*Toast.makeText(mMapView.getContext(), "Clicked on bitmap2222",Toast.LENGTH_LONG).show();*/
        }
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix, float currentZoom, float currentRotateDegrees)
    {
       /* canvas.save();
        canvas.setMatrix(matrix);

        canvas.drawBitmap(mBitmap, x1, y1, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.drawBitmap(mBitmap, x2, y2, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.restore();*/
    }
}
