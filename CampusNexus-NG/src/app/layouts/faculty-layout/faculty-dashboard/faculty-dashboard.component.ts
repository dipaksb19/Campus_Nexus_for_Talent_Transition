import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-faculty-dashboard',
  templateUrl: './faculty-dashboard.component.html',
  styleUrls: ['./faculty-dashboard.component.css'],
})
export class FacultyDashboardComponent {
  constructor(private router: Router) {}

  userData: Object = {};

  ngOnInit(): void {
    const storedData = localStorage.getItem('user_Data');

    if (storedData) {
      this.userData = JSON.parse(storedData);
    }

    if (!storedData) {
      this.router.navigate(['/']);
    }
  }
}
