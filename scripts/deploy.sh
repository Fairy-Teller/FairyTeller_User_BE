#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/be
cd $REPOSITORY

JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

APP_LOG="$REPOSITORY/application.log"
ERROR_LOG="$REPOSITORY/error.log"
DEPLOY_LOG="$REPOSITORY/deploy.log"

TIME_NOW=$(date +%c)

echo "$TIME_NOW> Deploy - $JAR_PATH "
echo "$TIME_NOW > $JAR_NAME 파일 실행" >> $DEPLOY_LOG
nohup java -jar $JAR_PATH > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_NAME)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다" >> $DEPLOY_LOG