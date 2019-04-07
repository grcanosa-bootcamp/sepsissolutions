#!/bin/sh

#
# generador.sh
#

dir="$1"
if [ -z "$dir" ] ; then
    echo "Falta especificar el directorio de destino"
    exit 1
fi
dir="$(readlink -f "$dir")"

base="$(dirname $0)/BASE.csv"
if [ -f "${base}.gz" ] ; then
    zcat "${base}.gz"
else
    cat "$base"
fi |
awk -v now="$(date +%Y%m%d%H%M%S -d "now")" \
    -v yesterday="$(date +%Y%m%d%H%M%S -d "yesterday")" \
    -F\| \
    '
    BEGIN{OFS=FS}
    yesterday<=$NF && $NF<now { NF-- ; print }
    ' > "$dir/pruebas_$(date +%Y%m%dT%H%M%S).csv"
