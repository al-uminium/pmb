import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-profile-picture',
  standalone: true,
  imports: [],
  templateUrl: './profile-picture.component.html',
  styleUrl: './profile-picture.component.css'
})
export class ProfilePictureComponent {
  @Input()
  username!: string;

  @Input()
  showUsername: boolean = true;

  @Input()
  size: string = "normal";

  // to convert username to a color.
  getRGBColor(name: string): string {
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }

    const r = Math.abs((hash >> 0) & 255);
    const g = Math.abs((hash >> 8) & 255);
    const b = Math.abs((hash >> 16) & 255);

    return `rgb(${r}, ${g}, ${b})`;
  }

  getTextColor(name: string): string {
    const rgbColor = this.getRGBColor(name);
    const r = parseInt(rgbColor.substring(4, rgbColor.indexOf(',')), 10);
    const g = parseInt(rgbColor.substring(rgbColor.indexOf(',') + 2, rgbColor.lastIndexOf(',')), 10);
    const b = parseInt(rgbColor.substring(rgbColor.lastIndexOf(',') + 2, rgbColor.indexOf(')')), 10);

    const lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255;

    // Use white or black based on luminance
    return lum > 0.5 ? 'black' : 'white';
  }
}
