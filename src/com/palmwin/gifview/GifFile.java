package com.palmwin.gifview;

import java.io.InputStream;

import android.support.v4.util.LruCache;

public class GifFile {

    private long size;
    public GifDecoder gifDecoder;
    private String fileName;
    private static LruCache<String, GifFile> gifFiles=new LruCache<String,GifFile>((int)Runtime.getRuntime().maxMemory()/1024/10) {

        @Override
        protected int sizeOf(String key, GifFile value) {
            return (int)value.size/1024;
        }
        
    };
    public GifFile(String fileName, InputStream input,boolean sync) {
        this.fileName=fileName;
        gifDecoder =new GifDecoder(input,fileName,sync);
    }
    public static GifFile getGifFile(String fileName,InputStream input,boolean sync){
        GifFile file=gifFiles.get(fileName);
        if(file!=null){
            return file;
        }else{
            file=new GifFile(fileName,input,sync);
            gifFiles.put(fileName, file);
            return file;
        }
    }
    public boolean isReady(){
        return gifDecoder.isReady();
    }
    public int getFrameCount(){
        return gifDecoder.getFrameCount();
    }
    public GifFrame getFrame(int frame){
        if(isReady())
        {
            return gifDecoder.getFrame(frame%gifDecoder.getFrameCount());
        }else{
            return null;
        }
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
}
