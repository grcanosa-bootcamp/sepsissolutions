#!/bin/sh


while true; do
if [ -f /data/new_file ]; then
    echo "Generating new file"
    rm /data/new_file
    ./generador.sh /data/
else
    echo "Not generating new file"
fi
sleep 10

done