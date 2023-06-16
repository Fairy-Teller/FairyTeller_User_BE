#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/be
cd $REPOSITORY

JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

DEPLOY_LOG="$REPOSITORY/deploy.log"

CURRENT_PID=$(pgrep -f $JAR_NAME)

TIME_NOW=$(date +%c)

if [ -z $CURRENT_PID ] ;
then
  echo "$TIME_NOW > 종료할 애플리케이션이 없습니다." >> $DEPLOY_LOG
else
  echo "$TIME_NOW > kill -9 $CURRENT_PID" >> $DEPLOY_LOG
  kill -9 $CURRENT_PID
fi