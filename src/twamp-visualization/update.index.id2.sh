#!/bin/bash

ID="39463e30-56a8-11e9-be92-2f083c355e12"

for num in 1 10 60 300 600 3600 7200
do

echo "count = ${num}"

sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/Duplicate Packets/g" | sed -e "s/{2}/duplicate_packets/g" | sed -e "s/{3}/Duplicate Packets/g" | sed -e "s/{4}/${ID}/g" -> DuplicatePackets-${num}.json
sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/Latency Delay/g" | sed -e "s/{2}/latency_delay/g" | sed -e "s/{3}/Latency Delay/g" | sed -e "s/{4}/${ID}/g" -> LatencyDelay-${num}.json
sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/Lost Packets/g" | sed -e "s/{2}/lost_packets/g" | sed -e "s/{3}/Lost Packets/g" | sed -e "s/{4}/${ID}/g" -> LostPackets-${num}.json
sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/Out of Order Packets/g" | sed -e "s/{2}/outoforder_packets/g" | sed -e "s/{3}/Out of Order Packets/g" | sed -e "s/{4}/${ID}/g" -> OoOP-${num}.json
sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/TTL/g" | sed -e "s/{2}/ttl/g" | sed -e "s/{3}/TTL/g" | sed -e "s/{4}/${ID}/g" -> TTL-${num}.json

sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/UP Duplicate Packets/g" | sed -e "s/{2}/up_duplicate_packets/g" | sed -e "s/{3}/UP Duplicate Packets/g" | sed -e "s/{4}/${ID}/g" -> UpDuplicatePackets-${num}.json
sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/Down Duplicate Packets/g" | sed -e "s/{2}/down_duplicate_packets/g" | sed -e "s/{3}/Down Duplicate Packets/g" | sed -e "s/{4}/${ID}/g" -> DownDuplicatePackets-${num}.json

sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/UP Lost Packets/g" | sed -e "s/{2}/up_lost_packets/g" | sed -e "s/{3}/UP Lost Packets/g" | sed -e "s/{4}/${ID}/g" -> UpLostPackets-${num}.json
sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/Down Lost Packets/g" | sed -e "s/{2}/down_lost_packets/g" | sed -e "s/{3}/Down Lost Packets/g" | sed -e "s/{4}/${ID}/g" -> DownLostPackets-${num}.json

sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/UP Out of Order Packets/g" | sed -e "s/{2}/up_outoforder_packets/g" | sed -e "s/{3}/UP Out of Order Packets/g" | sed -e "s/{4}/${ID}/g" -> UpOoOP-${num}.json
sed -e "s/{0}/${num}/g" visualization-2.json | sed -e "s/{1}/Down Out of Order Packets/g" | sed -e "s/{2}/down_outoforder_packets/g" | sed -e "s/{3}/Down Out of Order Packets/g" | sed -e "s/{4}/${ID}/g" -> DownOoOP-${num}.json

sed -e "s/{0}/${num}/g" visualization-1.json | sed -e "s/{1}/Inter Delay/g" | sed -e "s/{2}/inter_delay/g" | sed -e "s/{3}/Inter Delay(ms)/g" | sed -e "s/{4}/${ID}/g" -> InterDelay-${num}.json
sed -e "s/{0}/${num}/g" visualization-1-ipdv.json | sed -e "s/{1}/IPDV/g" | sed -e "s/{2}/ipdv/g" | sed -e "s/{3}/IPDV(ms)/g" | sed -e "s/{4}/${ID}/g" -> IPDV-${num}.json
sed -e "s/{0}/${num}/g" visualization-1-pdv.json | sed -e "s/{1}/PDV/g" | sed -e "s/{2}/pdv/g" | sed -e "s/{3}/PDV(ms)/g" | sed -e "s/{4}/${ID}/g" -> PDV-${num}.json
sed -e "s/{0}/${num}/g" visualization-1.json | sed -e "s/{1}/RTT/g" | sed -e "s/{2}/rtt/g" | sed -e "s/{3}/RTT(ms)/g" | sed -e "s/{4}/${ID}/g" -> RTT-${num}.json
sed -e "s/{0}/${num}/g" visualization-1.json | sed -e "s/{1}/UP Delay/g" | sed -e "s/{2}/up_delay/g" | sed -e "s/{3}/UP Delay(ms)/g" | sed -e "s/{4}/${ID}/g" -> UpDelay-${num}.json
sed -e "s/{0}/${num}/g" visualization-1.json | sed -e "s/{1}/Down Delay/g" | sed -e "s/{2}/down_delay/g" | sed -e "s/{3}/Down Delay(ms)/g" | sed -e "s/{4}/${ID}/g" -> DownDelay-${num}.json

