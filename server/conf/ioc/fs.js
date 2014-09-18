var fs = {
	// Dao
	fsExtraMaker : {
		type : 'org.octopus.core.fs.FsExtraMaker',
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
			extraMaker : {
				refer : "fsExtraMaker"
			}
		}
	}
}