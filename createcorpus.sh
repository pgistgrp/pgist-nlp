#!/bin/sh

export INFOMAP_WORKING_DIR=/tmp/corpus

rm -rf $INFOMAP_WORKING_DIR
mkdir $INFOMAP_WORKING_DIR

infomap-build -s $1 $1

infomap-install $1


