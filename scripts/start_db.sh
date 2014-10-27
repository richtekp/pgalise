#!/bin/sh

# no further development, transformed to python script (due to better portability and scripting language preference)

POSTGRES_VERSION="9.1"
OSM_POSTGRES_VERSION="9.1"

BASE_DIR="/home/richter/DropBox/stratosphere/pgalise"
DATA_DIR=${BASE_DIR}/postgis_db-$POSTGRES_VERSION

LSB_RELEASE_ID_SHORT="`lsb_release -d -s`"
[ "$LSB_RELEASE_ID_SHORT" == *Ubuntu* ]; UBUNTU=$?
echo "$UBUNTU"
OPENSUSE=`expr index "$LSB_RELEASE_ID_SHORT" openSUSE`

if [ $UBUNTU -ne 0 ]; then
  POSTGRES="/usr/lib/postgresql/$POSTGRES_VERSION/bin/postgres";
  OSM_POSTGRES="/usr/lib/postgresql/$OSM_POSTGRES_VERSION/bin/postgres";
elif [ $OPENSUSE -ne 0 ]; then
  POSTGRES="/usr/bin/postgres";
  OSM_POSTGRES="/usr/bin/postgres";
else
  echo "Operating system not supported!"
  exit 1
fi

LOG_DIR="./log"
mkdir $LOG_DIR 2> /dev/null

TIMESTAMP="`date +%Y%m%d%H%M`"

TERMINAL="xterm -hold -e"

POSTGRES_LOG_FILE="$LOG_DIR/postgis-$TIMESTAMP.log"
( $TERMINAL $POSTGRES -D $DATA_DIR -p 5201 -h localhost -k /tmp > $POSTGRES_LOG_FILE ) &
POSTGRES_TERMINAL_P=$!
echo "started pgalise postgresql database process, logging to $POSTGRES_LOG_FILE"

OSM_BASE_DIR="/mnt/DATA/osm"
OSM_DATA_DIR=${OSM_BASE_DIR}/postgis_db_berlin

OSM_POSTGRES_LOG_FILE="$LOG_DIR/postgis-osm-$TIMESTAMP.log"
( $TERMINAL $OSM_POSTGRES -D $OSM_DATA_DIR -p 5204 -h localhost -k /tmp > $OSM_POSTGRES_LOG_FILE ) &
OSM_POSTGRES_TERMINAL_P=$!
echo "started PostGIS postgresql database process, logging to $OSM_POSTGRES_LOG_FILE"

echo "waiting for both terminal processes (of pgalise and PostGIS database processes) to terminate"

wait $POSTGRES_TERMINAL_P $OSM_POSTGRES_TERMINAL_P

