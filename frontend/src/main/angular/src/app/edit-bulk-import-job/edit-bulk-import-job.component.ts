import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BulkImportJobDto, EpoBulkImportDto } from '../services/dtos';

@Component({
  selector: 'app-edit-bulk-import-job',
  templateUrl: './edit-bulk-import-job.component.html',
  styleUrls: ['./edit-bulk-import-job.component.scss']
})
export class EditBulkImportJobComponent implements OnInit {

  name = new FormControl('', [Validators.required]);
  description = new FormControl('', [Validators.required]);
  paths = new FormControl('', [Validators.required]);

  title = ""

  job: BulkImportJobDto;
  edit: boolean;

  constructor(private dialogRef: MatDialogRef<EditBulkImportJobComponent>,
    @Inject(MAT_DIALOG_DATA) data: any) { 
      this.job = data.job;
      this.edit = data.edit;
      if (data.edit) {
        this.title = "bearbeiten"
      } else {
        this.title = "erstellen"
      }
  }

  ngOnInit(): void {
    this.name.setValue(this.job?.name);
    
    let epoBulkImportDto = new EpoBulkImportDto();

    if (this.job && this.job.bulkImportData) {
      epoBulkImportDto = JSON.parse(this.job.bulkImportData);
    }

    let paths = "";

    if (epoBulkImportDto.files) {
      epoBulkImportDto.files.forEach(p => {
        paths = paths + p + "\n"
      })
      paths = paths.trim();
    }

    this.paths.setValue(paths);
    this.description.setValue(this.job?.description);
  }

  public save() {
    this.job.name = this.name.value;

    let epoBulkImportDto = new EpoBulkImportDto();

    if (this.paths.value) {
      let pathString = this.paths.value as string;
      pathString = pathString.trim();
      var allPaths = pathString.split("\n");
      epoBulkImportDto.files = allPaths;
    }

    this.job.bulkImportData = JSON.stringify(epoBulkImportDto);
    this.job.description = this.description.value;
    this.dialogRef.close("saved");
  }

  public cancel() {
    this.dialogRef.close("closed");
  }

}
