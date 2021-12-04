package de.fernunihagen.techsearch.restservice;

/**
 * Klasse für einen API-Aufruf, der einfach nur erfolgreich durchgeführt wurde ohne dass Daten zurückgegeben werden müssen.
 */
public class ApiOkResponse extends ApiResponse<String> {
    public ApiOkResponse() {
        super("OK");
    }
}
