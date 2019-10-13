export class CurrentStatus {

    constructor(
        public senderIp?: string,
        public reflectorIp?: string,
        public sendCount?: number,
        public protocol?: string,
        public measureMode?: string,
    ) { }
}
