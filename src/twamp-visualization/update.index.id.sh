#!/bin/bash

for num in 1 5 10 50 100 500
do

echo "count = ${num}"

sed -i -e "s/$1/$2/g" DuplicatePackets-${num}.json
sed -i -e "s/$1/$2/g" InterDelay-${num}.json
sed -i -e "s/$1/$2/g" IPDV-${num}.json
sed -i -e "s/$1/$2/g" LostPackets-${num}.json
sed -i -e "s/$1/$2/g" OoOP-${num}.json
sed -i -e "s/$1/$2/g" PDV-${num}.json
sed -i -e "s/$1/$2/g" RTT-${num}.json
sed -i -e "s/$1/$2/g" LatencyDelay-${num}.json
sed -i -e "s/$1/$2/g" TTL-${num}.json

done
