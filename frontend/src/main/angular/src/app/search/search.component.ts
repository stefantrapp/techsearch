import { AfterViewInit, Component, Inject, OnInit, SecurityContext, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { Language, SearchDto, SearchResultDto, SearchResultEntryDto } from '../services/dtos';
import { SearchService } from '../services/search.service';
import { switchMap } from 'rxjs/operators';
import { LanguageUi } from '../shared/language';
import { NotificationService } from '../services/notification.service';
import { MatTabChangeEvent, MatTabGroup } from '@angular/material/tabs';

class ImageHolder {
  constructor(public data: string) {
  }
}

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit, AfterViewInit {

  languages = [
    new LanguageUi("GERMAN", "Deutsch"),
    new LanguageUi("ENGLISH", "Englisch")
  ];

  language: any;

  selectedTab: number = -1;

  @ViewChild("tabgroup") 
  tabgroup?: MatTabGroup; 

  constructor(private searchService: SearchService,
    private sanitizer: DomSanitizer,
    private router: Router,
    private route: ActivatedRoute,
    protected notificationService: NotificationService,
    @Inject("BASE_URL") private baseUrl: string) { }

  public searchResults?: SearchResultEntryDto[];

  public nothingFound = false;

  public query?: string;

  public queryWord2Vec?: string;
  
  public queryDoc2Vec?: string;

  public querySent2Vec?: string;

  public queryPos?: string;

  public tsnequery?: string;

  public tsneData: ImageHolder | null = null;

  private initialTablIndex = 0;

  public word2vecWords: string[] | null = null;
  
  ngOnInit(): void {
    var params = this.route.snapshot.paramMap;
    // Aufrufen mit http://localhost:4200/suche/w2v/query
    const type = params.get("type");
    const query = params.get("query");

    let index = 0;

    if (type && query) {
      if (type === "index") {
        this.query = query;
        index = 0;
      } else if (type === "word2vec") {
        this.queryWord2Vec = query;
        index = 1;
      } else if (type === "doc2vec") {
        this.queryDoc2Vec = query;
        index = 2;
      } else if (type === "sent2vec") {
        this.querySent2Vec = query;
        index = 3;
      } else if (type === "pos") {
        this.queryPos = query;
        index = 4;
      }

      this.initialTablIndex = index;

      if (this.tabgroup) {
        this.tabgroup.selectedIndex = index;
      }
    }
    
    this.word2vecWords = [];
  }

  ngAfterViewInit() {
    if (this.tabgroup) {
      this.tabgroup.selectedIndex = this.initialTablIndex;
    }
  }

  onTabChanged($event: MatTabChangeEvent) {
    this.selectedTab = $event.index;
    this.nothingFound = false;
  }

  public callGenerateTsne() {
    const searchDto: SearchDto = new SearchDto();
    searchDto.searchTerm = this.tsnequery;
    const lng = this.getLanguage();
    if (lng && searchDto.searchTerm) {
      searchDto.searchTerm = searchDto.searchTerm.toLowerCase();
      searchDto.language = lng;

      this.searchService.callGenerateTsne(searchDto).subscribe(
        result => {
          if (result.imageBase64 && result.imageBase64.length > 0) {
            this.tsneData = new ImageHolder("data:image/png;base64, " + result.imageBase64);
          } else {
            this.tsneData = null;
          }
        }
      )
    }
  }

  getData(imageHolder: ImageHolder): any {
    if (this.tsneData) {
      return this.sanitizer.bypassSecurityTrustResourceUrl(imageHolder.data);
    } else {
      return null;
    }
  }

  public searchIndex() {
    const searchDto: SearchDto = new SearchDto();
    searchDto.searchTerm = this.query;
    const lng = this.getLanguage();
    if (lng && searchDto.searchTerm) {
      searchDto.language = lng;

      this.searchService.callSearchIndex(searchDto).subscribe(
        result => {
          this.showDocuments(result);
        }
      )
    }
  }

  public searchPos() {
    const searchDto: SearchDto = new SearchDto();
    searchDto.searchTerm = this.queryPos;
    const lng = this.getLanguage();
    if (lng && searchDto.searchTerm) {
      searchDto.language = lng;
      this.searchService.callSearchsearchPos(searchDto).subscribe(
        result => {
          this.showDocuments(result);
        }
      )
    }
  }

  public searchWord2VecWithWord(word: string) {
    this.queryWord2Vec = word;
    this.searchWord2Vec();
  }

  public searchWord2Vec() {
    const searchDto: SearchDto = new SearchDto();
    searchDto.searchTerm = this.queryWord2Vec;
    const lng = this.getLanguage();
    if (lng && searchDto.searchTerm) {
      searchDto.language = lng;
      searchDto.searchTerm = searchDto.searchTerm.toLowerCase();
      this.searchService.callSearchWord2Vec(searchDto).subscribe(
        result => {
          this.showDocuments(result);
          if (result.word2vecWords) {
            this.word2vecWords = result.word2vecWords;
          } else {
            this.word2vecWords = null;
          }
        }
      )
    }
  }

  public searchDoc2Vec() {
    const searchDto: SearchDto = new SearchDto();
    searchDto.searchTerm = this.queryDoc2Vec;
    const lng = this.getLanguage();
    if (lng) {
      searchDto.language = lng;
      this.searchService.callSearchDoc2Vec(searchDto).subscribe(
        result => {
          this.showDocuments(result);
        }
      )
    }
  }

  public searchSent2Vec() {
    const searchDto: SearchDto = new SearchDto();
    searchDto.searchTerm = this.querySent2Vec;
    const lng = this.getLanguage();
    if (lng && searchDto.searchTerm) {
      searchDto.language = lng;
      searchDto.searchTerm = searchDto.searchTerm.toLowerCase();
      this.searchService.callSearchSent2Vec(searchDto).subscribe(
        result => {
          this.showDocuments(result);
        }
      )
    }
  }

  private showDocuments(result: SearchResultDto) {
    const searchResultDto = SearchResultDto.fromData(result);
    if (searchResultDto) {
      let guiList: SearchResultEntryDto[] = new Array<SearchResultEntryDto>();

      searchResultDto.entries?.forEach(element => {
        var gui = new SearchResultEntryDto();
        guiList.push(gui);
        gui.documentId = element.documentId;
        gui.titel = element.titel;
        const sani = this.sanitizer.sanitize(SecurityContext.HTML, element.auszug as string) as string;
        gui.auszug = sani;
      });

      this.searchResults = guiList;

      this.nothingFound = guiList.length === 0;
    }
  }

  

  public showDocument(searchResultEntryDto: SearchResultEntryDto) {
    var url = `/dokument/${searchResultEntryDto.documentId}`
    window.open(url);
  }

  getLanguage(): Language | null {
    if (this.language) {
      if (this.language.name === "GERMAN") {
        return Language.GERMAN;
      } else if (this.language.name === "ENGLISH") {
        return Language.ENGLISH;
      }
      return null;
    } else {
      this.showChooseLang();
      return null;
    }
  }
  showChooseLang() {
    this.notificationService.warn("Bitte eine Sprache auswählen!");
  }
}
