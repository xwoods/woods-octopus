var ioc = {
	// 配置文件
	conf : {
		type : 'org.octopus.OctopusConfig',
		args : [ 'web.properties' ]
	},
	// 数据源
	dataSource : {
		type : "com.alibaba.druid.pool.DruidDataSource",
		events : {
			depose : "close"
		},
		fields : {
			driverClassName : {
				java : "$conf.get('db-driver')"
			},
			url : {
				java : "$conf.get('db-url')"
			},
			username : {
				java : "$conf.get('db-username')"
			},
			password : {
				java : "$conf.get('db-password')"
			},
			initialSize : 10,
			maxActive : 100,
			testOnReturn : true,
			validationQuery : "select 1",
			filters : "stat"
		}
	},
	// Dao
	dao : {
		type : 'org.nutz.dao.impl.NutDao',
		args : [ {
			refer : "dataSource"
		}, {
			type : 'org.nutz.dao.impl.FileSqlManager',
			args : [ 'sql' ]
		} ]
	}
};