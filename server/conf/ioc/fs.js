var fs = {
	// Dao
	fsExtra : {
		type : 'org.octopus.core.fs.FsExtra',
		fields : {
			dao : {
				refer : "dao"
			}
		}
	},
	fsIO : {
		type : 'org.octopus.core.fs.FsIO',
		fields : {
			dao : {
				refer : "dao"
			},
			fsExtra : {
				refer : "fsExtra"
			}
		}
	}
}