#史上最快、最省内存的Android GIF播放器
#Howto

##set gif extract folder when app started
 
GifBitmapManager.getInstance().initFilePath(fileDir);

## Use GifView

gifView.setGif(path,name,width,height);

