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
echo curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d "@dashboard_empty.json" "http://${ELASTIC_HOST}:${ELASTIC_PORT}/.kibana/doc/dashboard:${DASHBOARD_UUID}"
echo ""


