#!/bin/sh

ELASTIC_HOST=""
ELASTIC_PORT=""

#ELASTIC_HOST="39.119.118.191"
#ELASTIC_PORT="9200"

export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin


# 실행 명령 확인
COMMAND=$(stat -c "%n" "$0")
CMDNAME=$(basename "${COMMAND}")
CMDDIR=$(readlink -fn "$(dirname "${COMMAND}")")

# 실행 명령에 대한 실제 파일 확인
REALPATH=$(readlink -fn "${COMMAND}")
REALNAME=$(basename "${REALPATH}")
REALDIR=$(dirname "${REALPATH}")

# 사용법
usage() {
	echo "Usage: $0 [options] " 1>&2
	echo "	-h           this message" 1>&2
	echo "	-H           ElasticSearch Host" 1>&2
	echo "	-P           ElasticSearch Port" 1>&2
	echo "" 1>&2

	exit 1;
}

# option 처리
# 참고: c/c++ 프로그램의 arguments 는 option과 섞여서 사용될 수 있지만
#       getopts는 option/option parameter 뒤에 arguments 가 올 수 있다.
#       즉, option과 argument가 섞이면 오류
while getopts ":H:P:h" opt; do
	case "${opt}" in
		H)
			ELASTIC_HOST=${OPTARG}
			;;
		P)
			ELASTIC_PORT=${OPTARG}
			;;
		h)
			usage
			;;
		*)
			usage
			;;
	esac
done

# option 및 option parameter는 건너뛰기
shift $((OPTIND-1))

if [ -z "${ELASTIC_HOST}" ] ; then
	echo "Must be set -H parameter"
	usage
fi

if [ -z "${ELASTIC_PORT}" ] ; then
	echo "Must be set -P parameter"
	usage
fi

DASHBOARD_UUID="twamp_empty"
echo "Dashboard UUID: ${DASHBOARD_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@dashboard_empty.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/dashboard:${DASHBOARD_UUID}"
echo ""

for num in 1 10 60 300 600 3600 7200 1s 5s
do

echo "---------------------------------------"
echo "count = ${num}"
echo "---------------------------------------"

DP_UUID="duplicate_packets_${num}"
echo "DP UUID: ${DP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@DuplicatePackets-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${DP_UUID}"
echo ""

ID_UUID="inter_delay_${num}"
echo "ID UUID: ${ID_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@InterDelay-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${ID_UUID}"
echo ""

IPDV_UUID="ipdv_${num}"
echo "IPDV UUID: ${IPDV_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@IPDV-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${IPDV_UUID}"
echo ""

LP_UUID="lp_${num}"
echo "LP UUID: ${LP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@LostPackets-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${LP_UUID}"
echo ""

OoOP_UUID="ooop_${num}"
echo "OoOP UUID: ${OoOP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@OoOP-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${OoOP_UUID}"
echo ""

PDV_UUID="pdv_${num}"
echo "PDV UUID: ${PDV_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@PDV-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${PDV_UUID}"
echo ""

RTT_UUID="rtt_${num}"
echo "RTT UUID: ${RTT_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@RTT-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${RTT_UUID}"
echo ""

LD_UUID="ld_${num}"
echo "LD UUID: ${LD_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@LatencyDelay-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${LD_UUID}"
echo ""

TTL_UUID="ttl_${num}"
echo "TTL UUID: ${TTL_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@TTL-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${TTL_UUID}"
echo ""



UD_UUID="up_delay_${num}"
echo "Up Delay UUID: ${UD_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@UpDelay-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${UD_UUID}"
echo ""

DD_UUID="down_delay_${num}"
echo "Down Delay UUID: ${DD_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@DownDelay-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${DD_UUID}"
echo ""



ULP_UUID="up_lp_${num}"
echo "ULP UUID: ${ULP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@UpLostPackets-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${ULP_UUID}"
echo ""

DLP_UUID="down_lp_${num}"
echo "DLP UUID: ${DLP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@DownLostPackets-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${DLP_UUID}"
echo ""

UDP_UUID="up_duplicate_packets_${num}"
echo "UDP UUID: ${UDP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@UpDuplicatePackets-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${UDP_UUID}"
echo ""

DDP_UUID="down_duplicate_packets_${num}"
echo "DDP UUID: ${DDP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@DownDuplicatePackets-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${DDP_UUID}"
echo ""

# UID_UUID="up_inter_delay_${num}"
# echo "UID UUID: ${UID_UUID}"
# curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@UpInterDelay-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${UID_UUID}"
# echo ""

# DID_UUID="down_inter_delay_${num}"
# echo "DID UUID: ${DID_UUID}"
# curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@DownInterDelay-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${DID_UUID}"
# echo ""

UIPDV_UUID="up_ipdv_${num}"
echo "UIPDV UUID: ${UIPDV_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@UpIPDV-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${UIPDV_UUID}"
echo ""

DIPDV_UUID="down_ipdv_${num}"
echo "DIPDV UUID: ${DIPDV_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@DownIPDV-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${DIPDV_UUID}"
echo ""

UPDV_UUID="up_pdv_${num}"
echo "UPDV UUID: ${UPDV_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@UpPDV-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${UPDV_UUID}"
echo ""

DPDV_UUID="down_pdv_${num}"
echo "DPDV UUID: ${DPDV_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@DownPDV-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${DPDV_UUID}"
echo ""






UOoOP_UUID="up_ooop_${num}"
echo "UOoOP UUID: ${UOoOP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@UpOoOP-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${UOoOP_UUID}"
echo ""

DOoOP_UUID="down_ooop_${num}"
echo "DOoOP UUID: ${DOoOP_UUID}"
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@DownOoOP-${num}.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/visualization:${DOoOP_UUID}"
echo ""


done

