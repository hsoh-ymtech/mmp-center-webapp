export class Pagination {

    constructor(
        public content: object,
        public count: number,
        public allpage: number,
        public pg: number,
        public startpage: number,
        public endpage: number,
        public paginationIndex: number[]
    ) { }

}