sed -e "s/{0}/${num}/g" visualization-1-ipdv.json | sed -e "s/{1}/UP IPDV/g" | sed -e "s/{2}/up_ipdv/g" | sed -e "s/{3}/UP IPDV(ms)/g" | sed -e "s/{4}/${ID}/g" -> UpIPDV-${num}.json
sed -e "s/{0}/${num}/g" visualization-1-ipdv.json | sed -e "s/{1}/Down IPDV/g" | sed -e "s/{2}/down_ipdv/g" | sed -e "s/{3}/Down IPDV(ms)/g" | sed -e "s/{4}/${ID}/g" -> DownIPDV-${num}.json

sed -e "s/{0}/${num}/g" visualization-1-pdv.json | sed -e "s/{1}/UP PDV/g" | sed -e "s/{2}/up_pdv/g" | sed -e "s/{3}/UP PDV(ms)/g" | sed -e "s/{4}/${ID}/g" -> UpPDV-${num}.json
sed -e "s/{0}/${num}/g" visualization-1-pdv.json | sed -e "s/{1}/Down PDV/g" | sed -e "s/{2}/down_pdv/g" | sed -e "s/{3}/Down PDV(ms)/g" | sed -e "s/{4}/${ID}/g" -> DownPDV-${num}.json


done

for num in 1s 5s
do
echo "count = ${num}"

sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/Duplicate Packets/g" | sed -e "s/{2}/duplicate_packets/g" | sed -e "s/{3}/${ID}/g" -> DuplicatePackets-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/Latency Delay/g" | sed -e "s/{2}/latency_delay/g" | sed -e "s/{3}/${ID}/g" -> LatencyDelay-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/Lost Packets/g" | sed -e "s/{2}/lost_packets/g" | sed -e "s/{3}/${ID}/g" -> LostPackets-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/Out of Order Packets/g" | sed -e "s/{2}/outoforder_packets/g" | sed -e "s/{3}/${ID}/g" -> OoOP-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/TTL/g" | sed -e "s/{2}/ttl/g" | sed -e "s/{3}/${ID}/g" -> TTL-${num}.json

sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/UP Duplicate Packets/g" | sed -e "s/{2}/up_duplicate_packets/g" | sed -e "s/{3}/${ID}/g" -> UpDuplicatePackets-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/Down Duplicate Packets/g" | sed -e "s/{2}/down_duplicate_packets/g" | sed -e "s/{3}/${ID}/g" -> DownDuplicatePackets-${num}.json

sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/UP Lost Packets/g" | sed -e "s/{2}/up_lost_packets/g" | sed -e "s/{3}/${ID}/g" -> UpLostPackets-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/Down Lost Packets/g" | sed -e "s/{2}/down_lost_packets/g" | sed -e "s/{3}/${ID}/g" -> DownLostPackets-${num}.json

sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/UP Out of Order Packets/g" | sed -e "s/{2}/up_outoforder_packets/g" | sed -e "s/{3}/${ID}/g" -> UpOoOP-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval2.json | sed -e "s/{1}/Down Out of Order Packets/g" | sed -e "s/{2}/down_outoforder_packets/g" | sed -e "s/{3}/${ID}/g" -> DownOoOP-${num}.json

sed -e "s/{0}/${num}/g" visualization-interval1.json | sed -e "s/{1}/Inter Delay/g" | sed -e "s/{2}/inter_delay/g" | sed -e "s/{3}/${ID}/g" -> InterDelay-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval1-ipdv.json | sed -e "s/{1}/IPDV/g" | sed -e "s/{2}/ipdv/g" | sed -e "s/{3}/${ID}/g" -> IPDV-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval1-pdv.json | sed -e "s/{1}/PDV/g" | sed -e "s/{2}/pdv/g" | sed -e "s/{3}/${ID}/g" -> PDV-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval1.json | sed -e "s/{1}/RTT/g" | sed -e "s/{2}/rtt/g" | sed -e "s/{3}/${ID}/g" -> RTT-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval1.json | sed -e "s/{1}/Up Delay/g" | sed -e "s/{2}/up_delay/g" | sed -e "s/{3}/${ID}/g" -> UpDelay-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval1.json | sed -e "s/{1}/Down Delay/g" | sed -e "s/{2}/down_delay/g" | sed -e "s/{3}/${ID}/g" -> DownDelay-${num}.json

sed -e "s/{0}/${num}/g" visualization-interval1-ipdv.json | sed -e "s/{1}/UP IPDV/g" | sed -e "s/{2}/up_ipdv/g" | sed -e "s/{3}/${ID}/g" -> UpIPDV-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval1-ipdv.json | sed -e "s/{1}/Down IPDV/g" | sed -e "s/{2}/down_ipdv/g" | sed -e "s/{3}/${ID}/g" -> DownIPDV-${num}.json

sed -e "s/{0}/${num}/g" visualization-interval1-pdv.json | sed -e "s/{1}/UP PDV/g" | sed -e "s/{2}/up_pdv/g" | sed -e "s/{3}/${ID}/g" -> UpPDV-${num}.json
sed -e "s/{0}/${num}/g" visualization-interval1-pdv.json | sed -e "s/{1}/Down PDV/g" | sed -e "s/{2}/down_pdv/g" | sed -e "s/{3}/${ID}/g" -> DownPDV-${num}.json

done
