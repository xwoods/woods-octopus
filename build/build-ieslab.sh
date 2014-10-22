#! /bin/bash
export CONF_NM=ieslab
export OCT_PATH=$HOME/gitrep/nutz-xwoods/woods-octopus
export OCT_LIB=$HOME/deps/project/ocotpus
export BUILD_PATH=$HOME/tmp/octopus-publish

mkdir $BUILD_PATH

rm -r $BUILD_PATH/*

function cp2buildWithROOT() {
	TARGET_PATH=$1
	cp -rf $TARGET_PATH/src $BUILD_PATH
	cp -rf $TARGET_PATH/conf $BUILD_PATH
	cp -rf $TARGET_PATH/ROOT $BUILD_PATH
	cp -rf $TARGET_PATH/rs $BUILD_PATH/ROOT/
	cp -rf $TARGET_PATH/views $BUILD_PATH/ROOT/
}

function cp2build() {
	TARGET_PATH=$1
	cp -rf $TARGET_PATH/src $BUILD_PATH
	cp -rf $TARGET_PATH/conf $BUILD_PATH
	cp -rf $TARGET_PATH/rs $BUILD_PATH/ROOT/
	cp -rf $TARGET_PATH/views $BUILD_PATH/ROOT/
}

# cp依赖jar
mkdir $BUILD_PATH/lib
cp -rf $OCT_LIB/* $BUILD_PATH/lib/

# cp所有需要编译的项目
cp2buildWithROOT $OCT_PATH/server
cp2build $HOME/gitrep/xwoods/octopus-danoo
cp2build $HOME/gitrep/pangwu86/octopus-ieslab/server

# build_jar
export BUILD_DEPS=$OCT_LIB
export BUILD_OUT=$BUILD_PATH
export BUILD_SRC=$BUILD_PATH/src
export BUILD_TMP=$BUILD_PATH/tmp

ant -f build.xml

# cp配置文件覆盖
cp -rf $OCT_PATH/myconf/$CONF_NM/* $BUILD_PATH/conf

# cp运行的sh
cp -rf $OCT_PATH/build/app_* $BUILD_PATH

# 清理没用的文件
rm -r $BUILD_PATH/src

# 打包
CD=`date +%Y%m%d`
cd $BUILD_PATH
tar zcvf octopus2-$CD.tar.gz *

