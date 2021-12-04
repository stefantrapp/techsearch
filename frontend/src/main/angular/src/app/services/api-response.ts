export class ApiResponse<T>
{
    public error: boolean | undefined
    public errorMessage: string | undefined
    public result: T | undefined;
}

export const ApiOkResponseValue = "OK";