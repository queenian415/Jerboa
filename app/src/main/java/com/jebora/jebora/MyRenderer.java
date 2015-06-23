package com.jebora.jebora;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mshzhb on 15/6/23.
 */
public class MyRenderer implements GLSurfaceView.Renderer {
    public Resources res;
    // scale
    private int thingScale = 1;
    // world对象
    private World world;
    // FrameBuffer对象
    private FrameBuffer fb;
    // Object3D
    private Object3D o3d;
    //light
    private Light light;
    private GLSLShader shader;
    public Bitmap composedTexture;
    public static int type = 0; // 0 : T-shirt
    public static int x = 690;
    public static int y = 600;




    public void onDrawFrame(GL10 gl) {


        if (LoadModels.dx != 0) {
            o3d.rotateY(LoadModels.dx);
            LoadModels.dx = 0;
        }

        if (LoadModels.dy != 0) {
            //o3d.rotateX(LoadModels.dy);
            LoadModels.dy = 0;
        }

        // 以黑色清除整个屏幕
        fb.clear(RGBColor.BLACK);
        // 对所有多边形进行变换及灯光操作
        world.renderScene(fb);
        // 绘制已经由renderScene产生的fb
        world.draw(fb);
        // 渲染显示图像
        fb.display();

    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (fb != null) {
            fb = null;
        }



        fb = new FrameBuffer(gl, width, height);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
        res = LoadAssets.res;
        world = new World();
        // 设置环境光


        light = new Light(world);
        light.enable();

        light.setIntensity(160, 150, 150);
        light.setPosition(SimpleVector.create(-10, -50, -100));
        world.setAmbientLight(30, 30, 30);

        composedTexture = composeMyTexture();

        //贴图
        TextureManager tm = TextureManager.getInstance();
        Texture face = new Texture(composedTexture);
        //Texture texture = new Texture(BitmapHelper.rescale(LoadImage.bitmap, 64, 64));
        //tm.addTexture("texture", texture);
        //Texture face = new Texture(res.openRawResource(R.raw.color_1));
        //Texture nazi = new Texture(res.openRawResource(R.raw.nazi));
        //Texture normals = new Texture(res.openRawResource(R.raw.nmm), true);
        //normals.removeAlpha();

        tm.addTexture("face", face);
        //tm.addTexture("nazi", nazi);
        //tm.addTexture("normals", normals);

        TextureInfo ti = new TextureInfo(TextureManager.getInstance().getTextureID("face"));
        //ti.add(TextureManager.getInstance().getTextureID("nazi"), TextureInfo.MAX_PHYSICAL_TEXTURE_STAGES);
        //ti.add(TextureManager.getInstance().getTextureID("normals"), TextureInfo.MODE_BLEND);


        shader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.vertexshader_offset)), Loader.loadTextFile(res.openRawResource(R.raw.fragmentshader_offset)));


        o3d = loadModel("shirt.3ds", thingScale);

        o3d.setShader(shader);
        o3d.setSpecularLighting(true);
        // 为Object3D对象设置纹理
        // o3d.setTexture("texture");
        o3d.setTexture(ti);
        // 渲染绘制前进行的操作
        o3d.build();
        // 将thing添加Object3D对象中
        world.addObject(o3d);
        // 调整坐标系
        Camera cam = world.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 120);
        cam.moveCamera(Camera.CAMERA_MOVEUP, 65);
        cam.moveCamera(Camera.CAMERA_MOVEIN, 40);
        // 在world中应用刚设置好了的坐标系
        cam.lookAt(o3d.getTransformedCenter());
        MemoryHelper.compact();
    }

    // 载入模型
    private Object3D loadModel(String filename, float scale) {
        // 将载入的3ds文件保存到model数组中
        Object3D[] model = com.threed.jpct.Loader.load3DS(LoadAssets.loadf(filename), scale);
        // 取第一个3ds文件
        Object3D o3d = new Object3D(0);
        // 临时变量temp
        Object3D temp = null;
        // 遍历model数组
        for (int i = 0; i < model.length; i++) {
            // 给temp赋予model数组中的某一个
            temp = model[i];
            // 设置temp的中心为 origin (0,0,0)
            temp.setCenter(SimpleVector.ORIGIN);
            // 沿x轴旋转坐标系到正常的坐标系(jpct-ae的坐标中的y,x是反的)
            temp.rotateX((float) (-.5 * Math.PI));
            // 使用旋转矩阵指定此对象旋转网格的原始数据
            temp.rotateMesh();
            // new 一个矩阵来作为旋转矩阵
            temp.setRotationMatrix(new Matrix());
            // 合并o3d与temp
            o3d = Object3D.mergeObjects(o3d, temp);
            // 主要是为了从桌面版JPCT到android版的移徝(桌面版此处为o3d.build())
            o3d.compile();
        }
        // 返回o3d对象
        return o3d;
    }

    private Bitmap composeMyTexture()
    {
        Bitmap background = null;
        Bitmap logo = null;

        if(type == 0) //T-shirt
        {
            background = BitmapFactory.decodeStream(res.openRawResource(R.raw.color));
            logo = BitmapFactory.decodeStream(res.openRawResource(R.raw.logo));
        }

        return ImageFilter.mergeBitmap(background,logo,type);


    }


}
