import { Component, OnInit } from '@angular/core';
import { ApicallsService } from '../apicalls.service';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
Chart.register(...registerables);

@Component({
  selector: 'app-analysis',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.css']
})
export class AnalysisComponent implements OnInit {
  members: any[] = []; // List of members
  selectedMemberId: string = 'total'; // Default to 'total'
  analysisData: any;
  billsPaidColumns: any[] = []; // Define columns for bills paid table
  percentageData: number[] = []; // Data for percentage chart
  percentageLabels: string[] = []; // Labels for percentage chart
  totalExpenditureData: any[] = []; // Data for total expenditure bar chart
  totalExpenditureLabels: string[] = []; // Labels for total expenditure
  comparativeData: any[] = []; // Data for comparative analysis bar chart
  comparativeLabels: string[] = []; // Labels for comparative analysis
  chartOptions: any = { responsive: true }; // Chart options


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
      this.prepareTotalSummaryData();
      // You can add more logic here to handle the data and display it
    });
  }
  prepareTotalSummaryData() {
    // Prepare data for percentage chart
    this.percentageData = this.analysisData.memberPercentages.map((member: { percentageOfTotal: any; }) => member.percentageOfTotal);
    this.percentageLabels = this.analysisData.memberPercentages.map((member: { memberName: any; }) => member.memberName);

    // Prepare data for total expenditure chart
    this.totalExpenditureData = this.analysisData.eachMemberTotalExpenditure.map((member: { totalExpenditure: any; memberName: any; memberId:any; }) => ({
      data: [member.totalExpenditure],
      label: member.memberName
    }));
    this.totalExpenditureLabels = this.analysisData.eachMemberTotalExpenditure.map((member: { memberName: any; }) => member.memberName);
    
    // Prepare comparative analysis data
    for (const key in this.analysisData.eachMemberTotalExpenditure) {
      if (this.analysisData.eachMemberTotalExpenditure.hasOwnProperty(key)) {
        const member = this.analysisData.eachMemberTotalExpenditure[key];
        this.comparativeData.push(member.totalExpenditure);
        this.comparativeLabels.push(member.memberName);
      }
    }

    // Define columns for bills paid table if needed
    this.billsPaidColumns = [
      { prop: 'billName', name: 'Bill Name' },
      { prop: 'amount', name: 'Amount' }
    ];
  }

  selectMember(memberId: any) {
    console.log('Selected Member ID:', memberId); // Debugging: Check selected member ID
    this.selectedMemberId = memberId;
    if (memberId === 'total') {
      this.loadTotalSummary(); // Set to total summary data
    } else {
      this.loadAnalysis(memberId);
    }
  }

  loadAnalysis(memberId: any) {
    let apiUrl = `/api/trips/${this.ser.tempTripId}/personal-summary/${memberId}`; 
    this.ser.getAnalysisForMember(apiUrl).subscribe(data => {
      this.analysisData = data;
      console.log('Analysis Data:', this.analysisData);
      // Prepare data for individual member analysis
      this.prepareMemberAnalysisData();
    });
  }
  prepareMemberAnalysisData() {
    // Prepare data for bills paid table
    this.analysisData.billsPaid = this.analysisData.billsPaid || [];
    this.billsPaidColumns = [
      { prop: 'billName', name: 'Bill Name' },
      { prop: 'amount', name: 'Amount' }
    ];

    // Prepare comparative analysis data
    this.comparativeData = [];
    this.comparativeLabels = [];
    for (const key in this.analysisData.comparativeAnalysis) {
      if (this.analysisData.comparativeAnalysis.hasOwnProperty(key)) {
        const member = this.analysisData.comparativeAnalysis[key];
        this.comparativeData.push(member.totalExpenditure);
        this.comparativeLabels.push(member.memberName);
      }
    }
  }


  getMemberName(memberId: any): any {
    const member = this.members.find(m => m.memberId === memberId);
    return member ? member.name : 'Unknown Member';
  }

}
