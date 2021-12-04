import { Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { MessageBoxResult } from '../message-box/message-box.component';
import { AdministrationService } from '../services/administration.service';
import { DocumentService } from '../services/document.service';
import { ApplicationJobName, JobStatusResponseDto, SettingDto, SettingUpdateDto } from '../services/dtos';
import { MessageBoxService } from '../services/message-box.service';
import { NotificationService } from '../services/notification.service';
import { ComponentBase } from '../shared/component-base';

@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.scss']
})
export class AdministrationComponent extends ComponentBase implements OnInit {

  constructor(private administrationService: AdministrationService,
    protected notificationService: NotificationService,
    private documentService: DocumentService,
    private messageBoxService: MessageBoxService,
    private route: ActivatedRoute,
    private router: Router) {
    super(notificationService);
  }

  jobNameName = {
    REBUILD_SEARCH_INDEX : "Index neu erzeugen",
    REBUILD_WORD2VEC_MODEL : "Word2Vec-Model erzeugen",
    REBUILD_DOC2VEC_MODEL : "Doc2Vec-Model erzeugen",
    REBUILD_SENT2VEC_MODEL : "Sent2vec-Model erzeugen",
    PREPROCESS_DOCUMENTS : "Dokumente vorverarbeiten",
    PROCESS_CRAWL_JOBS : "Webcrawl-Jobs ausführen",
    BULK_IMPORT_BATCH : "Massendaten importieren",
  }

  public preprocessDocumentsJobStartRespone: string = "";
  public preprocessDocumentsJobCancelRespone: string = "";

  tabIndex = 0;

  jobStatusAll?: JobStatusResponseDto[];

  settings?: SettingDto[]

  ngOnInit(): void {
    this.getAllJobStates();

    this.route.queryParams.subscribe(params => {
      const tabIndex = params["tab"];
      if (tabIndex) {
        this.tabIndex = Number.parseInt(tabIndex);

        this.tabChanged(this.tabIndex);
      }
    });
  }

  tabChanged(index: number): void {
    if (index === 1 && !this.settings) { // Einstellungen
      this.getAllSettings();
    }

    const queryParams: Params = {
      tab: index
    };

    this.router.navigate(
      [], {
         relativeTo: this.route,
         queryParams: queryParams,
         queryParamsHandling: 'merge',
      });
  }

  getJobName(job: JobStatusResponseDto) {
    if (job.jobName) {
      return this.jobNameName[job.jobName];
    }
    return "";
  }

  cancelJob(jobName?: ApplicationJobName) {
    if (jobName) {
      this.administrationService.cancelJob(jobName).subscribe(result => {
        if (result.success) {
          this.notificationService.success("Job zum Abbrechen markiert");
        }
      }, error => super.showError(error));
    }
  }

  scheduleJob(jobName?: ApplicationJobName) {
    if (jobName) {
      this.administrationService.scheduleJob(jobName).subscribe(result => {
        if (result.success) {
         this.notificationService.success("Job eingeplant");
        }
      },
        error => super.showError(error));
    }
  }

  getAllJobStates() {
    this.administrationService.getAllJobStates().subscribe(result => {
      this.jobStatusAll = result.jobStatus?.sort((a, b) => {
        if (typeof a.jobName === "string" && typeof b.jobName === "string" ) {
          return this.jobNameName[a.jobName] < this.jobNameName[b.jobName] ? -1 : 1;
        }
        return 0;
      });
    }, error => super.showError(error));
  }

  getAllSettings() {
    this.administrationService.getAllSettings().subscribe(result => {
      this.settings = result.settings?.sort((a, b) => {
        if (typeof a.position === "number" && typeof b.position === "number" ) {
          return a.position < b.position ? -1 : 1;
        }
        return 0;
      });
    }, error => super.showError(error));
  }

  saveSetting(setting: SettingDto) {
    const settingUpdateDto  = new SettingUpdateDto();
    settingUpdateDto.key = setting.key;
    settingUpdateDto.value = setting.value;
    this.administrationService.saveSetting(settingUpdateDto).subscribe(result => {
      this.notificationService.success("Gespeichert");
    }, error => super.showError(error));
  }

  deleteAllDocuments() {
    this.messageBoxService.queryYesNo("Wollen Sie wirklich alle Dokumente löschen?").subscribe(
      answer => {
        if (answer === MessageBoxResult.Yes) {
          this.documentService.deleteAllDocuments().subscribe(r => {
            if (r === "OK") {
              this.notificationService.success("Alle Dokumente gelöscht.");
            }
          }, error => super.showError(error));
        }
      }
    );
  }
}
