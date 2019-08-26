export class LesionHistory {

    constructor(
        public sessId: number,
        public startTime: string,
        public completeTime: string,
        public senderIp: string,
        public reflectorip: string,
        public Protocol: string,
        public lesionCode: number
    ) { }

}
