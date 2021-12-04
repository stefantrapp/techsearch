package de.fernunihagen.techsearch.restservice;

/**
 * Klase für eine Response der API.
 *
 * @param <T>
 */
public class ApiResponse<T> {
    
    /**
     * Das eigentlich Ergebnis der API-Aufrufs.
     */
    private T result;
    
    /**
     * true, wenn ein Fehler aufgetreten ist.
     */
    private boolean error;
    
    /**
     * Eine Fehlermeldung bei einem Fehler. 
     */
    private String errorMessage;

    public ApiResponse() {
    }
    
    public ApiResponse(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
