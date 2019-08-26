#!/bin/bash

PROTOCOLS=("TWAMP" "ICMP")
HOSTS=("10.11.1.157" "10.11.1.158" "10.11.1.159" "10.11.1.160" "10.11.1.161" "10.11.1.162" "10.11.1.163" "10.11.1.164")

NUM=1
REPEATCOUNT=1
SLEEPCOUNT=1
SENDCOUNT=1
PROTO="TWAMP"

while true
do
	case $((${NUM} % 5)) in
	0)
                REPEATCOUNT=100
		SLEEPCOUNT=9
		SENDCOUNT=100
		PROTO="TWAMP"
		;;
	1)
		REPEATCOUNT=1
		SLEEPCOUNT=1
		SENDCOUNT=1
		PROTO="ICMP"
		;;
	2)
                REPEATCOUNT=5
		SLEEPCOUNT=3
		SENDCOUNT=1
		PROTO="ICMP"
		;;
	3)
                REPEATCOUNT=10
		SLEEPCOUNT=5
		SENDCOUNT=10
		PROTO="TWAMP"
		;;
	4)
                REPEATCOUNT=50
		SLEEPCOUNT=7
		SENDCOUNT=100
		PROTO="TWAMP"
		;;
	*)
		;;
	esac

	r=$((RANDOM % 8))
	curl -X POST -H "Content-Type: application/json" -d '
{
	"senderIp" : "192.168.80.128",
	"senderPort" : 2000,
	"reflectorIp" : '\"${HOSTS[$r]}\"',
	"reflectorPort" : 20000,
	"protocol" : '\"${PROTO}\"',
	"repeatCount" : '"${REPEATCOUNT}"',
	"sendCount" : '"${SENDCOUNT}"',
	"startTime" : "2018-09-01 10:00:00.000",
	"timeout" : 3
}
' "http://localhost:8090/current-status"
	NUM=`expr $NUM + 1`
	sleep ${SLEEPCOUNT}
done
