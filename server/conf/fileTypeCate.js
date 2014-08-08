{
	// 文件类型
	"default": {
		"name": "xxx", // 文件后缀
		"as": "TXT", // T:文本 D:文件夹 B:二进制					
		"mime": "text/plain" // mimeType, 下载时使用
	},
	"support": [{
		"name": "folder",
		"as": "DIR"
	}, {
		"name": "bin",
		"as": "BIN",
		"mime": "application/octet-stream"
	}, {
		"name": "txt"
	}, {
		"name": "md"
	}, {
		"name": "zdoc"
	}, {
		"name": "jpg"
		"as": "BIN",
		"mime": "image/jpeg"
	}, {
		"name": "jpeg"
		"as": "BIN",
		"mime": "image/jpeg"
	}, {
		"name": "png"
		"as": "BIN",
		"mime": "image/png"
	}, {
		"name": "gif"
		"as": "BIN",
		"mime": "image/gif"
	}]
	// 文件类型分类
	"cate": [{
		"name": "video",
		"types": ["mp4", "avi", "wmv", "mpg", "mov", "3gp"]
	}, {
		"name": "music",
		"types": ["mp3", "ogg"]
	}, {
		"name": "image",
		"types": ["jpg", "jpeg", "png", "gif"]
	}, {
		"name": "package",
		"types": ["zip", "rar", "tar", "gz", "7z"]
	}, {
		"name": "text",
		"types": ["txt", "md", "zdoc"]
	}, {
		"name": "sourceCode",
		"types": ["sh", "bat", "js", "json", "java", "py", "go", "rb"]
	}]
}