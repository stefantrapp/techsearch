import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'jaNein'
})
export class JaNeinPipe implements PipeTransform {

  transform(value: unknown, ...args: unknown[]): unknown {
    if (value === true) {
      return "Ja";
    } else if (value === false) {
      return "Nein";
    }
    return "";
  }

}
