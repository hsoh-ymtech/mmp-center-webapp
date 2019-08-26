export class CurrentStatus {

    constructor(
        public sessId?: number,
        public senderIp?: string,
        public reflectorIp?: string,
        public sendCount?: number,
        public repeatCount?: number,
        public senderPort?: number,
        public reflectorPort?: number,
        public protocol?: string,
        public startTime?: string,
        public pid?: number,
        public timeout?: number,
        public measureMode?: string,
        public id?: string,
        public password?: string
    ) { }
}
