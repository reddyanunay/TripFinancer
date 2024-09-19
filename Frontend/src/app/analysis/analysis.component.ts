import { Component, OnInit } from '@angular/core';
import { ApicallsService } from '../apicalls.service';

@Component({
  selector: 'app-analysis',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.css']
})
export class AnalysisComponent implements OnInit {
  members: any[] = []; // List of members
  selectedMemberId: string = 'total'; // Default to 'total'
  analysisData: any;

  constructor(private ser: ApicallsService) {}

  ngOnInit() {
    this.ser.getMembers(this.ser.tempTripId).subscribe(data => {
      this.members = data;
      console.log('Members:', this.members);
      this.loadTotalSummary();
    });
  }

  loadTotalSummary() {
    this.ser.getTotalTripSummary(this.ser.tempTripId).subscribe(data => {
      this.analysisData = data;
      console.log('Total Summary Data:', this.analysisData);
      // You can add more logic here to handle the data and display it
    });
  }

  selectMember(memberId: any) {
    console.log('Selected Member ID:', memberId); // Debugging: Check selected member ID
    this.selectedMemberId = memberId;
    if (memberId === 'total') {
      this.analysisData = this.loadTotalSummary(); // Set to total summary data
    } else {
      this.loadAnalysis(memberId);
    }
  }

  loadAnalysis(memberId: any) {
    let apiUrl = `/api/trips/${this.ser.tempTripId}/personal-summary/${memberId}`; 
    this.ser.getAnalysisForMember(apiUrl).subscribe(data => {
      this.analysisData = data;
      console.log('Analysis Data:', this.analysisData); // Debugging: Check analysis data
    });
  }

  getMemberName(memberId: any): any {
    const member = this.members.find(m => m.memberId === memberId);
    return member ? member.name : 'Unknown Member';
  }

}
