import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { StudentService } from 'src/app/core/services/student/student.service';

interface DashboardStats {
  appliedJobs: number;
  upcomingInterviews: number;
  profileComplete: number;
  notifications: number;
}

interface Deadline {
  title: string;
  company: string;
  date: Date;
  type: string;
}

interface JobPosting {
  title: string;
  company: string;
  location: string;
  deadline: Date;
}

@Component({
  selector: 'app-student-dashboard',
  templateUrl: './student-dashboard.component.html',
  styleUrls: ['./student-dashboard.component.css'],
})
export class StudentDashboardComponent {
  studentName = 'John Doe';
  stats: DashboardStats = {
    appliedJobs: 0,
    upcomingInterviews: 0,
    profileComplete: 0,
    notifications: 0,
  };

  deadlines: Deadline[] = [];
  recentJobs: JobPosting[] = [];
  isLoading = true;

  constructor(private studentService: StudentService, private router: Router) {}

  private loadDashboardData(): void {
    // this.studentService.getDashboardData().subscribe({
    //   next: (data) => {
    //     this.stats = data.stats;
    //     this.deadlines = data.deadlines;
    //     this.recentJobs = data.recentJobs;
    //     this.isLoading = false;
    //   },
    //   error: (error) => {
    //     console.error('Error loading dashboard data:', error);
    //     this.isLoading = false;
    //   },
    // });
  }

  // Add methods for quick actions
  updateProfile(): void {
    // Navigate to profile update page
  }

  viewApplications(): void {
    // Navigate to applications page
  }

  uploadResume(): void {
    // Handle resume upload
  }

  viewSchedule(): void {
    // Navigate to schedule page
  }

  studentImage: string = '';
  studentData: Object = {};

  ngOnInit(): void {
    const storedData = localStorage.getItem('student_Data');
    const storedImage = localStorage.getItem('student_Image');

    if (storedData) {
      this.studentData = JSON.parse(storedData);
    }

    if (storedImage) {
      this.studentImage = storedImage;
    }

    // if student data not is local storage then don't allow to access dashboard
    // if (!storedData) {
    //   this.router.navigate(['/']);
    // }
    this.loadDashboardData();
  }
}
