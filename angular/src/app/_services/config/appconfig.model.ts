export interface AppconfigModel {
    env: {
        name: string;
    };
    apiServer: {
        url: string;
    };
    pagination: {
        size: number;
        block: number;
    };
    kibana: {
        version: string;
    }
    login: {
        id: string;
        password: string;
    }
}