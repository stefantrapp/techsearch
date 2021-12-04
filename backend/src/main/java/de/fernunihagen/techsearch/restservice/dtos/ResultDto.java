package de.fernunihagen.techsearch.restservice.dtos;

/* DTO, das immer zurückgegeben werden kann. */
public class ResultDto {
    
    private boolean success = true;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    } 
}
