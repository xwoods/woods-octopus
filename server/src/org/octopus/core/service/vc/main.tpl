
nohup $VIDEO_CONVERT_PATH/f_fmpeg -i ${srcPath} -loglevel quiet -b ${bitrate} -r 24 -y -threads ${ffmpeg_thread} \
	${other} > /dev/null
