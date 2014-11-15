nohup $VIDEO_CONVERT_PATH/f_fmpeg -i ${srcPath} -vcodec libx264 -s ${previewWidth}x${previewHeight} -b ${bitrate} -r 24 -ac 2 -y -threads ${ffmpeg_thread} /tmp/tmp.mp4
rm -fr ${previewPath}
nohup $VIDEO_CONVERT_PATH/qt-faststart /tmp/tmp.mp4 ${previewPath}
