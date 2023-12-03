import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';

@Component({
  standalone: true,
  selector: 'app-flight-search',
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule
  ],
  templateUrl: './flight-search.component.html',
  styleUrls: ['./flight-search.component.css']
})

export class FlightSearchComponent {
  departureCity: string = "";
  destinationCity: string = "";
  date: Date = new Date("");
  passengerCount: number = 0;

  search() {
    console.log('Откуда:', this.departureCity);
    console.log('Куда:', this.destinationCity);
    console.log('Когда:', this.date);
    console.log('Количество пассажиров:', this.passengerCount);
  }
}