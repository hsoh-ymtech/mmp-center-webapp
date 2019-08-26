export class KibanaInfo {

    constructor(
        public kibanaId?: number,
        public url?: string,
        public host?: string,
        public eport?: number,
        public kport?: number
    ) { }

}

export class AddKibanaInfo {

    constructor(
        public host?: string,
        public eport?: number,
        public kport?: number
    ) { }

}

