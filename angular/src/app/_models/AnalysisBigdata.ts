export class AnalysisBigdata {

    public senderIp: string;
    public reflectorIp: string;
    public lostPacketTH: number;
    public duplicatePacketTH: number;
    public outoforderPacketTH: number;
    public pdvTH: number;
    public ipdvTH: number;
    public startTime: any;
    public endtime: any;

    constructor() {
        this.senderIp = '';
        this.reflectorIp = '';
        this.lostPacketTH = 0;
        this.duplicatePacketTH = 0;
        this.outoforderPacketTH = 0;
        this.pdvTH = 0;
        this.ipdvTH = 0;
        this.startTime = '';
        this.endtime = '';
    }
}
