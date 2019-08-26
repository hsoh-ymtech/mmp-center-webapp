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
        public deleteCheck?: boolean
    ) {
        deleteCheck = false;
     }

}
