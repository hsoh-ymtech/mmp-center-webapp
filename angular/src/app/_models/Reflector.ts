import { Protocol } from "./Dashboard";

export class Reflector {

    constructor(
        public reflectorId: number,
        public reflectorIp?: string,
        public protocol?: Protocol,
        public port?: number,
        public lat?: number,
        public lng?: number,
        public address?: string,
        public country?: string,
        public os?: string,
        public osVersion?: string,
        public meshId?: string,
        public enabled?:boolean,
        public macAddress?: string,
        public outboundIpAddress?: string,
        public deleteCheck?: boolean,
        public alive?:boolean
    ) {
        deleteCheck = false;
     }

}
