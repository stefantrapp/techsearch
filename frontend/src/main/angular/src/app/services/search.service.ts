import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SearchDto, SearchResultDto, TsneDto } from './dtos';
import { ServiceHelper } from './service-helper';
import { ServiceHelperFactory } from './service-helper-factory';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  
  private serviceHelper: ServiceHelper;

  constructor(serviceHelperFactory: ServiceHelperFactory) {
    this.serviceHelper = serviceHelperFactory.createServiceHelper("api/query");
  }

  public callSearchIndex(searchDto: SearchDto): Observable<SearchResultDto> {
    return this.serviceHelper.callService<SearchResultDto>("search-index", searchDto);
  }

  public callSearchWord2Vec(searchDto: SearchDto): Observable<SearchResultDto> {
    return this.serviceHelper.callService<SearchResultDto>("search-word2vec", searchDto);
  }

  public callSearchDoc2Vec(searchDto: SearchDto): Observable<SearchResultDto> {
    return this.serviceHelper.callService<SearchResultDto>("search-doc2vec", searchDto);
  }

  public callSearchSent2Vec(searchDto: SearchDto): Observable<SearchResultDto> {
    return this.serviceHelper.callService<SearchResultDto>("search-sent2vec", searchDto);
  }

  public callSearchsearchPos(searchDto: SearchDto): Observable<SearchResultDto> {
    return this.serviceHelper.callService<SearchResultDto>("search-pos", searchDto);
  }
  
  public callGenerateTsne(searchDto: SearchDto): Observable<TsneDto> {
    return this.serviceHelper.callService<TsneDto>("generateTsne", searchDto);
  }
}
