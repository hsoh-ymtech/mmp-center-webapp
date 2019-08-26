export class QualityHistory {

    constructor(
        public sessId: number,
        public senderIp: string,
        public reflectorip: string,
        public sendCount: number,
        public repeatCount: number,
        public startTime: string,
        public completeTime: string,
        public requestProtocol: string,
        public actualProtocol: string,
        public senderPort: number,
        public reflectorPort: number,
        public measureResult: string
    ) { }

}
