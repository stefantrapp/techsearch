import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DocumentDto, DocumentFullDto, DocumentIdDto, DocumentQueryDto, DocumentQueryResponseDto } from './dtos';
import { ServiceHelper } from './service-helper';
import { ServiceHelperFactory } from './service-helper-factory';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  
  private serviceHelper: ServiceHelper;

  constructor(serviceHelperFactory: ServiceHelperFactory) {
    this.serviceHelper = serviceHelperFactory.createServiceHelper("api/document");
  }

  public searchDocuments(query: DocumentQueryDto): Observable<DocumentQueryResponseDto> {
    return this.serviceHelper.callService<DocumentQueryResponseDto>("search", query);
  }

  public getDocumentAsText(id: number): Observable<DocumentDto> {
    return this.serviceHelper.callService<DocumentDto>("doc", id);
  }

  public deleteDocument(id: number): Observable<DocumentDto> {
    const dto = new DocumentIdDto();
    dto.id = id;
    return this.serviceHelper.callService<DocumentDto>("delete", dto);
  }

  public testPreprocessDocument(id: number): Observable<DocumentFullDto> {
    const dto = new DocumentIdDto();
    dto.id = id;
    return this.serviceHelper.callService<DocumentFullDto>("test-preprocess", dto);
  }

  public getDocumentDownloadUrl(id: Number): string {
    return this.serviceHelper.createGetUrl("doc-orig", `id=${id}`);
  }

  public deleteAllDocuments(): Observable<string> {
    return this.serviceHelper.callService("delete-all", null);
  }
}
